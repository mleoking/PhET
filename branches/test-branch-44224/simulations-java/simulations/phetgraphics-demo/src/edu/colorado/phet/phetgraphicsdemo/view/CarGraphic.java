/* Copyright 2005-2007, University of Colorado */

package edu.colorado.phet.phetgraphicsdemo.view;

import java.awt.Component;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.phetgraphicsdemo.PhetGraphicsDemoResources;
import edu.colorado.phet.phetgraphicsdemo.model.CarModelElement;

/**
 * CarGraphic is the graphical representation of a car that moves horizontally.
 * The origin is at the bottom-center of the car, at its starting point.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CarGraphic extends PhetImageGraphic implements SimpleObserver {

    private CarModelElement _carModelElement;
    private double _previousX;
    private double _previousDirection;
     
    public CarGraphic( Component component, CarModelElement carModelElement ) {
        super( component, PhetGraphicsDemoResources.getImage( "car.png" ) );
        _carModelElement = carModelElement;
        _carModelElement.addObserver( this );
        
        // Registration point is bottom center of the car.
        int rx = getImage().getWidth() / 2;
        int ry = getImage().getHeight();
        setRegistrationPoint( rx, ry );
    }

    public void finalize() {
        _carModelElement.removeObserver( this );
    }
    
    public void update() {
        // NOTE: 
        // All updates to loction and transform are done in a manner 
        // that allows parent to setLocation and transforms.
        
        // Location
        double currentX = _carModelElement.getLocation();
        double deltaX = currentX - _previousX;
        setLocation( (int)( getX() + deltaX ), getY() );
        _previousX = currentX;
        
        // Direction (either left or right)
        double direction = _carModelElement.getDirection();
        if ( direction != _previousDirection ) {
            // Refect, car has changed directions.
            scale( -1.0, 1.0 );
            _previousDirection = direction;
        }
    }
}
