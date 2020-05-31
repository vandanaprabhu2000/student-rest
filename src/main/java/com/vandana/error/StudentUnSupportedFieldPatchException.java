package com.vandana.error;
import java.util.Set;
public class StudentUnSupportedFieldPatchException extends RuntimeException {
    public StudentUnSupportedFieldPatchException(Set<String> keys){
        super("Field " + keys.toString() + " update is not allow.");
    }
}
