package edu.guvi.dreamhome.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import edu.guvi.dreamhome.Model.Property;
import edu.guvi.dreamhome.Model.User;
import edu.guvi.dreamhome.Repository.PropertyRepository;
import edu.guvi.dreamhome.Security.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private MultipartFile imageFile;

    @InjectMocks
    private PropertyService propertyService;

    private Property property;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        property = new Property();
        property.setId(1L);
        property.setTitle("Luxury Villa");
        property.setApprove(false);
        property.setOwner(user);
    }

    @Test
    void testGetAllProperties() {
        when(propertyRepository.findAll()).thenReturn(Arrays.asList(property));

        List<Property> properties = propertyService.getAllProperties();

        assertEquals(1, properties.size());
        verify(propertyRepository, times(1)).findAll();
    }

    @Test
    void testSaveProperty_WithImageUpload() throws Exception {
        // Mock Cloudinary uploader
        Uploader uploaderMock = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploaderMock);

        // Mock upload response
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "https://mock-cloudinary.com/image.jpg");

        when(uploaderMock.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);
        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        when(imageFile.isEmpty()).thenReturn(false);
        when(imageFile.getBytes()).thenReturn(new byte[]{1, 2, 3});

        // ✅ FIXED LINE — removed the stray hyphen
        Property saved = propertyService.saveProperty(property, imageFile, user);

        assertEquals("https://mock-cloudinary.com/image.jpg", saved.getImageUrl());
        assertFalse(saved.isApprove());
        assertEquals(user, saved.getOwner());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testApproveProperty_ShouldSetApproveTrue() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        propertyService.approveProperty(1L);

        assertTrue(property.isApprove());
        verify(propertyRepository, times(1)).save(property);
    }

    @Test
    void testRejectProperty_ShouldSetApproveFalse() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        propertyService.rejectProperty(1L);

        assertFalse(property.isApprove());
        verify(propertyRepository, times(1)).save(property);
    }

    @Test
    void testDeletePropertyById() {
        doNothing().when(propertyRepository).deleteById(1L);

        propertyService.deletePropertyById(1L);

        verify(propertyRepository, times(1)).deleteById(1L);
    }
}
