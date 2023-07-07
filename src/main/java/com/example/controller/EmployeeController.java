package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.model.Employee;
import com.example.service.EmployeeService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee createEmployee(@RequestBody Employee employee){
        return employeeService.saveEmployee(employee);
    }

    @ApiOperation(value = "Ver una lista de Empleados", response = Employee[].class)
    @ApiResponses( value = {
    		@ApiResponse(code = 200, message = "Empleados obtenidos Correctamente"),
    	     @ApiResponse(code = 401, message = "No estas autorizado para ver Empleados"),
    	     @ApiResponse(code = 403, message = "Está tratando de acceder a Empleado No permitido"),
    	     @ApiResponse(code = 404, message = "Recurso de Empleado no fue encontrado")
    	     })
    @GetMapping
    public List<Employee> getAllEmployees(){
        return employeeService.getAllEmployees();
    }

    @GetMapping("{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") long employeeId){
        return employeeService.getEmployeeById(employeeId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable("id") long employeeId,
                                                   @RequestBody Employee employee){
        return employeeService.getEmployeeById(employeeId)
                .map(savedEmployee -> {

                    savedEmployee.setFirstName(employee.getFirstName());
                    savedEmployee.setLastName(employee.getLastName());
                    savedEmployee.setEmail(employee.getEmail());

                    Employee updatedEmployee = employeeService.updateEmployee(savedEmployee);
                    return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);

                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") long employeeId){

        employeeService.deleteEmployee(employeeId);

        return new ResponseEntity<String>("Employee deleted successfully!.", HttpStatus.OK);

    }
}