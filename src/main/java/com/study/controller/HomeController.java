package com.study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
	
	@RequestMapping("")
	// @ResponseBody // (x) view name	
	public String home() {
		return "redirect:/board/list";
	}
	
}
