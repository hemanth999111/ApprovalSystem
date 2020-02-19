package org.example.approvalsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@ToString
public class Response<T> {

    private HttpStatus httpStatus;
    private T body;
}
