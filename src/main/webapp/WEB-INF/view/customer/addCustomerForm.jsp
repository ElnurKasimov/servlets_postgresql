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
            <form action="/customer/add" method="post">
                <label for="customerName"> Customer name: </label>
                <input type="text" id="customerName" name="customerName"><br>
                <label for="reputation"> Reputation (insolvent, trustworthy, respectable) : </label>
                 <input type="text" id="reputation" name="reputation"><br>
                <button type="submit">Save</button>
            </form>
    </body>
</html>