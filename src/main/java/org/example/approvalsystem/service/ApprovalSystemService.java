package org.example.approvalsystem.service;

import org.example.approvalsystem.constant.Messages;
import org.example.approvalsystem.entity.*;
import org.example.approvalsystem.enums.ApprovalStatus;
import org.example.approvalsystem.enums.DocumentType;
import org.example.approvalsystem.exception.ApprovalSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Component
public class ApprovalSystemService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalSystemService.class);

    private Map<String, Document> documentsMap = new HashMap<>();
    private Map<String, Person> personsMap = new HashMap<>();

    private Map<DocumentType, Set<Approver>> documentTypeToApproversMap = new HashMap<>();
    private Map<Approver, Set<Document>> approverToUnapprovedDocumentsMap = new HashMap<>();

    private Random random = new Random();

    public synchronized Response<Message> uploadDocument(MultipartFile multipartFile, Integer documentTypeValue, String employeeId) {

        validateEmployeeId(employeeId);

        logger.info("Received request to upload document: {} of documentTypeValue: {} from employee: {}", multipartFile, documentTypeValue, employeeId);
        Person person = personsMap.get(employeeId);

        if(person instanceof Employee) {
            logger.info("Employee with employeeId: {} exists", employeeId);
            String documentId = String.valueOf(random.nextInt(Integer.MAX_VALUE));
            Employee employee = (Employee) person;
            DocumentType documentType = DocumentType.getDocumentType(documentTypeValue);

            if(!documentsMap.containsKey(documentId)) {
                Document document = new Document(multipartFile, documentType, employee, documentId);
                Map<Approver, ApprovalStatus> approvalStatusMapInDocument = document.getApproverToApprovalStatusMap();

                documentsMap.put(documentId, document);
                logger.info("Created document: {}", document);

                Set<Approver> approvers = documentTypeToApproversMap.get(documentType);
                logger.info("Approvers for documentType: {} are {}", documentType, approvers);

                if(approvers!=null && !approvers.isEmpty()) {
                    for(Approver approver: approvers) {
                        Set<Document> documentsToBeApproved = approverToUnapprovedDocumentsMap.computeIfAbsent(approver, k -> new HashSet<>());
                        documentsToBeApproved.add(document);

                        approvalStatusMapInDocument.put(approver, ApprovalStatus.PENDING);
                    }
                } else {
                    logger.info("Document is auto approved as there are no approvers for document type: {}", documentType);
                    document.setApprovalStatus(ApprovalStatus.APPROVED);
                }

                Message message = new Message(String.format(Messages.CREATED_DOCUMENT, documentId));
                Response<Message> response = new Response<>(HttpStatus.CREATED, message);
                logger.info("Returning response: {}", response);
                return response;
            } else {
                logger.error("Document exists with documentId: {}", documentId);
                throw new ApprovalSystemException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.INTERNAL_SERVER_ERROR);
            }
        } else {
            logger.error("Received Person: {}. No employee exists with employeeId: {}", person, employeeId);
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, String.format(Messages.NO_EMPLOYEE_EXISTS, employeeId));
        }
    }

    public synchronized Response<List<Document>> getAllDocuments() {
        logger.info("Received request to get All documents");

        List<Document> documents = new ArrayList<>(documentsMap.values());
        Response<List<Document>> response = new Response<>(HttpStatus.OK, documents);

        logger.info("Returning response: {}", response);
        return response;
    }

    public synchronized Response<Document> getDocument(String documentId) {
        validateDocumentId(documentId);
        logger.info("Received request to get document: {}", documentId);

        Document document = documentsMap.get(documentId);

        if(document == null) {
            logger.error("Document with id: {} doesn't exist", documentId);
            throw new ApprovalSystemException(HttpStatus.NOT_FOUND, String.format(Messages.NO_DOCUMENT_EXISTS, documentId));
        }
        Response<Document> response = new Response<>(HttpStatus.OK, document);

        logger.info("Returning response: {}", response);
        return response;
    }

    public synchronized Response<Message> updateDocument(MultipartFile file, String documentId, String employeeId) {
        validateDocumentId(documentId);
        validateEmployeeId(employeeId);

        logger.info("Started update document with file: {}, documentId: {}, employeeId: {}", file, documentId, employeeId);
        Document document = documentsMap.get(documentId);
        if(document != null) {
            logger.info("Document with documentId: {} exists. document: {}", documentId, document);
            Employee uploadedByEmployee = document.getUploadedByEmployee();
            if(uploadedByEmployee.getId().equals(employeeId)) {
                logger.info("employeeId: {} is authorized to update document: {}", employeeId, document);

                document.setFile(file);
                document.setDocumentName(file.getOriginalFilename());
                logger.info("Successfully updated file");

                Message message = new Message(String.format(Messages.DOCUMENT_UPDATED, documentId));
                Response<Message> response = new Response<>(HttpStatus.OK, message);
                logger.info("Returning response: {}", response);
                return response;
            } else {
                logger.error("Document cannot be updated by employeeId: {}", employeeId);
                throw new ApprovalSystemException(HttpStatus.UNAUTHORIZED, String.format(Messages.EMPLOYEE_UNAUTHORIZED, employeeId));
            }
        } else {
            logger.error("DocumentId: {} doesn't exist", documentId);
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, String.format(Messages.NO_DOCUMENT_EXISTS, documentId));
        }
    }

    public synchronized Response<Message> updateApprovalStatusForDocument(String documentId, String approvalStatusString, String approverId) {
        validateDocumentId(documentId);
        validateApprovalStatus(approvalStatusString);
        validateApproverId(approverId);

        logger.info("updateApprovalStatusForDocument started with documentId: {}, approvalStatus: {}, approverId: {}", documentId, approvalStatusString, approverId);
        ApprovalStatus newApprovalStatusByApprover = ApprovalStatus.getApprovalStatus(approvalStatusString);
        if(!newApprovalStatusByApprover.isTerminal()) {
            logger.error("Approval status cannot be pending");
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, Messages.INVALID_PENDING_APPROVAL_STATUS);
        }


        Document document = documentsMap.get(documentId);
        if(document != null) {
            logger.info("Document with documentId: {} is valid. document: {}", documentId, document);
            ApprovalStatus currentDocumentStatus = document.getApprovalStatus();
            if(currentDocumentStatus.isTerminal()) {
                logger.error("Document is already approved/rejected. document: {}", document);
                throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, String.format(Messages.DOCUMENT_TERMINAL_STATUS, documentId));
            }

            Person person = personsMap.get(approverId);
            if(person instanceof Approver) {
                Approver approver = (Approver) person;
                logger.info("Approver with approverId: {} is valid. approver: {}", approverId, approver);

                Map<Approver, ApprovalStatus> approvalStatusMapInDocument = document.getApproverToApprovalStatusMap();
                ApprovalStatus existingApprovalStatusByApprover = approvalStatusMapInDocument.get(approver);
                if(existingApprovalStatusByApprover == null) {
                    logger.error("Approver: {} is not authorized to approver/reject document: {}", approver, document);
                    throw new ApprovalSystemException(HttpStatus.UNAUTHORIZED, String.format(Messages.APPROVER_UNAUTHORIZED_DOCUMENT, approverId, documentId));
                }

                if(existingApprovalStatusByApprover.isTerminal()) {
                    logger.error("Document is already approved/rejected by approver. document: {}", document);
                    throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, String.format(Messages.DOCUMENT_TERMINAL_STATUS_BY_APPROVER, documentId, approverId));
                }

                if(newApprovalStatusByApprover.equals(ApprovalStatus.APPROVED)) {
                    approvalStatusMapInDocument.put(approver, newApprovalStatusByApprover);
                    Integer approvedCount = document.getApprovedCount();
                    approvedCount++;
                    document.setApprovedCount(approvedCount);

                    if(approvedCount.equals(approvalStatusMapInDocument.size())) {
                        document.setApprovalStatus(ApprovalStatus.APPROVED);
                    }

                    Set<Document> documentsToApprove = approverToUnapprovedDocumentsMap.get(approver);
                    documentsToApprove.remove(document);
                } else {
                    document.setApprovalStatus(newApprovalStatusByApprover);

                    for(Map.Entry<Approver, ApprovalStatus>  entry: approvalStatusMapInDocument.entrySet()) {
                        Approver key = entry.getKey();
                        ApprovalStatus value = entry.getValue();

                        if(!value.isTerminal()) {
                            Set<Document> documentsToApprove = approverToUnapprovedDocumentsMap.get(key);
                            documentsToApprove.remove(document);
                        }
                    }

                    approvalStatusMapInDocument.put(approver, newApprovalStatusByApprover);
                }

                logger.info("Successfully updated approval status for document. document: {}, approverToUnapprovedDocumentsMap: {}", document, approverToUnapprovedDocumentsMap);
                Message message = new Message(String.format(Messages.DOCUMENT_STATUS_UPDATED, documentId));
                Response<Message> response = new Response<>(HttpStatus.OK, message);

                logger.info("Returning response: {}", response);
                return response;

            } else {
                logger.error("Received Person: {}. No approver exists with approverId: {}", person, approverId);
                throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, String.format(Messages.NO_APPROVER_EXISTS, approverId));
            }
        } else {
            logger.error("DocumentId: {} doesn't exist", documentId);
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, String.format(Messages.NO_DOCUMENT_EXISTS, documentId));
        }
    }

    public synchronized Response<Message> createEmployee(String employeeId, String employeeName) {
        validateEmployeeId(employeeId);
        validateEmployeeName(employeeName);

        logger.info("Started create employee with employeeId: {} and employeeName: {}", employeeId, employeeName);
        Person person = personsMap.get(employeeId);
        if(person != null) {
            logger.error("Person already exists with employeeId: {}", employeeId);
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, String.format(Messages.PERSON_ALREADY_EXISTS, employeeId));
        }

        Employee employee = new Employee(employeeId, employeeName);
        personsMap.put(employeeId, employee);

        logger.info("Successfully created employee: {}", employee);
        Message  message = new Message(String.format(Messages.CREATED_EMPLOYEE, employeeId));
        Response<Message> response = new Response<>(HttpStatus.CREATED, message);

        logger.info("Returning response: {}", response);
        return response;
    }

    public synchronized Response<Message> createApprover(String approverId, String approverName, List<Integer> documentTypeIntegerList) {
        validateApproverId(approverId);
        validateApproverName(approverName);
        validateDocumentTypeList(documentTypeIntegerList);

        logger.info("Started create approver with approverId: {}, approverName: {} and approver can approve documents of type: {}", approverId, approverName, documentTypeIntegerList);
        Person person = personsMap.get(approverId);
        if(person != null) {
            logger.error("Person already exists with approverId: {}", approverId);
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, String.format(Messages.PERSON_ALREADY_EXISTS, approverId));
        }

        List<DocumentType> documentTypeList = new ArrayList<>();
        for(Integer documentTypeInteger: documentTypeIntegerList) {
            DocumentType documentType = DocumentType.getDocumentType(documentTypeInteger);
            documentTypeList.add(documentType);
        }

        Approver approver = new Approver(approverId, approverName);
        personsMap.put(approverId, approver);
        logger.info("Successfully created approver: {}", approver);

        documentTypeList.forEach(documentType -> {
            Set<Approver> approverSet = documentTypeToApproversMap.computeIfAbsent(documentType, k -> new HashSet<>());
            approverSet.add(approver);
        });
        logger.info("Successfully updated documentTypeToApproversMap: {}", documentTypeToApproversMap);

        Message  message = new Message(String.format(Messages.CREATED_APPROVER, approverId));
        Response<Message> response = new Response<>(HttpStatus.CREATED, message);

        logger.info("Returning response: {}", response);
        return response;
    }

    public synchronized Response<Set<Document>> getAllDocumentsToApprove(String approverId) {
        validateApproverId(approverId);
        logger.info("getAllDocumentsToApprove started with approverId: {}", approverId);

        Person person = personsMap.get(approverId);
        if(person instanceof Approver) {
            Approver approver = (Approver) person;
            logger.info("Approver with approverId: {}  is {}", approverId, approver);
            Set<Document> documents = approverToUnapprovedDocumentsMap.get(approver);

            Response<Set<Document>> response = new Response<>(HttpStatus.OK, documents);
            logger.info("Returning response: {}", response);
            return response;
        } else {
            logger.error("Received Person: {}. No approver exists with approverId: {}", person, approverId);
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, String.format(Messages.NO_APPROVER_EXISTS, approverId));
        }
    }

    private void validateEmployeeId(String employeeId) {
        if(StringUtils.isEmpty(employeeId)) {
            logger.error("Employee Id cannot be null");
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, Messages.EMPLOYEE_ID_NULL);
        }
    }

    private void validateEmployeeName(String employeeName) {
        if(StringUtils.isEmpty(employeeName)) {
            logger.error("Employee Id cannot be null");
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, Messages.EMPLOYEE_NAME_NULL);
        }
    }

    private void validateApproverId(String approverId) {
        if(StringUtils.isEmpty(approverId)) {
            logger.error("Approver Id cannot be null");
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, Messages.APPROVER_ID_NULL);
        }
    }

    private void validateApproverName(String approverName) {
        if(StringUtils.isEmpty(approverName)) {
            logger.error("Approver Id cannot be null");
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, Messages.APPROVER_NAME_NULL);
        }
    }

    private void validateApprovalStatus(String approvalStatus) {
        if(StringUtils.isEmpty(approvalStatus)) {
            logger.error("Approval Status cannot be null");
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, Messages.APPROVAL_STATUS_NULL);
        }
    }

    private void validateDocumentId(String documentId) {
        if(StringUtils.isEmpty(documentId)) {
            logger.error("document Id cannot be null");
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, Messages.DOCUMENT_ID_NULL);
        }
    }

    private void validateDocumentTypeList(List<Integer> documentTypeIntegerList) {
        if(documentTypeIntegerList == null || documentTypeIntegerList.isEmpty()) {
            logger.error("Document Type Integer List cannot be null");
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, Messages.DOCUMENT_TYPE_LIST_NULL);
        }
    }

}
