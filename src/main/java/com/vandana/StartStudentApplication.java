package com.vandana;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
@SpringBootApplication
public class StartStudentApplication {
    public static void main(String[] args) {
        SpringApplication.run(StartStudentApplication.class, args);
    }
        @Profile("demo")
        @Bean
        CommandLineRunner initDataBase(StudentRepository repository){
            return args -> {
                repository.save(new Student(1, "Abby", "University of Michigan", 3.9));
                repository.save(new Student(2, "Chris", "Purdue University", 3.5));
                repository.save(new Student(3, "Maddie", "University of Wisconsin Madison", 3.3));
                repository.save(new Student(4, "Grant", "University of Iowa", 3.3));
                repository.save(new Student(5, "Hailey", "Minnesota State University", 4.5));
            };
        }
    }
