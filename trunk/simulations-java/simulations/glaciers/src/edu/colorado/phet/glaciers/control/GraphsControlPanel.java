/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.*;
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

    private boolean _glacierLengthVersusTimeChartWasOpen;
    private boolean _equilibriumLineAltitudeVersusTimeChartWasOpen;
    private boolean _glacialBudgetVersusElevationChartWasOpen;
    private boolean _temperatureVersusElevationChartWasOpen;
    
    private Point _glacierLengthVersusTimeChartLocation;
    private Point _equilibriumLineAltitudeVersusTimeChartLocation;
    private Point _glacialBudgetVersusElevationChartLocation;
    private Point _temperatureVersusElevationChartLocation;
    
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
        layout.addComponent( _glacierLengthVersusTimeCheckBox, row++, column );
        layout.addComponent( _equilibriumLineAltitudeVersusTimeCheckBox, row++, column );
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
            handleGlacierLengthVersusTimeCheckBox();
        }
    }

    public boolean isGlacierLengthVerusTimeSelected() {
        return _glacierLengthVersusTimeCheckBox.isSelected();
    }

    public void setEquilibriumLineAltitudeVersusTimeSelected( boolean selected ) {
        if ( selected != isEquilibriumLineAltitudeVersusTimeSelected() ) {
            _equilibriumLineAltitudeVersusTimeCheckBox.setSelected( selected );
            handleEquilibriumLineAltitudeVersusTimeCheckBox();
        }
    }

    public boolean isEquilibriumLineAltitudeVersusTimeSelected() {
        return _equilibriumLineAltitudeVersusTimeCheckBox.isSelected();
    }

    public void setGlacialBudgetVersusElevationSelected( boolean selected ) {
        if ( selected != isGlacialBudgetVersusElevationSelected() ) {
            _glacialBudgetVersusElevationCheckBox.setSelected( selected );
            handleGlacialBudgetVersusElevationCheckBox();
        }
    }

    public boolean isGlacialBudgetVersusElevationSelected() {
        return _glacialBudgetVersusElevationCheckBox.isSelected();
    }

    public void setTemperatureVersusElevationSelected( boolean selected ) {
        if ( selected != isTemperatureVersusElevationSelected() ) {
            _temperatureVersusElevationCheckBox.setSelected( selected );
            handleTemperatureVersusElevationCheckBox();
        }
    }

    public boolean isTemperatureVersusElevationSelected() {
        return _temperatureVersusElevationCheckBox.isSelected();
    }
    
    public void activate() {
        setGlacierLengthVerusTimeSelected( _glacierLengthVersusTimeChartWasOpen );
        setEquilibriumLineAltitudeVersusTimeSelected( _equilibriumLineAltitudeVersusTimeChartWasOpen );
        setGlacialBudgetVersusElevationSelected( _glacialBudgetVersusElevationChartWasOpen );
        setTemperatureVersusElevationSelected( _temperatureVersusElevationChartWasOpen );
    }
    
    public void deactivate() {
        _glacierLengthVersusTimeChartWasOpen = isGlacierLengthVerusTimeSelected();
        _equilibriumLineAltitudeVersusTimeChartWasOpen = isEquilibriumLineAltitudeVersusTimeSelected();
        _glacialBudgetVersusElevationChartWasOpen = isGlacialBudgetVersusElevationSelected();
        _temperatureVersusElevationChartWasOpen = isTemperatureVersusElevationSelected();
        setGlacierLengthVerusTimeSelected( false );
        setEquilibriumLineAltitudeVersusTimeSelected( false );
        setGlacialBudgetVersusElevationSelected( false );
        setTemperatureVersusElevationSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleGlacierLengthVersusTimeCheckBox() {
        if ( _glacierLengthVersusTimeCheckBox.isSelected() ) {
            _glacierLengthVersusTimeChart = new GlacierLengthVersusTimeChart( _dialogOwner, CHART_SIZE, _model.getGlacier(), _model.getClock() );
            setDialogLocation( _glacierLengthVersusTimeChart, _glacierLengthVersusTimeChartLocation );
            _glacierLengthVersusTimeChart.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    setGlacierLengthVerusTimeSelected( false );
                }
            } );
            _glacierLengthVersusTimeChart.setVisible( true );
        }
        else {
            _glacierLengthVersusTimeChartLocation = _glacierLengthVersusTimeChart.getLocation();
            _glacierLengthVersusTimeChart.dispose();
            _glacierLengthVersusTimeChart = null;
        }
    }
    
    private void handleEquilibriumLineAltitudeVersusTimeCheckBox() {
        if ( _equilibriumLineAltitudeVersusTimeCheckBox.isSelected() ) {
            _equilibriumLineAltitudeVersusTimeChart = new EquilibriumLineAltitudeVersusTimeChart( _dialogOwner, CHART_SIZE, _model.getClimate(), _model.getClock() );
            setDialogLocation( _equilibriumLineAltitudeVersusTimeChart, _equilibriumLineAltitudeVersusTimeChartLocation );
            _equilibriumLineAltitudeVersusTimeChart.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    setEquilibriumLineAltitudeVersusTimeSelected( false );
                }
            } );
            _equilibriumLineAltitudeVersusTimeChart.setVisible( true );
        }
        else {
            _equilibriumLineAltitudeVersusTimeChartLocation = _equilibriumLineAltitudeVersusTimeChart.getLocation();
            _equilibriumLineAltitudeVersusTimeChart.dispose();
            _equilibriumLineAltitudeVersusTimeChart = null;
        }
    }

    private void handleGlacialBudgetVersusElevationCheckBox() {
        if ( _glacialBudgetVersusElevationCheckBox.isSelected() ) {
            _glacialBudgetVersusElevationChart = new GlacialBudgetVersusElevationChart( _dialogOwner, CHART_SIZE, _model.getClimate() );
            setDialogLocation( _glacialBudgetVersusElevationChart, _glacialBudgetVersusElevationChartLocation );
            _glacialBudgetVersusElevationChart.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    setGlacialBudgetVersusElevationSelected( false );
                }
            } );
            _glacialBudgetVersusElevationChart.setVisible( true );
        }
        else {
            _glacialBudgetVersusElevationChartLocation = _glacialBudgetVersusElevationChart.getLocation();
            _glacialBudgetVersusElevationChart.dispose();
            _glacialBudgetVersusElevationChart = null;
        }
    }
    
    private void handleTemperatureVersusElevationCheckBox() {
        if ( _temperatureVersusElevationCheckBox.isSelected() ) {
            _temperatureVersusElevationChart = new TemperatureVersusElevationChart( _dialogOwner, CHART_SIZE, _model.getClimate() );
            setDialogLocation( _temperatureVersusElevationChart, _temperatureVersusElevationChartLocation );
            _temperatureVersusElevationChart.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    setTemperatureVersusElevationSelected( false );
                }
            } );
            _temperatureVersusElevationChart.setVisible( true );
        }
        else {
            _temperatureVersusElevationChartLocation = _temperatureVersusElevationChart.getLocation();
            _temperatureVersusElevationChart.dispose();
            _temperatureVersusElevationChart = null;
        }
    }
    
    /*
     * Sets a dialog's location. 
     * If the location is null, center the dialog in its parent Frame.
     */
    private static void setDialogLocation( JDialog dialog, Point location ) {
        if ( location != null ) {
            dialog.setLocation( location );
        }
        else {
            SwingUtils.centerDialogInParent( dialog );
        }
    }
}
