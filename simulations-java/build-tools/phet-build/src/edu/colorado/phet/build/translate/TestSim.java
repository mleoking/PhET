package edu.colorado.phet.build.translate;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import javax.imageio.ImageIO;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.PhetProjectFlavor;
import edu.colorado.phet.common.phetcommon.resources.DummyConstantStringTester;

/**
 * Created by: Sam
 * May 13, 2008 at 9:02:34 PM
 */
public class TestSim {
    public static void main( String[] args ) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException, AWTException {
        String project = args[0];
        String sim = args[1];

        PhetProject phetProject = new PhetProject( new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations" ), project );
        PhetProjectFlavor flavor = phetProject.getFlavor( sim );

        DummyConstantStringTester.setConstantTestString( "\u30A8\u30CD\u30EB\u30AE\u30FC\u306E\u6642\u9593\u5909\u5316" );
        Locale.setDefault( new Locale( "ja" ) );

        Class c = Class.forName( flavor.getMainclass() );
        Method m = c.getMethod( "main", new Class[]{new String[0].getClass()} );
        m.invoke( null, new Object[]{new String[0]} );

        Thread.sleep( 5000 );//todo: sleep until the main frame is available
        Frame[] allFrames = Frame.getFrames();
        Robot robot = new Robot();
        for ( int i = 0; i < allFrames.length; i++ ) {
            Frame allFrame = allFrames[i];
            if ( allFrame.isShowing() ) {
                System.out.println( "i=" + i + ", allFrame.getBounds() = " + allFrame.getBounds() );
                BufferedImage im = robot.createScreenCapture( allFrame.getBounds() );
                File output = new File( "C:/Users/Sam/Desktop/sim-out-test-1/" + project + "_" + sim + "_frame_" + i + ".png" );
                output.getParentFile().mkdirs();
                ImageIO.write( im, "PNG", output );
            }
        }
        System.exit( 0 );
    }
}
