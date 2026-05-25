package com.meli.inventory.repository;

import com.meli.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    long countByAvailable(Integer available);

    @Query("SELECT COALESCE(SUM(p.available), 0) FROM Product p")
    Integer sumAvailable();
}
