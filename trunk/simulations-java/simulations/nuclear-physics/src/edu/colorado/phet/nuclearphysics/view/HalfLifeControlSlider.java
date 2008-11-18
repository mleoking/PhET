/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;

/**
 * This class is the slider that is used to control the half life of the
 * current set of nuclei.
 * 
 * @author John Blanco
 *
 */
public class HalfLifeControlSlider extends JSlider {
	
    private static final int MAX_SLIDER_VALULE = 1;
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
        
        setBackground(StatesOfMatterConstants.ALPHA_DECAY_CHART_COLOR);
    }
    
    public double getNormalizedValue(){
    	return ((double)getValue() / (double)MAX_SLIDER_VALULE);
    }
}
