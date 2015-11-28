package be.uantwerpen.iw.ei.se.controllers;

import be.uantwerpen.iw.ei.se.models.User;
import be.uantwerpen.iw.ei.se.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Thomas on 19/10/2015.
 */
@Controller
public class HomeController
{
    @Autowired
    private UserService userService;

    @ModelAttribute("currentUserFirstName")
    public String getCurrentUserFirstName()
    {
        return userService.getPrincipalUser().getFirstName();
    }

    @ModelAttribute("allUsers")
    public Iterable<User> populateUsers()
    {
        return userService.findAll();
    }

    @RequestMapping({"/", "/homepage"})
    @PreAuthorize("hasRole('logon')")
    public String showHomepage(ModelMap model)
    {
        return "mainPortal/homepage";
    }
}
