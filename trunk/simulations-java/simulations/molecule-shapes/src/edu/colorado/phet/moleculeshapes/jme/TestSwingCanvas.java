package edu.colorado.phet.moleculeshapes.jme;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

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
        Logger.getLogger( "de.lessvoid" ).setLevel( Level.SEVERE );
        Logger.getLogger( "com.jme3" ).setLevel( Level.SEVERE );
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
            settings.setFrameRate( 60 );

            final MoleculeApplication app = new MoleculeApplication();

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

            setClockControlPanel( null );

            setControlPanel( new JPanel() {{
                JComponent parent = this;
                setLayout( new BoxLayout( parent, BoxLayout.Y_AXIS ) );
                add( new JButton( "(Test) Add Atom" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            app.enqueue( new Callable<Object>() {
                                public Object call() throws Exception {
                                    app.testAddAtom( false );
                                    return null;
                                }
                            } );
                        }
                    } );
                }} );
                add( new JButton( "(Test) Add Lone Pair" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            app.enqueue( new Callable<Object>() {
                                public Object call() throws Exception {
                                    app.testAddAtom( true );
                                    return null;
                                }
                            } );
                        }
                    } );
                }} );
                add( new JButton( "(Test) Remove Random" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            app.enqueue( new Callable<Object>() {
                                public Object call() throws Exception {
                                    app.testRemoveAtom();
                                    return null;
                                }
                            } );
                        }
                    } );
                }} );

                class VSEPRButton extends JButton {
                    VSEPRButton( String nickname, final int X, final int E ) {
                        super( nickname + " AX" + X + "E" + E );
                        addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                app.setState( X, E );
                            }
                        } );
                    }
                }

                add( new VSEPRButton( "Linear", 2, 3 ) );
                add( new VSEPRButton( "Trigonal pyramidal", 3, 1 ) );
                add( new VSEPRButton( "T-shaped", 3, 2 ) );
                add( new VSEPRButton( "Seesaw", 4, 1 ) );
                add( new VSEPRButton( "Square Planar", 4, 2 ) );
                add( new VSEPRButton( "Square Pyramidal", 5, 1 ) );
                add( new VSEPRButton( "Pentagonal pyramidal", 6, 1 ) );
                add( new ResetAllButton( parent ) );
            }} );

            setSimulationPanel( new JPanel( new BorderLayout() ) {{
                add( canvas, BorderLayout.CENTER );
                setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
            }} );

        }
    }
}