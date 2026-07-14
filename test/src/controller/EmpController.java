package controller;

import com.annotation.Controllerako;
import com.annotation.UrlMapping;
import com.model.ModelAndView;

import model.Employee;
import service.EmpService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Controllerako
@Component
public class EmpController{

    @Autowired
    private EmpService service;

    @UrlMapping(value="/", method="GET")
    public ModelAndView list() throws Exception {
        
        List<Employee> employees = service.getEmployees();
        ModelAndView mv = new ModelAndView("emp");
        mv.addAttribute("employees",employees);
        return mv;

    }
    
    
    // @UrlMapping(method="GET", value="/emp/list")
    // public void list(){
    //     System.out.println("Voici une liste de langages: ");
    //     System.out.println("1) Java");
    //     System.out.println("2) Python");
    //     System.out.println("3) SQL");
    //     System.out.println("4) C++");

    // }

    // @UrlMapping(method="POST", value="/emp/new")
    // public void create(){

    // }

    // @UrlMapping("/emp/delete")
    // public String delete(){
    //     System.out.println("Je te supprime");
    //     return "Je te supprime";

    // }
    // // @UrlMapping("/emp/delete")
    // // public void supprimer(){

    // // }
    
    

    
}