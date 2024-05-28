package com.sds.foodfit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class MainController {
  	@GetMapping("/")
	public String getMain() {
		return "main/index";
		
	}
  	
  	@GetMapping("/demo")
	public String goDemo() {
		return "demo/input_form";
		
	}
  	
  	@GetMapping("/recotable")
  	public String getRecoTable() {
  		return "recotable/insert";
  	}
  	

}
