

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

public class StoveControlPanel extends JPanel {
    private static final int s_stoveSliderHeight = 60;
    private Color m_controlColor = new Color(240, 230, 255);
    private ArrayList m_listeners = new ArrayList();
    private JSlider m_stoveSlider;
    private int m_maxStoveSliderValue;

    public StoveControlPanel() {
        
        // This panel will be put on the ApparatusPanel, which has a null LayoutManager.
        // When a JPanel is added to a JPanel with a null LayoutManager, the nested panel
        // doesn't lay out properly if it is at all complicated. To get it to lay out properly,
        // it must be put into an intermediate JPanel with a simple layout manager (in this case
        // we use the default), and that intermediate panel is then added to the ApparatusPanel.
        JPanel stovePanel = this;
        m_maxStoveSliderValue = 40;
        m_stoveSlider = new JSlider(JSlider.VERTICAL, -m_maxStoveSliderValue,
                m_maxStoveSliderValue, 0);
        m_stoveSlider.setMajorTickSpacing(m_maxStoveSliderValue);
        m_stoveSlider.setMinorTickSpacing(10);
        m_stoveSlider.setSnapToTicks(true);
        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(40), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_ADD_LABEL));
        labelTable.put(new Integer(0), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_ZERO_LABEL));
        labelTable.put(new Integer(-40), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_REMOVE_LABEL));
        m_stoveSlider.setLabelTable(labelTable);
        m_stoveSlider.setPaintTicks(true);
        m_stoveSlider.setSnapToTicks(true);
        m_stoveSlider.setPaintLabels(true);
        m_stoveSlider.setPreferredSize(new Dimension(100, s_stoveSliderHeight));
        m_stoveSlider.setFont( new PhetFont(11) );

        m_stoveSlider.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                m_stoveSlider.setValue(0);
            }
        });

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
    }

    private void notifyListeners() {
        for (int i = 0; i < m_listeners.size(); i++) {
            Listener listener = (Listener) m_listeners.get(i);
            listener.valueChanged(getSliderValue());
        }
    }

    private double getSliderValue() {
        //assumes symmetric range on slider
        return m_stoveSlider.getValue() / (double)m_stoveSlider.getMaximum();
    }

    public static interface Listener {
        void valueChanged(double value);
    }

    public void addListener(Listener listener) {
        m_listeners.add(listener);
    }
}
