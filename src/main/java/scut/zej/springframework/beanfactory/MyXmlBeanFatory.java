package scut.zej.springframework.beanfactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

import scut.zej.springframework.bean.*;

public class MyXmlBeanFatory implements MyBeanFactory{

	private Map<String,MyBeanDefinition> beanDefinitions=new HashMap<>();//bean信息
	private Map<String,Object>instances=new HashMap<>();//bean实例
	@Override
	public Object getBean(String name) {
		if(!beanDefinitions.containsKey(name))
			return null;
		else
			return createBean(name);
	}
	
	public void register(ArrayList<MyBeanDefinition> beanDefinitionList) {
		for(MyBeanDefinition mbd:beanDefinitionList) {
			beanDefinitions.put(mbd.getBeanName(), mbd);
		}
	}
	
	private Object createBean(String beanName) {
		Class<?> clazz = null;
		try {
			clazz = this.getClass().forName(beanDefinitions.get(beanName).getClassType());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object instance = null;
		try {
			instance = clazz.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Method[]methods=clazz.getMethods();
		return instance;
	}

}
