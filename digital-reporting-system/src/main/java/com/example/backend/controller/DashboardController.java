package com.example.backend.controller;

import com.example.backend.model.Report;
import com.example.backend.model.User;
import com.example.backend.repository.ReportRepository;
import com.example.backend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final ReportRepository reports;
    private final UserRepository users;
    public DashboardController(ReportRepository reports, UserRepository users) { this.reports=reports; this.users=users; }

    @GetMapping
    public ResponseEntity<?> dashboard(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (!(id instanceof Long userId)) return ResponseEntity.status(401).body(Map.of("message", "Please log in."));
        User user = users.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Please log in."));
        List<Report> all = ("POLICE".equals(user.getRole()) || "ADMIN".equals(user.getRole()))
                ? reports.findAllByOrderByCreatedAtDesc()
                : reports.findByReporterIdOrderByCreatedAtDesc(userId);
        long pending = all.stream().filter(r -> "PENDING".equals(r.getStatus())).count();
        long resolved = all.stream().filter(r -> "RESOLVED".equals(r.getStatus())).count();
        return ResponseEntity.ok(Map.of("total", all.size(), "pending", pending, "resolved", resolved,
                "recent", all.stream().limit(5).map(r -> Map.of("id", r.getId(), "eventType", r.getEventType(),
                        "status", r.getStatus(), "createdAt", r.getCreatedAt())).toList()));
    }
}
