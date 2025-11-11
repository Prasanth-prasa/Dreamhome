package edu.guvi.dreamhome.Security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import edu.guvi.dreamhome.Repository.PropertyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

@Controller
public class HomeController {

    @Autowired
    PropertyRepository propertyRepository;

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            // Already logged in → redirect to dashboard
            return "redirect:/dashboard";
        }
        // Not logged in → show login page
        return "redirect:/login";
    }

    // @GetMapping("/dashboard")
    // public String dashboard() {
    // return "dashboard";
    // }

    public String admin(Model model) {
        model.addAttribute("properties", propertyRepository.findAll());
        return "admin-dashboard";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

}
