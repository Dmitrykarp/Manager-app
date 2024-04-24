package com.dmitrykarp.managerapp.repository;

import com.dmitrykarp.managerapp.entity.ManagerUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ManagerUserRepository extends CrudRepository<ManagerUser, Integer> {

    Optional<ManagerUser> findByUsername(String username);
}
