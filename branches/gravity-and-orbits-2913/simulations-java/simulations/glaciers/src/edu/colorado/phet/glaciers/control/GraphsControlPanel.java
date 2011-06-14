// Copyright 2002-2011, University of Colorado

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
import edu.colorado.phet.glaciers.charts.ELAVersusTimeChart;
import edu.colorado.phet.glaciers.charts.GlacialBudgetVersusElevationChart;
import edu.colorado.phet.glaciers.charts.GlacierLengthVersusTimeChart;
import edu.colorado.phet.glaciers.charts.TemperatureVersusElevationChart;
import edu.colorado.phet.glaciers.model.GlaciersModel;
import edu.colorado.phet.glaciers.view.UnitsChangeListener;

/**
 * GraphsControlPanel is the control panel for creating graphs.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphsControlPanel extends AbstractSubPanel implements UnitsChangeListener {

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
    private final JCheckBox _elaVersusTimeCheckBox;
    private final JCheckBox _glacialBudgetVersusElevationCheckBox;
    private final JCheckBox _temperatureVersusElevationCheckBox;
    
    private GlacierLengthVersusTimeChart _glacierLengthVersusTimeChart;
    private ELAVersusTimeChart _elaVersusTimeChart;
    private JDialog _glacialBudgetVersusElevationChart;
    private JDialog _temperatureVersusElevationChart;

    private boolean _glacierLengthVersusTimeChartWasOpen;
    private boolean _elaVersusTimeChartWasOpen;
    private boolean _glacialBudgetVersusElevationChartWasOpen;
    private boolean _temperatureVersusElevationChartWasOpen;
    
    private Point _glacierLengthVersusTimeChartLocation;
    private Point _elaVersusTimeChartLocation;
    private Point _glacialBudgetVersusElevationChartLocation;
    private Point _temperatureVersusElevationChartLocation;
    
    private boolean _englishUnits;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GraphsControlPanel( GlaciersModel model, Frame dialogOwner, boolean englishUnits ) {
        super( TITLE_STRING, TITLE_COLOR, TITLE_FONT );

        _model = model;
        _dialogOwner = dialogOwner;
        _englishUnits = englishUnits;
        
        _glacierLengthVersusTimeCheckBox = new JCheckBox( GlaciersStrings.TITLE_GLACIER_LENGTH_VERSUS_TIME );
        _glacierLengthVersusTimeCheckBox.setFont( CONTROL_FONT );
        _glacierLengthVersusTimeCheckBox.setForeground( CONTROL_COLOR );
        _glacierLengthVersusTimeCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleGlacierLengthVersusTimeCheckBox();
            }
        } );

        _elaVersusTimeCheckBox = new JCheckBox( GlaciersStrings.TITLE_ELA_VERSUS_TIME );
        _elaVersusTimeCheckBox.setFont( CONTROL_FONT );
        _elaVersusTimeCheckBox.setForeground( CONTROL_COLOR );
        _elaVersusTimeCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleELAVersusTimeCheckBox();
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
        layout.addComponent( _elaVersusTimeCheckBox, row++, column );
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

    public void setELAVersusTimeSelected( boolean selected ) {
        if ( selected != isELAVersusTimeSelected() ) {
            _elaVersusTimeCheckBox.setSelected( selected );
            handleELAVersusTimeCheckBox();
        }
    }

    public boolean isELAVersusTimeSelected() {
        return _elaVersusTimeCheckBox.isSelected();
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
        setELAVersusTimeSelected( _elaVersusTimeChartWasOpen );
        setGlacialBudgetVersusElevationSelected( _glacialBudgetVersusElevationChartWasOpen );
        setTemperatureVersusElevationSelected( _temperatureVersusElevationChartWasOpen );
    }
    
    public void deactivate() {
        _glacierLengthVersusTimeChartWasOpen = isGlacierLengthVerusTimeSelected();
        _elaVersusTimeChartWasOpen = isELAVersusTimeSelected();
        _glacialBudgetVersusElevationChartWasOpen = isGlacialBudgetVersusElevationSelected();
        _temperatureVersusElevationChartWasOpen = isTemperatureVersusElevationSelected();
        setGlacierLengthVerusTimeSelected( false );
        setELAVersusTimeSelected( false );
        setGlacialBudgetVersusElevationSelected( false );
        setTemperatureVersusElevationSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleGlacierLengthVersusTimeCheckBox() {
        if ( _glacierLengthVersusTimeCheckBox.isSelected() ) {
            openGlacierLengthVersusTimeChart();
        }
        else {
            closeGlacierLengthVersusTimeChart();
        }
    }
    
    private void openGlacierLengthVersusTimeChart() {
        _glacierLengthVersusTimeChart = new GlacierLengthVersusTimeChart( _dialogOwner, CHART_SIZE, _model.getGlacier(), _model.getClock(), _englishUnits );
        setDialogLocation( _glacierLengthVersusTimeChart, _glacierLengthVersusTimeChartLocation );
        _glacierLengthVersusTimeChart.addWindowListener( new WindowAdapter() {
            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                setGlacierLengthVerusTimeSelected( false );
            }
        } );
        _glacierLengthVersusTimeChart.setVisible( true );
    }
    
    private void closeGlacierLengthVersusTimeChart() {
        if ( _glacierLengthVersusTimeChart != null ) {
            _glacierLengthVersusTimeChartLocation = _glacierLengthVersusTimeChart.getLocation();
            _glacierLengthVersusTimeChart.dispose();
            _glacierLengthVersusTimeChart = null;
        }
    }
    
    private void handleELAVersusTimeCheckBox() {
        if ( _elaVersusTimeCheckBox.isSelected() ) {
            openELAVersusTimeChart();
        }
        else {
            closeELAVersusTimeChart();
        }
    }
    
    private void openELAVersusTimeChart() {
        _elaVersusTimeChart = new ELAVersusTimeChart( _dialogOwner, CHART_SIZE, _model.getClimate(), _model.getClock(), _englishUnits );
        setDialogLocation( _elaVersusTimeChart, _elaVersusTimeChartLocation );
        _elaVersusTimeChart.addWindowListener( new WindowAdapter() {
            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                setELAVersusTimeSelected( false );
            }
        } );
        _elaVersusTimeChart.setVisible( true );
    }
    
    private void closeELAVersusTimeChart() {
        if ( _elaVersusTimeChart != null ) {
            _elaVersusTimeChartLocation = _elaVersusTimeChart.getLocation();
            _elaVersusTimeChart.dispose();
            _elaVersusTimeChart = null;
        }
    }

    private void handleGlacialBudgetVersusElevationCheckBox() {
        if ( _glacialBudgetVersusElevationCheckBox.isSelected() ) {
            openGlacialBudgetVersusElevationChart();
        }
        else {
            closeGlacialBudgetVersusElevationChart();
        }
    }
    
    private void openGlacialBudgetVersusElevationChart() {
        _glacialBudgetVersusElevationChart = new GlacialBudgetVersusElevationChart( _dialogOwner, CHART_SIZE, _model.getClimate(), _englishUnits );
        setDialogLocation( _glacialBudgetVersusElevationChart, _glacialBudgetVersusElevationChartLocation );
        _glacialBudgetVersusElevationChart.addWindowListener( new WindowAdapter() {
            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                setGlacialBudgetVersusElevationSelected( false );
            }
        } );
        _glacialBudgetVersusElevationChart.setVisible( true );
    }
    
    private void closeGlacialBudgetVersusElevationChart() {
        if ( _glacialBudgetVersusElevationChart != null ) {
            _glacialBudgetVersusElevationChartLocation = _glacialBudgetVersusElevationChart.getLocation();
            _glacialBudgetVersusElevationChart.dispose();
            _glacialBudgetVersusElevationChart = null;
        }
    }
    
    private void handleTemperatureVersusElevationCheckBox() {
        if ( _temperatureVersusElevationCheckBox.isSelected() ) {
            openTemperatureVersusElevationChart();
        }
        else {
            closeTemperatureVersusElevationChart();
        }
    }
    
    private void openTemperatureVersusElevationChart() {
        _temperatureVersusElevationChart = new TemperatureVersusElevationChart( _dialogOwner, CHART_SIZE, _model.getClimate(), _englishUnits );
        setDialogLocation( _temperatureVersusElevationChart, _temperatureVersusElevationChartLocation );
        _temperatureVersusElevationChart.addWindowListener( new WindowAdapter() {
            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                setTemperatureVersusElevationSelected( false );
            }
        } );
        _temperatureVersusElevationChart.setVisible( true );
    }
    
    private void closeTemperatureVersusElevationChart() {
        if ( _temperatureVersusElevationChart != null ) {
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
    
    /*
     * UnitsChangeListener implementation, updates any charts that are open.
     */
    public void unitsChanged( boolean englishUnits ) {
        if ( englishUnits != _englishUnits ) {
            
            _englishUnits = englishUnits;
            
            // time-based charts are converted so we don't lose data
            if ( _glacierLengthVersusTimeChart != null ) {
                _glacierLengthVersusTimeChart.setEnglishUnits( englishUnits );
            }
            if ( _elaVersusTimeChart != null ) {
                _elaVersusTimeChart.setEnglishUnits( englishUnits );
            }
            
            // static charts are closed and reopened
            if ( _glacialBudgetVersusElevationChart != null ) {
                closeGlacialBudgetVersusElevationChart();
                openGlacialBudgetVersusElevationChart();
            }
            if ( _temperatureVersusElevationChart != null ) {
                closeTemperatureVersusElevationChart();
                openTemperatureVersusElevationChart();
            }
        }
    }
}
