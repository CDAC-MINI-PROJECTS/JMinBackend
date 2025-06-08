package com.cdac.dreamblog.service;

import com.cdac.dreamblog.dto.request.UserLoginRequestDto;
import com.cdac.dreamblog.dto.request.UserRequestDto;
import com.cdac.dreamblog.dto.response.UserLoginResponseDto;
import com.cdac.dreamblog.dto.response.UserResponseDto;
import com.cdac.dreamblog.model.User; // Assuming User model is returned by getAllUsers

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// This interface defines the contract for user-related business operations.
// The implementation will provide the actual logic.
public interface IUserService {

    /**
     * Creates a new user in the system.
     *
     * @param userDto The data transfer object containing user details for creation.
     * @return UserResponseDto representing the newly created user.
     * @throws IllegalArgumentException if username or email already exists.
     */
    UserResponseDto create(UserRequestDto userDto);

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     *
     * @param userLoginDto The data transfer object containing user login credentials (username/email and password).
     * @return UserLoginResponseDto containing a success message and the JWT token.
     * @throws org.springframework.security.core.AuthenticationException if authentication fails (e.g., bad credentials).
     * @throws BadRequestException if username or password is incorrect. (Consider mapping AuthenticationException to appropriate HTTP status in controller).
     */
    UserLoginResponseDto login(UserLoginRequestDto userLoginDto);

    /**
     * Updates an existing user's information. Fields in userDto that are null will be ignored,
     * while explicitly provided values (including empty strings) will overwrite existing ones.
     *
     * @param userId The ID of the user to update.
     * @param userDto The data transfer object containing updated user details.
     * @return UserResponseDto representing the updated user.
     * @throws jakarta.persistence.EntityNotFoundException if the user with the given ID is not found.
     * @throws IllegalArgumentException if the updated username or email is already taken by another user.
     */
    UserResponseDto update(Long userId, UserRequestDto userDto);

    /**
     * Deletes a user from the system by their ID.
     *
     * @param id The ID of the user to delete.
     * @throws jakarta.persistence.EntityNotFoundException if the user with the given ID is not found.
     */
    void deleteUser(Long id);

    /**
     * Retrieves the details of the currently authenticated user.
     *
     * @return UserResponseDto representing the current user.
     * @throws ResourceNotFoundException if the authenticated username is not found in the database.
     */
    UserResponseDto getCurrentUser();

    /**
     * Retrieves a user's details by their username.
     *
     * @param username The username of the user to retrieve.
     * @return UserResponseDto representing the found user.
     * @throws ResourceNotFoundException if no user is found with the given username.
     */
    UserResponseDto getUserByUsername(String username);

    /**
     * Toggles the active status of a user (activates if inactive, deactivates if active).
     *
     * @param username The username of the user to activate/deactivate.
     * @return The new active status of the user (true if activated, false if deactivated).
     * @throws ResourceNotFoundException if no user is found with the given username.
     */
    boolean activateUser(String username);

    /**
     * Retrieves a paginated and sortable list of all users.
     * This method is typically secured to only allow specific roles (e.g., ADMIN).
     *
     * @param pageable Pagination and sorting information.
     * @return A Page of User objects.
     */
    Page<User> getAllUsers(Pageable pageable);

    // Note: The `toUserResponseDto` method is a private helper method within the implementation
    // and should not be part of the public interface.
}