package com.patriclee.controller;

import com.patriclee.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    // 模拟的用户列表
    private final List<User> userList = List.of(
            new User("1", "Patrick Lee", "123456", "https://cdn.pixabay.com/photo/2021/11/12/03/04/woman-6787784_1280.png"),
            new User("2", "Alice Johnson", "123456", "https://avatars.githubusercontent.com/u/2198388?v=4"),
            new User("3", "John Doe", "123456", "https://avatars.githubusercontent.com/u/1234567?v=4")
    );

    @GetMapping("/{userId}")
    public User findById( @PathVariable String userId) {
        return userList.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
    @GetMapping("/findAll")
    public List<User> findAll() {
        return userList;
    }

}
