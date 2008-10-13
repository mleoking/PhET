/* Copyright 2005-2007, University of Colorado */

package edu.colorado.phet.phetgraphicsdemo.view;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.phetgraphicsdemo.model.CarModelElement;

import java.awt.*;

/**
 * CarOnRoadGraphic is the graphic representation of a car moving horizontally on a road.
 * The origin is at the lower-left corner of the road.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CarOnRoadGraphic extends CompositePhetGraphic {

    private CarGraphic _carGraphic;
    
    public CarOnRoadGraphic( Component component, CarModelElement carModelElement ) {
        super( component );
        
        // Graphic components
        {
            // Car
            _carGraphic = new CarGraphic( component, carModelElement );
            _carGraphic.setLocation( _carGraphic.getWidth()/2, -5 );

            // Road
            int width = (int) carModelElement.getRange() + _carGraphic.getWidth();
            RoadGraphic roadGraphic = new RoadGraphic( component, width );
            roadGraphic.setLocation( 0, 0 );

            addGraphic( roadGraphic );
            addGraphic( _carGraphic );
        }
        
        // Interactivity
        InteractivityHandler.register( this );
    }
}
