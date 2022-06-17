package com.springboot.blog.controller;

import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.payload.JWTAuthResponse;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.SignUpDto;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;

@Api(value = "Auth controller exposes signin and signup REST APIs")
@RestController
@RequestMapping("/api/v1/auth")
    public class AuthorController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @ApiOperation(value = "REST API to Register or Signup user to Blog app")
    @PostMapping("/signin")
    public ResponseEntity<JWTAuthResponse> authenticateUser(@RequestBody LoginDto loginDto) {

        // get authen
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        // set authen
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);

        // return
        return ResponseEntity.ok(new JWTAuthResponse(token));
    }

    @ApiOperation(value = "REST API to Signin or Login user to Blog app")
    @PostMapping("/signup")
    public ResponseEntity<?> resgiterUser(@RequestBody SignUpDto signUpDto) {

        // check username exists in DB
        if (userRepository.existsByUsername(signUpDto.getUsername()))
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);

        // check email exists in DB
        if (userRepository.existsByEmail(signUpDto.getEmail()))
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);

        // set user
        User user = new User();
        user.setName(signUpDto.getName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(encodePassword(signUpDto.getPassword()));

        // set role
        Role roles = roleRepository.findByName("ROLE_ADMIN").get();
        user.setRoles(Collections.singleton(roles));

        // save
        userRepository.save(user);

        // return
        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }

    private String encodePassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

}
