package scut.zej.springframework.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import scut.zej.springframework.bean.MyBeanDefinition;
import scut.zej.springframework.beanfactory.MyXmlBeanFatory;

public class MyXmlReader {

	private MyXmlBeanFatory factory;//需要注册的工厂
	private MyDocumentReader docReader=new MyDocumentReader();//读取Document类解析成BeanDefinition的类
	private static DocumentBuilderFactory dbFactory=null;
	private static DocumentBuilder db =null;
	private Document document=null;
	static{  
        try {  
            dbFactory = DocumentBuilderFactory.newInstance();  
            db = dbFactory.newDocumentBuilder();  
        } catch (ParserConfigurationException e) {  
            e.printStackTrace();  
        }  
    }  
	
	public MyXmlReader(MyXmlBeanFatory f){
		this.factory=f;
	}
	public void load(String path) throws Exception {
		File f=new File(path);
		System.out.println(f.getAbsolutePath());
		InputStream in = new FileInputStream(f);
		document=db.parse(in);
		ArrayList<MyBeanDefinition> beanDefinitionList=docReader.prase(document);
		//System.out.println(beanDefinitionList);
		factory.register(beanDefinitionList);
	}
	public static void main(String[] args) throws Exception {
		MyXmlBeanFatory fac=new MyXmlBeanFatory();
		MyXmlReader reader=new MyXmlReader(fac);
		reader.load("src/main/resources/application.xml");
		System.out.println(fac);
		Object obj=fac.getBean("test");
		System.out.println(obj);
	}
}
