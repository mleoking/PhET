package edu.colorado.phet.tomcattest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestHeaderExample extends HttpServlet {

    public void doGet( HttpServletRequest request, HttpServletResponse response )
            throws IOException, ServletException {
        response.setContentType( "text/html" );
        PrintWriter out = response.getWriter();
        Enumeration e = request.getHeaderNames();
        while ( e.hasMoreElements() ) {
            String name = (String) e.nextElement();
            String value = request.getHeader( name );
            out.println( name + " = " + value + "<br/>" );
        }
    }
}