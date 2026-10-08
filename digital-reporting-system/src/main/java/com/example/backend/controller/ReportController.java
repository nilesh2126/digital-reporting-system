package com.example.backend.controller;

import com.example.backend.dto.ReportRequest;
import com.example.backend.model.Report;
import com.example.backend.model.User;
import com.example.backend.repository.ReportRepository;
import com.example.backend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportRepository reports;
    private final UserRepository users;
    public ReportController(ReportRepository reports, UserRepository users) {
        this.reports = reports; this.users = users;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ReportRequest request, HttpSession session) {
        User reporter = currentUser(session);
        if (reporter == null) return unauthorized();
        Report r = new Report();
        r.setEventType(request.getEventType().trim());
        r.setDescription(request.getDescription().trim());
        r.setLoc(request.getLoc().trim());
        r.setDateTime(request.getDateTime());
        r.setReporter(reporter);
        r.setStatus("PENDING");
        return ResponseEntity.ok(toMap(reports.save(r)));
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required=false) String status, HttpSession session) {
        User user = currentUser(session);
        if (user == null) return unauthorized();
        boolean staff = isStaff(user);
        List<Report> result;
        if (staff) {
            result = (status == null || status.isBlank())
                    ? reports.findAllByOrderByCreatedAtDesc()
                    : reports.findByStatusIgnoreCaseOrderByCreatedAtDesc(status.trim());
        } else {
            result = (status == null || status.isBlank())
                    ? reports.findByReporterIdOrderByCreatedAtDesc(user.getId())
                    : reports.findByReporterIdAndStatusIgnoreCaseOrderByCreatedAtDesc(user.getId(), status.trim());
        }
        return ResponseEntity.ok(result.stream().map(this::toMap).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id, HttpSession session) {
        User user = currentUser(session);
        if (user == null) return unauthorized();
        Report r = reports.findById(id).orElse(null);
        if (r == null) return ResponseEntity.notFound().build();
        if (!isStaff(user) && !r.getReporter().getId().equals(user.getId())) return ResponseEntity.status(403).body(Map.of("message", "Forbidden."));
        return ResponseEntity.ok(toMap(r));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String,String> body, HttpSession session) {
        User user = currentUser(session);
        if (user == null) return unauthorized();
        if (!isStaff(user)) return ResponseEntity.status(403).body(Map.of("message", "Police/admin access required."));
        String status = body.getOrDefault("status", "").trim().toUpperCase(Locale.ROOT);
        if (!Set.of("PENDING", "UNDER_REVIEW", "INVESTIGATING", "RESOLVED", "CLOSED").contains(status)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid status."));
        }
        Report r = reports.findById(id).orElse(null);
        if (r == null) return ResponseEntity.notFound().build();
        r.setStatus(status);
        if (body.containsKey("firNumber") && user.getRole().equals("ADMIN")) {
            String fir = body.get("firNumber");
            r.setFirNumber(fir == null || fir.isBlank() ? null : fir.trim());
        }
        return ResponseEntity.ok(toMap(reports.save(r)));
    }

    private User currentUser(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (!(id instanceof Long userId)) return null;
        return users.findById(userId).orElse(null);
    }
    private boolean isStaff(User user) {
        return "POLICE".equals(user.getRole()) || "ADMIN".equals(user.getRole());
    }
    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(401).body(Map.of("message", "Please log in."));
    }
    private Map<String,Object> toMap(Report r) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("id", r.getId()); m.put("eventType", r.getEventType());
        m.put("description", r.getDescription()); m.put("loc", r.getLoc());
        m.put("dateTime", r.getDateTime()); m.put("status", r.getStatus());
        m.put("firNumber", r.getFirNumber()); m.put("reporterUsername", r.getReporter().getUsername());
        m.put("createdAt", r.getCreatedAt()); m.put("updatedAt", r.getUpdatedAt());
        return m;
    }
}
