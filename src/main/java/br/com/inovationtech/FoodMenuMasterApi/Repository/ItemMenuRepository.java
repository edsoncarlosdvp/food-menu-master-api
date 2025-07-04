package br.com.inovationtech.FoodMenuMasterApi.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.inovationtech.FoodMenuMasterApi.Entity.CategoryItem;
import br.com.inovationtech.FoodMenuMasterApi.Entity.ItemMenuEntity;

@Repository
public interface ItemMenuRepository extends JpaRepository<ItemMenuEntity, Long>, JpaSpecificationExecutor<ItemMenuEntity> {

    // Search by name
    List<ItemMenuEntity> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    
    // Search by rating (avaliacao → rating)
    List<ItemMenuEntity> findByRatingGreaterThanEqualAndActiveTrue(BigDecimal minimumRating);
    
    // Unique name validation
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    boolean existsByNameIgnoreCase(String name);
    
    // Search by category
    List<ItemMenuEntity> findByCategoryAndActiveTrue(CategoryItem category);
    Page<ItemMenuEntity> findByCategoryAndActive(CategoryItem category, Boolean active, Pageable pageable);
    
    // Search by price
    List<ItemMenuEntity> findByPriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Search by active status
    Page<ItemMenuEntity> findByActiveTrue(Pageable pageable);
    Page<ItemMenuEntity> findByActive(Boolean active, Pageable pageable);
    
    // Search by QR Code
    Optional<ItemMenuEntity> findByQrCode(String qrCode);
    Optional<ItemMenuEntity> findByQrCodeAndActiveTrue(String qrCode);
    boolean existsByQrCode(String qrCode);
    
    // Statistics by category
    @Query("SELECT i.category, COUNT(i) FROM ItemMenuEntity i WHERE i.active = true GROUP BY i.category")
    List<Object[]> countItemsByCategory();
    
    // Advanced search with filters
    @Query("SELECT i FROM ItemMenuEntity i WHERE " +
           "(:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:category IS NULL OR i.category = :category) AND " +
           "(:minPrice IS NULL OR i.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR i.price <= :maxPrice) AND " +
           "(:active IS NULL OR i.active = :active)")
    Page<ItemMenuEntity> searchWithFilters(
        @Param("name") String name, 
        @Param("category") CategoryItem category, 
        @Param("minPrice") BigDecimal minPrice, 
        @Param("maxPrice") BigDecimal maxPrice, 
        @Param("active") Boolean active, 
        Pageable pageable
    );
}