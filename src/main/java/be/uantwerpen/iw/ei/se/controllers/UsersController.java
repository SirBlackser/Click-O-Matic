package be.uantwerpen.iw.ei.se.controllers;

import be.uantwerpen.iw.ei.se.models.User;
import be.uantwerpen.iw.ei.se.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Quinten on 21/10/2015.
 */
@Controller
public class UsersController
{
    @Autowired
    private UserService userService;

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        return userService.getPrincipalUser();
    }

    @ModelAttribute("allUsers")
    public Iterable<User> populateUsers()
    {
        return userService.findAll();
    }

    @RequestMapping({"/users"})
    @PreAuthorize("hasRole('viewUsers') and hasRole('logon')")
    public String showViewUsers(ModelMap model) {   System.out.println("lala0"); return "mainPortal/users";    }


    @RequestMapping(value="/users?{userName}/", method=RequestMethod.GET)
    @PreAuthorize("hasRole('editUsers') and hasRole('logon')")      // rollen voor wie wat mag editen, bv enkel eigen profiel
    public String editUserForm(@PathVariable("userName") String userName, Model model)
    {
        System.out.println("lala: " + userName);
        try
        {
            User user = userService.loadSimpleUserByUsername(userName);
            model.addAttribute("user", user);
            System.out.println("lalala: " + userName);
            return "mainPortal/users/" + userName;
        }
        catch (UsernameNotFoundException e)
        {
            System.out.println("lelele: " + userName);
            model.addAttribute("user", null);
            return "mainPortal/users/";
        }
    }

}
