package edu.colorado.phet.glaciers.module.basic;

import edu.colorado.phet.glaciers.control.BasicClimateControlPanel;
import edu.colorado.phet.glaciers.control.BasicClimateControlPanel.BasicClimateControlPanelListener;
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
        final BasicClimateControlPanel climateControlPanel = controlPanel.getClimateControlPanel();
        climateControlPanel.addBasicClimateControlPanelListener( new BasicClimateControlPanelListener() {

            public void snowfallChanged( double snowfall ) {
                _model.getClimate().setReferencePrecipitation( snowfall );
            }

            public void temperatureChanged( double temperature ) {
                _model.getClimate().setReferenceTemperature( temperature );
            }
        });
        
        // Update the climate controls when the climate model is changed.
        _model.getClimate().addClimateListener( new ClimateListener() {

            public void referencePrecipitationChanged() {
                climateControlPanel.setSnowfall( _model.getClimate().getReferencePrecipition() );
            }

            public void referenceTemperatureChanged() {
                climateControlPanel.setTemperture( _model.getClimate().getReferenceTemperature() );
            }
        } );
        
        // Initialization
        climateControlPanel.setSnowfall( _model.getClimate().getReferencePrecipition() );
        climateControlPanel.setTemperture( _model.getClimate().getReferenceTemperature() );
    }

}
