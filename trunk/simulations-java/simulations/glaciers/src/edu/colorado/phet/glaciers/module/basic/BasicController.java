/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.module.basic;

import edu.colorado.phet.glaciers.control.MassBalanceControlPanel;
import edu.colorado.phet.glaciers.control.SnowfallAndTemperatureControlPanel;
import edu.colorado.phet.glaciers.control.ViewControlPanel;
import edu.colorado.phet.glaciers.control.MassBalanceControlPanel.MassBalanceControlPanelListener;
import edu.colorado.phet.glaciers.control.SnowfallAndTemperatureControlPanel.SnowfallAndTemperatureControlPanelListener;
import edu.colorado.phet.glaciers.control.ViewControlPanel.ViewControlPanelAdapter;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
import edu.colorado.phet.glaciers.view.PlayArea;

/**
 * BasicController is the controller portion of the MVC architecture for the "Basic" module.
 * It handles all of the wiring between model, view, and controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicController {
    
    public BasicController( final BasicModel model, final PlayArea playArea, final BasicControlPanel controlPanel ) {
        
        // Model
        final Climate climate = model.getClimate();
        
        // Controls
        final ViewControlPanel viewControlPanel = controlPanel.getViewControlPanel();
        final SnowfallAndTemperatureControlPanel snowfallAndTemperatureControlPanel = controlPanel.getClimateControlPanel().getSnowfallAndTemperatureControlPanel();
        final MassBalanceControlPanel massBalanceControlPanel = controlPanel.getClimateControlPanel().getMassBalanceControlPanel();
        
        // Climate
        climate.addClimateListener( new ClimateListener() {

            public void snowfallChanged() {
                snowfallAndTemperatureControlPanel.setSnowfall( climate.getSnowfall() );
                massBalanceControlPanel.setMaximumSnowfall( climate.getMaximumSnowfall() );
//                massBalanceControlPanel.setEquilibriumLineAltitude( climate.getEquilibriumLineAltitude() );
            }

            public void snowfallReferenceElevationChanged() {
                snowfallAndTemperatureControlPanel.setSnowfallReferenceElevation( climate.getSnowfallReferenceElevation() );
//                massBalanceControlPanel.setEquilibriumLineAltitude( climate.getEquilibriumLineAltitude() );
            }

            public void temperatureChanged() {
                snowfallAndTemperatureControlPanel.setTemperature( climate.getTemperature() );
//                massBalanceControlPanel.setEquilibriumLineAltitude( climate.getEquilibriumLineAltitude() );
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
//                climate.setEquilibriumLineAltitude( altitude );
            }

            public void maximumSnowfallChanged( double maximumSnowfall ) {
                climate.setMaximumSnowfall( maximumSnowfall );
            }
        } );
        
        // Initialization
        playArea.setEquilibriumLineVisible( viewControlPanel.isEquilibriumLineSelected() );
        snowfallAndTemperatureControlPanel.setSnowfall( climate.getSnowfall() );
        snowfallAndTemperatureControlPanel.setSnowfallReferenceElevation( climate.getSnowfallReferenceElevation() );
        snowfallAndTemperatureControlPanel.setTemperature( climate.getTemperature() );
        massBalanceControlPanel.setMaximumSnowfall( climate.getMaximumSnowfall() );
//      massBalanceControlPanel.setEquilibriumLineAltitude( climate.getEquilibriumLineAltitude() );
    }

}
