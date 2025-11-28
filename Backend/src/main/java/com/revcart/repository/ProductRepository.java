package com.revcart.repository;

import com.revcart.entity.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT p FROM Product p
        WHERE (:keyword IS NULL OR lower(p.name) LIKE lower(concat('%', :keyword, '%')))
          AND p.active = true
    """)
    Page<Product> searchActiveProducts(@Param("keyword") String keyword, Pageable pageable);

    List<Product> findTop12ByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);
}

