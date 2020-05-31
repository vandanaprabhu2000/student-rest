package com.vandana;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
@Entity
public class Student {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String universityName;
    private double gpa;
    public Student(){

    }
    public Student(int id, String name, String universityName, double gpa) {
       this.id = id;
       this.name = name;
       this.universityName = universityName;
       this.gpa = gpa;
    }
    public int getId(){
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUniversityName() {
        return this.universityName;
    }
    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }
    public double getGpa(){
        return this.gpa;
    }
    public void setGpa(double gpa){
        this.gpa = gpa;
    }
    @Override
    public String toString() {
        return "Student{" + "id=" + id + ", name='" + name + '\'' + ", universityName='" + universityName + '\'' + ", gpa=" + gpa + '}';
    }
}