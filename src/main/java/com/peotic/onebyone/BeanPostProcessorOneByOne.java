package com.peotic.onebyone;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.MethodProxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class BeanPostProcessorOneByOne implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods){
            if (method.isAnnotationPresent(OneByOne.class)){
                return createOneByOneProxyBean(bean);
            }
        }
        return bean;
    }

    private Object createOneByOneProxyBean(final Object bean) {
        Class<? extends Object> clazz = bean.getClass();
        Class<?>[] interfaceClasses = clazz.getInterfaces();
        ClassLoader classLoader = clazz.getClassLoader();
        Object proxy;
     
        if (interfaceClasses.length != 0){
            InvocationHandler invocationHandler = new InvocationHandler(){
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    
                    return method.invoke(bean, args);
                }
            };
            ProxyFactory factory = new ProxyFactory(classLoader, interfaceClasses, invocationHandler);
            proxy = factory.createProxyInstance();
            
        } else {
            Callback callback = new net.sf.cglib.proxy.MethodInterceptor(){
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    
                    return method.invoke(bean, args);
                }
            };
            CglibFactory factory = new CglibFactory(clazz, callback);
            proxy = factory.createCglibInstance();
        }
        
        return proxy;
    }

}
