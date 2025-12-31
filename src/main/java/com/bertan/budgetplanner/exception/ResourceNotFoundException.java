package com.bertan.budgetplanner.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BudgetPlannerException {

    public ResourceNotFoundException(String error) {
        super(error, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String resourceType, String error) {
        super(String.format("%s not found: %s", resourceType, error), HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String error, Throwable cause) {

        super(error, cause, HttpStatus.NOT_FOUND);
    }
}
