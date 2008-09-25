

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;

public class StoveControlSlider extends JSlider {
    private static final int STOVE_SLIDER_HEIGHT = 85;
    private static final int STOVE_SLIDER_WIDTH = 100;
    private static final int MAX_SLIDER_VALULE = 50;
    private static final int MIN_SLIDER_VALULE = -50;
    private static final Color CONTROL_COLOR = new Color(240, 230, 255);
    private ArrayList m_listeners = new ArrayList();

    public StoveControlSlider() {
    	
        setOrientation( JSlider.VERTICAL );
        setMinimum( MIN_SLIDER_VALULE );
        setMaximum( MAX_SLIDER_VALULE );
        setValue( 0 );
        setMajorTickSpacing( (MAX_SLIDER_VALULE - MIN_SLIDER_VALULE) / 2 );
        setMinorTickSpacing( (MAX_SLIDER_VALULE - MIN_SLIDER_VALULE) / 10 );
        setPaintTicks( true );
        setPaintLabels( true );
        setPreferredSize(new Dimension(STOVE_SLIDER_WIDTH, STOVE_SLIDER_HEIGHT));
        setFont( new PhetFont(11) );

        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(MAX_SLIDER_VALULE), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_ADD_LABEL));
        labelTable.put(new Integer(0), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_ZERO_LABEL));
        labelTable.put(new Integer(MIN_SLIDER_VALULE), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_REMOVE_LABEL));
        setLabelTable(labelTable);
        
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
            	// Return to 0 when the user releases the slider.
                setValue(0);
            }
        });
        
        TitledBorder border = new TitledBorder(new EtchedBorder(BevelBorder.RAISED, 
                new Color(40, 20, 255),
                Color.black),
                StatesOfMatterStrings.STOVE_CONTROL_PANEL_TITLE);
        border.setTitleFont( new PhetFont(12) );
        setBorder(border);
        setBackground(CONTROL_COLOR);
        setBackground(CONTROL_COLOR);

        /*
        m_stoveSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                notifyListeners();
            }
        });
        */
    }
}
