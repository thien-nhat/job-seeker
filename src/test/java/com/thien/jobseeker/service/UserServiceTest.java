package com.thien.jobseeker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.thien.jobseeker.domain.User;
import com.thien.jobseeker.domain.response.ResultPaginationDTO;
import com.thien.jobseeker.repository.UserRepository;
import com.thien.jobseeker.util.constant.GenderEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setAge(25);
        testUser.setGender(GenderEnum.MALE);
        testUser.setAddress("123 Test St");
    }

    @Test
    void testHandleCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User created = userService.handleCreateUser(testUser);

        assertNotNull(created);
        assertEquals("test@example.com", created.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testHandleDeleteUser() {
        doNothing().when(userRepository).deleteById(testUser.getId());

        userService.handleDeleteUser(testUser.getId());

        verify(userRepository).deleteById(testUser.getId());
    }

    @Test
    void testFetchUserById() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        User fetched = userService.fetchUserById(testUser.getId());

        assertNotNull(fetched);
        assertEquals("test@example.com", fetched.getEmail());
        verify(userRepository).findById(testUser.getId());
    }

    @Test
    void testFetchAllUser() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<User> page = new PageImpl<>(Arrays.asList(testUser));
        when(userRepository.findAll((Specification<User>) null, pageable)).thenReturn(page);

        ResultPaginationDTO result = userService.fetchAllUser(null, pageable);

        assertNotNull(result);
        assertEquals(page.getTotalElements(), ((List<User>) result.getResult()).size());
        verify(userRepository).findAll((Specification<User>) null, pageable);
    }

    @Test
    void testHandleUpdateUser() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updated = userService.handleUpdateUser(testUser);

        assertNotNull(updated);
        assertEquals("test@example.com", updated.getEmail());
        verify(userRepository).findById(testUser.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testHandleGetUserByUsername() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);

        User fetched = userService.handleGetUserByUsername(testUser.getEmail());

        assertNotNull(fetched);
        assertEquals("test@example.com", fetched.getEmail());
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void testIsEmailExist() {
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        boolean exists = userService.isEmailExit(testUser.getEmail());

        assertTrue(exists);
        verify(userRepository).existsByEmail(testUser.getEmail());
    }

}
