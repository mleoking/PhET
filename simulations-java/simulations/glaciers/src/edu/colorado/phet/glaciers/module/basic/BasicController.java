/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.module.basic;

import edu.colorado.phet.glaciers.control.SnowfallAndTemperatureControlPanel;
import edu.colorado.phet.glaciers.control.SnowfallAndTemperatureControlPanel.SnowfallAndTemperatureControlPanelListener;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;

/**
 * BasicController is the controller portion of the MVC architecture for the "Basic" module.
 * It handles all of the wiring between model, view, and controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicController {
    
    private BasicModel _model;
    private BasicControlPanel _controlPanel;
    
    public BasicController( BasicModel model, BasicControlPanel controlPanel ) {
        
        _model = model;
        _controlPanel = controlPanel;
        
        // Update the climate model when the climate controls are changed.
        final SnowfallAndTemperatureControlPanel snowfallAndTemperatureControlPanel = controlPanel.getClimateControlPanel().getSnowfallAndTemperatureControlPanel();
        snowfallAndTemperatureControlPanel.addBasicClimateControlPanelListener( new SnowfallAndTemperatureControlPanelListener() {

            public void snowfallChanged( double snowfall ) {
                _model.getClimate().setSnowfallLapseRate( snowfall );
            }

            public void temperatureChanged( double temperature ) {
                _model.getClimate().setTemperatureOffset( temperature );
            }
        });
        
        // Update the climate controls when the climate model is changed.
        _model.getClimate().addClimateListener( new ClimateListener() {

            public void snowfallChanged() {
                snowfallAndTemperatureControlPanel.setSnowfall( _model.getClimate().getSnowfallLapseRate() );
            }

            public void temperatureChanged() {
                snowfallAndTemperatureControlPanel.setTemperature( _model.getClimate().getTemperatureOffset() );
            }
        } );
        
        // Initialization
        snowfallAndTemperatureControlPanel.setSnowfall( _model.getClimate().getSnowfallLapseRate() );
        snowfallAndTemperatureControlPanel.setTemperature( _model.getClimate().getTemperatureOffset() );
    }

}
