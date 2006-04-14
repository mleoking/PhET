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

import java.awt.geom.Point2D;

/**
 * RadiowaveSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RadiowaveSource implements ModelElement {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------
    public static class Orientation{};
    public static Orientation VERTICAL;
    public static Orientation HORIZONTAL;


    private double frequency;
    private double power;
    private Point2D location;
    private double length;
    private Object orientation;

    public RadiowaveSource( Point2D location, double length, Object orientation ) {
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
    }

    public double getPower() {
        return power;
    }

    public void setPower( double power ) {
        this.power = power;
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
}
