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
        <form action="/project/project_expenses" method ="get">
                        <label for="projectName"> projectName: </label>
                        <input type="text" id="projectName" name="projectName"><br>
                        <button type="submit">Find</button>
        </form>
        <c:out value="${result}"/>
         <c:if test="${expenses != 0}">
             <c:out value="${expenses}."/><br>
         </c:if>
    </body>
</html>