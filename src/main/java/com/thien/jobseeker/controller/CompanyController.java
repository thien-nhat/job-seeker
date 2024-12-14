package com.thien.jobseeker.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import org.hibernate.annotations.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thien.jobseeker.domain.Company;
import com.thien.jobseeker.domain.response.ResultPaginationDTO;
import com.thien.jobseeker.service.CompanyService;

@RestController
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping("/companies")
    public ResponseEntity<?> createCompany(@Valid @RequestBody Company reqCompany){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleCreateCompany(reqCompany));
    }

    @GetMapping("/companies")
     public ResponseEntity<ResultPaginationDTO> getAllCompany(
            Specification<Company> spec,
            Pageable pageable
            ){
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.handleFetchAllCompany(spec, pageable));
    }

    @PutMapping("/companies")
    public ResponseEntity<?> updateCompany(@Valid @RequestBody Company reqCompany){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.companyService.handleUpdateCompany(reqCompany));
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cập nhật không thành công");
        }
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<?> deleteCompany(@Min(value = 0) @PathVariable long id){
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.status(HttpStatus.OK).body("Xóa thành công");
    }
}