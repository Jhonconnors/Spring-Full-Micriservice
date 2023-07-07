package com.example.service;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.EmployeeRepository;
import com.example.model.Employee;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
    private EmployeeRepository employeeRepository;

//    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
//        this.employeeRepository = employeeRepository;
//    }

    @Override
    public Employee saveEmployee(Employee employee) {

        Optional<Employee> savedEmployee = employeeRepository.findByEmail(employee.getEmail());
        if(savedEmployee.isPresent()){
            throw new ServiceException(HttpStatus.BAD_REQUEST+"Duplicado");

        }
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(long id) {
    	Optional<Employee> getEmployee = employeeRepository.findById(id);
    	 if(getEmployee.isEmpty()){
             throw new ServiceException(HttpStatus.BAD_REQUEST+"Id No encontrado");
         }
        return employeeRepository.findById(id);
    }

    @Override
    public Employee updateEmployee(Employee updatedEmployee) {
    	Optional<Employee> editEmployee = employeeRepository.findById(updatedEmployee.getId());
    	if(editEmployee.isEmpty()){
            throw new ServiceException(HttpStatus.BAD_REQUEST+"Id No encontrado para Actualizar");
        }
        return employeeRepository.save(updatedEmployee);
    }

    @Override
    public void deleteEmployee(long id) {
    	Optional<Employee> getEmployee = employeeRepository.findById(id);
   	 	if(getEmployee.isEmpty()){
            throw new ServiceException(HttpStatus.BAD_REQUEST+"Id No encontrado para Eliminar");
        }
        employeeRepository.deleteById(id);
    }
}