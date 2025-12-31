package com.bertan.budgetplanner.exception;

import org.springframework.http.HttpStatus;

public abstract class BudgetPlannerException extends RuntimeException{

    private final HttpStatus httpStatus;

    protected BudgetPlannerException(String error, HttpStatus httpStatus) {
        super(error);
        this.httpStatus = httpStatus;
    }

    protected BudgetPlannerException(String error, Throwable cause, HttpStatus httpStatus) {
        super(error, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
