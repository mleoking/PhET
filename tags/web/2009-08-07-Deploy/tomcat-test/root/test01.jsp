<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Properties" %>
<%--
  Created by IntelliJ IDEA.
  User: jon
  Date: Jun 18, 2009
  Time: 12:21:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Simple jsp page</title></head>
<body>
<%
    Properties properties = System.getProperties();
    Enumeration en = properties.propertyNames();
    while ( en.hasMoreElements() ) {
        String key = (String) en.nextElement();
        String value = properties.getProperty( key );
%>
<%=key%> = <%=value%><br>
<%
    }
%>
</body>
</html>