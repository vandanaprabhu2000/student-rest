package com.vandana;
import com.vandana.error.StudentNotFoundException;
import com.vandana.error.StudentUnSupportedFieldPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
public class StudentController {
    @Autowired
    private StudentRepository repository;

    // Find
    @GetMapping("/students")
    List<Student> findAll() {
        return repository.findAll();
    }

    // Save
    @PostMapping("/students")
    //return 201 instead of 200
    @ResponseStatus(HttpStatus.CREATED)
    Student newStudent(@RequestBody Student student) {
        return repository.save(student);
    }

    // Find
    @GetMapping("/students/{id}")
    Student findOne(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    // Save or update
    @PutMapping("/students/{id}")
    Student saveOrUpdate(@RequestBody Student newStudent, @PathVariable int id) {

        return repository.findById(id)
                .map(x -> {
                    x.setName(newStudent.getName());
                    x.setUniversityName(newStudent.getUniversityName());
                    x.setGpa(newStudent.getGpa());
                    return repository.save(x);
                })
                .orElseGet(() -> {
                    newStudent.setId(id);
                    return repository.save(newStudent);
                });
    }

    // update university only
    @PatchMapping("/students/{id}")
    Student patch(@RequestBody Map<String, String> update, @PathVariable int id) {

        return repository.findById(id)
                .map(x -> { String university = update.get("universityName");
                    if (!StringUtils.isEmpty(university)) {
                        x.setUniversityName(university);

                        // better create a custom method to update a value = :newValue where id = :id
                        return repository.save(x);
                    } else {
                        throw new StudentUnSupportedFieldPatchException(update.keySet());
                    }

                })
                .orElseGet(() -> {
                    throw new StudentNotFoundException(id);
                });

    }

    @DeleteMapping("/students/{id}")
    void deleteStudent(@PathVariable int id) {
        repository.deleteById(id);
    }

}
