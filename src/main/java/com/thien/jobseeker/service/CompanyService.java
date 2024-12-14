package com.thien.jobseeker.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thien.jobseeker.domain.Company;
import com.thien.jobseeker.domain.response.Meta;
import com.thien.jobseeker.domain.response.ResultPaginationDTO;
import com.thien.jobseeker.repository.CompanyRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    @Transactional
    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleFetchAllCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageCompany);

        return rs;
    }

    public Company handleGetCompany(long id) {
        return this.companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Data not found"));
    }

    @Transactional
    public Company handleUpdateCompany(Company companyDTO) {
        Company company = handleGetCompany(companyDTO.getId());
        company.setName(companyDTO.getName());
        company.setDescription(companyDTO.getDescription());
        company.setAddress(companyDTO.getAddress());
        company.setLogo(companyDTO.getLogo());
        return company;
    }

    @Transactional
    public void handleDeleteCompany(long id) {
        Company company = handleGetCompany(id);
        this.companyRepository.delete(company);
    }
}
