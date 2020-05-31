package com.vandana;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // for restTemplate
@ActiveProfiles("test")
public class StudentControllerRestTemplateTest {
    private static final ObjectMapper om = new ObjectMapper();
    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private StudentRepository mockRepository;
    @Before
    public void init() {
        Student student  = new Student(1, "Abby", "University of Michigan", 3.9);
        when(mockRepository.findById(1)).thenReturn(Optional.of(student));
    }
    @Test
    public void find_studentId_OK() throws JSONException {
        String expected = "{id:1,name:\"Abby\",universityName:\"University of Michigan\",gpa: 3.9}";
        ResponseEntity<String> response = restTemplate.getForEntity("/students/1", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());
        JSONAssert.assertEquals(expected, response.getBody(), false);
        verify(mockRepository, times(1)).findById(1);
    }
    @Test
    public void find_allStudent_OK() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1, "Abby", "University of Michigan", 3.9));
                new Student(2, "Chris", "Purdue University", 3.5);
        when(mockRepository.findAll()).thenReturn(students);
        String expected = om.writeValueAsString(students);
        ResponseEntity<String> response = restTemplate.getForEntity("/students", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);
        verify(mockRepository, times(1)).findAll();
    }
    @Test
    public void find_studentIdNotFound_404() throws Exception {
        String expected = "{status:404,error:\"Not Found\",message:\"Student id not found : 5\",path:\"/students/5\"}";
        ResponseEntity<String> response = restTemplate.getForEntity("/students/5", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    @Test
    public void save_student_OK() throws Exception {
        Student student = new Student(1, "Taylor", "University of Alabama", 3.5);
        when(mockRepository.save(any(Student.class))).thenReturn(student);
        String expected = om.writeValueAsString(student);
        ResponseEntity<String> response = restTemplate.postForEntity("/students", student, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);
        verify(mockRepository, times(1)).save(any(Student.class));
    }
    @Test
    public void update_student_OK() throws Exception {
        Student updateStudent = new Student(1, "Nicole", "Ilinois State University", 3.6);
        when(mockRepository.save(any(Student.class))).thenReturn(updateStudent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(updateStudent), headers);
        ResponseEntity<String> response = restTemplate.exchange("/students/1", HttpMethod.PUT, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(om.writeValueAsString(updateStudent), response.getBody(), false);
        verify(mockRepository, times(1)).findById(1);
        verify(mockRepository, times(1)).save(any(Student.class));
    }
    @Test
    public void patch_universityName_OK() {
        when(mockRepository.save(any(Student.class))).thenReturn(new Student());
        String patchInJson = "{\"universityName\":\"ultraman\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(patchInJson, headers);
        ResponseEntity<String> response = restTemplate.exchange("/students/1", HttpMethod.PATCH, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockRepository, times(1)).findById(1);
        verify(mockRepository, times(1)).save(any(Student.class));
    }
    @Test
    public void patch_studentGpa_405() throws JSONException {
        String expected = "{status:405,error:\"Method Not Allowed\",message:\"Field [gpa] update is not allow.\"}";

        String patchInJson = "{\"gpa\":\"5.0\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(patchInJson, headers);
        ResponseEntity<String> response = restTemplate.exchange("/students/1", HttpMethod.PATCH, entity, String.class);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);
        verify(mockRepository, times(1)).findById(1);
        verify(mockRepository, times(0)).save(any(Student.class));
    }
    @Test
    public void delete_student_OK() {
        doNothing().when(mockRepository).deleteById(1);
        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange("/students/1", HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockRepository, times(1)).deleteById(1);
    }
    private static void printJSON(Object object) {
        String result;
        try {
            result = om.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            System.out.println(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}