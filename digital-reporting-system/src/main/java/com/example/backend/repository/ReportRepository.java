package com.example.backend.repository;

import com.example.backend.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByOrderByCreatedAtDesc();
    List<Report> findByReporterIdOrderByCreatedAtDesc(Long reporterId);
    List<Report> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<Report> findByReporterIdAndStatusIgnoreCaseOrderByCreatedAtDesc(Long reporterId, String status);
}
