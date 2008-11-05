/*  */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.theramp.common.AudioSourceDataLinePlayer;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.model.RampObject;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.timeseries.TimeSeriesModel;
import edu.colorado.phet.theramp.timeseries.TimeSeriesPlaybackPanel;
import edu.colorado.phet.theramp.view.FireDog;
import edu.colorado.phet.theramp.view.RampPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 9:57:09 AM
 */

public class RampModule extends PiccoloModule {
    private RampPanel rampPanel;
    private RampModel rampModel;
    private RampControlPanel rampControlPanel;
    private RampObject[] rampObjects;
    private TimeSeriesPlaybackPanel rampMediaPanel;

    private ArrayList listeners = new ArrayList();

    public static final double FORCE_LENGTH_SCALE = 0.06;//1.0;
    private PhetFrame phetFrame;
    public static final int MAX_TIME = 30;
    //    public static final int MAX_TIME = 5;//testing only
    public static final boolean MINIMIZE_READOUT_TEXT_FOR_SMALL_SCREEN = false;
    private boolean firedogInProgress = false;
//    private Timer timer;

    public RampModule( PhetFrame frame, IClock clock ) {
        this( TheRampStrings.getString( "module.more-features" ), frame, clock );
    }

    public RampModule( String name, PhetFrame phetFrame, final IClock clock ) {
        super( name, clock );
        this.phetFrame = phetFrame;
        setModel( new BaseModel() );
        rampModel = new RampModel( this, clock );
        rampObjects = new RampObject[]{
                new RampObject( "the-ramp/images/cabinet.gif", TheRampStrings.getString( "object.file-cabinet" ), 0.8, 100, 0.3, 0.3, 0.4 ),
                new RampObject( "the-ramp/images/fridge.gif", TheRampStrings.getString( "object.refrigerator" ), 0.35, 175, 0.5, 0.5, 0.4 ),
                new RampObject( "the-ramp/images/piano.png", TheRampStrings.getString( "object.piano" ), 0.8, 225, 0.4, 0.4, 0.6, 20 ),
                new RampObject( "the-ramp/images/crate.gif", TheRampStrings.getString( "object.crate" ), 0.8, 300, 0.7, 0.7, 0.3 ),
                new RampObject( "the-ramp/images/ollie.gif", TheRampStrings.getString( "object.dog" ), 0.8, 15, 0.1, 0.1, 0.30, 5 ),
        };

        rampPanel = createRampPanel();
        super.setPhetPCanvas( rampPanel );

        rampControlPanel = createRampControlPanel();
        setControlPanel( rampControlPanel );
        setObject( rampObjects[0] );

        rampMediaPanel = new TimeSeriesPlaybackPanel( getRampTimeSeriesModel() );
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                getRampPhysicalModel().setupForces();
                updateGraphics( event );
            }
        } );

        setClockControlPanel( rampMediaPanel );
        rampModel.getBlock().addListener( new CollisionHandler( this ) );
        doReset();
    }

    protected RampControlPanel createRampControlPanel() {
        return new AdvancedRampControlPanel( this );
    }

    protected RampPanel createRampPanel() {
        return new RampPanel( this );
    }

    private TimeSeriesModel getRampTimeSeriesModel() {
        return rampModel.getRampTimeSeriesModel();
    }

    public PhetFrame getPhetFrame() {
        return phetFrame;
    }

    public void updateGraphics( ClockEvent event ) {
        super.updateGraphics( event );
        rampPanel.updateGraphics();
    }

    public RampPanel getRampPanel() {
        return rampPanel;
    }

    public RampPhysicalModel getRampPhysicalModel() {
        return getRampModel().getRampPhysicalModel();
    }

    private RampModel getRampModel() {
        return rampModel;
    }

    public void reset() {
        if( resetDialogOk() ) {
            doReset();
        }
    }

    public boolean resetDialogOk() {
        JOptionPane pane = new JOptionPane( TheRampStrings.getString( "confirm-reset" ), JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION );

        JDialog dialog = pane.createDialog( rampPanel, TheRampStrings.getString( "confirm.reset.title" ) );
        pane.selectInitialValue();
        Point loc = getPhetPCanvas().getLocationOnScreen();
        Rectangle2D clearButton = getRampPanel().getClearButtonCanvasRect();
        Point2D.Double offset = new Point2D.Double( clearButton.getMaxX(), clearButton.getY() );
        dialog.setLocation( (int)( loc.x + offset.x ), (int)( loc.y + offset.y - dialog.getHeight() / 2 ) );

        dialog.show();
        dialog.dispose();

        return pane.getValue().equals( new Integer( JOptionPane.OK_OPTION ) );
    }

    public void doReset() {
        rampModel.reset();
        rampPanel.reset();
        rampControlPanel.reset();
        setObject( rampObjects[0] );
        updateReadouts();
//        resetPlotStates();
    }

//    private void resetPlotStates() {
//    }

    public void setObject( RampObject rampObject ) {
        rampModel.setObject( rampObject );
        getRampPanel().setObject( rampObject );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.objectChanged();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public Block getBlock() {
        return getRampModel().getBlock();
    }

    public void clearHeat() {
        cueFirefighter();
    }

    public void clearHeatSansFiredog() {
        getRampPhysicalModel().clearHeat();
    }

    public void cueFirefighter() {
        if( !firedogInProgress ) {
            firedogInProgress = true;
            new FireDog( this ).putOutFire();
        }
    }

    public void setAppliedForce( double appliedForce ) {
        getRampModel().setAppliedForce( appliedForce );
    }

    public double getRampAngle() {
        return getRampPhysicalModel().getRampAngle();
    }

    public void setRampAngle( double value ) {
        getRampPhysicalModel().setRampAngle( value );
    }

    public double getGlobalMinPosition() {
        return getRampPhysicalModel().getGlobalMinPosition();
    }

    public double getGlobalMaxPosition() {
        return getRampPhysicalModel().getGlobalMaxPosition();
    }

    public double getGlobalBlockPosition() {
        return getRampPhysicalModel().getGlobalBlockPosition();
    }

    public void setGlobalBlockPosition( double position ) {
        getRampPhysicalModel().setGlobalBlockPosition( position );
    }

    public boolean isRecording() {
        return getRampTimeSeriesModel().isRecording();
    }

    public void setAudioEnabled( boolean enabled ) {
        AudioSourceDataLinePlayer.setAudioEnabled( enabled );
    }

    public int numMaximizedBarGraphs() {
        return rampPanel.numMaximizedBarGraphs();
    }

    public void updateReadouts() {
        rampPanel.updateReadouts();
    }

    public void firedogFinished() {
        firedogInProgress = false;
    }

    public void applicationStarted() {
        rampPanel.applicationStarted();
    }

    public static interface Listener {
        void objectChanged();
    }

    public RampObject[] getRampObjects() {
        return rampObjects;
    }

    public void record() {
        rampModel.record();
    }

    public void playback() {
        rampModel.playback();
    }

    public void repaintBackground() {
        rampPanel.repaintBackground();
    }

    public void updateModel( double dt ) {
        getRampPhysicalModel().stepInTime( dt );
    }

    public void updatePlots( RampPhysicalModel state, double recordTime ) {
        getRampPlotSet().updatePlots( state, recordTime );
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return getRampModel().getRampTimeSeriesModel();
    }

    public void setMass( double value ) {
        getRampModel().setMass( value );
    }

    public RampPlotSet getRampPlotSet() {
        return rampPanel.getRampPlotSet();
    }

    public RampControlPanel getRampControlPanel() {
        return rampControlPanel;
    }

}
