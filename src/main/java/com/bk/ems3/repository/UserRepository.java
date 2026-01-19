package com.bk.ems3.repository;

import com.bk.ems3.model.Role;
import com.bk.ems3.model.User;
import com.bk.ems3.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    List<User> findByStatus(UserStatus userStatus);

    List<User> findByRole(Role role);

    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
    int updateStatus(@Param("id") Long id,
                     @Param("status") UserStatus status);

    List<User> findByRoleIn(List<Role> roles);

    List<User> findByManager(User manager);

    List<User> findByRoleAndStatus(Role role, UserStatus userStatus);


}
