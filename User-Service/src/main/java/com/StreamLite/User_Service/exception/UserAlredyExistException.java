package com.StreamLite.User_Service.exception;

public class UserAlredyExistException extends RuntimeException{
    public UserAlredyExistException(String message) {
        super(message);
    }
}
