/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.Color;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.fourier.event.HarmonicColorChangeEvent;
import edu.colorado.phet.fourier.event.HarmonicColorChangeListener;
import edu.colorado.phet.fourier.model.Harmonic;


/**
 * HarmonicColors is a singleton that manages the set
 * of harmonic colors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicColors {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /* Singleton instance */
    private static HarmonicColors _instance;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Color[] _harmonicColors;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the singleton instance of this class.
     * 
     * @return singleton
     */
    public static HarmonicColors getInstance() {
        if ( _instance == null ) {
            _instance = new HarmonicColors();
        }
        return _instance;
    }
    
    /*
     * Singleton, accessed via getInstance.
     */
    private HarmonicColors() {
        // Colors for harmonics
        _harmonicColors = new Color[]
        {
                new Color( 1f, 0f, 0f ),
                new Color( 1f, 0.5f, 0f ),
                new Color( 1f, 1f, 0f ),
                new Color( 0f, 1f, 0f ),
                new Color( 0f, 0.790002f, 0.340007f ),
                new Color( 0.392193f, 0.584307f, 0.929395f ),
                new Color( 0f, 0f, 1f ),
                new Color( 0f, 0f, 0.501999f ),
                new Color( 0.569994f, 0.129994f, 0.61999f ),
                new Color( 0.729408f, 0.333293f, 0.827494f ),
                new Color( 1f, 0.411802f, 0.705893f )
        };
        _listenerList = new EventListenerList();
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
     * Sets the color for a specific order harmonic.
     * 
     * @param order the order of the harmonic
     * @param color
     */
    public void setColor( int order, Color color ) {
        _harmonicColors[ order ] = color;
        fireChangeEvent( order, color );
    }
    
    /**
     * Gets the color that corresponds to a specified harmonic.
     * 
     * @param order the harmonic order, starting from zero
     * @throws IllegalArgumentException if n is out of range
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
     * @throws IllegalArgumentException if n is out of range
     */
    public Color getColor( Harmonic harmonic ) {
        return getColor( harmonic.getOrder() );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * Adds a listener.
     * 
     * @param listener
     */
    public void addHarmonicColorChangeListener( HarmonicColorChangeListener listener ) {
        _listenerList.add( HarmonicColorChangeListener.class, listener );
    }
  
    /**
     * Removed a listener.
     * 
     * @param listener
     */
    public void removeHarmonicColorChangeListener( HarmonicColorChangeListener listener ) {
        _listenerList.remove( HarmonicColorChangeListener.class, listener );
    }
    
    /*
     * Fires a HarmonicColorChangeEvent to all listeners.
     * 
     * @param order
     * @param color
     */
    private void fireChangeEvent( int order, Color color ) {
        HarmonicColorChangeEvent event = new HarmonicColorChangeEvent( this, order, color );
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == HarmonicColorChangeListener.class ) {
                ((HarmonicColorChangeListener) listeners[ i + 1 ] ).harmonicColorChanged( event );
            }
        }
    }
}
