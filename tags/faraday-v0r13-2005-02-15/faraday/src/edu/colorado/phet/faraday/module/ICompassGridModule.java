/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.module;

import java.awt.Dimension;


/**
 * ICompassGridModule is the interface that is implemented by all Modules
 * that require external configuration of their "compass grid".  Any Module
 * that contains a CompassGridGraphic should implement this interface.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface ICompassGridModule {

    /**
     * Sets the grid spacing.
     * 
     * @param xSpacing space between needles in horizontal dimension, in pixels
     * @param ySpacing space between needles in the vertical dimension, in pixels
     */
    public void setGridSpacing( int xSpacing, int ySpacing );
    
    /**
     * Gets the horizontal spacing between needles.
     * 
     * @return the spacing, in pixels
     */
    public int getGridXSpacing();
    
    /**
     * Gets the vertical spacing between needles.
     * 
     * @return the spacing, in pixels
     */
    public int getGridYSpacing();
    
    /**
     * Sets the size used for all needles in the grid.
     * 
     * @param size the size, in pixels
     */
    public void setGridNeedleSize( Dimension size );
    
    /**
     * Gets the size of all needles in the grid.
     * 
     * @return the size, in pixels
     */
    public Dimension getGridNeedleSize();
}
