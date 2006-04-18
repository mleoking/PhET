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
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.quantum.model.PhotonEmissionListener;
import edu.colorado.phet.quantum.model.PhotonSource;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * RadiowaveSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RadiowaveSource_org implements ModelElement, PhotonSource {
//public class RadiowaveSource_org implements ModelElement, PhotonSource {
    private static final double MAX_POWER = MriConfig.MAX_POWER;

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------
    public static class Orientation {
    }

    ;
    public static RadiowaveSource_org.Orientation VERTICAL;
    public static RadiowaveSource_org.Orientation HORIZONTAL;


    private double frequency;
    private double power;
    private Point2D location;
    private double length;
    private Object orientation;

    public RadiowaveSource_org( Point2D location, double length, Object orientation ) {
        this.location = location;
        this.length = length;
        this.orientation = orientation;
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency( double frequency ) {
        this.frequency = frequency;
        changeListenerProxy.wavelengthChanged( new ChangeEvent( this ) );
    }

    public double getMaxPower() {
        return RadiowaveSource_org.MAX_POWER;
    }

    public double getPower() {
        return power;
    }

    public void setPower( double power ) {
        this.power = power;
        changeListenerProxy.rateChangeOccurred( new ChangeEvent( this ) );
    }

    public Point2D getLocation() {
        return location;
    }

    public void setLocation( Point2D location ) {
        this.location = location;
    }

    public double getLength() {
        return length;
    }

    public void setLength( double length ) {
        this.length = length;
    }

    public Object getOrientation() {
        return orientation;
    }

    public void setOrientation( Object orientation ) {
        this.orientation = orientation;
    }

    //----------------------------------------------------------------
    // Time-dependent behavior
    //----------------------------------------------------------------

    public void stepInTime( double dt ) {


    }

    //----------------------------------------------------------------
    // PhotonSource implementation
    //----------------------------------------------------------------
    private EventChannel changeListenerChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeListenerChannel.getListenerProxy();
    private EventChannel photonEmissionChannel = new EventChannel( PhotonEmissionListener.class );
    private PhotonEmissionListener emissionListenerProxy = (PhotonEmissionListener)photonEmissionChannel.getListenerProxy();

    public Shape getBounds() {
        return null;
    }

    public double getPhotonsPerSecond() {
        return getPower();
    }

    public double getMaxPhotonsPerSecond() {
        return getMaxPower();
    }

    public double getWavelength() {
        return 1 / getFrequency();
    }

    public void addChangeListener( ChangeListener listener ) {
        changeListenerChannel.addListener( listener );
    }

    public void addPhotonEmissionListener( PhotonEmissionListener listener ) {
        photonEmissionChannel.addListener( listener );
    }

    public void removePhotonEmissionListener( PhotonEmissionListener listener ) {
        photonEmissionChannel.removeListener( listener );
    }
}
