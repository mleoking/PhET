/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

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
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class is the slider that is used to control the StoveNode, causing it
 * to add heat or cooling to the simulated system.
 * 
 * @author John Blanco
 *
 */
public class StoveControlSlider extends JSlider {
	
    private static final int STOVE_SLIDER_HEIGHT = 85;
    private static final int STOVE_SLIDER_MIN_WIDTH = 120;
    private static final int STOVE_SLIDER_MAX_WIDTH = 200;
    private static final int MAX_SLIDER_VALUE = 50;
    private static final int MIN_SLIDER_VALUE = -50;
    private static final Color CONTROL_COLOR = new Color(240, 230, 255);

    public StoveControlSlider() {
    	
        setOrientation( JSlider.VERTICAL );
        setMinimum( MIN_SLIDER_VALUE );
        setMaximum( MAX_SLIDER_VALUE );
        setValue( 0 );
        setMajorTickSpacing( (MAX_SLIDER_VALUE - MIN_SLIDER_VALUE) / 2 );
        setMinorTickSpacing( (MAX_SLIDER_VALUE - MIN_SLIDER_VALUE) / 10 );
        setPaintTicks( true );
        setPaintLabels( true );
        setFont( new PhetFont(11) );
        
        // Set the size of the stove based on the length of the labels.
        PText tempLabel = new PText(StatesOfMatterStrings.STOVE_CONTROL_PANEL_TITLE);
        tempLabel.setFont(new PhetFont(11));
        int neededStoveWidth = (int)Math.round(tempLabel.getFullBoundsReference().width + 35);
        neededStoveWidth = Math.max(STOVE_SLIDER_MIN_WIDTH, neededStoveWidth);
        neededStoveWidth = Math.min(STOVE_SLIDER_MAX_WIDTH, neededStoveWidth);
        setPreferredSize(new Dimension(neededStoveWidth, STOVE_SLIDER_HEIGHT));

        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(MAX_SLIDER_VALUE), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_ADD_LABEL));
        labelTable.put(new Integer(0), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_ZERO_LABEL));
        labelTable.put(new Integer(MIN_SLIDER_VALUE), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_REMOVE_LABEL));
        setLabelTable(labelTable);
        
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
            	// Return to 0 when the user releases the slider.
                setValue(0);
            }
        });
        
        
        TitledBorder border = new TitledBorder( new EtchedBorder( BevelBorder.RAISED, new Color(40, 20, 255), Color.black), 
        		StatesOfMatterStrings.STOVE_CONTROL_PANEL_TITLE );
        border.setTitleFont( new PhetFont(12) );
        setBorder(border);
        setBackground(CONTROL_COLOR);
    }
    
    public double getNormalizedValue(){
    	return ((double)getValue() / (double)MAX_SLIDER_VALUE);
    }
}
