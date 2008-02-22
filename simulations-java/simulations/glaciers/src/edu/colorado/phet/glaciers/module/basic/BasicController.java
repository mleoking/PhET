/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.module.basic;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.charts.AblationVersusElevationChart;
import edu.colorado.phet.glaciers.charts.AccumulationVersusElevationChart;
import edu.colorado.phet.glaciers.charts.GlacialBudgetVersusElevationChart;
import edu.colorado.phet.glaciers.charts.TemperatureVersusElevationChart;
import edu.colorado.phet.glaciers.control.GraphsControlPanel;
import edu.colorado.phet.glaciers.control.MassBalanceControlPanel;
import edu.colorado.phet.glaciers.control.SnowfallAndTemperatureControlPanel;
import edu.colorado.phet.glaciers.control.ViewControlPanel;
import edu.colorado.phet.glaciers.control.GraphsControlPanel.GraphsControlPanelListener;
import edu.colorado.phet.glaciers.control.MassBalanceControlPanel.MassBalanceControlPanelListener;
import edu.colorado.phet.glaciers.control.SnowfallAndTemperatureControlPanel.SnowfallAndTemperatureControlPanelListener;
import edu.colorado.phet.glaciers.control.ViewControlPanel.ViewControlPanelAdapter;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
import edu.colorado.phet.glaciers.view.PlayArea;

/**
 * BasicController is the controller portion of the MVC architecture for the "Basic" module.
 * It handles all of the wiring between model, view, and controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicController {
    
    private static Frame DIALOG_OWNER = GlaciersApplication.instance().getPhetFrame();
    private static Dimension DIALOG_SIZE = new Dimension( 700, 400 );
    
    private JDialog _accumulationVersusElevationChart;
    private JDialog _ablationVersusElevationChart;
    private JDialog _glacialBudgetVersusElevationChart;
    private JDialog _temperatureVersusElevationChart;
    
    public BasicController( final BasicModel model, final PlayArea playArea, final BasicControlPanel controlPanel ) {
        
        // Model
        final Glacier glacier = model.getGlacier();
        final Climate climate = model.getClimate();
        
        // Controls
        final ViewControlPanel viewControlPanel = controlPanel.getViewControlPanel();
        final SnowfallAndTemperatureControlPanel snowfallAndTemperatureControlPanel = controlPanel.getClimateControlPanel().getSnowfallAndTemperatureControlPanel();
        final MassBalanceControlPanel massBalanceControlPanel = controlPanel.getClimateControlPanel().getMassBalanceControlPanel();
        final GraphsControlPanel graphsControlPanel = controlPanel.getGraphsControlPanel();
        
        // Climate
        climate.addClimateListener( new ClimateListener() {

            public void snowfallChanged() {
                snowfallAndTemperatureControlPanel.setSnowfall( climate.getSnowfall() );
                massBalanceControlPanel.setMaximumSnowfall( climate.getMaximumSnowfall() );
                massBalanceControlPanel.setEquilibriumLineAltitude( climate.getEquilibriumLineAltitude() );
            }

            public void snowfallReferenceElevationChanged() {
                snowfallAndTemperatureControlPanel.setSnowfallReferenceElevation( climate.getSnowfallReferenceElevation() );
                massBalanceControlPanel.setEquilibriumLineAltitude( climate.getEquilibriumLineAltitude() );
            }

            public void temperatureChanged() {
                snowfallAndTemperatureControlPanel.setTemperature( climate.getTemperature() );
                massBalanceControlPanel.setEquilibriumLineAltitude( climate.getEquilibriumLineAltitude() );
            }
        } );
        
        // "View" controls
        viewControlPanel.addViewControlPanelListener( new ViewControlPanelAdapter() {
            
            public void equilibriumLineChanged( boolean b ) {
                playArea.setEquilibriumLineVisible( viewControlPanel.isEquilibriumLineSelected() );
            };
        });
        
        // "Snowfall & Temperature" controls
        snowfallAndTemperatureControlPanel.addSnowfallAndTemperatureControlPanelListener( new SnowfallAndTemperatureControlPanelListener() {

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
        
        // "Mass Balance" controls
        massBalanceControlPanel.addMassBalanaceControlPanelListener( new MassBalanceControlPanelListener() {

            public void equilibriumLineAltitudeChanged( double altitude ) {
                climate.setEquilibriumLineAltitude( altitude );
            }

            public void maximumSnowfallChanged( double maximumSnowfall ) {
                climate.setMaximumSnowfall( maximumSnowfall );
            }
        } );
        
        // "Graphs" controls
        graphsControlPanel.addGraphsControlPanelListener( new GraphsControlPanelListener() {

            public void glacierLengthVersusTimeChanged( boolean selected ) {
                // TODO Auto-generated method stub
            }
            
            public void equilibriumLineAltitudeVersusTimeChanged( boolean selected ) {
                // TODO Auto-generated method stub
            }
            
            public void ablationVersusElevationChanged( boolean selected ) {
                if ( selected ) {
                    _ablationVersusElevationChart = new AblationVersusElevationChart( DIALOG_OWNER, DIALOG_SIZE, climate );
                    _ablationVersusElevationChart.addWindowListener( new WindowAdapter() {
                        // called when the close button in the dialog's window dressing is clicked
                        public void windowClosing( WindowEvent e ) {
                            graphsControlPanel.setAblationVersusElevationSelected( false );
                        }
                    } );
                    SwingUtils.centerDialogInParent( _ablationVersusElevationChart );
                    _ablationVersusElevationChart.setVisible( true );
                }
                else {
                    _ablationVersusElevationChart.dispose();
                    _ablationVersusElevationChart = null;
                }
            }

            public void accumulationVersusElevationChanged( boolean selected ) {
                if ( selected ) {
                    _accumulationVersusElevationChart = new AccumulationVersusElevationChart( DIALOG_OWNER, DIALOG_SIZE, climate );
                    _accumulationVersusElevationChart.addWindowListener( new WindowAdapter() {
                        // called when the close button in the dialog's window dressing is clicked
                        public void windowClosing( WindowEvent e ) {
                            graphsControlPanel.setAccumulationVersusElevationSelected( false );
                        }
                    } );
                    SwingUtils.centerDialogInParent( _accumulationVersusElevationChart );
                    _accumulationVersusElevationChart.setVisible( true );
                }
                else {
                    _accumulationVersusElevationChart.dispose();
                    _accumulationVersusElevationChart = null;
                }
            }

            public void glacialBudgetVersusElevationChanged( boolean selected ) {
                if ( selected ) {
                    _glacialBudgetVersusElevationChart = new GlacialBudgetVersusElevationChart( DIALOG_OWNER, DIALOG_SIZE, climate );
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
                    _temperatureVersusElevationChart = new TemperatureVersusElevationChart( DIALOG_OWNER, DIALOG_SIZE, climate );
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
        
        // Initialization
        playArea.setEquilibriumLineVisible( viewControlPanel.isEquilibriumLineSelected() );
        snowfallAndTemperatureControlPanel.setSnowfall( climate.getSnowfall() );
        snowfallAndTemperatureControlPanel.setSnowfallReferenceElevation( climate.getSnowfallReferenceElevation() );
        snowfallAndTemperatureControlPanel.setTemperature( climate.getTemperature() );
        massBalanceControlPanel.setMaximumSnowfall( climate.getMaximumSnowfall() );
        massBalanceControlPanel.setEquilibriumLineAltitude( climate.getEquilibriumLineAltitude() );
    }
}
