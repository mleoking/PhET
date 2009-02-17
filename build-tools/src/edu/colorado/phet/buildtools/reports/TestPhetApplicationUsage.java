package edu.colorado.phet.buildtools.reports;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;

/**
 * Created by: Sam
 * May 13, 2008 at 9:02:34 PM
 */
public class TestPhetApplicationUsage {
    public static void main( String[] args ) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException, AWTException {
        final String project = args[0];
        final String sim = args[1];

        final PhetProject phetProject = new JavaSimulationProject( new File( "C:\\reid-not-backed-up\\phet\\svn\\trunk2\\simulations-java\\simulations" ), project );
        final Simulation flavor = phetProject.getSimulation( sim );

        Class c = Class.forName( flavor.getMainclass() );
        Method m = c.getMethod( "main", new Class[]{new String[0].getClass()} );
        m.invoke( null, new Object[]{new String[0]} );

        new Thread( new Runnable() {
            public void run() {
                try {
                    Thread.sleep( 5000 );//TODO: sleep until the main frame is available
                    PhetApplication app = PhetApplication.instance();
                    System.out.println( "count = " + app );
                    log( "project=" + phetProject.getName() + ", sim=" + flavor.getName() + ", phetAppCount=" + app + "\n" );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                System.exit( 0 );
            }
        } ).start();

    }

    public static void log( String str ) {
        try {
            File file = new File( "C:/users/sam/desktop/simlog.txt" );
            if ( !file.exists() ) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter( file, true );
            fileWriter.write( str + "\n" );
            fileWriter.flush();
        }
        catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }
}