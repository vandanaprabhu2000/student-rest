package com.vandana;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vandana.error.StudentNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//@WebMvcTest(BookController.class)
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StudentControllerTest {
    private static final ObjectMapper om = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StudentRepository mockRepository;
    @Before
    public void init() {
        Student student = new Student(1, "Abby", "University of Michigan", 3.9);
        when(mockRepository.findById(1)).thenReturn(Optional.of(student));
    }
    @Test
    public void find_studentId_OK() throws Exception {
        mockMvc.perform(get("/students/1"))
                /*.andDo(print())*/
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Abby")))
                .andExpect(jsonPath("$.universityName", is("University of Michigan")))
                .andExpect(jsonPath("$.gpa", is(3.9)));
        verify(mockRepository, times(1)).findById(1);
    }
    @Test
    public void find_allStudent_OK() throws Exception {

        List<Student> students = Arrays.asList(
                new Student(1, "Radhika", "Mercer University", 4.5),
                new Student(2, "Harish", "Georgia Tech", 3.8));
        when(mockRepository.findAll()).thenReturn(students);
        mockMvc.perform(get("/students"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Radhika")))
                .andExpect(jsonPath("$[0].universityName", is("Mercer University")))
                .andExpect(jsonPath("$[0].gpa", is(4.5)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Harish")))
                .andExpect(jsonPath("$[1].universityName", is("Georgia Tech")))
                .andExpect(jsonPath("$[1].gpa", is(3.8)));
        verify(mockRepository, times(1)).findAll();
    }
    @Test
    public void find_studentIdNotFound_404() throws Exception {
        when(mockRepository.findById(6)).thenThrow(new StudentNotFoundException(6));
        mockMvc.perform(get("/students/6")).andExpect(status().isNotFound());
    }
    @Test
    public void save_student_OK() throws Exception {
        Student newStudent = new Student(1, "Jessica", "Purdue University", 3.0);
        when(mockRepository.save(any(Student.class))).thenReturn(newStudent);
        mockMvc.perform(post("/students")
                .content(om.writeValueAsString(newStudent))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                /*.andDo(print())*/
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Jessica")))
                .andExpect(jsonPath("$.universityName", is("Purdue University")))
                .andExpect(jsonPath("$.gpa", is(3.0)));
        verify(mockRepository, times(1)).save(any(Student.class));
    }
    @Test
    public void update_student_OK() throws Exception {
        Student updateStudent = new Student(1, "Jennifer", "Purdue University", 3.3);
        when(mockRepository.save(any(Student.class))).thenReturn(updateStudent);
        mockMvc.perform(put("/students/1")
                .content(om.writeValueAsString(updateStudent))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Jennifer")))
                .andExpect(jsonPath("$.universityName", is("Purdue University")))
                .andExpect(jsonPath("$.gpa", is(3.3)));
    }
    @Test
    public void patch_universityName_OK() throws Exception {
        when(mockRepository.save(any(Student.class))).thenReturn(new Student());
        String patchInJson = "{\"universityName\":\"ultraman\"}";
        mockMvc.perform(patch("/students/1")
                .content(patchInJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        verify(mockRepository, times(1)).findById(1);
        verify(mockRepository, times(1)).save(any(Student.class));
    }
    @Test
    public void patch_gpa_405() throws Exception {
        String patchInJson = "{\"gpa\":\"4.0\"}";
        mockMvc.perform(patch("/students/1")
                .content(patchInJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
        verify(mockRepository, times(1)).findById(1);
        verify(mockRepository, times(0)).save(any(Student.class));
    }
    @Test
    public void delete_student_OK() throws Exception {
        doNothing().when(mockRepository).deleteById(1);
        mockMvc.perform(delete("/students/1"))
                /*.andDo(print())*/
                .andExpect(status().isOk());
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
