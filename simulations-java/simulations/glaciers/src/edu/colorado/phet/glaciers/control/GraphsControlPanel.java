/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.charts.EquilibriumLineAltitudeVersusTimeChart;
import edu.colorado.phet.glaciers.charts.GlacialBudgetVersusElevationChart;
import edu.colorado.phet.glaciers.charts.GlacierLengthVersusTimeChart;
import edu.colorado.phet.glaciers.charts.TemperatureVersusElevationChart;
import edu.colorado.phet.glaciers.model.GlaciersModel;

/**
 * GraphsControlPanel is the control panel for creating graphs.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphsControlPanel extends AbstractSubPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static Dimension CHART_SIZE = new Dimension( 900, 350 );
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.SUBPANEL_BACKGROUND_COLOR;
    private static final String TITLE_STRING = GlaciersStrings.TITLE_GRAPH_CONTROLS;
    private static final Color TITLE_COLOR = GlaciersConstants.SUBPANEL_TITLE_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.SUBPANEL_TITLE_FONT;
    private static final Font CONTROL_FONT = GlaciersConstants.SUBPANEL_CONTROL_FONT;
    private static final Color CONTROL_COLOR = GlaciersConstants.SUBPANEL_CONTROL_COLOR;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final GlaciersModel _model;
    private final Frame _dialogOwner;
    
    private final JCheckBox _glacierLengthVersusTimeCheckBox;
    private final JCheckBox _equilibriumLineAltitudeVersusTimeCheckBox;
    private final JCheckBox _glacialBudgetVersusElevationCheckBox;
    private final JCheckBox _temperatureVersusElevationCheckBox;
    
    private JDialog _glacierLengthVersusTimeChart;
    private JDialog _equilibriumLineAltitudeVersusTimeChart;
    private JDialog _glacialBudgetVersusElevationChart;
    private JDialog _temperatureVersusElevationChart;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GraphsControlPanel( GlaciersModel model, Frame dialogOwner ) {
        super( TITLE_STRING, TITLE_COLOR, TITLE_FONT );

        _model = model;
        _dialogOwner = dialogOwner;
        
        _glacierLengthVersusTimeCheckBox = new JCheckBox( GlaciersStrings.TITLE_GLACIER_LENGTH_VERSUS_TIME );
        _glacierLengthVersusTimeCheckBox.setFont( CONTROL_FONT );
        _glacierLengthVersusTimeCheckBox.setForeground( CONTROL_COLOR );
        _glacierLengthVersusTimeCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleGlacierLengthVersusTimeCheckBox();
            }
        } );

        _equilibriumLineAltitudeVersusTimeCheckBox = new JCheckBox( GlaciersStrings.TITLE_EQUILIBRIUM_LINE_ALTITUDE_VERSUS_TIME );
        _equilibriumLineAltitudeVersusTimeCheckBox.setFont( CONTROL_FONT );
        _equilibriumLineAltitudeVersusTimeCheckBox.setForeground( CONTROL_COLOR );
        _equilibriumLineAltitudeVersusTimeCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleEquilibriumLineAltitudeVersusTimeCheckBox();
            }
        } );

        _glacialBudgetVersusElevationCheckBox = new JCheckBox( GlaciersStrings.TITLE_GLACIAL_BUDGET_VERSUS_ELEVATION );
        _glacialBudgetVersusElevationCheckBox.setFont( CONTROL_FONT );
        _glacialBudgetVersusElevationCheckBox.setForeground( CONTROL_COLOR );
        _glacialBudgetVersusElevationCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleGlacialBudgetVersusElevationCheckBox();
            }
        } );

        _temperatureVersusElevationCheckBox = new JCheckBox( GlaciersStrings.TITLE_TEMPERATURE_VERSUS_ELEVATION );
        _temperatureVersusElevationCheckBox.setFont( CONTROL_FONT );
        _temperatureVersusElevationCheckBox.setForeground( CONTROL_COLOR );
        _temperatureVersusElevationCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleTemperatureVersusElevationCheckBox();
            }
        } );

        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
//        layout.addComponent( _glacierLengthVersusTimeCheckBox, row++, column ); //XXX hide for 7/25/08 deadline
//        layout.addComponent( _equilibriumLineAltitudeVersusTimeCheckBox, row++, column ); // hide for 7/25/08 deadline
        layout.addComponent( _glacialBudgetVersusElevationCheckBox, row++, column );
        layout.addComponent( _temperatureVersusElevationCheckBox, row++, column );

        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, null /* exclusedClasses */, false /* processContentsOfExcludedContainers */);
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public void setGlacierLengthVerusTimeSelected( boolean selected ) {
        if ( selected != isGlacierLengthVerusTimeSelected() ) {
            _glacierLengthVersusTimeCheckBox.setSelected( selected );
        }
    }

    public boolean isGlacierLengthVerusTimeSelected() {
        return _glacierLengthVersusTimeCheckBox.isSelected();
    }

    public void setEquilibriumLineAltitudeVersusTimeSelected( boolean selected ) {
        if ( selected != isEquilibriumLineAltitudeVersusTimeSelected() ) {
            _equilibriumLineAltitudeVersusTimeCheckBox.setSelected( selected );
        }
    }

    public boolean isEquilibriumLineAltitudeVersusTimeSelected() {
        return _equilibriumLineAltitudeVersusTimeCheckBox.isSelected();
    }

    public void setGlacialBudgetVersusElevationSelected( boolean selected ) {
        if ( selected != isGlacialBudgetVersusElevationSelected() ) {
            _glacialBudgetVersusElevationCheckBox.setSelected( selected );
        }
    }

    public boolean isGlacialBudgetVersusElevationSelected() {
        return _glacialBudgetVersusElevationCheckBox.isSelected();
    }

    public void setTemperatureVersusElevationSelected( boolean selected ) {
        if ( selected != isTemperatureVersusElevationSelected() ) {
            _temperatureVersusElevationCheckBox.setSelected( selected );
        }
    }

    public boolean isTemperatureVersusElevationSelected() {
        return _temperatureVersusElevationCheckBox.isSelected();
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleGlacierLengthVersusTimeCheckBox() {
        if ( _glacierLengthVersusTimeCheckBox.isSelected() ) {
            _glacierLengthVersusTimeChart = new GlacierLengthVersusTimeChart( _dialogOwner, CHART_SIZE, _model.getGlacier(), _model.getClock() );
            _glacierLengthVersusTimeChart.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    setGlacierLengthVerusTimeSelected( false );
                }
            } );
            SwingUtils.centerDialogInParent( _glacierLengthVersusTimeChart );
            _glacierLengthVersusTimeChart.setVisible( true );
        }
        else {
            _glacierLengthVersusTimeChart.dispose();
            _glacierLengthVersusTimeChart = null;
        }
    }
    
    private void handleEquilibriumLineAltitudeVersusTimeCheckBox() {
        if ( _equilibriumLineAltitudeVersusTimeCheckBox.isSelected() ) {
            _equilibriumLineAltitudeVersusTimeChart = new EquilibriumLineAltitudeVersusTimeChart( _dialogOwner, CHART_SIZE, _model.getClimate(), _model.getClock() );
            _equilibriumLineAltitudeVersusTimeChart.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    setEquilibriumLineAltitudeVersusTimeSelected( false );
                }
            } );
            SwingUtils.centerDialogInParent( _equilibriumLineAltitudeVersusTimeChart );
            _equilibriumLineAltitudeVersusTimeChart.setVisible( true );
        }
        else {
            _equilibriumLineAltitudeVersusTimeChart.dispose();
            _equilibriumLineAltitudeVersusTimeChart = null;
        }
    }

    private void handleGlacialBudgetVersusElevationCheckBox() {
        if ( _glacialBudgetVersusElevationCheckBox.isSelected() ) {
            _glacialBudgetVersusElevationChart = new GlacialBudgetVersusElevationChart( _dialogOwner, CHART_SIZE, _model.getClimate() );
            _glacialBudgetVersusElevationChart.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    setGlacialBudgetVersusElevationSelected( false );
                }
            } );
            SwingUtils.centerDialogInParent( _glacialBudgetVersusElevationChart );
            _glacialBudgetVersusElevationChart.setVisible( true );
        }
        else {
            _glacialBudgetVersusElevationChart.dispose();
            _glacialBudgetVersusElevationChart = null;
        }
    }
    
    private void handleTemperatureVersusElevationCheckBox() {
        if ( _temperatureVersusElevationCheckBox.isSelected() ) {
            _temperatureVersusElevationChart = new TemperatureVersusElevationChart( _dialogOwner, CHART_SIZE, _model.getClimate() );
            _temperatureVersusElevationChart.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    setTemperatureVersusElevationSelected( false );
                }
            } );
            SwingUtils.centerDialogInParent( _temperatureVersusElevationChart );
            _temperatureVersusElevationChart.setVisible( true );
        }
        else {
            _temperatureVersusElevationChart.dispose();
            _temperatureVersusElevationChart = null;
        }
    }
    
}
