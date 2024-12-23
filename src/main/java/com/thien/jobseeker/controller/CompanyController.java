package com.thien.jobseeker.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thien.jobseeker.domain.Company;
import com.thien.jobseeker.domain.response.ResultPaginationDTO;
import com.thien.jobseeker.service.CompanyService;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final CompanyService companyService;

    
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<?> createCompany(@Valid @RequestBody Company reqCompany){
        logger.info("Executing createCompany method");

        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleCreateCompany(reqCompany));
    }

    @GetMapping("/companies")
     public ResponseEntity<ResultPaginationDTO> getAllCompany(
            Specification<Company> spec,
            Pageable pageable
            ){
        logger.info("Executing getAllCompany method");

        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.handleFetchAllCompany(spec, pageable));
    }

    @PutMapping("/companies")
    public ResponseEntity<?> updateCompany(@Valid @RequestBody Company reqCompany){
        logger.info("Executing updateCompany method");

        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.companyService.handleUpdateCompany(reqCompany));
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cập nhật không thành công");
        }
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<?> deleteCompany(@Min(value = 0) @PathVariable long id){
        logger.info("Executing deleteCompany method");
        
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.status(HttpStatus.OK).body("Xóa thành công");
    }
}