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
public enum DocumentType {

    TRAVEL(1),
    MEDICAL(2),
    MOBILE(3);

    private int value;
    private static Map<Integer, DocumentType> map = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(DocumentType.class);

    static {
        for (DocumentType documentType : DocumentType.values()) {
            map.put(documentType.value, documentType);
        }
    }

    DocumentType(int value) {
        this.value = value;
    }

    public static DocumentType getDocumentType(Integer value) {
        if(value == null) {
            return null;
        }

        DocumentType documentType =  map.get(value);
        if(documentType == null) {
            logger.error("Cannot convert value: {} to DocumentType", value);
            throw new ApprovalSystemException(HttpStatus.BAD_REQUEST, String.format(Messages.INVALID_DOCUMENT_TYPE, value));
        }
        return documentType;
    }
}
