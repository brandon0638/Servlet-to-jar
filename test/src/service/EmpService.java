package service;

import model.Employee;
import repository.EmpRepository;

import java.util.List;


public class EmpService {


    private EmpRepository repository = new EmpRepository();



    public List<Employee> getEmployees() throws Exception {

        return repository.findAll();

    }

}