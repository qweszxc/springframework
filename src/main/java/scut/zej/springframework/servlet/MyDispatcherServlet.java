package scut.zej.springframework.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scut.zej.springframework.annotation.MyAutowired;
import scut.zej.springframework.annotation.MyController;
import scut.zej.springframework.annotation.MyRequestMapping;
import scut.zej.springframework.annotation.MyService;

public class MyDispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Properties p=new Properties();
	private List<String> classNames=new ArrayList<>();
	private Map<String,Object>ioc=new HashMap<>();
	private Map<String,Method>handlerMapping =new HashMap<>();
	private Map<String,MethodExtend>extendMapping=new HashMap<>();
    
    public MyDispatcherServlet() {
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
		String url=request.getRequestURI();
		String contextPath=request.getContextPath();
		url=url.replace(contextPath, "").replaceAll("/+", "/");
		if(!handlerMapping.containsKey(url)) {
			response.getWriter().write("404 Not Found");
			return;
		}
		Method m=handlerMapping.get(url);
		MethodExtend methodExtend=extendMapping.get(url);
		
		System.out.println(m.getName());
		//obj调用方法的instance args方法实参
		//m.invoke(obj, args)
		Class<?> clazz;
		try {
			clazz = Class.forName(methodExtend.className);
			Object obj=clazz.newInstance();
			methodExtend.method.invoke(obj,request,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		//super.init(config);
		System.out.println("----------------------init--------------------------");
		//加载配置文件
		doLoad(config.getInitParameter("contextConfigLocation"));
		//根据配置文件扫描相关类
		doScanner(p.getProperty("scanPackage"));
		//初始化所有相关类实例，将其放入IOC容器中
		doIOC();
		//依赖注入
		doDI();
		//初始化HandlerMapping
		doHandlerMapping();
		//等待请求
	}
	
	private void doHandlerMapping() {
		if(ioc.isEmpty())
			return;
		for(Entry<String,Object>entry:ioc.entrySet()) {
			Class<?> clazz=entry.getValue().getClass();
			if(clazz.isAnnotationPresent(MyController.class)) {
				String baseUrl="";
				if(clazz.isAnnotationPresent(MyRequestMapping.class)) {
					MyRequestMapping requestMapping=clazz.getAnnotation(MyRequestMapping.class);
					baseUrl=requestMapping.value();
				}
				Method[] methods=clazz.getMethods();
				for(Method method:methods) {
					if(method.isAnnotationPresent(MyRequestMapping.class)) {
						MyRequestMapping requestMapping=method.getAnnotation(MyRequestMapping.class);
						String url=(baseUrl+requestMapping.value()).replaceAll("/+", "/");
						handlerMapping.put(url, method);
						MethodExtend methodExtend=new MethodExtend();
						methodExtend.method=method;
						methodExtend.className=clazz.getName();
						extendMapping.put(url, methodExtend);
					}else
						continue;
				}
			}else
				continue;
		}
	}

	private void doDI() {
		if(ioc.isEmpty())
			return;
		for(Entry<String,Object>entry:ioc.entrySet()) {
			//获取所有字段field
			Field fields[]=entry.getValue().getClass().getDeclaredFields();
			for(Field f:fields) {
				if(f.isAnnotationPresent(MyAutowired.class)) {
					MyAutowired auto=f.getAnnotation(MyAutowired.class);
					String beanName=auto.value().trim();
					if("".equals(beanName)) {
						beanName=f.getName();
					}
					f.setAccessible(true);
					try {
						Object o=ioc.get(beanName);
						f.set(entry.getValue(),ioc.get(beanName));
						
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}else
					continue;
			}
		}
	}

	private void doIOC() {
		//初始化
		if(classNames.isEmpty())
			return;
		//通过反射初始化
			try {
				for(String className:classNames) {
					Class<?> clazz=Class.forName(className);
					//进入Bean实例化阶段，初始化IOC容器
					//识别注解
					if(clazz.isAnnotationPresent(MyController.class)) {
						String beanName=lowFirst(clazz.getSimpleName());
						ioc.put(beanName, clazz.newInstance());//实例化bean并放入ioc容器
					}else if(clazz.isAnnotationPresent(MyService.class)) {
						//用户自定义名字
						MyService service=clazz.getAnnotation(MyService.class);
						String beanName=service.value();
						if(beanName.trim().equals("")) {
							beanName=lowFirst(clazz.getSimpleName());
						}
						Object instance=clazz.newInstance();
						ioc.put(beanName, instance);
						
						//如果是接口
						Class<?>[]interfaces=clazz.getInterfaces();
						for(Class<?> i:interfaces) {
							//将接口类型作为key
							ioc.put(i.getName(), instance);
						}
					}else 
						continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}
	private String lowFirst(String str) {
		char[] chars=str.toCharArray();
		chars[0]+=32;
		return String.valueOf(chars);
	}

	private void doLoad(String location) {
		//解析properties
		String u=this.getClass().getClassLoader().getResource(location).getFile();
		//InputStream ins=this.getClass().getClassLoader().getSystemResourceAsStream(location);
		InputStream ins=null;
		try {
			ins = new FileInputStream(new File(u));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			p.load(ins);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(null!=ins)
				try {
					ins.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}

	private void doScanner(String packageName) {
		//进行扫描
		URL url=this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.", "/"));
		System.out.println(url);
		File classDir=new File(url.getFile());
		for(File f:classDir.listFiles()) {
			String className=(packageName+"."+f.getName().replaceAll(".class", ""));
			classNames.add(className);
		}
	}
	class MethodExtend{
		Method method;
		String className;
		String[]parameterName;
	}

	
}
