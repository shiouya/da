package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home {

	@GetMapping("/page/home")
	public String home() {
		return "home";
	}

	@GetMapping("/page/png")
	public String png() {
		return "png";
	}

	@GetMapping("/page/sudo")
	public String png1() {
		return "sudo";
	}

}
