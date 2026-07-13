package repository;

import model.Employee;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class EmpRepository {



    public List<Employee> findAll() throws Exception {


        List<Employee> employees = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        
        String sql = "SELECT id, nom FROM employee";
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        while(rs.next()){


            Employee emp = new Employee();


            emp.setId(rs.getInt("id"));
            emp.setNom(rs.getString("nom"));
            employees.add(emp);

        }

        rs.close();
        ps.close();
        conn.close();

        return employees;

    }

}