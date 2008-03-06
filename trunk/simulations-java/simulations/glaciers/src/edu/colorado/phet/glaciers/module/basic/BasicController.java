/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.module.basic;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.charts.EquilibriumLineAltitudeVersusTimeChart;
import edu.colorado.phet.glaciers.charts.GlacialBudgetVersusElevationChart;
import edu.colorado.phet.glaciers.charts.GlacierLengthVersusTimeChart;
import edu.colorado.phet.glaciers.charts.TemperatureVersusElevationChart;
import edu.colorado.phet.glaciers.control.ClimateControlPanel;
import edu.colorado.phet.glaciers.control.GraphsControlPanel;
import edu.colorado.phet.glaciers.control.MiscControlPanel;
import edu.colorado.phet.glaciers.control.ViewControlPanel;
import edu.colorado.phet.glaciers.control.ClimateControlPanel.ClimateControlPanelListener;
import edu.colorado.phet.glaciers.control.GraphsControlPanel.GraphsControlPanelListener;
import edu.colorado.phet.glaciers.control.MiscControlPanel.MiscControlPanelAdapter;
import edu.colorado.phet.glaciers.control.ViewControlPanel.ViewControlPanelListener;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.view.PlayArea;

/**
 * BasicController is the controller portion of the MVC architecture for the "Basic" module.
 * It handles all of the wiring between model, view, and controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicController {
    
    private static Frame DIALOG_OWNER = GlaciersApplication.instance().getPhetFrame();
    private static Dimension CHART_SIZE = new Dimension( 900, 350 );
    
    private JDialog _glacierLengthVersusTimeChart;
    private JDialog _equilibriumLineAltitudeVersusTimeChart;
    private JDialog _glacialBudgetVersusElevationChart;
    private JDialog _temperatureVersusElevationChart;
    
    public BasicController( final BasicModel model, final PlayArea playArea, final BasicControlPanel controlPanel ) {
        
        // Model
        final GlaciersClock clock = model.getClock();
        final Glacier glacier = model.getGlacier();
        final Climate climate = model.getClimate();
        
        // Controls
        final ViewControlPanel viewControlPanel = controlPanel.getViewControlPanel();
        final ClimateControlPanel climateControlPanel = controlPanel.getClimateControlPanel();
        final GraphsControlPanel graphsControlPanel = controlPanel.getGraphsControlPanel();
        final MiscControlPanel miscControlPanel = controlPanel.getMiscControlPanel();
        
        // Glacier
        glacier.addGlacierListener( new GlacierAdapter() {
            public void steadyStateChanged() {
                miscControlPanel.setEquilibriumButtonEnabled( !glacier.isSteadyState() );
            }
        } );
        
        // Climate
        climate.addClimateListener( new ClimateListener() {

            public void snowfallChanged() {
                climateControlPanel.setSnowfall( climate.getSnowfall() );
            }

            public void snowfallReferenceElevationChanged() {
                climateControlPanel.setSnowfallReferenceElevation( climate.getSnowfallReferenceElevation() );
            }

            public void temperatureChanged() {
                climateControlPanel.setTemperature( climate.getTemperature() );
            }
        } );
        
        // "View" controls
        viewControlPanel.addViewControlPanelListener( new ViewControlPanelListener() {
            
            public void equilibriumLineChanged( boolean b ) {
                playArea.setEquilibriumLineVisible( b );
            }

            public void coordinatesChanged( boolean b ) {
                playArea.setAxesVisible( b );
            }

            public void iceFlowChanged( boolean b ) {
                playArea.setIceFlowVisible( b );
            };
        });
        
        // "Climate" controls
        climateControlPanel.addClimateControlPanelListener( new ClimateControlPanelListener() {

            public void snowfallChanged( double snowfall ) {
                climate.setSnowfall( snowfall );
            }

            public void snowfallReferenceElevationChanged( double snowfallReferenceElevation ) {
                climate.setSnowfallReferenceElevation( snowfallReferenceElevation );
            }
            
            public void temperatureChanged( double temperature ) {
                climate.setTemperature( temperature );
            }
        });
        
        // "Graphs" controls
        graphsControlPanel.addGraphsControlPanelListener( new GraphsControlPanelListener() {

            public void glacierLengthVersusTimeChanged( boolean selected ) {
                if ( selected ) {
                    _glacierLengthVersusTimeChart = new GlacierLengthVersusTimeChart( DIALOG_OWNER, CHART_SIZE, glacier, clock );
                    _glacierLengthVersusTimeChart.addWindowListener( new WindowAdapter() {
                        // called when the close button in the dialog's window dressing is clicked
                        public void windowClosing( WindowEvent e ) {
                            graphsControlPanel.setGlacierLengthVerusTimeSelected( false );
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
            
            public void equilibriumLineAltitudeVersusTimeChanged( boolean selected ) {
                if ( selected ) {
                    _equilibriumLineAltitudeVersusTimeChart = new EquilibriumLineAltitudeVersusTimeChart( DIALOG_OWNER, CHART_SIZE, climate, clock );
                    _equilibriumLineAltitudeVersusTimeChart.addWindowListener( new WindowAdapter() {
                        // called when the close button in the dialog's window dressing is clicked
                        public void windowClosing( WindowEvent e ) {
                            graphsControlPanel.setEquilibriumLineAltitudeVersusTimeSelected( false );
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
            
            public void glacialBudgetVersusElevationChanged( boolean selected ) {
                if ( selected ) {
                    _glacialBudgetVersusElevationChart = new GlacialBudgetVersusElevationChart( DIALOG_OWNER, CHART_SIZE, climate );
                    _glacialBudgetVersusElevationChart.addWindowListener( new WindowAdapter() {
                        // called when the close button in the dialog's window dressing is clicked
                        public void windowClosing( WindowEvent e ) {
                            graphsControlPanel.setGlacialBudgetVersusElevationSelected( false );
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

            public void temperatureVersusElevationChanged( boolean selected ) {
                if ( selected ) {
                    _temperatureVersusElevationChart = new TemperatureVersusElevationChart( DIALOG_OWNER, CHART_SIZE, climate );
                    _temperatureVersusElevationChart.addWindowListener( new WindowAdapter() {
                        // called when the close button in the dialog's window dressing is clicked
                        public void windowClosing( WindowEvent e ) {
                            graphsControlPanel.setTemperatureVersusElevationSelected( false );
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
        });
        
        // Misc controls
        miscControlPanel.addMiscControlPanelListener( new MiscControlPanelAdapter() {
            public void equilibriumButtonPressed() {
                glacier.setSteadyState();
            }
        });
        
        // Initialization
        playArea.setEquilibriumLineVisible( viewControlPanel.isEquilibriumLineSelected() );
        playArea.setAxesVisible( viewControlPanel.isCoordinatesSelected() );
        climateControlPanel.setSnowfall( climate.getSnowfall() );
        climateControlPanel.setSnowfallReferenceElevation( climate.getSnowfallReferenceElevation() );
        climateControlPanel.setTemperature( climate.getTemperature() );
        miscControlPanel.setEquilibriumButtonEnabled( !glacier.isSteadyState() );
    }
}
