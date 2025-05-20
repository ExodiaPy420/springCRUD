package com.alexdavid.gestorcrud.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexdavid.gestorcrud.models.Salesrep;

public interface SalesrepRepository extends JpaRepository<Salesrep, Integer>{

    public Salesrep findByEmail(String email);

}
