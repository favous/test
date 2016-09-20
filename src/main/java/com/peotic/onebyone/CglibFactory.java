package com.peotic.onebyone;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;

public class CglibFactory {
	
	Class<?> clazz;
	Callback callback;
	Callback[] callbacks;
	CallbackFilter callbackFilter;
	
	
	public CglibFactory(Class<?> clazz, Callback[] callbacks) {
		super();
		this.clazz = clazz;
		this.callbacks = callbacks;
	}


	public CglibFactory(Class<?> clazz, Callback[] callbacks,
			CallbackFilter callbackFilter) {
		super();
		this.clazz = clazz;
		this.callbacks = callbacks;
		this.callbackFilter = callbackFilter;
	}

	public CglibFactory(Class<?> clazz, Callback callback) {
		super();
		this.clazz = clazz;
		this.callback = callback;
	}


	public Object createCglibInstance() {
		Enhancer en = new Enhancer();
		en.setSuperclass(clazz);
		en.setCallback(callback);
		return en.create();
	}

	
	public Object createCglibInstanceWithFilter() {
//		new CallbackFilter(){
//			
//			@Override
//			public int accept(Method arg0) {
//				// TODO Auto-generated method stub
//				return 0;
//			}
//			
//		};
//		Callback callback = NoOp.INSTANCE;
//		
		Enhancer en = new Enhancer();
		en.setSuperclass(clazz);
		en.setCallbacks(callbacks);
		en.setCallbackFilter(callbackFilter);
		return en.create();
	}

}
