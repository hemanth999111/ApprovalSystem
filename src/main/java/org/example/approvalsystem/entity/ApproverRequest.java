package org.example.approvalsystem.entity;

import lombok.Data;

import java.util.List;

@Data
public class ApproverRequest {

    private String id;
    private String name;
    private List<Integer> documentTypeList;
}
