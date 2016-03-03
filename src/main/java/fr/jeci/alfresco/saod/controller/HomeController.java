package fr.jeci.alfresco.saod.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@Value("${description}")
	private String title = "Jeci SAOD";

	@Value("${version}")
	private String version = "0.0.0";

	@RequestMapping("/")
	public String form(Map<String, Object> model) {
		model.put("time", new Date());
		model.put("title", this.title);
		model.put("version", this.version);
		return "home";
	}

}
