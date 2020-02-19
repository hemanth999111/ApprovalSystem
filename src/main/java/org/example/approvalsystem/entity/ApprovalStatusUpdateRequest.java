package org.example.approvalsystem.entity;

import lombok.Data;

@Data
public class ApprovalStatusUpdateRequest {

    private String approverId;
    private String approvalStatus;
}
