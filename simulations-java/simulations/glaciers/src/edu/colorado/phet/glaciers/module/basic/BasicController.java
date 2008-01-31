/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.module.basic;

import edu.colorado.phet.glaciers.control.SnowfallAndTemperatureControlPanel;
import edu.colorado.phet.glaciers.control.ViewControlPanel;
import edu.colorado.phet.glaciers.control.SnowfallAndTemperatureControlPanel.SnowfallAndTemperatureControlPanelListener;
import edu.colorado.phet.glaciers.control.ViewControlPanel.ViewControlPanelAdapter;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
import edu.colorado.phet.glaciers.view.PlayArea;

/**
 * BasicController is the controller portion of the MVC architecture for the "Basic" module.
 * It handles all of the wiring between model, view, and controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicController {
    
    private BasicModel _model;
    private PlayArea _playArea;
    private BasicControlPanel _controlPanel;
    
    public BasicController( BasicModel model, PlayArea playArea, BasicControlPanel controlPanel ) {
        
        _model = model;
        _playArea = playArea;
        _controlPanel = controlPanel;
        
        // View controls
        final ViewControlPanel viewControlPanel = controlPanel.getViewControlPanel();
        viewControlPanel.addViewControlPanelListener( new ViewControlPanelAdapter() {
            
            public void equilibriumLineChanged( boolean b ) {
                _playArea.setEquilibriumLineVisible( viewControlPanel.isEquilibriumLineSelected() );
            };
        });
        
        // Update the climate model when the climate controls are changed.
        final SnowfallAndTemperatureControlPanel snowfallAndTemperatureControlPanel = controlPanel.getClimateControlPanel().getSnowfallAndTemperatureControlPanel();
        snowfallAndTemperatureControlPanel.addBasicClimateControlPanelListener( new SnowfallAndTemperatureControlPanelListener() {

            public void snowfallChanged( double snowfall ) {
                _model.getClimate().setSnowfall( snowfall );
            }

            public void temperatureChanged( double temperature ) {
                _model.getClimate().setTemperature( temperature );
            }
        });
        
        // Update the climate controls when the climate model is changed.
        _model.getClimate().addClimateListener( new ClimateListener() {

            public void snowfallChanged() {
                snowfallAndTemperatureControlPanel.setSnowfall( _model.getClimate().getSnowfall() );
            }

            public void temperatureChanged() {
                snowfallAndTemperatureControlPanel.setTemperature( _model.getClimate().getTemperature() );
            }
        } );
        
        // Initialization
        _playArea.setEquilibriumLineVisible( viewControlPanel.isEquilibriumLineSelected() );
        snowfallAndTemperatureControlPanel.setSnowfall( _model.getClimate().getSnowfall() );
        snowfallAndTemperatureControlPanel.setTemperature( _model.getClimate().getTemperature() );
    }

}
