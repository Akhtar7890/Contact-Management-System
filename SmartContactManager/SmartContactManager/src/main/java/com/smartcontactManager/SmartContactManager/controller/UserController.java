package com.smartcontactManager.SmartContactManager.controller;

import com.smartcontactManager.SmartContactManager.dao.ContactRepository;
import com.smartcontactManager.SmartContactManager.dao.UserRepository;
import com.smartcontactManager.SmartContactManager.entities.Contact;
import com.smartcontactManager.SmartContactManager.entities.User;
import com.smartcontactManager.SmartContactManager.helper.Message;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;


    //method for adding common data
    @ModelAttribute
    public void addCommonData(Model modal,Principal principal){
        String userName=principal.getName();
        System.out.println("USERNAME "+userName);

        //get the user using username
        User user=userRepository.getUserByUserName(userName);

        System.out.println("USER " +user);
        modal.addAttribute("user",user);

    }


    //dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal){
        String userName=principal.getName();
        System.out.println("USERNAME "+userName);

        User user=userRepository.getUserByUserName(userName);
        System.out.println("USER " +user);

        model.addAttribute("title","user_dashboard");
        model.addAttribute("user",user);
        return "normal/user_dashboard";
    }

    //open add handler

    @GetMapping("/add_contact")
    public String openAddContactForm(Model model){
        model.addAttribute("title","Add-Contact");
        model.addAttribute("contact",new Contact());
        return "normal/add_contact_form";
    }

    //saving the contact
    @PostMapping("/process_contact")
    public String processContact(@Valid @ModelAttribute Contact contact,
                                 BindingResult bindingResult, Model model, @RequestParam("image") MultipartFile file,
                                 Principal principal, HttpSession session){

        try{
            // Pahle jo bhi user login hai uska naam nikala using principal
            String userName=principal.getName();

            //then us user ko nikal liya database se
            User user=userRepository.getUserByUserName(userName);

            //image or file ko upload ya save krr rhe hai
            if(file.isEmpty()){
                //agar file empty hai
                System.out.println("File is empty");
                contact.setImage("Default_Contact_Profile.png");
            }else{
                //file to folder mein upload krr denge and save krr denge user ko
                //pahle contact k ander jo image field hai usme original naam save krr rhe hai
                contact.setImage(file.getOriginalFilename());

                //new path save kaha krr rhe using classPathResource
                File savedFile=new ClassPathResource("static/image").getFile();

                //finding path
                Path path=Paths.get(savedFile.getAbsolutePath()+File.separator+file.getOriginalFilename());

                //save krr rhe hai
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

                //message of success
                System.out.println("Image is uploaded");
            }

            //user ki id save krne k liye beacause bidirectional mapping hai
            contact.setUser(user);

            //user k list of contact naam ki field hai usme hi apna new contact add kra diya hai
            user.getContacts().add(contact);

            //phirse user ko save krr diya hai database mein means update krr diya hai
            userRepository.save(user);

            System.out.println("DATA "+contact);
            System.out.println("Contact added to database");

            // Create a new Message object
            Message message = new Message();
            message.setType("alert-success"); // or "alert-danger" for error
            message.setContent("Contact added successful !! Add more..."); // or an error message
            //Set the message in the session
            session.setAttribute("message", message);
            //Message message = (Message) session.getAttribute("message");

            // Add the message to the model
            model.addAttribute("message", message);
            // Remove the message from the session
            session.removeAttribute("message");
            //session.setAttribute("message",new Message("Contact added Successfully !! Add more..","alert-success"));

        }catch (Exception e){
            System.out.println("ERROR"+e.getMessage());
            session.setAttribute("message",new Message("Something went wrong !!","alert-danger"));
            System.out.println("Contact not added !!");
        }
        return "normal/add_contact_form";
    }


    //show contacts handler
    @GetMapping("/display_contact/{page}")
    public String displayContacts(@PathVariable("page") Integer page, Model model,Principal principal,HttpSession session){

        model.addAttribute("title","Show User Contacts");
//        //contact ki list bhejni hai
//        String userName=principal.getName();
//
//        User user=userRepository.getUserByUserName(userName);
//        List<Contact> con =user.getContacts();

        //user ko fetch kiya jo  login hai
       String userName=principal.getName();

       //phir uss user ki saari details fetch kiya database se
       User user=userRepository.getUserByUserName(userName);

       //now user ki id pass krke uske saare contacts ki list fetch krr liya using  contact repository
        //per page 5[n] contact chahiye
        //current page=0[page]
        Pageable pageable =PageRequest.of(page,3);
        Page<Contact> Allcontacts=contactRepository.findContactsByUserId(user.getId(),pageable);

        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message"); // Clear the message after displaying
        }
       model.addAttribute("contacts",Allcontacts);
       model.addAttribute("currentPage",page);
       model.addAttribute("totalPages",Allcontacts.getTotalPages());

        return "normal/display_contact";
    }


    //showing particular user detail
    @GetMapping("/contact/{c_id}")
    public String showParticularContactDetail(@PathVariable("c_id") Long c_id,Model model,Principal principal){
        System.out.println("CID "+c_id);

        Optional<Contact> contactOptional = contactRepository.findById(c_id);
        Contact contact = contactOptional.get();

        //applying validation on login user so that he can only see his contact not others
        String userName=principal.getName();
        User user=userRepository.getUserByUserName(userName);

        if(user.getId()==contact.getUser().getId()){
            model.addAttribute("contact",contact);
            model.addAttribute("title",contact.getName());
        }

        return "normal/contact_detail";
    }


    //delete contact handler
    @GetMapping("/delete/{c_id}")
    public String deleteContact(@PathVariable("c_id") Long c_id, Model model, Principal principal,HttpSession session){

        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

        Optional<Contact> contactOptional = contactRepository.findById(c_id);
        if(contactOptional.isPresent()){
            Contact contact=contactOptional.get();
            if(user.getId()==contact.getUser().getId()){

               user.getContacts().remove(contact);
               userRepository.save(user);

                session.setAttribute("message", new Message("Contact deleted successfully!", "alert-success"));
                System.out.println("Contact Deleted !!");
            } else {
                session.setAttribute("message", new Message("You do not have permission to delete this contact.","alert-danger"));
                System.out.println("Permission Denied !!");

            }
        } else {
            session.setAttribute("message", new Message("Contact not found.","alert-danger"));
            System.out.println("Something went wrong !!");

        }
        return "redirect:/user/display_contact/0";
    }

    //open a update contact form particular user
    @PostMapping("/update_contact/{c_id}")
    public String updateContact(@PathVariable("c_id") Long c_id, Model model){
        model.addAttribute("title","Update Contact");

        Contact contact = contactRepository.findById(c_id).get();
        model.addAttribute("contact",contact);
        return "normal/update_form";
    }

    //update process contact handler
    @PostMapping("/process_update")
    public String process_update(@Valid @ModelAttribute Contact contact,BindingResult bindingResult, Model model,
                                 Principal principal,@RequestParam("image") MultipartFile file,HttpSession session){

        try{
            Contact oldContactDetail = contactRepository.findById(contact.getC_id()).get();

            //file ko rewrite karna hai new wali upload and puraani wali delete
            if(!file.isEmpty()){
                //delete old photo
                File deleteFile=new ClassPathResource("static/image").getFile();
                File oldFile=new File(deleteFile,oldContactDetail.getImage());
                if (oldFile.exists()) {
                    oldFile.delete();
                }

                //update new photo
                File savedFile = new ClassPathResource("static/image").getFile();
                String newFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get(savedFile.getAbsolutePath() + File.separator + newFileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(newFileName);
            }else{
                contact.setImage(oldContactDetail.getImage());
            }

            String userName=principal.getName();
            User user=userRepository.getUserByUserName(userName);
            contact.setUser(user);
            contactRepository.save(contact);

//            session message
            session.setAttribute("message",new Message("Your contact is updated","alert-success"));
            System.out.println("Contact Updated !!");

        }catch (Exception e){
            System.out.println(e.getMessage());
            session.setAttribute("message", new Message("An unexpected error occurred: " + e.getMessage(), "alert-danger"));
            System.out.println("Contact Not updated something went wrong !!");

        }

        System.out.println("CONTACT NAME "+ contact.getName());
        System.out.println("CONTACT ID "+ contact.getC_id());

        return "redirect:/user/contact/"+contact.getC_id();
    }

    //Your profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model){
        model.addAttribute("title","Profile Page");
        return "/normal/profile";
    }
}


