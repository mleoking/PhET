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

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.IMagnet;

/**
 * CompassGraphic is the graphical representation of a compass.
 * It can be dragged around, and will indicate the direction of the magnetic field.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CompassGraphic extends CompositePhetGraphic implements SimpleObserver {
      
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IMagnet _magnetModel;
    private CompassNeedleGraphic _needle;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * The registration point is at the rotation point of the compass needle.
     * 
     * @param component the parent Component
     * @param magnetModel the magnet that the compass is observing
     */
    public CompassGraphic( Component component, IMagnet magnetModel ) {
        super( component );
        
        _magnetModel = magnetModel;
        
        PhetImageGraphic body = new PhetImageGraphic( component, FaradayConfig.COMPASS_IMAGE );
        int rx = body.getImage().getWidth() / 2;
        int ry = body.getImage().getHeight() / 2 + 11;
        body.setRegistrationPoint( rx, ry );
        
        _needle = new CompassNeedleGraphic( component );
        _needle.setSize( 55, 15 );
        
        PhetShapeGraphic anchor = new PhetShapeGraphic( component);
        anchor.setShape( new Ellipse2D.Double( -2, -2, 4, 4 ) );
        anchor.setPaint( Color.LIGHT_GRAY );
        anchor.setRenderingHints(new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        addGraphic( body );
        addGraphic( _needle );
        addGraphic( anchor );
        
        // Setup interactivity.
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent e ) {
                Point p = getLocation();
                setLocation( p.x + e.getDx(), p.y + e.getDy() );
                update();
            }
        } );
        
        update();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        if( isVisible() ) {
            Point p = getLocation();
            AbstractVector2D strength = _magnetModel.getStrength( p );
            _needle.setDirection( Math.toDegrees( strength.getAngle() ) );
            //System.out.println( "B = " + strength.getMagnitude() + " @ " + Math.toDegrees( strength.getAngle() ) ); // DEBUG
        }
    }

}
