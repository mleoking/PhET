package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

//Copied from http://code.google.com/p/jmonkeyengine/source/browse/trunk/engine/src/test/jme3test/awt/TestCanvas.java
public class TestSwingCanvas {

    private static JmeCanvasContext context;
    private static Canvas canvas;
    private static Application app;
    private static JFrame frame;

    private static void createFrame() {
        frame = new PhetFrame( new PhetApplication( new PhetApplicationConfig( new String[0], "molecule-shapes" ) ) );
        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosed( WindowEvent e ) {
                app.stop();
            }
        } );
    }

    public static void createCanvas() {
        AppSettings settings = new AppSettings( true );
        settings.setWidth( Math.max( 640, frame.getContentPane().getWidth() ) );
        settings.setHeight( Math.max( 480, frame.getContentPane().getHeight() ) );

        app = new ShowMolecule();

        app.setPauseOnLostFocus( false );
        app.setSettings( settings );
        app.createCanvas();

        context = (JmeCanvasContext) app.getContext();
        canvas = context.getCanvas();
        canvas.setSize( settings.getWidth(), settings.getHeight() );
    }

    public static void startApp() {
        app.startCanvas();
        app.enqueue( new Callable<Void>() {
            public Void call() {
                if ( app instanceof SimpleApplication ) {
                    SimpleApplication simpleApp = (SimpleApplication) app;
                    simpleApp.getFlyByCamera().setDragToRotate( true );
                }
                return null;
            }
        } );

    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JPopupMenu.setDefaultLightWeightPopupEnabled( false );

                createFrame();
                createCanvas();

                frame.getContentPane().add( canvas );
                frame.pack();
                startApp();
                frame.setLocationRelativeTo( null );
                frame.setVisible( true );
            }
        } );
    }
}