/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Font;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;

import edu.colorado.phet.opticaltweezers.OTStrings;
import edu.colorado.phet.opticaltweezers.model.OTClock;

/**
 * ClockSpeedSlider is the slider used to control the simulation clock speed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ClockSpeedSlider extends JSlider {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private OTClock _clock;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ClockSpeedSlider( OTClock clock, Font font ) {
        super();
        setFont( font );
        
        _clock = clock;
        
        setMinimum( 0 );
        setMaximum( 100  );
        setMajorTickSpacing( getMaximum() - getMinimum() );
        setPaintTicks( true );
        setPaintLabels( true );
        
        JLabel slowLabel = new JLabel( OTStrings.SLOW );
        slowLabel.setFont( font );
        JLabel fastLabel = new JLabel( OTStrings.FAST );
        fastLabel.setFont( font );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( getMinimum() ), slowLabel );
        labelTable.put( new Integer( getMaximum() ), fastLabel );
        setLabelTable( labelTable );
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public void setSpeed( double dt ) {
        // Convert from model to view coordinates
        double valueModel = dt;
        double minModel = _clock.getDtRange().getMin();
        double maxModel = _clock.getDtRange().getMax();
        int minView = getMinimum();
        int maxView = getMaximum();
        int valueView = minView + (int) ( ( maxView - minView ) * ( ( valueModel - minModel ) / ( maxModel - minModel ) ) );
        setValue( valueView );
    }
    
    public double getSpeed() {
        // Convert from view to model coordinates
        int valueView = getValue();
        int minView = getMinimum();
        int maxView = getMaximum();
        double minModel = _clock.getDtRange().getMin();
        double maxModel = _clock.getDtRange().getMax();
        return minModel + ( ( maxModel - minModel ) * ( ( valueView - minView ) / (double)( maxView - minView ) ) );
    }
}
