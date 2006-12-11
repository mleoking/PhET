/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.util;

import java.text.DecimalFormat;

import org.jfree.data.Range;

/**
 * AxisSpec describes the attributes of a chart's axis.
 * Objects of this type are immutable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AxisSpec {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Range _range;
    private double _tickSpacing;
    private DecimalFormat _tickFormat;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AxisSpec( Range range, double tickSpacing, DecimalFormat tickFormat ) {
        _range = range;
        _tickSpacing = tickSpacing;
        _tickFormat = tickFormat;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public Range getRange() {
        return _range;
    }
    
    public double getTickSpacing() {
        return _tickSpacing;
    }

    public DecimalFormat getTickFormat() {
        return _tickFormat;
    }
}
