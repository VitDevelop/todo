package com.vitdevelop.todo_app.core.service;

import com.vitdevelop.todo_app.core.domain.Todo;
import com.vitdevelop.todo_app.core.domain.User;
import com.vitdevelop.todo_app.core.domain.enums.ServiceErrorCode;
import com.vitdevelop.todo_app.core.exception.ServiceException;
import com.vitdevelop.todo_app.core.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Transactional
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final TodoService todoService;

    public UserService(UserRepository userRepository,
                       TodoService todoService) {
        this.userRepository = userRepository;
        this.todoService = todoService;
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));
    }

    public User createUser(User user) {
        if (userRepository.existsUserByUsername(user.getUsername())) {
            throw new ServiceException(ServiceErrorCode.USER_ALREADY_EXISTS);
        }

        user.setId(null);
        user.setCreatedOn(null);
        user.setUpdatedOn(null);

        return save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long userId, User user) {
        var existingUser = findUserById(userId);
        existingUser.setUsername(user.getUsername());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());

        return save(existingUser);
    }

    public void deleteUser(Long userId) {
        findUserById(userId);
        userRepository.deleteById(userId);
    }

    public List<Todo> findUserTodo(Long userId) {
        findUserById(userId);
        return todoService.findTodoByUserId(userId);
    }

    public Todo findUserTodoById(Long userId, Long todoId) {
        findUserById(userId);
        return todoService.findTodoByIdAndUserId(todoId, userId);
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));
    }

    public Todo createUserTodo(Long userId, Todo todo) {
        findUserById(userId);

        todo.setUserId(userId);
        return todoService.createTodo(todo);
    }

    public Todo updateUserTodo(Long userId, Long todoId, Todo todo) {
        findUserById(userId);

        return todoService.updateTodo(todoId, todo);
    }

    public void deleteUserTodoById(Long userId, Long todoId) {
        findUserById(userId);
        todoService.deleteTodoById(todoId);
    }
}
