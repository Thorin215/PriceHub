package com.pricehub;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(String userId);
    void deleteByUserId(String userId);
    Optional<Cart> findByUserIdAndGoodIdAndVersionId(String userId, Long goodId, Long versionId);
}
