<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
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
        <c:import url="${contextPath}/WEB-INF/view/navigation.jsp"/>
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