package org.example.approvalsystem.enums;

import lombok.Getter;
import lombok.ToString;
import org.example.approvalsystem.constant.Messages;
import org.example.approvalsystem.exception.ApprovalSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@ToString
@Getter
public enum  ApprovalStatus {

    PENDING("pending"),
    REJECTED("rejected"),
    APPROVED("approved");

    private ApprovalStatus(String value) {
        this.value = value;
    }

    private String value;
    private static Map<String, ApprovalStatus> map = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ApprovalStatus.class);

    static {
        for (ApprovalStatus approvalStatus : ApprovalStatus.values()) {
            map.put(approvalStatus.value, approvalStatus);
        }
    }

    public static ApprovalStatus getApprovalStatus(String value) {
        if(value == null) {
            return null;
        }

        ApprovalStatus approvalStatus =  map.get(value);
        if(approvalStatus == null) {
            logger.error("Cannot convert value: {} to ApprovalStatus", value);
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, String.format(Messages.INVALID_APPROVAL_STATUS, value));
        }
        return approvalStatus;
    }

    public boolean isTerminal() {
        return this.equals(REJECTED) || this.equals(APPROVED);
    }
}
