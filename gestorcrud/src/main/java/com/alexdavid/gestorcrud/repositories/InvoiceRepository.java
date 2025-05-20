package com.alexdavid.gestorcrud.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexdavid.gestorcrud.models.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    List<Invoice> findByCustomerIdOrderByIdDesc(Integer customerId);

    List<Invoice> findAllByOrderByIdDesc();

    List<Invoice> findByStatusOrderByIdDesc(String status);
}
