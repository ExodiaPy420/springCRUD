package com.alexdavid.gestorcrud.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexdavid.gestorcrud.models.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Customer findByEmail(String email);

    List<Customer> findBySalesrepIdOrderByIdDesc(Integer salesrepId);

    List<Customer> findAllByOrderByIdDesc();
}
