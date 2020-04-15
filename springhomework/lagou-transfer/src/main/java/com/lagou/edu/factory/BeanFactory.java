package com.lagou.edu.factory;

import com.lagou.edu.anno.Autowired;
import com.lagou.edu.anno.Component;
import com.lagou.edu.anno.Service;
import com.lagou.edu.anno.Transactional;
import com.lagou.edu.utils.ClassUtil;
import com.lagou.edu.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLOutput;
import java.util.*;

import com.lagou.edu.dao.impl.JdbcAccountDaoImpl;
/**
 * @author 应癫
 *
 * 工厂类，生产对象（使用反射技术）
 */
public class BeanFactory {

    /**
     * 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
     * 任务二：对外提供获取实例对象的接口（根据id获取）
     */

    private static Map<String,Object> map = new HashMap<>();  // 存储对象


    static {
        // 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
        // 加载xml
//       // InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
//        // 解析xml
//        SAXReader saxReader = new SAXReader();
//        try {
//            Document document = saxReader.read(resourceAsStream);
//            Element rootElement = document.getRootElement();
//            List<Element> beanList = rootElement.selectNodes("//bean");
//            for (int i = 0; i < beanList.size(); i++) {
//                Element element =  beanList.get(i);
//                // 处理每个bean元素，获取到该元素的id 和 class 属性
//                String id = element.attributeValue("id");        // accountDao
//                String clazz = element.attributeValue("class");  // com.lagou.edu.dao.impl.JdbcAccountDaoImpl
//                // 通过反射技术实例化对象
//                Class<?> aClass = Class.forName(clazz);
//                Object o = aClass.newInstance();  // 实例化之后的对象
//
//                // 存储到map中待用
//                map.put(id,o);
//
//            }
//
//            // 实例化完成之后维护对象的依赖关系，检查哪些对象需要传值进入，根据它的配置，我们传入相应的值
//            // 有property子元素的bean就有传值需求
//            List<Element> propertyList = rootElement.selectNodes("//property");
//            // 解析property，获取父元素
//            for (int i = 0; i < propertyList.size(); i++) {
//                Element element =  propertyList.get(i);   //<property name="AccountDao" ref="accountDao"></property>
//                String name = element.attributeValue("name");
//                String ref = element.attributeValue("ref");
//
//                // 找到当前需要被处理依赖关系的bean
//                Element parent = element.getParent();
//
//                // 调用父元素对象的反射功能
//                String parentId = parent.attributeValue("id");
//                Object parentObject = map.get(parentId);
//                // 遍历父对象中的所有方法，找到"set" + name
//                Method[] methods = parentObject.getClass().getMethods();
//                for (int j = 0; j < methods.length; j++) {
//                    Method method = methods[j];
//                    if(method.getName().equalsIgnoreCase("set" + name)) {  // 该方法就是 setAccountDao(AccountDao accountDao)
//                        method.invoke(parentObject,map.get(ref));
//                    }
//                }
//
//                // 把处理之后的parentObject重新放到map中
//                map.put(parentId,parentObject);
//
//            }
//
//
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
        //所有需要生成bean的类

//        String[] clazzName=new String[]{"com.lagou.edu.dao.impl.JdbcAccountDaoImpl","com.lagou.edu.service.impl.TransferServiceImpl","com.lagou.edu.utils.ConnectionUtils",
//        "com.lagou.edu.utils.TransactionManager","com.lagou.edu.factory.ProxyFactory"};
//        for(String n:clazzName){
//            try {
//                Class aClass = Class.forName(n);
//                map.put(aClass.getSimpleName(), aClass.newInstance());
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            }
//        }
        /*
        @Service 注解
        * */
        List<Class> clazzList=new ArrayList<Class>();
        Set<Class<?>> set=ClassUtil.getClasses("com.lagou.edu");
        List<String> ids=new ArrayList<>();
        for(Class clazz:set){
            Annotation[] annotationsclazz=clazz.getAnnotations();
            for(Annotation a:annotationsclazz){
                if(a.annotationType()==Service.class||a.annotationType()== Component.class){
                  clazzList.add(clazz);
                    try {
                        String id="";
                      if(a.annotationType()==Service.class){
                          id=((Service)clazz.getAnnotation(Service.class)).value();
                      }
                      if(a.annotationType()==Component.class){
                          id=((Component)clazz.getAnnotation(Component.class)).value();
                      }

                        map.put(id,clazz.newInstance());
                      ids.add(id);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /*
        @AutoWired 注解
        * */
        try {
            for(Map.Entry<String,Object> entry:map.entrySet()){
                String beanId=entry.getKey();
                Object o=entry.getValue();
            Method[] methods=o.getClass().getDeclaredMethods();
            for(Method method:methods){
               if( method.isAnnotationPresent(Autowired.class)){
                  for(String i:ids){
                      if(method.getName().equalsIgnoreCase("set"+i)){
                          method.invoke(o,map.get(i));
                          map.put(beanId,o);
                      }
                  }
               }

            }
        }
        /*
        @transactional 注解
        * */
         for(Map.Entry<String,Object> entry:map.entrySet()){
            String beanId=entry.getKey();
             Object o=entry.getValue();
              Method[] methods=o.getClass().getDeclaredMethods();
              for(Method method:methods){
                  if( method.isAnnotationPresent(Transactional.class)){
                      Object obj=((ProxyFactory)map.get("proxyFactory")).getCglibProxy(o);
                      map.put(beanId,obj);
                    }

              }
         }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }


    // 任务二：对外提供获取实例对象的接口（根据id获取）
    public static  Object getBean(String id) {

        return map.get(id);
    }

}
