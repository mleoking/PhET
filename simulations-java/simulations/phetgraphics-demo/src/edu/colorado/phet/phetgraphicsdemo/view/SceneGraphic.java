/* Copyright 2005-2007, University of Colorado */

package edu.colorado.phet.phetgraphicsdemo.view;

import java.awt.Component;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.phetgraphicsdemo.PhetGraphicsDemoResources;
import edu.colorado.phet.phetgraphicsdemo.model.CarModelElement;
import edu.colorado.phet.phetgraphicsdemo.model.WindmillModelElement;

/**
 * SceneGraphic demonstrates creation of a complex scene involving 
 * composite and non-composite graphics. The scene's origin is at
 * the upper left corner of the background image.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SceneGraphic extends CompositePhetGraphic {

    public SceneGraphic( Component component, WindmillModelElement windmillModelElement, CarModelElement carModelElement ) {
        super( component );

        // Graphics components
        BufferedImage image = PhetGraphicsDemoResources.getImage( "landscape.png" );
        PhetImageGraphic background = new PhetImageGraphic( component, image );
        WindmillGraphic windmill = new WindmillGraphic( component, windmillModelElement );
        CarOnRoadGraphic carOnRoad = new CarOnRoadGraphic( component, carModelElement );
        
        windmill.setLocation( 150, 70 );
        windmill.scale( 0.25 );
        
        carOnRoad.setLocation( 0, 128 );
        carOnRoad.scale( 0.25 );
        
        addGraphic( background );
        addGraphic( windmill );
        addGraphic( carOnRoad );
        
        // Interactivity
        InteractivityHandler.register( this );
    }
}
