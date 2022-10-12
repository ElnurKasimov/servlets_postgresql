<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
        <style>
        table {
          font-family: arial, sans-serif;
          border-collapse: collapse;
          width: 50%;
        }
        td, th {
          border: 2px solid #dddddd;
          text-align: center;
          padding: 3px;
        }
        tr:nth-child(even) {
          background-color: #dddddd;
        }
        </style>
    </head>
    <body>
        <nav class="navbar navbar-inverse">
          <div class="container-fluid">
            <div class="navbar-header">
              <a class="navbar-brand" href="#">IT market</a>
            </div>
            <ul class="nav navbar-nav">
              <li class="active"><a href="/">Home</a></li>
              <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Developers <span class="caret"></span></a>
                <ul class="dropdown-menu">
                  <li><a href="/developer/all_developers">List of  all developers</a></li>
                  <li><a href="/developer/developer_info">Full information about a developer</a></li>
                  <li><a href="/developer/java_developers">Quantity Java-developers</a></li>
                  <li><a href="/developer/middle_developers">List of middle developers</a></li>
                  <li><a href="/developer/add">Add developer</a></li>
                  <li><a href="/developer/update">Update developer</a></li>
                  <li><a href="/developer/delete">Delete developer</a></li>
                </ul>
              </li>
              <li  class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Projects <span class="caret"></span></a>
                 <ul class="dropdown-menu">
                    <li><a href="/project/all_projects">List of all projects</a></li>
                    <li><a href="/project/project_info">Full information about a project</a></li>
                    <li><a href="/project/project_developers">List project developers</a></li>
                    <li><a href="/project/project_budget">Project budget</a></li>
                    <li><a href="/project/all_projects_in_special_format">List of projects in special format</a></li>
                    <li><a href="/project/add">Add a project</a></li>
                    <li><a href="/project/update">Update a project</a></li>
                    <li><a href="/project/delete">Delete a project</a></li>
                  </ul>
               </li>
              <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Companies<span class="caret"></span></a>
                  <ul class="dropdown-menu">
                    <li><a href="/company/list_all_companies">List of all companies</a></li>
                    <li><a href="/company/add">Add a company</a></li>
                    <li><a href="/company/update">Update a company</a></li>
                    <li><a href="/company/delete">Delete a company</a></li>
                  </ul>
              </li>
              <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Customers<span class="caret"></span></a>
                   <ul class="dropdown-menu">
                     <li><a href="/customer/all_customers">List of all customers</a></li>
                     <li><a href="/customer/add">Add a customer</a></li>
                     <li><a href="/customer/update">Update a customer</a></li>
                     <li><a href="/customer/delete">Delete a customer</a></li>
                   </ul>
              </li>
            </ul>
          </div>
        </nav>
        <table>
             <thead>
                 <c:if test="${not empty customers}">
                     <tr>
                         <td>Customer id</td>
                         <td>Customer name</td>
                         <td>Reputation</td>
                         </tr>
                 </c:if>
                 <c:if test="${empty customers}">
                     <p>There is no any customer  in the database</p>
                 </c:if>
             </thead>
             <tbody>
                 <c:forEach var = "customer" items="${customers}">
                     <tr>
                         <td>
                             <c:out value="${customer.customer_id}"/>
                         </td>
                         <td>
                             <c:out value="${customer.customer_name}"/>
                         </td>
                         <td>
                             <c:out value="${customer.reputation}"/>
                         </td>
                     </tr>
                 </c:forEach>
             </tbody>
        </table>
    </body>
</html>