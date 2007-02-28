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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * SampleScanner
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SampleScanner implements ModelElement {
    private SampleTarget sampleTarget = new SampleTarget();
    private Point2D position = new Point2D.Double();
    private double speed = 0.5;
    private double rowSpacing = 20;
    private int rowNumber = 0;
    private Rectangle2D scanningArea;
    private double timeSinceLastStep;
    private double dwellTime;
    private double stepSize;

//    public SampleScanner( Sample sample ) {
//        scanningArea = sample.getBounds();
//        setPosition( scanningArea.getX(), scanningArea.getY() );
//        sampleTarget.setLocation( getPosition() );
//    }

    public SampleScanner( Sample sample, double dwellTime, double stepSize ) {
        this.dwellTime = dwellTime;
        this.stepSize = stepSize;
        scanningArea = sample.getBounds();
        setPosition( scanningArea.getX() + stepSize / 2, scanningArea.getY() + stepSize / 2 );
        sampleTarget.setLocation( getPosition() );

    }

    public void stepInTime( double dt ) {
        timeSinceLastStep += dt;
        if( timeSinceLastStep >= dwellTime ) {
            timeSinceLastStep = 0;
            setPosition( getPosition().getX() + stepSize, getPosition().getY() );
            if( getPosition().getX() > scanningArea.getMaxX() ) {
                rowNumber++;
                if( getPosition().getY() > scanningArea.getMaxY() ) {
                    rowNumber = 0;
                }
                setPosition( scanningArea.getX(), scanningArea.getY() + rowNumber * stepSize );
            }
            sampleTarget.setLocation( getPosition() );
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
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public static class ChangeEvent extends EventObject {
        public ChangeEvent( SampleScanner source ) {
            super( source );
        }

        public SampleScanner getSampleScanner() {
            return (SampleScanner)getSource();
        }
    }

    public static interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}
