package com.team02.spmpevaluator.controller;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.UserService;
import com.team02.spmpevaluator.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private UserService userService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserDetailsService userDetailsService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User loginUser) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
        } catch (AuthenticationException e) {
            throw new Exception("Invalid credentials");
        }
        final org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService.loadUserByUsername(loginUser.getUsername());
        return jwtUtil.generateToken(userDetails.getUsername());
    }
}
