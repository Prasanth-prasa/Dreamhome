package edu.guvi.dreamhome.Security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import edu.guvi.dreamhome.Model.Property;
import edu.guvi.dreamhome.Model.User;
import edu.guvi.dreamhome.Repository.PropertyRepository;

@Service
public class PropertyService {
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private Cloudinary cloudinary;

  public Property saveProperty(Property property, MultipartFile imageFile, User user) {
    try {
        if (cloudinary != null && imageFile != null && !imageFile.isEmpty()) {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    imageFile.getBytes(),
                    ObjectUtils.asMap("folder", "dreamhome_properties")
            );

            Object urlObj = uploadResult.get("secure_url");
            if (urlObj == null) urlObj = uploadResult.get("url");   // fallback
            if (urlObj != null) {
                property.setImageUrl(urlObj.toString());
            }
        }
        property.setOwner(user);
        property.setApprove(false);
        property.setDateListed(LocalDateTime.now());
        return propertyRepository.save(property);
    } catch (IOException e) {
        throw new RuntimeException("Error saving property image", e);
    }
}



    public void approveProperty(Long id) {
        Optional<Property> optional = propertyRepository.findById(id);
        if (optional.isPresent()) {
            Property property = optional.get();
            property.setApprove(true);
            propertyRepository.save(property);
        }
    }

    public void rejectProperty(Long id) {
        Optional<Property> optional = propertyRepository.findById(id);
        if (optional.isPresent()) {
            Property property = optional.get();
            property.setApprove(false);
            propertyRepository.save(property);
        }
    }

    public void updateProperty(Property updatedProperty, MultipartFile imageFile) {
        Property existing = propertyRepository.findById(updatedProperty.getId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        existing.setTitle(updatedProperty.getTitle());
        existing.setDescription(updatedProperty.getDescription());
        existing.setPrice(updatedProperty.getPrice());
        existing.setType(updatedProperty.getType());
        existing.setLocation(updatedProperty.getLocation());

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
                existing.setImageUrl((String) uploadResult.get("url"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error uploading new image", e);
        }

        propertyRepository.save(existing);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public List<Property> getApprovedProperties() {
        return propertyRepository.findByApprove(true);
    }

    public List<Property> getPropertiesByOwner(Long ownerId) {
        return propertyRepository.findByOwnerId(ownerId);
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + id));
    }

    public void deletePropertyById(Long id) {
        propertyRepository.deleteById(id);
    }

    public List<Property> searchProperties(String keyword, String location, String type, Double minPrice, Double maxPrice) {
    if (keyword != null && !keyword.isEmpty()) {
        return propertyRepository.findByTitleContainingIgnoreCase(keyword);
    } else if (location != null && !location.isEmpty()) {
        return propertyRepository.findByLocationContainingIgnoreCase(location);
    } else if (type != null && !type.isEmpty()) {
        return propertyRepository.findByTypeContainingIgnoreCase(type);
    } else if (minPrice != null && maxPrice != null) {
        return propertyRepository.findByPriceBetween(minPrice, maxPrice);
    } else {
        return propertyRepository.findAll();
    }
}
public Property savePropertyApi(Property property, User user) {
    property.setOwner(user);
    return propertyRepository.save(property);
}

public Property updatePropertyApi(Long id, Property updatedProperty) {
    Property existing = propertyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Property not found"));
    existing.setTitle(updatedProperty.getTitle());
    existing.setDescription(updatedProperty.getDescription());
    existing.setPrice(updatedProperty.getPrice());
    existing.setLocation(updatedProperty.getLocation());
    existing.setType(updatedProperty.getType());
    existing.setApprove(updatedProperty.isApprove());
    return propertyRepository.save(existing);
}

}
