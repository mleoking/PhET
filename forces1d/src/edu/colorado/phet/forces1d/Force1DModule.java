/** Sam Reid*/
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.forces1d.common.PhetLookAndFeel;
import edu.colorado.phet.forces1d.common.plotdevice.DefaultPlaybackPanel;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.view.Force1DPanel;
import edu.colorado.phet.forces1d.view.Force1dObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:06:43 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Force1DModule extends Module {
    private Force1DModel forceModel;
    private Force1DPanel forcePanel;
    private Force1dControlPanel forceControlPanel;
    private AbstractClock clock;
    private Force1dObject[] imageElements;
    private static boolean readyToRender = false;

    public Force1DModule( AbstractClock clock ) throws IOException {
        super( "Force1D" );
        this.clock = clock;

        forceModel = new Force1DModel( this );
        setModel( new BaseModel() );
        imageElements = new Force1dObject[]{
            new Force1dObject( "images/cabinet.gif", "File Cabinet", 0.8, 200, 0.3, 0.2 ),
            new Force1dObject( "images/fridge.gif", "Refrigerator", 0.35, 400, 0.7, 0.5 ),
            new Force1dObject( "images/phetbook.gif", "Textbook", 0.8, 10, 0.3, 0.25 ),
            new Force1dObject( "images/crate.gif", "Crate", 0.8, 300, 0.2, 0.2 ),
            new Force1dObject( "images/ollie.gif", "Sleepy Dog", 0.5, 25, 0.1, 0.1 ),
        };


        forcePanel = new Force1DPanel( this );
        forcePanel.addRepaintDebugGraphic( clock );
        setApparatusPanel( forcePanel );


        forceControlPanel = new Force1dControlPanel( this );
        setControlPanel( forceControlPanel );
        addModelElement( forceModel );

        ModelElement updateGraphics = new ModelElement() {
            public void stepInTime( double dt ) {
                forcePanel.updateGraphics();
            }
        };

        addModelElement( updateGraphics );
    }

    public void setHelpEnabled( boolean h ) {
        super.setHelpEnabled( h );
        forcePanel.setHelpEnabled( h );
    }

    public Force1DModel getForceModel() {
        return forceModel;
    }

    public Force1DPanel getForcePanel() {
        return forcePanel;
    }

    public void reset() {
        forceModel.reset();
        forcePanel.reset();
    }

    public void cursorMovedToTime( double modelX, int index ) {
        forcePanel.cursorMovedToTime( modelX, index );
    }

    public void relayoutPlots() {
        if( forcePanel != null ) {
            forcePanel.layoutPlots();
            forcePanel.invalidate();
            forcePanel.repaint();
        }
    }

    public static void main( String[] args ) throws UnsupportedLookAndFeelException, IOException, InterruptedException, InvocationTargetException {
        UIManager.setLookAndFeel( new PhetLookAndFeel() );

        AbstractClock clock = new SwingTimerClock( 1, 30 );
        final Force1DModule module = new Force1DModule( clock );

        module.getApparatusPanel().getGraphic().setVisible( false );
//        FrameSetup frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 200, 200 ) );
        FrameSetup frameSetup = ( new FrameSetup.CenteredWithInsets( 200, 200 ) );
        ApplicationModel model = new ApplicationModel( "Forces 1D", "Force1d applet", "1.0Alpha",
                                                       frameSetup, module, clock );
        final PhetApplication phetApplication = new PhetApplication( model );
        phetApplication.getPhetFrame().getBasicPhetPanel().setAppControlPanel( new DefaultPlaybackPanel( module.getForceModel().getPlotDeviceModel() ) );
        phetApplication.getPhetFrame().addWindowStateListener( new WindowStateListener() {
            public void windowStateChanged( WindowEvent e ) {
                int oldState = e.getOldState();
                int newState = e.getNewState();
                if( ( oldState & Frame.MAXIMIZED_BOTH ) == 0 &&
                        ( newState & Frame.MAXIMIZED_BOTH ) != 0 ) {
                    readyToRender = true;
                }

            }
        } );
        module.getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                if( readyToRender ) {
                    setup( module );
                    readyToRender = false;
                }
            }
        } );
        phetApplication.startApplication();
        new FrameSetup.MaxExtent().initialize( phetApplication.getPhetFrame() );
        Thread.sleep( 1000 );
        phetApplication.getPhetFrame().invalidate();
        phetApplication.getPhetFrame().validate();
        phetApplication.getPhetFrame().repaint();

        phetApplication.getPhetFrame().getContentPane().invalidate();
        phetApplication.getPhetFrame().getContentPane().validate();
        phetApplication.getPhetFrame().getContentPane().repaint();
        //Note, this rendering strategy depends on the order of operations: PhetFrame goes to max size, then apparatusPanel gets resized.
    }

    private static void setup( Force1DModule module ) {
        final Force1DPanel p = module.getForcePanel();
        p.resetRenderingSize();
        p.forceLayout();

        module.getApparatusPanel().getGraphic().setVisible( true );
        p.paintImmediately( 0, 0, p.getWidth(), p.getHeight() );
    }

    public AbstractClock getClock() {
        return clock;
    }

    public Force1dObject imageElementAt( int i ) {
        return imageElements[i];
    }

    public Force1dObject[] getImageElements() {
        return imageElements;
    }


}
