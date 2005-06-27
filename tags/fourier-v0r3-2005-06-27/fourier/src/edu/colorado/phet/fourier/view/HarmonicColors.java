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
 * HarmonicColors
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicColors {

    private static HarmonicColors _instance;
    
    private Color[] _harmonicColors;
    private EventListenerList _listenerList;
    
    public static HarmonicColors getInstance() {
        if ( _instance == null ) {
            _instance = new HarmonicColors();
        }
        return _instance;
    }
    
    /**
     * Singleton, accessed via getInstance.
     *
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
    
    public int getNumberOfColors() {
        return _harmonicColors.length;
    }
    
    public void setColor( int index, Color color ) {
        _harmonicColors[ index ] = color;
        fireChangeEvent( index, color );
    }
    
    /**
     * Gets the color that corresponds to a specified harmonic.
     * 
     * @param n the harmonic number, starting from zero
     * @throws IllegalArgumentException if n is out of range
     */
    public Color getColor( int n ) {
      if ( n < 0 || n >= _harmonicColors.length ) {
          throw new IllegalArgumentException( "n is out of range: " + n );
      }
      return _harmonicColors[ n ];
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
    
    public void addHarmonicColorChangeListener( HarmonicColorChangeListener listener ) {
        _listenerList.add( HarmonicColorChangeListener.class, listener );
    }
  
    public void removeHarmonicColorChangeListener( HarmonicColorChangeListener listener ) {
        _listenerList.remove( HarmonicColorChangeListener.class, listener );
    }
    
    private void fireChangeEvent( int harmonicNumber, Color color ) {
        HarmonicColorChangeEvent event = new HarmonicColorChangeEvent( this, harmonicNumber, color );
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == HarmonicColorChangeListener.class ) {
                ((HarmonicColorChangeListener) listeners[ i + 1 ] ).harmonicColorChanged( event );
            }
        }
    }
}
