package org.example.approvalsystem.controller.v1;

import lombok.extern.slf4j.Slf4j;
import org.example.approvalsystem.entity.Employee;
import org.example.approvalsystem.entity.EmployeeRequest;
import org.example.approvalsystem.entity.Message;
import org.example.approvalsystem.entity.Response;
import org.example.approvalsystem.service.ApprovalSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/employees")
public class EmployeeController {

    @Autowired
    private ApprovalSystemService approvalSystemService;

    @PostMapping(value = "/")
    public ResponseEntity createEmployee(@RequestBody EmployeeRequest employeeRequeste) {
        log.info("Started create employee with employeeRequeste: {}", employeeRequeste);

        Response<Message> response = approvalSystemService.createEmployee(employeeRequeste.getId(), employeeRequeste.getName());

        return new ResponseEntity<>(response.getBody(), response.getHttpStatus());
    }
}
