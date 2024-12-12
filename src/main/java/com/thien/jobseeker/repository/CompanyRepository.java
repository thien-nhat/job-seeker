package com.thien.jobseeker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thien.jobseeker.domain.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

}
