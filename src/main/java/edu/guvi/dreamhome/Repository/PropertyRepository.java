package edu.guvi.dreamhome.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.guvi.dreamhome.Model.Property;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByOwnerId(Long ownerId);

    List<Property> findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(String title, String location);

    List<Property> findByPriceBetween(Double min, Double max);

    List<Property> findByApprove(boolean approve);

    List<Property> findByTypeAndPriceBetween(String type, Double min, Double max);

    List<Property> findByLocationContainingIgnoreCase(String location);

    List<Property> findByTypeContainingIgnoreCase(String type);

    List<Property> findByTitleContainingIgnoreCase(String keyword);

   
}
