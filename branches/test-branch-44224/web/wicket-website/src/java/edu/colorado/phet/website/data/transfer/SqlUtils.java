package edu.colorado.phet.website.data.transfer;

import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class SqlUtils {

    private static final Logger logger = Logger.getLogger( SqlUtils.class.getName() );

    private static Connection getConnection( ServletContext context ) throws IOException, SQLException, ClassNotFoundException {
        String user = context.getInitParameter( "transfer-user" );
        String pass = context.getInitParameter( "transfer-pass" );
        String host = context.getInitParameter( "transfer-host" );
        String name = context.getInitParameter( "transfer-name" );

        Class.forName( "com.mysql.jdbc.Driver" );
        String url = "jdbc:mysql://" + host + ":3306/" + name + "?zeroDateTimeBehavior=convertToNull"; // weird null behaviors
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
            logger.warn( "sqlutils", e );
            success = false;
        }
        catch( IOException e ) {
            e.printStackTrace();
            logger.warn( "sqlutils", e );
            success = false;
        }
        catch( SQLException e ) {
            e.printStackTrace();
            logger.warn( "sqlutils", e );
            success = false;
        }
        return success;
    }

}
