package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.AdminRegister;

public interface CrudRepository extends JpaRepository<AdminRegister,Integer>{

}
