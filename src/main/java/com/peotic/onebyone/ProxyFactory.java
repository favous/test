package com.peotic.onebyone;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyFactory {
	
	ClassLoader classLoader;
	Class<?>[] interfaceClasses;
	private InvocationHandler handler;
	
	
	public ProxyFactory(ClassLoader classLoader, Class<?>[] interfaceClasses,
			InvocationHandler handler) {
		super();
		this.classLoader = classLoader;
		this.interfaceClasses = interfaceClasses;
		this.handler = handler;
	}


	public Object createProxyInstance() {
		return Proxy.newProxyInstance(classLoader, interfaceClasses, handler);
	}

}
