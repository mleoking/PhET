/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.*;

import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;


/**
 * MiniCompassGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MiniCompassGraphic extends PhetImageGraphic {

    private Point _location;
    private double _direction;
    
    /**
     * @param component
     */
    public MiniCompassGraphic( Component component ) {
        
        super( component, FaradayConfig.MINI_COMPASS_IMAGE );
        
        _location = new Point( 0, 0 );
        _direction = 0.0;
        
        // Set registration point to center of image.
        int x = getImage().getWidth() / 2;
        int y = getImage().getHeight() / 2;
        setRegistrationPoint( x, y );
    }

    public void setLocation( Point p ) {
        setLocation( p.x, p.y );
    }
    
    public void setLocation( int x, int y ) {
        if ( _location == null ) {
            _location = new Point(x,y);
        }
        else {
            _location.setLocation( x, y );
        }
        update();
    }
    
    public Point getLocation() {
        return new Point( _location );
    }
    
    public int getX() { 
        return _location.x;
    }
    
    public int getY() {
        return _location.y;
    }
    
    public void setDirection( double direction ) {
        _direction = direction;
        update();
    }
    
    public double getDirection() {
        return _direction;
    }
    
    private void update() {
        clearTransform();
        rotate( Math.toRadians( _direction ) );
        translate( _location.x, _location.y );
    }
}
