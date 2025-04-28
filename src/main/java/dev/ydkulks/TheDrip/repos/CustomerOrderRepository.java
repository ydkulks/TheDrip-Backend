package dev.ydkulks.TheDrip.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.CustomerOrder;
import dev.ydkulks.TheDrip.models.CustomerOrderId;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, CustomerOrderId> {
  Page<CustomerOrder> findByUserId(Integer userId, Pageable pageable);
}
