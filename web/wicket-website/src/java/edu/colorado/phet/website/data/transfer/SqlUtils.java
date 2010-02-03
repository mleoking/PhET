package edu.colorado.phet.website.data.transfer;

import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletContext;

public class SqlUtils {

    private static Connection getConnection( ServletContext context ) throws IOException, SQLException, ClassNotFoundException {
        String user = context.getInitParameter( "transfer-user" );
        String pass = context.getInitParameter( "transfer-pass" );
        String host = context.getInitParameter( "transfer-host" );
        String name = context.getInitParameter( "transfer-name" );

        Class.forName( "com.mysql.jdbc.Driver" );
        String url = "jdbc:mysql://" + host + ":3306/" + name;
        Connection con = DriverManager.getConnection( url, user, pass );
        return con;
    }

    public static boolean wrapTransaction( ServletContext context, String query, SqlResultTask task ) {
        boolean success = true;
        try {
            Connection con = getConnection( context );
            PreparedStatement stmt = con.prepareStatement( query );
            ResultSet resultSet = stmt.executeQuery();
            while ( resultSet.next() ) {
                success = task.process( resultSet );
                if ( !success ) {
                    return success;
                }
            }
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
            success = false;
        }
        catch( IOException e ) {
            e.printStackTrace();
            success = false;
        }
        catch( SQLException e ) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

}
