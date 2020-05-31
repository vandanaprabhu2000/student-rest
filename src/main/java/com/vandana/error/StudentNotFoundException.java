package com.vandana.error;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(int id){
        super("Student id not found : " + id);
    }
}
