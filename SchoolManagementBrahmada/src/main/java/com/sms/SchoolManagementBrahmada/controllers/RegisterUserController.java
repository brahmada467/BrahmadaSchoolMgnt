package com.sms.SchoolManagementBrahmada.controllers;

import java.util.HashMap;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sms.SchoolManagementBrahmada.models.AppUser;
import com.sms.SchoolManagementBrahmada.models.LoginUser;
import com.sms.SchoolManagementBrahmada.models.RegisterUser;
import com.sms.SchoolManagementBrahmada.repositories.AppUserRepository;
import com.sms.SchoolManagementBrahmada.services.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/account")
public class RegisterUserController {

	
	@Autowired
	private AppUserRepository appUserRepository;
	
	@Autowired
	private JwtService jwtService;
	
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	
	@GetMapping("/profile")
	public ResponseEntity<Object> profile (Authentication auth){
		
    var response = new HashMap<String , Object>();
		
		response.put("Username" , auth.getName());
		response.put("Authorities", auth.getAuthorities());
		
		var appUser = appUserRepository.findByEmail(auth.getName());
		response.put("User", appUser);
		return ResponseEntity.ok(response);
		
		
	}

	@PostMapping("/register")
	public ResponseEntity<Object> register (
			
			@Valid @RequestBody RegisterUser registerUser ,BindingResult result) {
		
		if(result.hasErrors()) {
			
			var errorsList = result.getAllErrors();
			var errorsMap = new HashMap<String , String>();
			
			for(int i=0;i<errorsList.size();i++) {
				var error = (FieldError) errorsList.get(i);
				errorsMap.put(error.getField(), error.getDefaultMessage());
			}
			return ResponseEntity.badRequest().body(errorsMap);
		}
		
		AppUser newUser = new AppUser();
        newUser.setFirstName(registerUser.getFirstName());
        newUser.setLastName(registerUser.getLastName());
        newUser.setEmail(registerUser.getEmail());
        newUser.setPhone(registerUser.getPhone());
       
        String role = registerUser.getRole();
        if (role == null || (!role.equalsIgnoreCase("STUDENT") && !role.equalsIgnoreCase("TEACHER"))) {
            return ResponseEntity.badRequest().body("Invalid role. Allowed values: STUDENT, TEACHER");
        }
        newUser.setRole(role.toUpperCase());
        
        var bCryptEncoder = new BCryptPasswordEncoder();
        newUser.setPassword(bCryptEncoder.encode(registerUser.getPassword()));

		try {
			
			var otherUser = appUserRepository.findByEmail(registerUser.getEmail());
			if(otherUser != null) {
				return ResponseEntity.badRequest().body("Email Address already used");
			}
		
		appUserRepository.save(newUser);
		
		String jwtToken = jwtService.createJwtToken(newUser);
		
		var response = new HashMap<String , Object>();
		
		response.put("token", jwtToken);
		response.put("user", newUser);
		return ResponseEntity.ok(response);
		
	}
	
		catch(Exception ex) {
			System.out.println("There is an Exception");
			ex.printStackTrace();
		}
		
		
		return ResponseEntity.badRequest().body("Error");
	}
			
			
	@PostMapping("/login")
	public ResponseEntity<Object> login (
			
			@Valid @RequestBody LoginUser loginUser ,BindingResult result) {
		
       if(result.hasErrors()) {
			
			var errorsList = result.getAllErrors();
			var errorsMap = new HashMap<String , String>();
			
			for(int i=0;i<errorsList.size();i++) {
				var error = (FieldError) errorsList.get(i);
				errorsMap.put(error.getField(), error.getDefaultMessage());
			}
			return ResponseEntity.badRequest().body(errorsMap);
		}
		
	try {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getEmail(),
				loginUser.getPassword()));
	
	
	AppUser appUser = appUserRepository.findByEmail(loginUser.getEmail());
	String jwtToken = jwtService.createJwtToken(appUser);
	
	var response = new HashMap<String , Object>();
	
	response.put("token", jwtToken);
	response.put("user", appUser);
	return ResponseEntity.ok(response);
	
	
	} 
	catch(Exception ex) {
		System.out.println("There is an Exception");
		ex.printStackTrace();
	}
       
		return ResponseEntity.badRequest().body("Bad username or password");
		
	}
	

	@PutMapping("/edit")
	public ResponseEntity<?> editProfile(@RequestBody AppUser updatedUser, Authentication auth) {
	    String currentEmail = auth.getName(); 
	    AppUser existingUser = appUserRepository.findByEmail(currentEmail);

	    if (existingUser == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	    }

	   
	    existingUser.setFirstName(updatedUser.getFirstName());
	    existingUser.setLastName(updatedUser.getLastName());
	    existingUser.setPhone(updatedUser.getPhone());
	   

	    appUserRepository.save(existingUser);

	    return ResponseEntity.ok(existingUser);
	}


}
