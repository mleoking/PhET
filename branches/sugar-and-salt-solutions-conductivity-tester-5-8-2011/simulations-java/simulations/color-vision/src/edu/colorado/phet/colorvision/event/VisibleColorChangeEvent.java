// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision.event;

import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;

import java.util.EventObject;

/**
 * VisibleColorChangeEvent occurs when a color changes in some way.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class VisibleColorChangeEvent extends EventObject {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // The color.
    protected VisibleColor _color;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param source the source of the event
     * @param color the color
     */
    public VisibleColorChangeEvent( Object source, VisibleColor color ) {
        super( source );
        _color = color;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the color.
     *
     * @return the color
     */
    public VisibleColor getColor() {
        return _color;
    }

    //----------------------------------------------------------------------------
    // Conversions
    //----------------------------------------------------------------------------

    /**
     * Provides a String representation of this event.
     * The format of this String may change in the future.
     *
     * @return a String
     */
    public String toString() {

        int r = _color.getRed();
        int g = _color.getGreen();
        int b = _color.getBlue();
        int a = _color.getAlpha();
        double w = _color.getWavelength();

        return this.getClass().getName() + "[" + 
            "color=[" + r + "," + g + "," + b + "," + a + "]" + 
            " wavelength=" + w + 
            " source=[" + super.getSource() + "]" + "]";
    }

}