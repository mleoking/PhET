/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.controller.GradientMagnetControl;
import edu.colorado.phet.mri.controller.SampleTargetModelConfigurator;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/**
 * SampleScannerB
 * <p/>
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SampleScannerB {
    private SampleTarget sampleTarget = new SampleTarget();
    private Point2D position = new Point2D.Double();
    private Rectangle2D scanningArea;
    private Detector detector;
    private double stepSize;
    private GradientElectromagnet horizontalGradientElectromagnet;
    private GradientElectromagnet verticalGradientElectromagnet;
    private State startState;
    private List states = new ArrayList();
    private Rectangle2D currentScanArea = new Rectangle2D.Double( 0, 0, 20, 300 );
    private int signal;
    private double startPointX;
    private double startPointY;
    private MriModel model;


    public SampleScannerB( MriModel model,
                           Sample sample,
                           Detector detector,
                           IClock clock,
                           double dwellTime,
                           double stepSize,
                           GradientElectromagnet horizontalGradientElectromagnet,
                           GradientElectromagnet verticalGradientElectromagnet ) {
        this.model = model;
        this.detector = detector;
        this.stepSize = stepSize;
        this.horizontalGradientElectromagnet = horizontalGradientElectromagnet;
        this.verticalGradientElectromagnet = verticalGradientElectromagnet;
        scanningArea = sample.getBounds();
        startPointX = scanningArea.getX() + stepSize / 2;
        startPointY = scanningArea.getY() + stepSize / 2;
        setPosition( startPointX, startPointY );
        sampleTarget.setLocation( getPosition() );
        sampleTarget.addChangeListener( new SampleTargetModelConfigurator( model ) );


        setPosition( startPointX, startPointY );
        currentScanArea.setRect( getPosition().getX(),
                                 getPosition().getY(),
                                 stepSize,
                                 scanningArea.getHeight() );
        sampleTarget.setLocation( getPosition() );


        State stepScannerState = new StepScannerState();
        State resetDetectorState = new ResetDetectorState();
        State enableRadioSourceState = new EnableRadioSourceState();
        State disableRadioSourceState = new DisableRadioSourceState();
        State detectorReportState = new DetectorReportState();

        stepScannerState.init( clock, resetDetectorState, 0 );
        resetDetectorState.init( clock, enableRadioSourceState, 0 );
        enableRadioSourceState.init( clock, disableRadioSourceState, dwellTime );
        disableRadioSourceState.init( clock, detectorReportState, dwellTime );
        detectorReportState.init( clock, stepScannerState, 0 );

        horizontalGradientElectromagnet.setFieldStrength( 2.5 * GradientMagnetControl.VIEW_TO_MODEL_FACTOR / MriConfig.CURRENT_TO_FIELD_FACTOR );
        verticalGradientElectromagnet.setFieldStrength( 0 * GradientMagnetControl.VIEW_TO_MODEL_FACTOR / MriConfig.CURRENT_TO_FIELD_FACTOR );
//        horizontalGradientElectromagnet.setCurrent( 2.5 * GradientMagnetControl.VIEW_TO_MODEL_FACTOR );
//        verticalGradientElectromagnet.setCurrent( 0 * GradientMagnetControl.VIEW_TO_MODEL_FACTOR );

        startState = resetDetectorState;
    }

    public void start() {
        startState.enterState();
    }

    public void stop() {
        for( int i = 0; i < states.size(); i++ ) {
            State state = (State)states.get( i );
            PhetUtilities.getActiveClock().removeClockListener( state );
        }
    }

    public Rectangle2D getCurrentScanArea() {
        return currentScanArea;
    }

    public int getSignal() {
        return signal;
    }

    //--------------------------------------------------------------------------------------------------
    // States
    //--------------------------------------------------------------------------------------------------

    abstract class State extends ClockAdapter {
        State nextState;
        double lifetime;
        double elapsedTime;
        private IClock clock;

        protected State() {
            states.add( this );
        }

        void init( IClock clock, State nextState, double lifetime ) {
            this.clock = clock;
            this.nextState = nextState;
            this.lifetime = lifetime;
        }

        void enterState() {
            elapsedTime = 0;
            clock.addClockListener( this );
        }

        void exitState() {
            clock.removeClockListener( this );
        }

        public void clockTicked( ClockEvent clockEvent ) {
            elapsedTime += clockEvent.getSimulationTimeChange();
            if( elapsedTime > lifetime ) {
                this.exitState();
                nextState.enterState();
            }
        }
    }

    class StepScannerState extends State {
        final int horizontal = 1, vertical = 2;
        int scanDirection = horizontal;
        double scanWidth = stepSize;
        double scanHeight = scanningArea.getHeight();
        double stepSizeX = stepSize;
        double stepSizeY = 0;


        void enterState() {
            super.enterState();
            double x = getPosition().getX() + stepSizeX;
            double y = getPosition().getY() + stepSizeY;

            // Have we scanner all the way across horizontally? If so, start stepping down
            if( x > scanningArea.getMaxX() ) {
                x = startPointX;
                y = startPointY;
                scanDirection = vertical;
                scanWidth = scanningArea.getWidth();
                scanHeight = stepSize;
                stepSizeX = 0;
                stepSizeY = stepSize;

                horizontalGradientElectromagnet.setFieldStrength( 0 * GradientMagnetControl.VIEW_TO_MODEL_FACTOR / MriConfig.CURRENT_TO_FIELD_FACTOR );
                verticalGradientElectromagnet.setFieldStrength( 2.5 * GradientMagnetControl.VIEW_TO_MODEL_FACTOR / MriConfig.CURRENT_TO_FIELD_FACTOR );
//                horizontalGradientElectromagnet.setCurrent( 0 * GradientMagnetControl.VIEW_TO_MODEL_FACTOR );
//                verticalGradientElectromagnet.setCurrent( 2.5 * GradientMagnetControl.VIEW_TO_MODEL_FACTOR );
            }
            if( y > scanningArea.getMaxY() ) {
                x = startPointX;
                y = startPointY;
                scanDirection = horizontal;
                scanWidth = stepSize;
                scanHeight = scanningArea.getHeight();
                stepSizeX = stepSize;
                stepSizeY = 0;

                horizontalGradientElectromagnet.setFieldStrength( 2.5 * GradientMagnetControl.VIEW_TO_MODEL_FACTOR / MriConfig.CURRENT_TO_FIELD_FACTOR );
                verticalGradientElectromagnet.setFieldStrength( 0 * GradientMagnetControl.VIEW_TO_MODEL_FACTOR / MriConfig.CURRENT_TO_FIELD_FACTOR );
//                horizontalGradientElectromagnet.setCurrent( 2.5 * GradientMagnetControl.VIEW_TO_MODEL_FACTOR );
//                verticalGradientElectromagnet.setCurrent( 0 * GradientMagnetControl.VIEW_TO_MODEL_FACTOR );
            }
            setPosition( x, y );
            currentScanArea.setRect( getPosition().getX(),
                                     getPosition().getY(),
                                     scanWidth,
                                     scanHeight );
            sampleTarget.setLocation( getPosition() );
        }
    }

    class ResetDetectorState extends State {
        void enterState() {
            super.enterState();
            detector.reset();
        }
    }

    class EnableRadioSourceState extends State {
        void enterState() {
            super.enterState();
            model.getRadiowaveSource().setEnabled( true );
        }
    }

    class DisableRadioSourceState extends State {
        void enterState() {
            super.enterState();
            model.getRadiowaveSource().setEnabled( false );
        }
    }

    class DetectorReportState extends State {

        void enterState() {
            super.enterState();
            signal = detector.getNumDetected();
            report();
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Setters and getters
    //--------------------------------------------------------------------------------------------------

    public double getStepSize() {
        return stepSize;
    }

    public SampleTarget getSampleTarget() {
        return sampleTarget;
    }


    public Point2D getPosition() {
        return position;
    }

    public void setPosition( double x, double y ) {
        position.setLocation( x, y );
    }

    private void report() {
        changeListenerProxy.stateChanged( new SampleScannerB.ChangeEvent( this ) );
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public static class ChangeEvent extends EventObject {
        public ChangeEvent( SampleScannerB source ) {
            super( source );
        }

        public SampleScannerB getSampleScanner() {
            return (SampleScannerB)getSource();
        }
    }

    public static interface ChangeListener extends EventListener {
        void stateChanged( SampleScannerB.ChangeEvent event );
    }

    private EventChannel changeEventChannel = new EventChannel( SampleScannerB.ChangeListener.class );
    private SampleScannerB.ChangeListener changeListenerProxy = (SampleScannerB.ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( SampleScannerB.ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( SampleScannerB.ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}
