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
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.Voltmeter;
import edu.colorado.phet.faraday.util.IRescaler;


/**
 * VoltmeterGraphic is the graphic representation of a voltmeter.
 * The meter's needle moves on a relative scale.
 * Registration point is at bottom-center of the meter body.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class VoltmeterGraphic extends CompositePhetGraphic implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Voltmeter _voltmeterModel;
    private PhetShapeGraphic _needle;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * The registration point is at the bottom center of the meter's body.
     * 
     * @param component the parent Component
     * @param voltmeterModel
     * @param magnetModel
     */
    public VoltmeterGraphic( Component component, Voltmeter voltmeterModel ) {
        super( component );
        assert( component != null );
        assert( voltmeterModel != null );

        _voltmeterModel = voltmeterModel;
        _voltmeterModel.addObserver( this );
        
        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Meter body
        {
            PhetImageGraphic body = new PhetImageGraphic( component, FaradayConfig.VOLTMETER_IMAGE );
            addGraphic( body );
            int rx = body.getImage().getWidth() / 2;
            int ry = body.getImage().getHeight();
            body.setRegistrationPoint( rx, ry );
        }
        
        // Screw that holds the needle in place.
        {
            int diameter = 11;
            PhetShapeGraphic screw = new PhetShapeGraphic( component );
            addGraphic( screw );
            screw.setShape( new Ellipse2D.Double( 0, 0, diameter, diameter ) );
            screw.setPaint( Color.BLUE );
            int rx = ( diameter / 2 ) + 1;
            int ry = rx;
            screw.setRegistrationPoint( rx, ry );
            screw.setLocation( 0, -23 );
        }
        
        // Needle
        {
            Point2D tail = new Point2D.Double( 0, 0 );
            Point2D tip = new Point2D.Double( 0, -70 );
            Dimension headSize = new Dimension( 12, 15 );
            int tailWidth = 3;
            Arrow arrow = new Arrow( tail, tip, headSize.height, headSize.width, tailWidth );
            _needle = new PhetShapeGraphic( component );
            addGraphic( _needle );
            _needle.setShape( arrow.getShape() );
            _needle.setPaint( Color.BLUE );
            _needle.setLocation( 0, -23 );
        }
        
        update();
    }

    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _voltmeterModel.removeObserver( this );
        _voltmeterModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Set the rescaler, applied to the voltage.
     * 
     * @param rescaler
     */
    public void setRescaler( IRescaler rescaler ) {
       _voltmeterModel.setRescaler( rescaler );  // HACK: scaling should be done in the view !
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        setVisible( _voltmeterModel.isEnabled() );
        if ( isVisible() ) {     
            double angle = _voltmeterModel.getNeedleAngle();
//            System.out.println( "VoltmeterGraphic.update - angle=" + Math.toDegrees(angle) );  // DEBUG
            _needle.clearTransform();
            _needle.rotate( angle );
            repaint();
        }
    }
}