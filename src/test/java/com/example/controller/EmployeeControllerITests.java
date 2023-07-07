package com.example.controller;
import com.example.EmployeeRepository;
import com.example.model.Employee;
import com.example.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeControllerITests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private EmployeeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        employeeRepository.deleteAll();
    }
    
    //Properties Zone
    Employee employee;
    
    //Loads Zone
    public void loadEmployee() {
    	employee = Employee.builder()
                .firstName("Ramesh")
                .lastName("Fadatare")
                .email("ramesh@gmail.com")
                .build();
    }

    @Test
    public void saveEmployeeOK() throws Exception{
    	loadEmployee();

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)));

        // then - verify the result or output using assert statements
        response.andDo(print()).
                andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName",
                        is(employee.getFirstName())))
                .andExpect(jsonPath("$.lastName",
                        is(employee.getLastName())))
                .andExpect(jsonPath("$.email",
                        is(employee.getEmail())));
    }

    // JUnit test for Get All employees REST API
    @Test
    public void getListOk() throws Exception{
        // given - precondition or setup
        List<Employee> listOfEmployees = new ArrayList<>();
        listOfEmployees.add(Employee.builder().firstName("Ramesh").lastName("Fadatare").email("ramesh@gmail.com").build());
        listOfEmployees.add(Employee.builder().firstName("Tony").lastName("Stark").email("tony@gmail.com").build());
        employeeRepository.saveAll(listOfEmployees);
        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/employees"));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(listOfEmployees.size())));
    }

    // positive scenario - valid employee id
    // JUnit test for GET employee by id REST API
    @Test
    public void getByIdOK() throws Exception{
        loadEmployee();
        employeeRepository.save(employee);

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employee.getId()));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee.getLastName())))
                .andExpect(jsonPath("$.email", is(employee.getEmail())));
    }

    @Test
    public void IvalidUpdateByEmployeeId() throws Exception{
        loadEmployee();
//        employeeRepository.save(employee);
        service.saveEmployee(employee);
        
        Employee employee2 = new Employee();
        employee2.setId(0);

        ServiceException resultado = assertThrows(ServiceException.class, 
        		() -> service.updateEmployee(employee2));
        assertNotNull(resultado);
        assertEquals("400 BAD_REQUESTId No encontrado para Actualizar",
        		resultado.getLocalizedMessage());
        

    }
    
    @Test
    public void ivalidDuplicateSave() throws Exception{
        loadEmployee();
//        employeeRepository.save(employee);
        service.saveEmployee(employee);
        
        Employee employee2 = employee;

        ServiceException resultado = assertThrows(ServiceException.class, 
        		() -> service.saveEmployee(employee2));
        assertNotNull(resultado);
        assertEquals("400 BAD_REQUESTDuplicado",
        		resultado.getLocalizedMessage());
        

    }
    
    @Test
    public void ivalidFindById() throws Exception{
        loadEmployee();
        service.saveEmployee(employee);

        ServiceException resultado = assertThrows(ServiceException.class, 
        		() -> service.getEmployeeById(0));
        assertNotNull(resultado);
        assertEquals("400 BAD_REQUESTId No encontrado",
        		resultado.getLocalizedMessage());
    }

    @Test
    public void ivalidDeleteIdNotFound() throws Exception{
        ServiceException resultado = assertThrows(ServiceException.class, 
        		() -> service.deleteEmployee(0));
        assertNotNull(resultado);
        assertEquals("400 BAD_REQUESTId No encontrado para Eliminar",
        		resultado.getLocalizedMessage());
    }
    
    // JUnit test for update employee REST API - positive scenario
    @Test
    public void updateOkAfterSave() throws Exception{
        loadEmployee();
        employeeRepository.save(employee);

        Employee updatedEmployee = Employee.builder()
                .firstName("Ram")
                .lastName("Jadhav")
                .email("ram@gmail.com")
                .build();

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));


        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedEmployee.getLastName())))
                .andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())));
    }

   
    // JUnit test for delete employee REST API
    @Test
    public void deleteOkAfterSave() throws Exception{
        loadEmployee();
        employeeRepository.save(employee);

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", employee.getId()));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print());
    }
}