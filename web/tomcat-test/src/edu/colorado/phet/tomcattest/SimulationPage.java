package edu.colorado.phet.tomcattest;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SimulationPage extends HttpServlet {
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        response.setContentType( "text/html" );
        PrintWriter out = response.getWriter();

        out.println( request.getParameter( "projectName" ) );
        out.println( request.getParameter( "simulationName" ) );
    }
}
