package edu.guvi.dreamhome.Controller;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import edu.guvi.dreamhome.Model.Property;
import edu.guvi.dreamhome.Model.User;
import edu.guvi.dreamhome.Repository.UserRepository;
import edu.guvi.dreamhome.Security.PropertyService;

@Controller
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserRepository userRepository;

    // ✅ Admin dashboard
    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "admin-dashboard";
    }

    // ✅ Add property form for customers
    @GetMapping("/property/add")
    public String addPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "property-add";
    }

    // ✅ Save property by customer
    @PostMapping("/property/save")
    public String saveProperty(Property property,
                               @RequestParam("imageFile") MultipartFile imageFile,
                               Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        propertyService.saveProperty(property, imageFile, user);
        return "redirect:/dashboard";
    }

    // ✅ Save property by admin
     @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/properties/add")
    public String saveProperties(@ModelAttribute Property property,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            propertyService.saveProperty(property, imageFile, user);
            return "redirect:/dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/properties/add")
    public String showAddPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "property-add";
    }

    // ✅ Approve / Reject (Admin only)
    @GetMapping("/properties/approve/{id}")
    public String approveProperty(@PathVariable Long id) {
        propertyService.approveProperty(id);
        return "redirect:/dashboard";
    }

     @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/properties/reject/{id}")
    public String rejectProperty(@PathVariable Long id) {
        propertyService.rejectProperty(id);
        return "redirect:/dashboard";
    }

    // ✅ Edit Property (Admin or Owner)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/properties/edit/{id}")
    public String editProperty(@PathVariable Long id, Model model, Authentication authentication) {
        Property property = propertyService.getPropertyById(id);

        model.addAttribute("property", property);
        return "edit-property";
    }

    // ✅ Update Property (Admin or Owner)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/properties/update")
    public String updateProperty(@ModelAttribute Property property,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                 Authentication authentication) {

        propertyService.updateProperty(property, imageFile);
        return "redirect:/dashboard";
    }

    // ✅ Delete Property (Admin or Owner)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/properties/delete/{id}")
    public String deleteProperty(@PathVariable Long id, Authentication authentication) {
        propertyService.deletePropertyById(id);
        return "redirect:/dashboard";
    }

    // ✅ View Property (everyone logged in)
    @GetMapping("/properties/view/{id}")
    public String viewPropertyDetails(@PathVariable Long id, Model model) {
        Property property = propertyService.getPropertyById(id);
        model.addAttribute("property", property);
        return "property-detail";
    }
}