<%@ page import="java.util.List" %>
<%@ page import="model.Employee" %>

<html>

<body>

<h1>Liste des Employes</h1>


<%
    List<Employee> employees = (List<Employee>) request.getAttribute("employees");


    for(Employee emp : employees){
%>

    <p><%= emp.getNom() %></p>


<%
    }
%>


</body>

</html>