package com.agenda.topicus.controller;

import java.util.*;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agenda.topicus.repository.UserRepository;
import com.agenda.topicus.exception.ResourceNotFoundException;
import com.agenda.topicus.model.User;

@RestController
@RequestMapping("/agenda")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	// get all Users
	@GetMapping("users")
	public List<User> getAllUsers(){
		return this.userRepository.findAll();
	} 
	
	// get User by ID
	@GetMapping("users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userid)
			throws ResourceNotFoundException {
		User user = userRepository.findById(userid)
				.orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + userid));
		return ResponseEntity.ok().body(user);
	}
	// save user
	@PostMapping("users")
	public User createUser(@RequestBody User user) {
	    List<User> allusers = userRepository.findAll();
	    
	    if (user.getEmail() == null) throw new NullPointerException("Email cannot be empty");
	    
	    if (user.getName() == null) throw new NullPointerException("Name cannot be empty");
	    
	    allusers.forEach(u -> {
	    	if (u.getEmail() == user.getEmail()) throw new DuplicateKeyException("User already exists with the email:" + u.getEmail());
	    });
		return userRepository.save(user);
	}
	
	// update user
	@PutMapping("/users/{id}")
	public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userid,
			 @RequestBody User userdetails) throws ResourceNotFoundException {
		User user = userRepository.findById(userid)
				.orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + userid));
		
	    List<User> allUsers = userRepository.findAll();
	    
	    if (userdetails.getEmail() == null) {}
	    else{
		    allUsers.forEach(u -> {
		    	if (u.getEmail() == user.getEmail()) throw new DuplicateKeyException("User already exists with the email: " + user.getEmail());
		    });
	    	user.setEmail(userdetails.getEmail());}
	    
	    if (userdetails.getName() == null) {} 
	    else {user.setName(userdetails.getName());}

		
		User updatedUser = userRepository.save(user);
		return ResponseEntity.ok(updatedUser);
	}

	// delete user
	
	@DeleteMapping("/users/{id}")
	public Map<String, Boolean> deleteEmployee(@PathVariable(value = "id") Long userid)
			throws ResourceNotFoundException {
		User user = userRepository.findById(userid)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + userid));

		userRepository.delete(user);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}

}
