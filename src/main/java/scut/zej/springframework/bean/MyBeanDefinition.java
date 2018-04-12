package scut.zej.springframework.bean;

import java.util.ArrayList;

public class MyBeanDefinition {

	private String beanName;
	private String classType;
	private ArrayList<String> fieldNames=new ArrayList<>();
	private ArrayList<String> fieldValue=new ArrayList<>();
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public String getClassType() {
		return classType;
	}
	public void setClassType(String classType) {
		this.classType = classType;
	}
	
	public ArrayList<String> getFieldNames() {
		return fieldNames;
	}
	public void setFieldNames(ArrayList<String> fieldNames) {
		this.fieldNames = fieldNames;
	}
	public ArrayList<String> getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(ArrayList<String> fieldValue) {
		this.fieldValue = fieldValue;
	}
	public void addField(String name,String value) {
		fieldNames.add(name);
		fieldValue.add(value);
	}
	
}
