package com.tcs;

import javax.validation.Valid;

import org.apache.hadoop.mapreduce.Mapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.webflow.execution.RequestContext;

@Controller
public class MainController {	
	
	
	    @RequestMapping(value="displayForm", method=RequestMethod.GET)
	    public String helloWorld(UserDetails ud) {
	    	System.out.println(" In helloWorld");
	    	RequestContext r;
	    	return "loginPage";	    	
	    }
	    
	    @RequestMapping("/login")
	    public String loginCheck(@Valid UserDetails userDetails, BindingResult result, ModelMap model) {
	    	if (result.hasErrors()) {
				return "loginPage";
			} else {
				model.addAttribute("lfobj", userDetails);
				return "success";
			}
	    }
	    	    
}
