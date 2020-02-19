package org.example.approvalsystem.controller.v1;

import lombok.extern.slf4j.Slf4j;
import org.example.approvalsystem.entity.*;
import org.example.approvalsystem.service.ApprovalSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/v1/approvers")
public class ApproverController {

    @Autowired
    private ApprovalSystemService approvalSystemService;

    @PostMapping(value = "/")
    public ResponseEntity createApprover(@RequestBody ApproverRequest approverRequest) {
        log.info("Started create approver with approverRequest: {}", approverRequest);

        Response<Message> response = approvalSystemService.createApprover(approverRequest.getId(), approverRequest.getName(), approverRequest.getDocumentTypeList());

        return new ResponseEntity<>(response.getBody(), response.getHttpStatus());
    }

    @GetMapping(value = "/{approverId}/pending-documents")
    public ResponseEntity getAllPendingDocuments(@PathVariable("approverId") String approverId) {
        log.info("Started getAllPendingDocuments for approverId: {}", approverId);

        Response<Set<Document>> response = approvalSystemService.getAllDocumentsToApprove(approverId);

        return new ResponseEntity<>(response.getBody(), response.getHttpStatus());
    }
}
