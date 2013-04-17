import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Runtime.getRuntime;

/**
 * Created by IntelliJ IDEA.
 * User: jon
 * Date: Mar 24, 2009
 * Time: 12:32:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptRunner {
    public static void main( String[] args ) {

        if ( args.length == 0 ) {
            System.out.println( "WARNING: need to specify a scriptk" );
        }

        try {
            File script = new File( args[0] );

            System.out.println( "Running: " + script.getCanonicalPath() );

            Process p = getRuntime().exec( script.getCanonicalPath() );

            String line;
            BufferedReader input = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
            while ( ( line = input.readLine() ) != null ) {
                System.out.println( line );
            }
            input.close();

            p.waitFor();

        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }

    }
}
