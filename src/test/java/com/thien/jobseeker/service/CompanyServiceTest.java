package com.thien.jobseeker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.thien.jobseeker.domain.Company;
import com.thien.jobseeker.domain.response.ResultPaginationDTO;
import com.thien.jobseeker.repository.CompanyRepository;

@SpringBootTest
public class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    private Company testCompany;

    @BeforeEach
    void setUp() {
        testCompany = new Company();
        testCompany.setId(1L);
        testCompany.setName("Test Company");
        testCompany.setDescription("Test Description");
        testCompany.setAddress("123 Test St");
        testCompany.setLogo("test-logo.png");
    }

    @Test
    void testHandleCreateCompany() {
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

        Company created = companyService.handleCreateCompany(testCompany);

        assertNotNull(created);
        assertEquals("Test Company", created.getName());
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void testHandleDeleteCompany() {
        when(companyRepository.findById(testCompany.getId())).thenReturn(Optional.of(testCompany));
        doNothing().when(companyRepository).delete(any(Company.class));

        companyService.handleDeleteCompany(testCompany.getId());

        verify(companyRepository).findById(testCompany.getId());
        verify(companyRepository).delete(any(Company.class));
    }

    @Test
    void testHandleGetCompany() {
        when(companyRepository.findById(testCompany.getId())).thenReturn(Optional.of(testCompany));

        Company fetched = companyService.handleGetCompany(testCompany.getId());

        assertNotNull(fetched);
        assertEquals("Test Company", fetched.getName());
        verify(companyRepository).findById(testCompany.getId());
    }

    @Test
    void testHandleUpdateCompany() {
        when(companyRepository.findById(testCompany.getId())).thenReturn(Optional.of(testCompany));
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

        Company updated = companyService.handleUpdateCompany(testCompany);

        assertNotNull(updated);
        assertEquals("Test Company", updated.getName());
        verify(companyRepository).findById(testCompany.getId());
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void testHandleFetchAllCompany() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Company> page = new PageImpl<>(Arrays.asList(testCompany));
        when(companyRepository.findAll((Specification<Company>) null, pageable)).thenReturn(page);

        ResultPaginationDTO result = companyService.handleFetchAllCompany(null, pageable);

        assertNotNull(result);
        assertEquals(page.getTotalElements(), ((Page<Company>) result.getResult()).getTotalElements());
        verify(companyRepository).findAll((Specification<Company>) null, pageable);
    }
}