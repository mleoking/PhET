package edu.colorado.phet.buildtools.reports;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.*;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.java.JavaSimulationProject;
import edu.colorado.phet.common.phetcommon.resources.DummyConstantStringTester;

/**
 * Created by: Sam
 * May 13, 2008 at 9:02:34 PM
 */
public class CaptureScreenshot {
    private static File parentDir = new File( "C:/Users/Sam/Desktop/sim-out-test-ar/" );

    public static void main( String[] args ) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException, AWTException {
        final String project = args[0];
        final String sim = args[1];

        PhetProject phetProject = new JavaSimulationProject( new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations" ), project );
        final Simulation flavor = phetProject.getSimulation( sim );

//        DummyConstantStringTester.setTestScenario( new Locale( "ja" ), "\u30A8\u30CD\u30EB\u30AE\u30FC\u306E\u6642\u9593\u5909\u5316" );
        DummyConstantStringTester.setTestScenario( new Locale( "ar" ), "\u0627\u0646\u062A\u0632\u0639" );


        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                try {
                    Class c = Class.forName( flavor.getMainclass() );
                    Method m = c.getMethod( "main", new Class[]{new String[0].getClass()} );
                    m.invoke( null, new Object[]{new String[0]} );
                }
                catch( ClassNotFoundException e ) {
                    e.printStackTrace();
                }
                catch( InvocationTargetException e ) {
                    e.printStackTrace();
                }
                catch( NoSuchMethodException e ) {
                    e.printStackTrace();
                }
                catch( IllegalAccessException e ) {
                    e.printStackTrace();
                }

            }
        } );


        new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    try {
                        Thread.sleep( 50 );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    if ( isApplicationStarted() ) {
                        try {
                            Thread.sleep( 1000 );//wait for window to become opaque
                        }
                        catch( InterruptedException e ) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                Frame[] allFrames = Frame.getFrames();
                if ( allFrames.length == 0 ) {
                    new Exception( "No frames found for " + project + ":" + sim ).printStackTrace();
                }
                Robot robot;
                try {
                    robot = new Robot();
                    for ( int i = 0; i < allFrames.length; i++ ) {
                        Frame allFrame = allFrames[i];
                        if ( allFrame.isShowing() ) {
                            System.out.println( "i=" + i + ", allFrame.getBounds() = " + allFrame.getBounds() );
                            BufferedImage im = robot.createScreenCapture( allFrame.getBounds() );
                            File output = new File( parentDir, project + "_" + sim + "_frame_" + i + ".png" );
                            output.getParentFile().mkdirs();
                            ImageIO.write( im, "PNG", output );

                        }
                    }
                }
                catch( AWTException e ) {
                    e.printStackTrace();
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
                System.exit( 0 );
            }
        } ).start();

    }

    private static boolean isApplicationStarted() {
        Frame[] allFrames = Frame.getFrames();
        for ( int i = 0; i < allFrames.length; i++ ) {
            Frame frame = allFrames[i];
            if ( frame instanceof JFrame && frame.isShowing() ) {
                return true;
            }
        }
        return false;
    }
}
