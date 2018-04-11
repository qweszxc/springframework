package scut.zej.demo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scut.zej.springframework.annotation.MyAutowired;
import scut.zej.springframework.annotation.MyController;
import scut.zej.springframework.annotation.MyRequestMapping;

@MyController
public class TestController {

	@MyAutowired
	TestService testService;

	@MyRequestMapping("/test")
	public void test(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("TestController");
		request.getRequestDispatcher("/WEB-INF/test.jsp").forward(request, response);
	}

	
	
	
}
