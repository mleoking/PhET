/** Sam Reid*/
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.forces1d.common.ColorDialog;
import edu.colorado.phet.forces1d.common.PhetLookAndFeel;
import edu.colorado.phet.forces1d.common.plotdevice.DefaultPlaybackPanel;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.model.Force1dObject;
import edu.colorado.phet.forces1d.view.Force1DPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    protected Force1DPanel forcePanel;
    private Force1dControlPanel forceControlPanel;
    private AbstractClock clock;
    private Force1dObject[] imageElements;
    private static boolean readyToRender = false;
    private DefaultPlaybackPanel playbackPanel;
    private PhetFrame phetFrame;

    public Force1DModule( AbstractClock clock ) throws IOException {
        this( clock, "Advanced Controls" );
    }

    public Force1DModule( AbstractClock clock, String name ) throws IOException {
        super( name );
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
                updateGraphics();
//                forcePanel.paint();
            }
        };

        addModelElement( updateGraphics );
        playbackPanel = new DefaultPlaybackPanel( getForceModel().getPlotDeviceModel() );

    }

    protected void updateGraphics() {
        forcePanel.updateGraphics();
        forceControlPanel.updateGraphics();
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
            forcePanel.layoutPlots();//TODO this looks wrong.
            forcePanel.invalidate();
            forcePanel.repaint();
        }
    }

    public static void main( String[] args ) throws UnsupportedLookAndFeelException, IOException, InterruptedException, InvocationTargetException {
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        UIManager.setLookAndFeel( new PhetLookAndFeel() );
        final Force1DModule module = new Force1DModule( clock );
        module.getApparatusPanel().getGraphic().setVisible( false );
        FrameSetup frameSetup = ( new FrameSetup.CenteredWithInsets( 200, 200 ) );
        final Force1DModule simpleModule = new SimpleForceModule( clock );
        Module[] m = new Module[]{simpleModule, module};

        ApplicationModel model = new ApplicationModel( "Forces 1D", "Force1d applet", "1.0Alpha",
                                                       frameSetup, m, clock );
        final PhetApplication phetApplication = new PhetApplication( model );

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
                    setup( simpleModule );
                    readyToRender = false;
                }
            }
        } );
        JMenu options = new JMenu( "Options" );
        JMenuItem item = new JMenuItem( "Chart Background Color" );
        item.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.showColorDialog();
            }
        } );
        options.add( item );

        phetApplication.getPhetFrame().addMenu( options );
//        PersistenceUtil.addMenuItems( phetApplication );
        phetApplication.startApplication();

        new FrameSetup.MaxExtent().initialize( phetApplication.getPhetFrame() );
        simpleModule.setPhetFrame( phetApplication.getPhetFrame() );
        module.setPhetFrame( phetApplication.getPhetFrame() );
    }

    private void setPhetFrame( PhetFrame phetFrame ) {
        this.phetFrame = phetFrame;
    }

    private void showColorDialog() {
        ColorDialog.showDialog( "Chart Color", getApparatusPanel(), Color.yellow, new ColorDialog.Listener() {
            public void colorChanged( Color color ) {
                setChartBackground( color );
            }

            public void cancelled( Color orig ) {
                setChartBackground( orig );
            }

            public void ok( Color color ) {
                setChartBackground( color );
            }
        } );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );

        app.getPhetFrame().getBasicPhetPanel().setAppControlPanel( playbackPanel );
    }

    private void setChartBackground( Color color ) {
        forcePanel.setChartBackground( color );
    }

    private static void setup( Force1DModule module ) {
        final Force1DPanel p = module.getForcePanel();
//        p.setRenderingSize( 3000,3000);
        p.setReferenceSize();
        p.forceLayout( p.getWidth(), p.getHeight() );

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

    public PhetFrame getPhetFrame() {
        return phetFrame;
    }
}
