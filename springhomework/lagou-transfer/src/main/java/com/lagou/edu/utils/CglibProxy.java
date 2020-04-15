package com.lagou.edu.utils;

import com.lagou.edu.anno.Autowired;
import com.lagou.edu.dao.AccountDao;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy  implements MethodInterceptor {

    private AccountDao accountDao;

    // 构造函数传值/set方法传值

    @Autowired
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return null;
    }
}
