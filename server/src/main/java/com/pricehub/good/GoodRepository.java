package com.pricehub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface GoodRepository extends JpaRepository<Good, Long> {

    @Query("SELECT g FROM Good g WHERE g.name = :name AND g.description = :description AND g.platform = :platform")
    Good findByAllFields(@Param("name") String name, 
                         @Param("description") String  description, 
                         @Param("platform") String platform);
}
