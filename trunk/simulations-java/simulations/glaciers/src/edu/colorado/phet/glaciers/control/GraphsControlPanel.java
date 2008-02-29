/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * GraphsControlPanel is the control panel for creating graphs.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphsControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.INNER_PANEL_BACKGROUND_COLOR;
    private static final Color TITLE_COLOR = GlaciersConstants.INNER_PANEL_TITLE_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.CONTROL_PANEL_TITLE_FONT;
    private static final Font CONTROL_FONT = GlaciersConstants.CONTROL_PANEL_CONTROL_FONT;
    private static final Color CONTROL_COLOR = GlaciersConstants.INNER_PANEL_CONTROL_COLOR;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JCheckBox _glacierLengthVersusTimeCheckBox;
    private JCheckBox _equilibriumLineAltitudeVersusTimeCheckBox;
    private JCheckBox _glacialBudgetVersusElevationCheckBox;
    private JCheckBox _temperatureVersusElevationCheckBox;
    
    private ArrayList _listeners; // list of GraphsControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GraphsControlPanel() {
        super();
        
        _listeners = new ArrayList();
        
        Border emptyBorder = BorderFactory.createEmptyBorder( 3, 3, 3, 3 );
        TitledBorder titledBorder = new TitledBorder( GlaciersStrings.TITLE_GRAPH_CONTROLS );
        titledBorder.setTitleFont( TITLE_FONT );
        titledBorder.setTitleColor( TITLE_COLOR );
        titledBorder.setBorder( BorderFactory.createLineBorder( TITLE_COLOR, 1 ) );
        Border compoundBorder = BorderFactory.createCompoundBorder( emptyBorder, titledBorder );
        setBorder( compoundBorder );
        
        _glacierLengthVersusTimeCheckBox = new JCheckBox( GlaciersStrings.TITLE_GLACIER_LENGTH_VERSUS_TIME );
        _glacierLengthVersusTimeCheckBox.setFont( CONTROL_FONT );
        _glacierLengthVersusTimeCheckBox.setForeground( CONTROL_COLOR );
        _glacierLengthVersusTimeCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyGlacierLengthVersusTimeChanged();
            }
        });
        
        _equilibriumLineAltitudeVersusTimeCheckBox = new JCheckBox( GlaciersStrings.TITLE_EQUILIBRIUM_LINE_ALTITUDE_VERSUS_TIME );
        _equilibriumLineAltitudeVersusTimeCheckBox.setFont( CONTROL_FONT );
        _equilibriumLineAltitudeVersusTimeCheckBox.setForeground( CONTROL_COLOR );
        _equilibriumLineAltitudeVersusTimeCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyEquilibriumLineAltitudeVersusTimeChanged();
            }
        });
        
        _glacialBudgetVersusElevationCheckBox = new JCheckBox( GlaciersStrings.TITLE_GLACIAL_BUDGET_VERSUS_ELEVATION );
        _glacialBudgetVersusElevationCheckBox.setFont( CONTROL_FONT );
        _glacialBudgetVersusElevationCheckBox.setForeground( CONTROL_COLOR );
        _glacialBudgetVersusElevationCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyGlacialBudgetVersusElevationChanged();
            }
        });
        
        _temperatureVersusElevationCheckBox = new JCheckBox( GlaciersStrings.TITLE_TEMPERATURE_VERSUS_ELEVATION );
        _temperatureVersusElevationCheckBox.setFont( CONTROL_FONT );
        _temperatureVersusElevationCheckBox.setForeground( CONTROL_COLOR );
        _temperatureVersusElevationCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyTemperatureVersusElevationChanged();
            }
        });
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _glacierLengthVersusTimeCheckBox, row++, column );
        layout.addComponent( _equilibriumLineAltitudeVersusTimeCheckBox, row++, column );
        layout.addComponent( _glacialBudgetVersusElevationCheckBox, row++, column );
        layout.addComponent( _temperatureVersusElevationCheckBox, row++, column );
        
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, null /* exclusedClasses */, false /* processContentsOfExcludedContainers */ );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setGlacierLengthVerusTimeSelected( boolean selected ) {
        if ( selected != isGlacierLengthVerusTimeSelected() ) {
            _glacierLengthVersusTimeCheckBox.setSelected( selected );
            notifyGlacierLengthVersusTimeChanged();
        }
    }
    
    public boolean isGlacierLengthVerusTimeSelected() {
        return _glacierLengthVersusTimeCheckBox.isSelected();
    }
    
    public void setEquilibriumLineAltitudeVersusTimeSelected( boolean selected ) {
        if ( selected != isEquilibriumLineAltitudeVersusTimeSelected() ) {
            _equilibriumLineAltitudeVersusTimeCheckBox.setSelected( selected );
            notifyEquilibriumLineAltitudeVersusTimeChanged();
        }
    }
    
    public boolean isEquilibriumLineAltitudeVersusTimeSelected() {
        return _equilibriumLineAltitudeVersusTimeCheckBox.isSelected();
    }
    
    public void setGlacialBudgetVersusElevationSelected( boolean selected ) {
        if ( selected != isGlacialBudgetVersusElevationSelected() ) {
            _glacialBudgetVersusElevationCheckBox.setSelected( selected );
            notifyGlacialBudgetVersusElevationChanged();
        }
    }
    
    public boolean isGlacialBudgetVersusElevationSelected() {
        return _glacialBudgetVersusElevationCheckBox.isSelected();
    }
    
    public void setTemperatureVersusElevationSelected( boolean selected ) {
        if ( selected != isTemperatureVersusElevationSelected() ) {
            _temperatureVersusElevationCheckBox.setSelected( selected );
            notifyTemperatureVersusElevationChanged();
        }
    }
    
    public boolean isTemperatureVersusElevationSelected() {
        return _temperatureVersusElevationCheckBox.isSelected();
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    /**
     * Interface implemented by all listeners who are interested in events related to this control panel.
     */
    public interface GraphsControlPanelListener {
        public void glacierLengthVersusTimeChanged( boolean selected );
        public void equilibriumLineAltitudeVersusTimeChanged( boolean selected );
        public void glacialBudgetVersusElevationChanged( boolean selected );
        public void temperatureVersusElevationChanged( boolean selected );
    }
    
    public static class GraphsControlPanelAdapter implements GraphsControlPanelListener {
        public void glacierLengthVersusTimeChanged( boolean selected ) {}
        public void equilibriumLineAltitudeVersusTimeChanged( boolean selected ) {}
        public void glacialBudgetVersusElevationChanged( boolean selected ) {}
        public void temperatureVersusElevationChanged( boolean selected ) {}
    }
    
    public void addGraphsControlPanelListener( GraphsControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeGraphsControlPanelListener( GraphsControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    private void notifyGlacierLengthVersusTimeChanged() {
        boolean selected = isGlacierLengthVerusTimeSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (GraphsControlPanelListener) i.next() ).glacierLengthVersusTimeChanged( selected );
        }
    }
    
    private void notifyEquilibriumLineAltitudeVersusTimeChanged() {
        boolean selected = isEquilibriumLineAltitudeVersusTimeSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (GraphsControlPanelListener) i.next() ).equilibriumLineAltitudeVersusTimeChanged( selected );
        }
    }
    
    private void notifyGlacialBudgetVersusElevationChanged() {
        boolean selected = isGlacialBudgetVersusElevationSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (GraphsControlPanelListener) i.next() ).glacialBudgetVersusElevationChanged( selected );
        }
    }
    
    private void notifyTemperatureVersusElevationChanged() {
        boolean selected = isTemperatureVersusElevationSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (GraphsControlPanelListener) i.next() ).temperatureVersusElevationChanged( selected );
        }
    }
}
