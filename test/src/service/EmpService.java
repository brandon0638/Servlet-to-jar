package service;


import model.Employee;
import repository.EmpRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EmpService {


    @Autowired
    private EmpRepository repository;



    public List<Employee> getEmployees() throws Exception {


        return repository.findAll();

    }

}