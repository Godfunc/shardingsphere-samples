package com.godfunc.shardingsphere.controller;

import com.godfunc.shardingsphere.entity.UserEntity;
import com.godfunc.shardingsphere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("save")
    public UserEntity save(@RequestBody UserEntity user) {
        userService.save(user);
        return user;
    }
}
