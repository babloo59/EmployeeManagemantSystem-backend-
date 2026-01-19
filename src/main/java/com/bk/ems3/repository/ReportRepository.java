package com.bk.ems3.repository;

import com.bk.ems3.model.Report;
import com.bk.ems3.model.ReportStatus;
import com.bk.ems3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByEmployee(User employee);

    List<Report> findByStatus(ReportStatus status);

    List<Report> findByEmployeeAndStatus(User manager, ReportStatus status);

    List<Report> findByEmployee_ManagerAndStatus(
            User manager,
            ReportStatus status
    );

}
