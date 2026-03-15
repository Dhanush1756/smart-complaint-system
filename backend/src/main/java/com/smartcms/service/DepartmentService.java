package com.smartcms.service;

import com.smartcms.dto.Dtos.DepartmentRequest;
import com.smartcms.exception.DepartmentNotFoundException;
import com.smartcms.model.Department;
import com.smartcms.repository.DepartmentRepository;
import com.smartcms.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository deptRepo;
    private final AuditService auditService;

    public DepartmentService(DepartmentRepository deptRepo, AuditService auditService) {
        this.deptRepo = deptRepo;
        this.auditService = auditService;
    }

    public Department createDepartment(DepartmentRequest req, String adminId) {
        Department dept = new Department(
                IdGenerator.generateDepartmentId(),
                req.getCode().toUpperCase(),
                req.getName(),
                req.getDescription()
        );
        deptRepo.save(dept);
        auditService.log(adminId, "ADMIN", "CREATE_DEPT", "DEPARTMENT",
                dept.getId(), "Department created: " + dept.getName());
        return dept;
    }

    public List<Department> getAllDepartments() {
        return deptRepo.findAll();
    }

    public Department getById(String id) {
        return deptRepo.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    public Department getByCode(String code) {
        return deptRepo.findByCode(code)
                .orElseThrow(() -> new DepartmentNotFoundException("code:" + code));
    }

    public boolean deleteDepartment(String id, String adminId) {
        boolean deleted = deptRepo.delete(id);
        if (deleted) {
            auditService.log(adminId, "ADMIN", "DELETE_DEPT", "DEPARTMENT", id, "Department deleted");
        }
        return deleted;
    }

    public Department updateDepartment(String id, DepartmentRequest req, String adminId) {
        Department dept = getById(id);
        if (req.getName() != null) dept.setName(req.getName());
        if (req.getDescription() != null) dept.setDescription(req.getDescription());
        deptRepo.save(dept);
        auditService.log(adminId, "ADMIN", "UPDATE_DEPT", "DEPARTMENT", id, "Department updated");
        return dept;
    }
}
