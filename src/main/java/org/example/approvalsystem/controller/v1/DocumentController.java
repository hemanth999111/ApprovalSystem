package org.example.approvalsystem.controller.v1;

import org.example.approvalsystem.entity.ApprovalStatusUpdateRequest;
import org.example.approvalsystem.entity.Document;
import org.example.approvalsystem.entity.Message;
import org.example.approvalsystem.entity.Response;
import org.example.approvalsystem.service.ApprovalSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/documents")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalSystemService.class);

    @Autowired
    private ApprovalSystemService approvalSystemService;

    @PostMapping(value="/")
    public ResponseEntity uploadDocument(@RequestParam(name = "file", required = true) MultipartFile multipartFile,
                                         @RequestParam(name = "document-type", required = true) Integer documentTypeValue,
                                         @RequestParam(name = "employee-id", required = true) String employeeId ) {
        logger.info("Started uploadDocument with documentTypeValue: {} and employeeId: {}", documentTypeValue, employeeId);
        Response<Message> response = approvalSystemService.uploadDocument(multipartFile, documentTypeValue, employeeId);

        return new ResponseEntity<>(response.getBody(), response.getHttpStatus());
    }

    @PutMapping(value="/")
    public ResponseEntity updateDocument(@RequestParam(name = "file", required = true) MultipartFile multipartFile,
                                         @RequestParam(name = "document-id", required = true) String documentId,
                                         @RequestParam(name = "employee-id", required = true) String employeeId ) {
        logger.info("Started updateDocument with documentId: {} and employeeId: {}", documentId, employeeId);
        Response<Message> response = approvalSystemService.updateDocument(multipartFile, documentId, employeeId);

        return new ResponseEntity<>(response.getBody(), response.getHttpStatus());
    }

    @PutMapping(value="/{documentId}/approval-status")
    public ResponseEntity updateDocumentApprovalStatus(@PathVariable("documentId") String documentId,
                                                       @RequestBody ApprovalStatusUpdateRequest approvalStatusUpdateRequest) {
        logger.info("Started updateDocumentApprovalStatus with documentId: {} and approvalStatusUpdateRequest: {}", documentId, approvalStatusUpdateRequest);
        Response<Message> response = approvalSystemService.updateApprovalStatusForDocument(documentId, approvalStatusUpdateRequest.getApprovalStatus(), approvalStatusUpdateRequest.getApproverId());

        return new ResponseEntity<>(response.getBody(), response.getHttpStatus());
    }


    @GetMapping(value="/")
    public ResponseEntity getAllDocuments() {
        logger.info("Started getAllDocuments");
        Response<List<Document>> response = approvalSystemService.getAllDocuments();

        return new ResponseEntity<>(response.getBody(), response.getHttpStatus());
    }

    @GetMapping(value="/{documentId}")
    public ResponseEntity getDocument(@PathVariable("documentId") String documentId) {
        logger.info("Started getDocument for documentId: {}", documentId);
        Response<Document> response = approvalSystemService.getDocument(documentId);

        return new ResponseEntity<>(response.getBody(), response.getHttpStatus());
    }
}
