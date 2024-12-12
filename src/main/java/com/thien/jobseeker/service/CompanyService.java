package com.thien.jobseeker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.thien.jobseeker.domain.Company;
import com.thien.jobseeker.repository.CompanyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    @Transactional
    public Company handleCreateCompany(Company company){
        return this.companyRepository.save(company);
    }

    public List<Company> handleFetchAllCompany(){
        return this.companyRepository.findAll();
    }

    public Company handleGetCompany(long id){
        return this.companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Data not found"));
    }

    @Transactional
    public Company handleUpdateCompany(Company companyDTO){
        Company company = handleGetCompany(companyDTO.getId());
        company.setName(companyDTO.getName());
        company.setDescription(companyDTO.getDescription());
        company.setAddress(companyDTO.getAddress());
        company.setLogo(companyDTO.getLogo());
        return company;
    }

    @Transactional
    public void handleDeleteCompany(long id){
        Company company = handleGetCompany(id);
        this.companyRepository.delete(company);
    }
}
