package edu.colorado.phet.testlocation.view;

import java.awt.Component;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.testlocation.model.CarModelElement;

/**
 * CarOnRoadGraphic is the graphic representation of a car moving horizontally on a road.
 * The origin is at the lower-left corner of the road.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
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
            int x = -_carGraphic.getImage().getWidth() / 2;
            roadGraphic.setLocation( 0, 0 );

            addGraphic( roadGraphic );
            addGraphic( _carGraphic );
        }
        
        // Interactivity
        InteractivityHandler.register( this );
    }
}
