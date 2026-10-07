package com.example.backend.controller;

import com.example.backend.model.Report;
import com.example.backend.service.ReportService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMappping("/reports")

public class ReportController {
    private final com.example.backend.Service.ReportService service;
    public ReportController(ReportService service){
        this.service = service;
    }
    @PostMapping
    public report createReport(@RequesstBody Report report){
        return service.createReport(report);
    }
    @PGetMapping
    public List<Report> getAllReports()[
        return service.getAllReports();
    ]
    @GetMapping("/{id}")
    public Reprot getReportById(@Pathvariable long id){
        return service.getReportById(id);
    }
    @DeleteMapping("/{id}")
public String deleteReport(@PathVariable long id){
    service.deleteReport(id);
    return "Report deleted";
}
@PatchMapping("/{id}")
public Report updateReport(
    @PathVariable long id,@RequestBody Report report){
        return service.updateReport(id, report);
}
    
}
