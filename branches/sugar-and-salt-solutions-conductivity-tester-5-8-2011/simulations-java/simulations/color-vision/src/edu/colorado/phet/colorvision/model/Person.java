// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;


/**
 * Person is the model of the person who is perceiving some color.
 * Any changes to the model's properties (via its setters or getters)
 * results in the notification of all registered observers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Person extends SimpleObservable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // The color perceived by the person
    private VisibleColor _color;
    // The person's location in 2D space
    private double _x, _y;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * Creates a person who is located at (0,0) and is seeing no color.
     */
    public Person() {
        // Initialize member data.
        _color = VisibleColor.INVISIBLE;
        _x = _y = 0.0;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the color perceived by the person.
     * 
     * @return the color
     */
    public VisibleColor getColor() {
        return _color;
    }

    /**
     * Sets the color perceived by the person.
     * 
     * @param color the color
     */
    public void setColor( VisibleColor color ) {
        _color = color;
        notifyObservers();
    }

    /**
     * Gets the X coordinate of the person's location.
     * 
     * @return the X coordinate
     */
    public double getX() {
        return _x;
    }

    /**
     * Gets the Y coordinate of the person's location.
     * 
     * @return the Y coordinate
     */
    public double getY() {
        return _y;
    }

    /**
     * Sets the person's location.
     * 
     * @param x the X coordinate
     * @param y the Y coordinate
     */
    public void setLocation( double x, double y ) {
        _x = x;
        _y = y;
        notifyObservers();
    }

}