package edu.guvi.dreamhome.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;


import edu.guvi.dreamhome.Model.Property;
import edu.guvi.dreamhome.Model.User;
import edu.guvi.dreamhome.Repository.UserRepository;
import edu.guvi.dreamhome.Security.PropertyService;

@Controller
public class DashboardController {
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private UserRepository userRepository;

@GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return "redirect:/login";

        // ✅ ADMIN Dashboard
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            List<Property> allProps = propertyService.getAllProperties();
            model.addAttribute("user", user);
            model.addAttribute("properties", allProps);
            return "admin-dashboard";
        }

        // ✅ CUSTOMER Dashboard
        List<Property> myProps = propertyService.getPropertiesByOwner(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("properties", myProps);
        model.addAttribute("properties", propertyService.getApprovedProperties());
        return "dashboard";
    }

    @GetMapping("/admin/search")
public String searchProperties(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        Model model) {

    List<Property> results = propertyService.searchProperties(keyword, location, type, minPrice, maxPrice);
    model.addAttribute("properties", results);
    return "admin-dashboard";
}

}
