package scut.zej.springframework.reader;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scut.zej.springframework.bean.MyBeanDefinition;

public class MyDocumentReader {

	public ArrayList<MyBeanDefinition> prase(Document document) {
		ArrayList<MyBeanDefinition> list=new ArrayList<>();
		NodeList nodeList=document.getElementsByTagName("bean");
		for(int i=0;i<nodeList.getLength();i++) {
			MyBeanDefinition beanDefinition=new MyBeanDefinition();
			Node node=nodeList.item(i);
			NamedNodeMap namedNodeMap = node.getAttributes(); 
			String id = namedNodeMap.getNamedItem("id").getTextContent();//System.out.println(id); 
			String classType=namedNodeMap.getNamedItem("class").getTextContent();
			beanDefinition.setBeanName(id);
			beanDefinition.setClassType(classType);
			System.out.println("class= "+classType);
			NodeList cList = node.getChildNodes();//System.out.println(cList.getLength());9
			for(int j=1;j<cList.getLength();j+=2){  
                
                Node cNode = cList.item(j);  
                String name=cNode.getAttributes().getNamedItem("name").getTextContent();
                String value=cNode.getAttributes().getNamedItem("value").getTextContent();
                //System.out.println("name= "+name+" value= "+value);
                beanDefinition.addField(name, value);
            }  
			list.add(beanDefinition);
		}
		return list;
	}

}
