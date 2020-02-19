package org.example.approvalsystem.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.approvalsystem.constant.Messages;
import org.example.approvalsystem.entity.Message;
import org.example.approvalsystem.exception.ApprovalSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends DefaultHandlerExceptionResolver {

    @ExceptionHandler({ApprovalSystemException.class})
    public ResponseEntity handleApprovalSystemException(ApprovalSystemException ex, HttpServletResponse response) {
        log.error("Inside GlobalExceptionHandler.handleApprovalSystemException ", ex);

        Message message = new Message(ex.getMessage());
        return new ResponseEntity<>(message, ex.getHttpStatus());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity handleException(Exception ex, HttpServletResponse response) {
        log.error("Inside GlobalExceptionHandler.handleException ", ex);

        Message message = new Message(Messages.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
