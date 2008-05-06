/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.nuclearreactor;

import java.awt.geom.Rectangle2D;

/**
 * This class represents the position and behavior of the control rods within
 * a model of a nuclear reactor.
 *
 * @author John Blanco
 */
public class ControlRod {

    //-----------------------------------------------------------------------------
    // Class Data
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // Instance Data
    //-----------------------------------------------------------------------------

    Rectangle2D _rect;
    
    //-----------------------------------------------------------------------------
    // Constructor(s)
    //-----------------------------------------------------------------------------

    ControlRod(double xPos, double yPos, double width, double height){
        _rect = new Rectangle2D.Double(xPos, yPos, width, height);
    }
    
    //-----------------------------------------------------------------------------
    // Accessor Methods
    //-----------------------------------------------------------------------------

    public Rectangle2D getRectangleReference(){
        return _rect;
    }

    //-----------------------------------------------------------------------------
    // Other Public Methods
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // Private Methods
    //-----------------------------------------------------------------------------

}
