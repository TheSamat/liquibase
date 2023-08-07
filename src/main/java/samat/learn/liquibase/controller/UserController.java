package samat.learn.liquibase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import samat.learn.liquibase.model.user.UserResponse;
import samat.learn.liquibase.repository.UserRepository;
import samat.learn.liquibase.service.impl.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/user/all")
    public List<UserResponse> all() {
        return service.findAll();
    }
}