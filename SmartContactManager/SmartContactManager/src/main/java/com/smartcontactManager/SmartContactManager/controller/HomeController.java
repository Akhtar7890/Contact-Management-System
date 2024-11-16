package com.smartcontactManager.SmartContactManager.controller;

import com.smartcontactManager.SmartContactManager.entities.User;
import com.smartcontactManager.SmartContactManager.helper.Message;
import com.smartcontactManager.SmartContactManager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@SessionAttributes("user")
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    //handler for home page
    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title","Home - Smart Contact Manager");
        return "home";
   }

   //handler for about page
   @GetMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About - Smart Contact Manager");
        return "about";
   }

   //handler for signup page
   @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title","Register - Smart Contact Manager");
        model.addAttribute("user",new User());
        return "signup";
   }


   //This handler for register user
    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model, HttpSession session,
                               @RequestParam(value="agreement",defaultValue = "false") boolean agreement){

        try{

            //Checking if user has agreed the terms or condition or not
            if (!agreement) {
                model.addAttribute("user", user);
                System.out.println("You have not agreed to the terms and conditions");
                session.setAttribute("message", new Message("You have not agreed to the terms and conditions", "alert-danger"));
                return "signup";
            }

            //Server side validation is checking
            if (bindingResult.hasErrors()) {
                model.addAttribute("user", user);
                return "signup";
            }

            //Checking if email already exists or not
            if(userService.isEmailExists(user.getEmail())){
                session.setAttribute("message",new Message("Email already exits !","alert-danger"));
                System.out.println("Duplicate Email");
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("login-user-default-image.png");

            //Saving the user
            userService.saveUser(user);

            // Clear user object for the next registration
            model.addAttribute("user", new User());

            //Message for successful registration
            session.setAttribute("message", new Message("Successfully registered !!", "alert-success"));
            System.out.println("Successfully registered !!");
            return "signup";
        }

        catch(Exception e){
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
            System.out.println("Something went wrong !!");
            return "signup";
        }
    }

    //handler for custom login
      @GetMapping("/login")
      public String login(Model model) {
          return "login"; // Return the login template
      }


//      //this method checks the authentication of user
//      @PostMapping("/login")
//      public String loginSuccess(HttpServletRequest request, RedirectAttributes redirectAttributes) {
//          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//          if (authentication != null && authentication.isAuthenticated()) {
//              redirectAttributes.addFlashAttribute("message", "Welcome back!");
//              return "redirect:/normal/user_dashboard"; // redirect to your home page or dashboard
//          }
//          redirectAttributes.addFlashAttribute("error", "Invalid login attempt.");
//          return "redirect:/login"; // redirect back to login on failure
//      }
//    @PostMapping("/logout")
//    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
//        // Invalidate session and set logout message
//        request.getSession().invalidate();
//        redirectAttributes.addFlashAttribute("logout", "You have been logged out!");
//        return "redirect:/login"; // redirect back to login
//    }

}


