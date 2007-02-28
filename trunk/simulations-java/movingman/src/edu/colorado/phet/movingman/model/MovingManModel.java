/** Sam Reid*/
package edu.colorado.phet.movingman.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.plots.TimePoint;
import edu.colorado.phet.movingman.plots.TimeSeries;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Oct 19, 2004
 * Time: 6:50:36 PM
 * Copyright (c) Oct 19, 2004 by Sam Reid
 */
public class MovingManModel {
    private MovingManTimeModel timeModel;
    private int maxManPosition = 10;
    private Man man;
    private AbstractClock clock;
    private DataSuite positionDataSuite;
    private DataSuite velocityDataSuite;
    private DataSuite accelerationDataSuite;
    private MovingManModule module;
    private double minTime = 0;
    private double maxTime = 20;
    private TimeSeries directionSeries;
    private int constancyTestWindowSize = 10;
    private boolean useConstancyWindow = false;
    private boolean smoothingSmooth = true;

    private int smoothPositionSize = 8;
    private int smoothVelocitySize = 6;
    private int smoothAccelerationSize = 4;

    private JFrame controls;

    public MovingManModel( MovingManModule movingManModule, AbstractClock clock ) {
        timeModel = new MovingManTimeModel( movingManModule );
        this.module = movingManModule;
        this.clock = clock;

        positionDataSuite = new DataSuite( getSmoothPositionSize() );
        velocityDataSuite = new DataSuite( getSmoothVelocitySize() );
        accelerationDataSuite = new DataSuite( getSmoothAccelerationSize() );

        directionSeries = new TimeSeries();

//        positionDataSuite.setDerivative( velocityDataSuite );
//        velocityDataSuite.setDerivative( accelerationDataSuite );
        man = new Man( 0, -maxManPosition, maxManPosition );
        man.addListener( new CollisionAudioEffects( movingManModule, man ) );

        controls = new ControlFrame();
    }

    public void showControls() {
        controls.setVisible( true );
    }

    public boolean isSmoothingSmooth() {
        return smoothingSmooth;
    }

    public void setSmoothingSharp() {
        setNumSmoothingPoints( 2 );
        smoothingSmooth = false;
    }

    public void setSmoothingSmooth() {
        positionDataSuite.setNumSmoothingPoints( getSmoothPositionSize() );
        velocityDataSuite.setNumSmoothingPoints( getSmoothVelocitySize() );
        accelerationDataSuite.setNumSmoothingPoints( getSmoothAccelerationSize() );
        smoothingSmooth = true;
    }

    private int getSmoothAccelerationSize() {
        return smoothAccelerationSize;
    }

    private int getSmoothVelocitySize() {
        return smoothVelocitySize;
    }

    private int getSmoothPositionSize() {
        return smoothPositionSize;
    }

    class ControlFrame extends JFrame {
        private JPanel contentPanel;

        public ControlFrame() {
            super( "Model Controls" );
            contentPanel = new VerticalLayoutPanel();
            setContentPane( contentPanel );
            final JSpinner xs = new JSpinner( new SpinnerNumberModel( smoothPositionSize, 1, 20, 1 ) );
            xs.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    smoothPositionSize = ( (Number)xs.getValue() ).intValue();
                    positionDataSuite.setNumSmoothingPoints( smoothPositionSize );
                }
            } );
            xs.setBorder( BorderFactory.createTitledBorder( "x" ) );

            final JSpinner vs = new JSpinner( new SpinnerNumberModel( smoothVelocitySize, 1, 20, 1 ) );
            vs.setBorder( BorderFactory.createTitledBorder( "v" ) );
            vs.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
//                    velocityDataSuite.setNumSmoothingPoints( ( (Number)vs.getValue() ).intValue() );
                    smoothVelocitySize = ( ( (Number)vs.getValue() ).intValue() );
                    velocityDataSuite.setNumSmoothingPoints( smoothVelocitySize );
                }
            } );

            final JSpinner as = new JSpinner( new SpinnerNumberModel( smoothAccelerationSize, 1, 20, 1 ) );
            as.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    smoothAccelerationSize = ( (Number)as.getValue() ).intValue();
                    accelerationDataSuite.setNumSmoothingPoints( smoothAccelerationSize );
                }
            } );
            as.setBorder( BorderFactory.createTitledBorder( "a" ) );
