package com.nextuple.walletbankingapp.controller;

import com.nextuple.walletbankingapp.dto.response.UserDTO;
import com.nextuple.walletbankingapp.jwt.JwtHelper;
import com.nextuple.walletbankingapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;

@CrossOrigin("*")
@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtHelper jwtHelper;
    @GetMapping("/get-user")
    public ResponseEntity<UserDTO> getUser(@RequestHeader("Authorization") String token){

        //System.out.println("request received inside get-user");
        UserDTO userDTO = null;
        token = token.substring(7);
        //System.out.println("token : "+ token);
        userDTO = userService.getUserDTOByEmail(jwtHelper.getUsernameFromToken(token));
        if (userDTO != null)
            return ResponseEntity.ok(userDTO);
        return ResponseEntity.notFound().build();
    }
}
