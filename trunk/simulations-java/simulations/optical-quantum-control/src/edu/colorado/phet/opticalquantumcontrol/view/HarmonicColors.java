// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticalquantumcontrol.view;

import java.awt.Color;

import edu.colorado.phet.opticalquantumcontrol.model.Harmonic;


/**
 * HarmonicColors is manages the set of harmonic colors.
 * It is based on the "singleton with static factory" pattern.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicColors {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /* Singleton instance */
    private static final HarmonicColors INSTANCE = new HarmonicColors();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Color[] _harmonicColors;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the singleton instance of this class.
     * 
     * @return singleton
     */
    public static HarmonicColors getInstance() {
        return INSTANCE;
    }
    
    /*
     * Singleton, accessed via getInstance.
     */
    private HarmonicColors() {
        // Colors for harmonics
        _harmonicColors = new Color[]
        {
                Color.RED,
                new Color( 232, 107, 27 ), /* pumpkin orange */
                Color.YELLOW,
                Color.GREEN,
                Color.BLUE,
                new Color( 52, 48, 147 ), /* indigo */
                new Color( 144, 0, 164 ) /* violet */
        };
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the number of colors that are managed.
     * 
     * @return number of colors
     */
    public int getNumberOfColors() {
        return _harmonicColors.length;
    }
    
    /**
     * Gets the color that corresponds to a specified harmonic.
     * 
     * @param order the harmonic order, starting from zero
     * @throws IllegalArgumentException is order is out of range
     */
    public Color getColor( int order ) {
      if ( order < 0 || order >= _harmonicColors.length ) {
          throw new IllegalArgumentException( "order is out of range: " + order );
      }
      return _harmonicColors[ order ];
    }
    
    /**
     * Gets the color that corresponds to a specified harmonic.
     * 
     * @param Harmonic the harmonic
     */
    public Color getColor( Harmonic harmonic ) {
        return getColor( harmonic.getOrder() );
    }
}
