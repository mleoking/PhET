/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * GraphsControlPanel is the control panel for creating graphs.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphsControlPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color( 82, 126, 90 ); // green
    private static final Color TITLE_COLOR = Color.WHITE;
    private static final Color CONTROL_COLOR = Color.WHITE;
    
    private JRadioButton _noGraphRadioButton;
    private JRadioButton _glacierLengthVerusTimeRadioButton;
    private JRadioButton _equilibriumLineAltitudeVersusTimeRadioButton;
    private JRadioButton _accumulationVersusAltitudeRadioButton;
    private JRadioButton _ablationVersusAltitudeRadioButton;
    private JRadioButton _massBalanceVersusAltitudeRadioButton;
    private JRadioButton _temperatureVersusAltitudeRadioButton;
    private JRadioButton _valleyFloorVersusAltitudeRadioButton;
    
    private ArrayList _listeners;
    
    public GraphsControlPanel( Font titleFont, Font controlFont ) {
        super();
        
        _listeners = new ArrayList();
        
        Border emptyBorder = BorderFactory.createEmptyBorder( 3, 3, 3, 3 );
        TitledBorder titledBorder = new TitledBorder( GlaciersStrings.TITLE_GRAPHS );
        titledBorder.setTitleFont( titleFont );
        titledBorder.setTitleColor( TITLE_COLOR );
        titledBorder.setBorder( BorderFactory.createLineBorder( TITLE_COLOR, 1 ) );
        Border compoundBorder = BorderFactory.createCompoundBorder( emptyBorder, titledBorder );
        setBorder( compoundBorder );
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifySelectionChanged();
            }
        };
        
        _noGraphRadioButton = createRadioButton( GlaciersStrings.RADIO_BUTTON_NO_GRAPH, controlFont, CONTROL_COLOR );
        _noGraphRadioButton.addActionListener( actionListener );
        
        _glacierLengthVerusTimeRadioButton = createRadioButton( GlaciersStrings.RADIO_BUTTON_GLACIER_LENGTH_VERSUS_TIME, controlFont, CONTROL_COLOR );
        _glacierLengthVerusTimeRadioButton.addActionListener( actionListener );
        
        _equilibriumLineAltitudeVersusTimeRadioButton = createRadioButton( GlaciersStrings.RADIO_BUTTON_EQUILIBRIUM_LINE_VERSUS_TIME, controlFont, CONTROL_COLOR );
        _equilibriumLineAltitudeVersusTimeRadioButton.addActionListener( actionListener );
        
        _accumulationVersusAltitudeRadioButton = createRadioButton( GlaciersStrings.RADIO_BUTTON_ACCUMULATION_VERSUS_ALTITUDE, controlFont, CONTROL_COLOR );
        _accumulationVersusAltitudeRadioButton.addActionListener( actionListener );
        
        _ablationVersusAltitudeRadioButton = createRadioButton( GlaciersStrings.RADIO_BUTTON_ABLATION_VERSUS_ALTITUDE, controlFont, CONTROL_COLOR );
        _ablationVersusAltitudeRadioButton.addActionListener( actionListener );
        
        _massBalanceVersusAltitudeRadioButton = createRadioButton( GlaciersStrings.RADIO_BUTTON_MASS_BALANCE_VERSUS_ALTITUDE, controlFont, CONTROL_COLOR );
        _massBalanceVersusAltitudeRadioButton.addActionListener( actionListener );
        
        _temperatureVersusAltitudeRadioButton = createRadioButton( GlaciersStrings.RADIO_BUTTON_TEMPERATURE_VERSUS_ALTITUDE, controlFont, CONTROL_COLOR );
        _temperatureVersusAltitudeRadioButton.addActionListener( actionListener );
        
        _valleyFloorVersusAltitudeRadioButton = createRadioButton( GlaciersStrings.RADIO_BUTTON_VALLEY_FLOOR_VERSUS_ALTITUDE, controlFont, CONTROL_COLOR );
        _valleyFloorVersusAltitudeRadioButton.addActionListener( actionListener );
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _noGraphRadioButton );
        buttonGroup.add( _glacierLengthVerusTimeRadioButton );
        buttonGroup.add( _equilibriumLineAltitudeVersusTimeRadioButton );
        buttonGroup.add( _accumulationVersusAltitudeRadioButton );
        buttonGroup.add( _ablationVersusAltitudeRadioButton );
        buttonGroup.add( _massBalanceVersusAltitudeRadioButton );
        buttonGroup.add( _temperatureVersusAltitudeRadioButton );
        buttonGroup.add( _valleyFloorVersusAltitudeRadioButton );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _noGraphRadioButton, row++, column );
        layout.addComponent( _glacierLengthVerusTimeRadioButton, row++, column );
        layout.addComponent( _equilibriumLineAltitudeVersusTimeRadioButton, row++, column );
        layout.addComponent( _accumulationVersusAltitudeRadioButton, row++, column );
        layout.addComponent( _ablationVersusAltitudeRadioButton, row++, column );
        layout.addComponent( _massBalanceVersusAltitudeRadioButton, row++, column );
        layout.addComponent( _temperatureVersusAltitudeRadioButton, row++, column );
        layout.addComponent( _valleyFloorVersusAltitudeRadioButton, row++, column );
        
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, null, false );
        
        // default state
        _noGraphRadioButton.setSelected( true );
    }
    
    private JRadioButton createRadioButton( String label, Font font, Color foreground ) {
        JRadioButton radioButton = new JRadioButton( label );
        radioButton.setFont( font );
        radioButton.setForeground( foreground );
        return radioButton;
    }
    
    public boolean isNoGraphSelected() {
        return _noGraphRadioButton.isSelected();
    }
    
    public boolean isGlacierLengthVerusTimeSelected() {
        return _glacierLengthVerusTimeRadioButton.isSelected();
    }
    
    public boolean isEquilibriumLineAltitudeVersusTimeSelected() {
        return _equilibriumLineAltitudeVersusTimeRadioButton.isSelected();
    }
    
    public boolean isAccumulationVersusAltitudeSelected() {
        return _accumulationVersusAltitudeRadioButton.isSelected();
    }
    
    public boolean isAblationVersusAltitudeSelected() {
        return _ablationVersusAltitudeRadioButton.isSelected();
    }
    
    public boolean isMassBalanceVersusAltitudeSelected() {
        return _massBalanceVersusAltitudeRadioButton.isSelected();
    }
    
    public boolean isTemperatureVersusAltitudeSelected() {
        return _temperatureVersusAltitudeRadioButton.isSelected();
    }
    
    public boolean isValleyFloorVersusAltitudeSelected() {
        return _valleyFloorVersusAltitudeRadioButton.isSelected();
    }
    
    /**
     * Interface implemented by all listeners who are interested in events related to this control panel.
     */
    public static interface GraphsControlPanelListener {
        public void selectionChanged();
    }
    
    public static class GraphsControlPanelAdapter implements GraphsControlPanelListener {
        public void selectionChanged() {}
    }
    
    public void addListener( GraphsControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeListener( GraphsControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifySelectionChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            if ( o instanceof GraphsControlPanelListener ) {
                ( (GraphsControlPanelListener) o ).selectionChanged();
            }
        }
    }
}
