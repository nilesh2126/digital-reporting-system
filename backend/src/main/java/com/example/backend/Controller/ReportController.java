package com.example.backend.Controller;

import com.example.backend.model.Report;
import com.example.backend.Service.ReportService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return service.createReport(report);
    }

    @GetMapping
    public List<Report> getAllReports() {
        return service.getAllReports();
    }

    @GetMapping("/{id}")
    public Report getReportById(@PathVariable long id) {
        return service.getReportById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteReport(@PathVariable long id) {
        service.deleteReport(id);
        return "Report deleted";
    }

    @PatchMapping("/{id}")
    public Report updateReport(
            @PathVariable long id,
            @RequestBody Report report) {

        return service.updateReport(id, report);
    }
}