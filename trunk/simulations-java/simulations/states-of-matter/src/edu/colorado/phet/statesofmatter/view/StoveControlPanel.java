package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.idealgas.IdealGasResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.phetgraphicsdemo.view.DebuggerGraphic;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.BevelBorder;
import java.util.Hashtable;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StoveControlPanel extends JPanel {
    private static final int s_stoveSliderHeight = 60;
    private Color controlColor = new Color(240, 230, 255);
    private ArrayList listeners = new ArrayList();
    private JSlider stoveSlider;
    private int maxStoveSliderValue;

    public StoveControlPanel() {
        
        // This panel will be put on the ApparatusPanel, which has a null LayoutManager.
        // When a JPanel is added to a JPanel with a null LayoutManager, the nested panel
        // doesn't lay out properly if it is at all complicated. To get it to lay out properly,
        // it must be put into an intermediate JPanel with a simple layout manager (in this case
        // we use the default), and that intermediate panel is then added to the ApparatusPanel.
        JPanel stovePanel = this;
        maxStoveSliderValue = 40;
        stoveSlider = new JSlider(JSlider.VERTICAL, -maxStoveSliderValue,
                maxStoveSliderValue, 0);
        stoveSlider.setMajorTickSpacing(maxStoveSliderValue);
        stoveSlider.setMinorTickSpacing(10);
        stoveSlider.setSnapToTicks(true);
        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(40), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_ADD_LABEL));
        labelTable.put(new Integer(0), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_ZERO_LABEL));
        labelTable.put(new Integer(-40), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_REMOVE_LABEL));
        stoveSlider.setLabelTable(labelTable);
        stoveSlider.setPaintTicks(true);
        stoveSlider.setSnapToTicks(true);
        stoveSlider.setPaintLabels(true);
        stoveSlider.setPreferredSize(new Dimension(100, s_stoveSliderHeight));
        stoveSlider.setFont( new PhetFont(11) );

        stoveSlider.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                stoveSlider.setValue(0);
            }
        });

        TitledBorder border = new TitledBorder(new EtchedBorder(BevelBorder.RAISED, 
                new Color(40, 20, 255),
                Color.black),
                IdealGasResources.getString("IdealGasControlPanel.Heat_Control"));
        border.setTitleFont( new PhetFont(12) );
        stovePanel.setBorder(border);
        stoveSlider.setBackground(controlColor);
        stovePanel.setBackground(controlColor);
        add(stoveSlider);

        stoveSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                notifyListeners();
            }
        });
    }

    private void notifyListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            Listener listener = (Listener) listeners.get(i);
            listener.valueChanged(getSliderValue());
        }
    }

    private double getSliderValue() {
        //assumes symmetric range on slider
        return stoveSlider.getValue() / (double)stoveSlider.getMaximum();
    }

    public static interface Listener {
        void valueChanged(double value);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }
}
