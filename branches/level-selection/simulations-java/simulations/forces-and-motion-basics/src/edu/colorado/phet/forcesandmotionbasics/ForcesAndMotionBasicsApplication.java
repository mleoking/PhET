// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics;

import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Strings;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents;
import edu.colorado.phet.forcesandmotionbasics.motion.MotionModule;
import edu.colorado.phet.forcesandmotionbasics.touch.HomeScreen;
import edu.colorado.phet.forcesandmotionbasics.touch.ThumbnailTabPane;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarModule;

/**
 * PhET Application for "Forces and Motion: Basics", which has 3 tabs: "Tug of War", "Motion" and "Friction"
 *
 * @author Sam Reid
 */
public class ForcesAndMotionBasicsApplication extends PiccoloPhetApplication {

    public static final Color BROWN = new Color( 197, 154, 91 );
    public static final Color TOOLBOX_COLOR = new Color( 231, 232, 233 );
    private final Container modulePane;
    public static ForcesAndMotionBasicsApplication app;

    public ForcesAndMotionBasicsApplication( PhetApplicationConfig config, ITabbedModulePane tabbedModulePanePiccolo ) {
        super( config, tabbedModulePanePiccolo );

        JFrame frame = getPhetFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); // Already there
        frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
        frame.setUndecorated( true );

        addModule( new TugOfWarModule() );
        addModule( new MotionModule( UserComponents.motionTab, Strings.MOTION, false, false ) );
        addModule( new MotionModule( UserComponents.frictionTab, Strings.FRICTION, true, false ) );
        addModule( new MotionModule( UserComponents.accelerationLabTab, Strings.ACCELERATION_LAB, true, true ) );
        modulePane = getPhetFrame().getContentPane();

        getPhetFrame().setContentPane( new HomeScreen( this ) );
        getPhetFrame().setJMenuBar( null );
        app = this;

        //Exit on escape key
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher( new KeyEventDispatcher() {
            public boolean dispatchKeyEvent( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
                    System.exit( 0 );
                }
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        } );
        Runnable listener = new Runnable() {
            public void run() {
                getPhetFrame().setContentPane( new HomeScreen( ForcesAndMotionBasicsApplication.this ) );
                //http://stackoverflow.com/questions/6010915/change-contentpane-of-frame-after-button-clicked
                getPhetFrame().revalidate();
                getPhetFrame().repaint();
            }
        };
        PhetTabbedPane.homeButtonListener = listener;
        ThumbnailTabPane.homeButtonListener = listener;
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, ForcesAndMotionBasicsResources.PROJECT_NAME, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new ForcesAndMotionBasicsApplication( config, new TabbedModulePanePiccolo() );
            }
        } );
    }

    public void showModule( int i ) {
        setActiveModule( i );
        getPhetFrame().setContentPane( modulePane );

        //http://stackoverflow.com/questions/6010915/change-contentpane-of-frame-after-button-clicked
        getPhetFrame().revalidate();
        getPhetFrame().repaint();
    }

    public void showHomeScreenForSelection( int i ) {
        getPhetFrame().setContentPane( new HomeScreen( ForcesAndMotionBasicsApplication.this, i ) );
        //http://stackoverflow.com/questions/6010915/change-contentpane-of-frame-after-button-clicked
        getPhetFrame().revalidate();
        getPhetFrame().repaint();
    }
}