//            JTextField instructions = new JTextField( "Select the number of points to include\n" +
//                                                      "In the smoothing window for each variable." );
            JLabel instructions = new JLabel( "<html>Select the number of points<br>to include in the<br>" +
                                              "smoothing window for each variable.<br> The 'constant-check-window' checks the last unsmoothed<br>" +
                                              "data points of position, and if they are the same<br>" +
                                              "sets velocity and acceleration (pre-smoothing)<br>" +
                                              "to zero.<html>" );
            addComponent( instructions );
            addComponent( xs );
            addComponent( vs );
            addComponent( as );

            JSpinner constantWindowSize = new JSpinner( new SpinnerNumberModel( constancyTestWindowSize, 1, 20, 1 ) );
            constantWindowSize.setBorder( BorderFactory.createTitledBorder( "constant-check-window" ) );

            final JCheckBox useConstantWindow = new JCheckBox( "Use Constant Window", useConstancyWindow );
            useConstantWindow.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    MovingManModel.this.useConstancyWindow = useConstantWindow.isSelected();
                }
            } );
            addComponent( useConstantWindow );
            addComponent( constantWindowSize );
            pack();
            SwingUtils.centerWindowOnScreen( this );
        }

        private void addComponent( JComponent constantWindowSize ) {
            contentPanel.add( constantWindowSize );
        }

    }

    public void setNumSmoothingPoints( int n ) {
//        System.out.println( "n = " + n );
        positionDataSuite.setNumSmoothingPoints( n );
        velocityDataSuite.setNumSmoothingPoints( n );
        accelerationDataSuite.setNumSmoothingPoints( n );
    }

    public void reset() {
        getTimeModel().reset();
        man.reset();
        positionDataSuite.reset();
        velocityDataSuite.reset();
        accelerationDataSuite.reset();
        directionSeries.reset();
    }

    public void step( double dt ) {
        double time = timeModel.getRecordTimer().getTime();
        directionSeries.addPoint( getDirection().doubleValue(), time );

        positionDataSuite.addPoint( man.getPosition(), time );
        positionDataSuite.updateSmoothedSeries();


        boolean constant = isConstant( positionDataSuite.getRawData(), constancyTestWindowSize );

//        System.out.println( "constant = " + constant );
//        TimePoint dx = positionDataSuite.getDerivative( dt );
        TimePoint dx = new CenteredDifferenceDerivative().getLatestDerivative( positionDataSuite.getSmoothedDataSeries() );
        if( dx != null ) {
            if( constant && useConstancyWindow ) {
                velocityDataSuite.addPoint( new TimePoint( 0, dx.getTime() ) );
            }
            else {
                velocityDataSuite.addPoint( dx );
            }
        }

//        velocityDataSuite.setNumSmoothingPoints( 4 );
        velocityDataSuite.updateSmoothedSeries();
//        TimePoint dv = velocityDataSuite.getDerivative( dt );
//        TimePoint dv = new Taylor2ndDerivative().getLatestDerivative( positionDataSuite.getSmoothedDataSeries() );

//        TimePoint dv = new Taylor2ndDerivative().getLatestDerivative( positionDataSuite.getRawData());


        TimePoint dv = new CenteredDifferenceDerivative().getLatestDerivative( velocityDataSuite.getSmoothedDataSeries() );
//        TimePoint dv = new Taylor2ndDerivativeO4().getLatestDerivative( positionDataSuite.getSmoothedDataSeries() );
        if( dv != null ) {
            if( constant ) {
                accelerationDataSuite.addPoint( new TimePoint( 0, dv.getTime() ) );
            }
            else {
                accelerationDataSuite.addPoint( dv );
            }
        }
        accelerationDataSuite.updateSmoothedSeries();
    }

    private boolean isConstant( TimeSeries smoothedDataSeries, int numPoints ) {

        TimePoint val = smoothedDataSeries.lastPointAt( 0 );
        double v = val.getValue();
        for( int i = 1; i < numPoints && i < smoothedDataSeries.numPoints(); i++ ) {
            if( smoothedDataSeries.lastPointAt( i ).getValue() != v ) {
                return false;
            }
        }
        return true;
    }

    private Man.Direction getDirection() {
        return module.getDirection();
    }

    public double getVelocityDataSuite() {
        if( velocityDataSuite == null ) {
            return 0;
        }
        if( velocityDataSuite.numSmoothedPoints() == 0 ) {
            return 0;
        }
        else {
            return velocityDataSuite.smoothedPointAt( velocityDataSuite.numSmoothedPoints() - 1 );
        }
    }

    public Man getMan() {
        return man;
    }

    public DataSuite getPositionDataSuite() {
        return positionDataSuite;
    }

    public DataSuite getVelocitySeries() {
        return velocityDataSuite;
    }

    public DataSuite getAccelerationDataSuite() {
        return accelerationDataSuite;
    }

//    public double getNumSmoothingPosition() {
//        return numSmoothingPosition;
//    }
//
//    public int getNumVelocitySmoothPoints() {
//        return numVelocitySmoothPoints;
//    }

    public double getMaxTime() {
        return maxTime;
    }

    public double getMinTime() {
        return minTime;
    }

    public double getMaxManPosition() {
        return maxManPosition;
    }

    public ModelElement getMainModelElement() {
        return timeModel.getMainModelElement();
    }

    public void fireReset() {
        timeModel.fireReset();
    }

    public void setRecordMode() {
        timeModel.setRecordMode();
    }

//    public int getNumSmoothingPoints() {
//        return timeModel.getNumSmoothingPoints();
//    }

    public MovingManTimeModel getTimeModel() {
        return timeModel;
    }

    public void setReplayTime( double requestedTime ) {
        TimePoint timePoint = positionDataSuite.getSmoothedDataSeries().getValueForTime( requestedTime );
        man.setPosition( timePoint.getValue() );
        double direction = directionSeries.getValueForTime( requestedTime ).getValue();
        module.setReplayManDirection( direction );
    }

    public void setBoundaryConditionsClosed() {
        man.setBoundaryConditionsClosed();
    }

    public void setBoundaryConditionsOpen() {
        man.setBoundaryConditionsOpen();
    }
}
