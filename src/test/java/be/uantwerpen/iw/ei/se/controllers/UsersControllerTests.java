package be.uantwerpen.iw.ei.se.controllers;

import be.uantwerpen.iw.ei.se.models.User;
import be.uantwerpen.iw.ei.se.repositories.UserRepository;
import be.uantwerpen.iw.ei.se.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by Quinten on 28/10/2015.
 */
public class UsersControllerTests
{
    @Mock
    private UserService userService;

    @InjectMocks
    private UsersController usersController;

    private MockMvc mockMvc;

    User principalUser;
    User testUser;
    Iterable<User> users;

    @Before
    public void setup()
    {
        List<User> userList = new ArrayList<User>();

        // Create principal user and test user
        principalUser = new User("Test", "User", "testusername", "test");
        testUser = new User("User", "Testing", "userName", "test");

        userList.add(principalUser);
        userList.add(testUser);

        users = (Iterable<User>) userList;

        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(usersController).build();
    }

    @Test
    public void viewUsersTest() throws Exception
    {
        when(userService.getPrincipalUser()).thenReturn(principalUser);

        mockMvc.perform(get("/users")).andExpect(view().name("mainPortal/users"));
    }

    @Test
    public void viewEditSpecificUser() throws Exception
    {
        when(userService.findByUserName("userName")).thenReturn(principalUser);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users/userName/")).andExpect(view().name("mainPortal/profile"));
    }
}
