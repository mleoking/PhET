package edu.colorado.phet.forces1d;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.forces1d.common.ColorDialog;
import edu.colorado.phet.forces1d.common.plotdevice.DefaultPlaybackPanel;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.model.Force1dObject;
import edu.colorado.phet.forces1d.phetcommon.application.Module;
import edu.colorado.phet.forces1d.phetcommon.application.PhetApplication;
import edu.colorado.phet.forces1d.phetcommon.model.BaseModel;
import edu.colorado.phet.forces1d.phetcommon.model.clock.AbstractClock;
import edu.colorado.phet.forces1d.phetcommon.model.clock.ClockTickEvent;
import edu.colorado.phet.forces1d.phetcommon.util.QuickTimer;
import edu.colorado.phet.forces1d.phetcommon.view.PhetFrame;
import edu.colorado.phet.forces1d.view.Force1DLookAndFeel;
import edu.colorado.phet.forces1d.view.Force1DPanel;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:06:43 PM
 */
public class Forces1DModule extends Module {
    public static final String LOCALIZATION_BUNDLE_BASENAME = "forces-1d/localization/forces-1d-strings";
    private Color backgroundColor;
    private Force1DModel forceModel;
    private Force1DPanel forcePanel;
    private Forces1DControlPanel simpleControlPanel;
    private Force1dObject[] imageElements;
    private DefaultPlaybackPanel playbackPanel;
    private PhetFrame phetFrame;
    private Force1DLookAndFeel force1DLookAndFeel = new Force1DLookAndFeel();
    private int objectIndex;
    private IForceControl currentControlPanel;

    public Forces1DModule( AbstractClock clock, Color backgroundColor ) throws IOException {
        this( clock, Force1DResources.get( "Force1DModule.moduleName" ), backgroundColor );
    }

    public Forces1DModule( AbstractClock clock, String name, Color backgroundColor ) throws IOException {
        super( name, clock );
        this.backgroundColor = backgroundColor;

        forceModel = new Force1DModel( this );
        setModel( new BaseModel() );
        imageElements = new Force1dObject[]{
                new Force1dObject( "forces-1d/images/cabinet.gif", Force1DResources.get( "Force1DModule.fileCabinet" ), 0.8, 200, 0.3, 0.2 ),
                new Force1dObject( "forces-1d/images/fridge.gif", Force1DResources.get( "Force1DModule.refrigerator" ), 0.35, 400, 0.7, 0.5 ),
                new Force1dObject( "forces-1d/images/phetbook.gif", Force1DResources.get( "Force1DModule.textbook" ), 0.8, 10, 0.3, 0.25 ),
                new Force1dObject( "forces-1d/images/crate.gif", Force1DResources.get( "Force1DModule.crate" ), 0.8, 300, 0.2, 0.2 ),
                new Force1dObject( "forces-1d/images/ollie.gif", Force1DResources.get( "Force1DModule.sleepyDog" ), 0.5, 25, 0.1, 0.1 ),
        };
        forcePanel = new Force1DPanel( this );
        forcePanel.addRepaintDebugGraphic( clock );
        setApparatusPanel( forcePanel );

        simpleControlPanel = new Forces1DControlPanel( this );

        setControlPanel( simpleControlPanel );
        addModelElement( forceModel );

        playbackPanel = new DefaultPlaybackPanel( getForceModel().getPlotDeviceModel() );

        getForceModel().setBoundsWalled();

        CrashAudioPlayer crashAudioPlayer = new CrashAudioPlayer();
        getForceModel().addCollisionListener( crashAudioPlayer );
    }

    public void updateGraphics( ClockTickEvent event ) {
        super.updateGraphics( event );
        forcePanel.updateGraphics();
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
        simpleControlPanel.reset();
    }

    public void cursorMovedToTime( double modelX, int index ) {
        forcePanel.cursorMovedToTime( modelX, index );
    }

    public void relayoutPlots() {
        if ( forcePanel != null ) {
            forcePanel.layoutPlots();
        }
    }

    void setPhetFrame( PhetFrame phetFrame ) {
        this.phetFrame = phetFrame;
    }

