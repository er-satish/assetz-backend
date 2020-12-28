package com.skumar.assetz.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skumar.assetz.dto.AuthenticationBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin("*")
public class AuthController {
	@GetMapping(path = "/basicauth")
	public AuthenticationBean authenticate() {
		log.info("login is successful");
		return new AuthenticationBean("You are authenticated");
	}

}
