package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;
import java.io.File;
import java.util.concurrent.Callable;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

import com.jme3.app.Application;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.Natives;

//Copied from http://code.google.com/p/jmonkeyengine/source/browse/trunk/engine/src/test/jme3test/awt/TestCanvas.java
public class TestSwingCanvas extends PiccoloPhetApplication {

    public TestSwingCanvas( PhetApplicationConfig config ) {
        super( config );
        addModule( new JMEModule( "Module 1" ) );
        addModule( new JMEModule( "Module 2" ) );
    }

    public static void main( String[] args ) {
        final File tempDir = new File( System.getProperty( "system.io.tmpdir" ), "phet-" + System.currentTimeMillis() );
        tempDir.mkdirs();
        final String path = tempDir.getAbsolutePath();
        System.out.println( "Extracting native JME3 libraries to: " + path );
        Natives.setExtractionDir( path );
        tempDir.deleteOnExit();

        //TODO: check write permissions on the temp folder.  If no permission, search out another place or inform the user
        new PhetApplicationLauncher().launchSim( args, "molecule-shapes", TestSwingCanvas.class );
    }

    private class JMEModule extends Module {
        public JMEModule( String name ) {
            super( name, new ConstantDtClock( 30.0 ) );
            AppSettings settings = new AppSettings( true );

            final Application app = new MoleculeApplication();

            //Improve default camera angle and mouse behavior
            app.enqueue( new Callable<Void>() {
                public Void call() {
                    BaseApplication simpleApp = (BaseApplication) app;
                    //simpleApp.getFlyByCamera().setDragToRotate( true );
                    return null;
                }
            } );

            app.setPauseOnLostFocus( false );
            app.setSettings( settings );
            app.createCanvas();

            JmeCanvasContext context = (JmeCanvasContext) app.getContext();
            final Canvas canvas = context.getCanvas();
            canvas.setSize( settings.getWidth(), settings.getHeight() );
            addListener( new Listener() {
                public void activated() {
                    app.startCanvas();
                }

                public void deactivated() {
                }
            } );

            setControlPanel( new VerticalLayoutPanel() {{
                JComponent parent = this;
                add( new ResetAllButton( parent ) );
            }} );

            setSimulationPanel( new JPanel( new BorderLayout() ) {{
                add( canvas, BorderLayout.CENTER );
                setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
            }} );

        }
    }
}