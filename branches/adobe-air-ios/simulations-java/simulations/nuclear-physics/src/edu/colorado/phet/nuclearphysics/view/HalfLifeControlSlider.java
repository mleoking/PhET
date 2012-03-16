// Copyright 2002-2011, University of Colorado

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
        setBackground(NuclearPhysicsConstants.CHART_BACKGROUND_COLOR);
    }
    
    public double getNormalizedValue(){
    	return ((double)getValue() / (double)MAX_SLIDER_VALULE);
    }
    
    public void setNormalizedValue(double normalizedValue){
    	if (normalizedValue > 1){
    		normalizedValue = 1;
    	}
    	else if (normalizedValue < 0){
    		normalizedValue = 0;
    	}
    	setValue((int)(normalizedValue * (double)MAX_SLIDER_VALULE));
    }
}
