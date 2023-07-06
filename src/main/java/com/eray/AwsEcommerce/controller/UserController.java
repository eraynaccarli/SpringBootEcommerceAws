package com.eray.AwsEcommerce.controller;

import com.eray.AwsEcommerce.dto.CreateUserRequest;
import com.eray.AwsEcommerce.dto.UpdateUserRequest;
import com.eray.AwsEcommerce.dto.UserDto;
import com.eray.AwsEcommerce.model.User;
import com.eray.AwsEcommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{mail}")
    public ResponseEntity<UserDto> getUserByMail(@PathVariable("mail") String mail){
        return ResponseEntity.ok(userService.getUserByMail(mail));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest createUserRequest){
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }


    @PutMapping("/{mail}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("mail") String mail,@RequestBody UpdateUserRequest updateUserRequest){
        return ResponseEntity.ok(userService.updateUser(mail,updateUserRequest));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> activeUser(@PathVariable("id") Long id){
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable("id") Long id){
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }


}
