/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.event;

import java.awt.Color;
import java.util.EventObject;


/**
 * HarmonicColorChangeEvent
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicColorChangeEvent extends EventObject {
    
    private int _harmonicNumber;
    private Color _color;
    
    public HarmonicColorChangeEvent( Object source, int harmonicNumber, Color color ) {
        super( source );
        _harmonicNumber = harmonicNumber;
        _color = color;
    }
    
    public int getOrder() {
        return _harmonicNumber;
    }
    
    public Color getColor() {
        return _color;
    }
}
