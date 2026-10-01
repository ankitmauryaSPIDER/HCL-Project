package com.portfoliopro.service;
import com.portfoliopro.entity.User; import com.portfoliopro.repository.UserRepository; import org.springframework.stereotype.Service; import java.util.List;
@Service public class UserService { private final UserRepository users; public UserService(UserRepository users){this.users=users;} public List<User> all(){return users.findAll();} public User get(Long id){return users.findById(id).orElseThrow(()->new IllegalArgumentException("User not found"));} }
