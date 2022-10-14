<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
    </head>
    <body>
       <c:import url="${contextPath}/WEB-INF/view/navigation.jsp"/>
        <form action="/developer/developer_info">
            <label for="lastName"> Last name: </label><br>
            <input type="text" id="lastName" name="lastName"><br>
            <label for="firstName"> First name: </label><br>
            <input type="text" id="firstName" name="firstName"><br>
            <button type="submit">Find</button>
        </form>

        <c:choose>
          <c:when test="${a boolean expr}">
            do something
          </c:when>
          <c:otherwise>
            <p>There is not developer by specified name. Please enter correct data.</p>
          </c:otherwise>
        </c:choose>

    </body>
</html>