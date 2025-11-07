package com.fitcaster.weatherfit.user.domain.repository;

import com.fitcaster.weatherfit.user.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
