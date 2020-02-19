package org.example.approvalsystem.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class ApprovalSystemException extends RuntimeException {

    private HttpStatus httpStatus;

    public ApprovalSystemException(HttpStatus httpStatus, String errorMessage) {
        super(errorMessage);
        this.httpStatus = httpStatus;
    }
}
