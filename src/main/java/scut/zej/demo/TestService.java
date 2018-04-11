package scut.zej.demo;

import scut.zej.springframework.annotation.MyService;

@MyService
public class TestService {

	private String message="hello";
	public void print() {
		System.out.println(message);
	}
}
