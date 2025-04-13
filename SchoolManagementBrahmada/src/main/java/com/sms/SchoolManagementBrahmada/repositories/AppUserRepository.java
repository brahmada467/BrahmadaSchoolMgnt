package com.sms.SchoolManagementBrahmada.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sms.SchoolManagementBrahmada.models.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Integer>{

public AppUser findByEmail(String email);
}
