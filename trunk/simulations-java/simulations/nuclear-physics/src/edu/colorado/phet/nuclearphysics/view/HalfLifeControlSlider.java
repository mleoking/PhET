/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import javax.swing.JSlider;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;

/**
 * This class is the slider that is used to control the half life of the
 * current set of nuclei.
 * 
 * @author John Blanco
 *
 */
public class HalfLifeControlSlider extends JSlider {
	
    private static final int MAX_SLIDER_VALULE = 1000;
    private static final int MIN_SLIDER_VALULE = 0;

    public HalfLifeControlSlider() {
    	
        setOrientation( JSlider.HORIZONTAL );
        setMinimum( MIN_SLIDER_VALULE );
        setMaximum( MAX_SLIDER_VALULE );
        setValue( 0 );
        setPaintTicks( false );
        setPaintLabels( false );

        /* TODO: JPB TBD - I don't think this will be needed, but keep around for a bit just in case.
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
            	// Return to 0 when the user releases the slider.
                setValue(0);
            }
        });
        */
        
        setBackground(NuclearPhysicsConstants.ALPHA_DECAY_CHART_COLOR);
    }
    
    public double getNormalizedValue(){
    	return ((double)getValue() / (double)MAX_SLIDER_VALULE);
    }
}
