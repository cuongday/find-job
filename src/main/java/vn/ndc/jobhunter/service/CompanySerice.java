package vn.ndc.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ndc.jobhunter.domain.Company;
import vn.ndc.jobhunter.domain.dto.Meta;
import vn.ndc.jobhunter.domain.dto.ResultPaginationDTO;
import vn.ndc.jobhunter.repository.CompanyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanySerice {
    private final CompanyRepository companyRepository;

    public CompanySerice(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }
    public ResultPaginationDTO handleGetCompany(Specification<Company> companySpec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(companySpec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageCompany.getContent());
        return rs;
    }

    public Company fetchCompanyById(Long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            return companyOptional.get();
        }
        return null;
    }

    public Company handleUpdateCompany(Long id, Company company){
        Company companyUpdate = this.fetchCompanyById(id);
        if (companyUpdate!=null) {
            companyUpdate.setName(company.getName());
            companyUpdate.setAddress(company.getAddress());
            companyUpdate.setDescription(company.getDescription());
            companyUpdate.setLogo(company.getLogo());
            companyUpdate = this.companyRepository.save(companyUpdate);
        }
        return companyUpdate;
    }

    public void handleDeleteCompany(Long id) {
        this.companyRepository.deleteById(id);
    }
}
