package org.example.approvalsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Setter;
import org.example.approvalsystem.enums.ApprovalStatus;
import org.example.approvalsystem.enums.DocumentType;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Data
public class Document {

    @Setter
    @JsonIgnore
    private MultipartFile file;
    private String documentId;
    private String documentName;
    private DocumentType documentType;
    private Employee uploadedByEmployee;

    @Setter
    private ApprovalStatus approvalStatus;
    private Map<Approver, ApprovalStatus> approverToApprovalStatusMap;
    private int approvedCount;

    public Document(MultipartFile file, DocumentType documentType, Employee uploadedByEmployee, String documentId) {
        this.file = file;
        this.documentType = documentType;
        this.uploadedByEmployee = uploadedByEmployee;
        this.documentId = documentId;
        this.documentName = file.getOriginalFilename();
        approvalStatus = ApprovalStatus.PENDING;
        approverToApprovalStatusMap = new HashMap<>();
        approvedCount = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;

        Document document = (Document) o;

        return documentId != null ? documentId.equals(document.documentId) : document.documentId == null;
    }

    @Override
    public int hashCode() {
        return documentId != null ? documentId.hashCode() : 0;
    }
}
