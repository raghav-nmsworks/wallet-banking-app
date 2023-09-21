package com.nextuple.walletbankingapp.controller;

import com.nextuple.walletbankingapp.dto.request.LoginRequestDTO;
import com.nextuple.walletbankingapp.dto.response.LoginResponseDTO;
import com.nextuple.walletbankingapp.dto.response.RegistrationResponseDTO;
import com.nextuple.walletbankingapp.dto.response.UserDTO;
import com.nextuple.walletbankingapp.entity.User;
import com.nextuple.walletbankingapp.jwt.JwtHelper;
import com.nextuple.walletbankingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtHelper jwtHelper;


    /*@GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }*/

    @PostMapping("/signup")
    @CrossOrigin("http://localhost:3000")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        RegistrationResponseDTO registrationResponseDTO = userService.createUser(user);

        return ResponseEntity.ok(registrationResponseDTO);
    }



    /*@PutMapping("/updateUser/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        User updated = userService.updateUser(id, updatedUser);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }*/

    /*@DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }*/


    @PostMapping ("/login")
    @CrossOrigin("http://localhost:3000")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseDTO loginResponseDTO = userService.login(loginRequestDTO);
        if (loginResponseDTO != null){
            return ResponseEntity.ok(loginResponseDTO);
        }
        return ResponseEntity.notFound().build();
    }
}

