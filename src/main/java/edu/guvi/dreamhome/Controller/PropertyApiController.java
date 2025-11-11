package edu.guvi.dreamhome.Controller;

import edu.guvi.dreamhome.Model.Property;
import edu.guvi.dreamhome.Model.User;
import edu.guvi.dreamhome.Repository.UserRepository;
import edu.guvi.dreamhome.Security.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
    name = "Property APIs",
    description = "REST APIs for managing property listings (view, add, update, delete, approve, reject)"
)
@RestController
@RequestMapping("/api/properties")
public class PropertyApiController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserRepository userRepository;

    // ✅ Get all properties (for admins/customers)
    @Operation(summary = "Get all properties", description = "Fetch all property listings from the database")
    @GetMapping
    public List<Property> getAllProperties() {
        return propertyService.getAllProperties();
    }

    // ✅ Get a property by ID
    @Operation(summary = "Get property by ID", description = "Retrieve a single property by its ID")
    @GetMapping("/{id}")
    public Property getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id);
    }

    // ✅ Add new property (authenticated user)
    @Operation(summary = "Add property", description = "Add a new property listing for the logged-in user")
    @PostMapping
    public Property addProperty(@RequestBody Property property, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return propertyService.savePropertyApi(property, user);
    }

    // ✅ Update property (Admin only ideally)
    @Operation(summary = "Update property", description = "Update property details (Admin only)")
    @PutMapping("/{id}")
    public Property updateProperty(@PathVariable Long id, @RequestBody Property property) {
        return propertyService.updatePropertyApi(id, property);
    }

    // ✅ Delete property
    @Operation(summary = "Delete property", description = "Delete property by ID (Admin only)")
    @DeleteMapping("/{id}")
    public String deleteProperty(@PathVariable Long id) {
        propertyService.deletePropertyById(id);
        return "Property deleted successfully.";
    }

    // ✅ Approve a property (Admin only)
    @Operation(summary = "Approve property", description = "Approve a property listing (Admin only)")
    @PutMapping("/approve/{id}")
    public String approveProperty(@PathVariable Long id) {
        propertyService.approveProperty(id);
        return "Property approved successfully.";
    }

    // ✅ Reject a property (Admin only)
    @Operation(summary = "Reject property", description = "Reject a property listing (Admin only)")
    @PutMapping("/reject/{id}")
    public String rejectProperty(@PathVariable Long id) {
        propertyService.rejectProperty(id);
        return "Property rejected successfully.";
    }
}

