package org.example.approvalsystem.constant;

import java.util.Locale;

public class Messages {

    public static final String NO_EMPLOYEE_EXISTS = "No employee exists with employee id: %s";
    public static final String INTERNAL_SERVER_ERROR = "Something went wrong! Please try again later.";
    public static final String CREATED_DOCUMENT = "Created document with id: %s";
    public static final String NO_DOCUMENT_EXISTS = "No document exists with document id: %s";
    public static final String EMPLOYEE_UNAUTHORIZED = "Employee: %s  is not authorized";
    public static final String DOCUMENT_UPDATED = "Updated document: %s successfully";
    public static final String NO_APPROVER_EXISTS = "No approver exists with employee id: %s";
    public static final String INVALID_APPROVAL_STATUS = "Invalid Approval Status: %s";
    public static final String INVALID_DOCUMENT_TYPE = "Invalid Document Type: %s";
    public static final String DOCUMENT_TERMINAL_STATUS = "Document: %s is already approved/rejected.";
    public static final String DOCUMENT_TERMINAL_STATUS_BY_APPROVER =  "Document: %s is already approved/rejected by approver: %s.";
    public static final String APPROVER_UNAUTHORIZED_DOCUMENT = "Approver: %s is not authorized to approve/reject document: %s";
    public static final String INVALID_PENDING_APPROVAL_STATUS = "Approval status cannot be pending";
    public static final String DOCUMENT_STATUS_UPDATED = "Document status for documentId: %s is updated successfully";
    public static final String PERSON_ALREADY_EXISTS = "Person already exists with id: %s";
    public static final String CREATED_EMPLOYEE = "Created Employee with id: %s";
    public static final String CREATED_APPROVER = "Created Approver with id: %s";
    public static final String DOCUMENT_TYPE_LIST_NULL = "Document type List should not be empty/null";
    public static final String EMPLOYEE_ID_NULL = "Employee Id should not be empty/null";
    public static final String DOCUMENT_ID_NULL = "Document Id should not be empty/null";
    public static final String APPROVER_ID_NULL = "Approver Id should not be empty/null";
    public static final String APPROVAL_STATUS_NULL = "Approval Status should not be empty/null";
    public static final String EMPLOYEE_NAME_NULL = "Employee Name should not be empty/null";
    public static final String APPROVER_NAME_NULL = "Approver Name should not be empty/null";
}
