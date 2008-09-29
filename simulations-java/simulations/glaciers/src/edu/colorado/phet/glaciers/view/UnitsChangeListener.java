/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

/**
 * UnitsChangeListener is the interface implemented by all listeners who
 * are interested in switching between English and metric units.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface UnitsChangeListener {
    
    /**
     * Called when units are changed.
     * 
     * @param englishUnits true if units were changed to English, false if changed to metric
     */
    public void unitsChanged( boolean englishUnits );
}

