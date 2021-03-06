package com.example.springdemo.services;

import com.example.springdemo.dto.UserDTO;
import com.example.springdemo.dto.UserDTO;
import com.example.springdemo.dto.builders.UserBuilder;
import com.example.springdemo.entities.User;
import com.example.springdemo.errorhandler.ResourceNotFoundException;
import com.example.springdemo.repositories.UserRepository;
import com.example.springdemo.validators.UserFieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO findUserById(String id) {
        Optional<User> user = userRepository.findById(id);

        if (!user.isPresent()) {
            throw new ResourceNotFoundException("User", "user id", id);
        }
        return UserBuilder.generateDTOFromEntity(user.get());
    }

    public List<UserDTO> findAll() {
        List<User> users = userRepository.getAllOrdered();

        return users.stream()
                .map(UserBuilder::generateDTOFromEntity)
                .collect(Collectors.toList());
    }

    public String insert(UserDTO userDTO) {

        UserFieldValidator.validateInsertOrUpdate(userDTO);
        userDTO.setPassword(getPassword(userDTO.getPassword()));
        return userRepository
                .save(UserBuilder.generateEntityFromDTO(userDTO))
                .getUsername();
    }

    public String update(UserDTO userDTO) {

        Optional<User> user = userRepository.findById(userDTO.getUsername());

        if (!user.isPresent()) {
            throw new ResourceNotFoundException("User", "user id", userDTO.getUsername());
        }
        UserFieldValidator.validateInsertOrUpdate(userDTO);

        return userRepository.save(UserBuilder.generateEntityFromDTO(userDTO)).getUsername();
    }

    public void delete(UserDTO userDTO) {
        this.userRepository.deleteById(userDTO.getUsername());
    }
    private String getPassword(String password) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}
