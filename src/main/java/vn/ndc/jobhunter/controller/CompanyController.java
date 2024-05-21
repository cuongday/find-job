package vn.ndc.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ndc.jobhunter.domain.Company;
import vn.ndc.jobhunter.domain.dto.ResultPaginationDTO;
import vn.ndc.jobhunter.service.CompanySerice;
import vn.ndc.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanySerice companySerice;

    public CompanyController(CompanySerice companySerice) {
        this.companySerice = companySerice;
    }

    @PostMapping("/companies")
    @ApiMessage("Create new company")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        Company newCompany = this.companySerice.handleCreateCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch all company")
    public ResponseEntity<ResultPaginationDTO> fetchAllCompany(@Filter Specification<Company> companySpec,
                                                               Pageable pageable) {

        ResultPaginationDTO rs = this.companySerice.handleGetCompany(companySpec, pageable);
        return ResponseEntity.ok(rs);
    }

    @PutMapping("/companies/{id}")
    @ApiMessage("Update company by id")
    public ResponseEntity<Company> updateCompany(@PathVariable("id") Long id, @Valid @RequestBody Company company) {
        Company companyUpdate = this.companySerice.handleUpdateCompany(id, company);
        return ResponseEntity.ok(companyUpdate);
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("Delete company by id")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") Long id) {
        this.companySerice.handleDeleteCompany(id);
        return ResponseEntity.ok(null);
    }
}
