package com.bk.ems3.repository;

import com.bk.ems3.model.Leave;
import com.bk.ems3.model.LeaveStatus;
import com.bk.ems3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByStatus(LeaveStatus status);

    List<Leave> findByEmployeeId(Long userId);

    List<Leave> findByEmployee(User employee);

    List<Leave> findByEmployeeAndStatus(User manager, LeaveStatus status);

    List<Leave> findByEmployee_ManagerAndStatus(User manager, LeaveStatus status);


}
