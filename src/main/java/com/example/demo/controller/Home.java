package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home {

	@GetMapping("/page/home")
	public String home() {
		return "home";
	}

}
