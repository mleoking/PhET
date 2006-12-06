/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.collision.Box2D;

import java.awt.geom.Point2D;

/**
 * MRBox
 * <p>
 * This is an extension of the common class Box2D. It extends that class
 * it has a temperature, and when molecules hit the box, they then have the
 * same temperature as the box.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRBox extends Box2D {
    private double temperature;

    public MRBox() {
        super();
    }

    public MRBox( Point2D corner1, Point2D corner2 ) {
        super( corner1, corner2 );
    }

    public MRBox( Point2D corner1, Point2D corner2, double width0 ) {
        super( corner1, corner2, width0 );
    }

    public void setTemperature( double temperature ) {
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }
}