    void showColorDialog() {
        String title = Force1DResources.get( "Force1DModule.chartcolor" );
        ColorDialog.showDialog( title, getApparatusPanel(), Color.yellow, new ColorDialog.Listener() {
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

    public static void setup( Forces1DModule module ) {
        final Force1DPanel p = module.getForcePanel();
        p.setReferenceSize();
        p.updateLayout( p.getWidth(), p.getHeight() );

        module.getApparatusPanel().getGraphic().setVisible( true );
        p.paintImmediately( 0, 0, p.getWidth(), p.getHeight() );
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

    public Force1DLookAndFeel getForce1DLookAndFeel() {
        return force1DLookAndFeel;
    }

    public void setObject( Force1dObject force1dObject ) {
        objectIndex = Arrays.asList( imageElements ).indexOf( force1dObject );
        try {
            getForcePanel().getBlockGraphic().setImage( force1dObject.getImage() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        forceModel.getBlock().setMass( force1dObject.getMass() );
        forceModel.getBlock().setStaticFriction( force1dObject.getStaticFriction() );
        forceModel.getBlock().setKineticFriction( force1dObject.getKineticFriction() );
    }

    public void clearData() {
        getForcePanel().clearData();
    }

    public void setFrictionEnabled( boolean useFriction ) {
        getForceModel().setFrictionEnabled( useFriction );
    }

    public void setImageIndex( int imageIndex ) {
        try {
            getForcePanel().getBlockGraphic().setImage( imageElements[imageIndex].getImage() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public int getImageIndex() {
        return objectIndex;
    }

    public void restoreDefaults() {
        setObject( imageElements[objectIndex] );
        getForceModel().setGravity( 9.8 );
    }

    public void clockTicked( ClockTickEvent event ) {
        QuickTimer totalTime = new QuickTimer();

        QuickTimer userInputTime = new QuickTimer();
        handleControlUserInputs();
        getApparatusPanel().handleUserInput();
        debug( "userInputTime = " + userInputTime );

        QuickTimer modelTime = new QuickTimer();
        getModel().clockTicked( event );
        debug( "modelTime = " + modelTime );

        QuickTimer updateControlPanelTime = new QuickTimer();
        updateControlPanelGraphics();
        debug( "updateControlPanelTime = " + updateControlPanelTime );

        QuickTimer updateGraphicsTime = new QuickTimer();
        updateGraphics( event );
        debug( "updateGraphicsTime = " + updateGraphicsTime );

        QuickTimer paintTime = new QuickTimer();
        getApparatusPanel().paint();
        debug( "paintTime = " + paintTime );

        debug( "totalTime = " + totalTime );
    }

    private void handleControlUserInputs() {
        if ( getActiveControlPanel() != null ) {
            getActiveControlPanel().handleUserInput();
        }
    }

    private void updateControlPanelGraphics() {
        if ( getActiveControlPanel() != null ) {
            getActiveControlPanel().updateGraphics();
        }
    }

    public static void debug( String str ) {
        boolean debug = false;
        if ( debug ) {
            System.out.println( "str = " + str );
        }
    }

    public IForceControl getActiveControlPanel() {
        return currentControlPanel;
    }

    public void setSimpleControlPanel() {
        setControlPanel( simpleControlPanel );
    }

    public void setControlPanel( IForceControl controlPanel ) {
        this.currentControlPanel = controlPanel;
        super.setControlPanel( controlPanel );
        if ( phetFrame != null ) {
            phetFrame.getBasicPhetPanel().setControlPanel( controlPanel );
            phetFrame.getBasicPhetPanel().invalidate();
            phetFrame.getBasicPhetPanel().validate();
            phetFrame.getBasicPhetPanel().doLayout();
        }
        Window window = SwingUtilities.getWindowAncestor( controlPanel );
        if ( window instanceof JFrame ) {
            JFrame frame = (JFrame) window;
            frame.invalidate();
            frame.doLayout();
        }
        simpleControlPanel.getFreeBodyDiagramSuite().controlsChanged();
    }

    public Paint getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isShowComponentForces() {
        return forcePanel.isShowComponentForces();
    }

    public void setShowComponentForces( boolean selected ) {
        forcePanel.setShowComponentForces( selected );
    }

    public boolean isShowTotalForce() {
        return forcePanel.isShowTotalForce();
    }

    public void setShowTotalForce( boolean showTotalForce ) {
        forcePanel.setShowTotalForce( showTotalForce );
    }
}
