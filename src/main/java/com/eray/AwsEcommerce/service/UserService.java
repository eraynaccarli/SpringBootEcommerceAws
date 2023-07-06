package com.eray.AwsEcommerce.service;

import com.eray.AwsEcommerce.dto.CreateUserRequest;
import com.eray.AwsEcommerce.dto.UpdateUserRequest;
import com.eray.AwsEcommerce.dto.UserDto;
import com.eray.AwsEcommerce.dto.UserDtoConverter;
import com.eray.AwsEcommerce.exception.UserIsNotActiveException;
import com.eray.AwsEcommerce.exception.UserNotFoundException;
import com.eray.AwsEcommerce.model.User;
import com.eray.AwsEcommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    private final UserDtoConverter userDtoConverter;
    public UserService(UserRepository userRepository, UserDtoConverter userDtoConverter) {
        this.userRepository = userRepository;
        this.userDtoConverter = userDtoConverter;
    }

    public List<UserDto> getAllUsers() {
        return userDtoConverter.convert(userRepository.findAll());
    }

    public UserDto getUserByMail(String mail) {
        User user = findUserByMail(mail);
        return userDtoConverter.convert(user);
    }

    public UserDto createUser(CreateUserRequest createUserRequest) {
    User user = new User(
            createUserRequest.getMail(),
            createUserRequest.getFirstName(),
            createUserRequest.getLastName(),
            createUserRequest.getMiddleName(),
            false);

    return userDtoConverter.convert(userRepository.save(user));

    }


    public UserDto updateUser(String mail,UpdateUserRequest updateUserRequest) {
        User user = findUserByMail(mail);
        if(!user.getActive()){
            logger.warn(String.format("The user wanted update is not active ! User Mail: %s",mail));
            throw new UserIsNotActiveException("The user wanted update is not active ! ");
        }
        User updatedUser =  new User(user.getId(),
                user.getMail(),
                updateUserRequest.getFirstName(),
                updateUserRequest.getLastName(),
                updateUserRequest.getMiddleName(),
                user.getActive());
        return userDtoConverter.convert(userRepository.save(updatedUser));
    }

    public void deleteUser(Long id){
        findUserById(id);
        userRepository.deleteById(id);
    }
    public void deactivateUser(Long id) {
        User user = findUserById(id);
        User updatedUser =  new User(user.getId(),
                user.getMail(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                false);
        userRepository.save(updatedUser);

    }

    public void activateUser(Long id) {
        User user = findUserById(id);
        User updatedUser =  new User(user.getId(),
                user.getMail(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                true);
        userRepository.save(updatedUser);
    }

    private User findUserByMail(String mail){
        return userRepository.findByMail(mail)
                .orElseThrow(() -> new UserNotFoundException("User not found by mail: " + mail));
    }
    private User findUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + id));
    }


}
