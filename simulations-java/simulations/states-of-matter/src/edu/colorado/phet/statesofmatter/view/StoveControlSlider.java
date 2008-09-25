

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
    private static final int s_stoveSliderHeight = 60;
    private Color m_controlColor = new Color(240, 230, 255);
    private ArrayList m_listeners = new ArrayList();
    private JSlider m_stoveSlider;

    public StoveControlSlider() {
    	
        setOrientation( JSlider.VERTICAL );
        setMinimum( -50 );
        setMaximum( 50 );
        setValue( 0 );
        setMajorTickSpacing( 50 );
        setMinorTickSpacing( 10 );
        setPaintTicks( true );
        setPaintLabels( true );
        setPreferredSize(new Dimension(100, s_stoveSliderHeight));
        setFont( new PhetFont(11) );

        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(40), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_ADD_LABEL));
        labelTable.put(new Integer(0), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_ZERO_LABEL));
        labelTable.put(new Integer(-40), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_REMOVE_LABEL));
        setLabelTable(labelTable);
        
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
            	// Return to 0 when the user releases the slider.
                setValue(0);
            }
        });
        
        /*
        TitledBorder border = new TitledBorder(new EtchedBorder(BevelBorder.RAISED, 
                new Color(40, 20, 255),
                Color.black),
                StatesOfMatterStrings.STOVE_CONTROL_PANEL_TITLE);
        border.setTitleFont( new PhetFont(12) );
        stovePanel.setBorder(border);
        m_stoveSlider.setBackground(m_controlColor);
        stovePanel.setBackground(m_controlColor);
        add(m_stoveSlider);

        m_stoveSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                notifyListeners();
            }
        });
        */
    }
}
