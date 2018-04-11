package scut.zej.demo;

import scut.zej.springframework.annotation.MyAutowired;
import scut.zej.springframework.annotation.MyController;
import scut.zej.springframework.annotation.MyRequestMapping;

@MyController
public class TestController {

	@MyAutowired
	TestService testService;

	@MyRequestMapping("/test")
	public String test() {
		System.out.println("TestController");
		return "test";
	}

	
	
	
}
