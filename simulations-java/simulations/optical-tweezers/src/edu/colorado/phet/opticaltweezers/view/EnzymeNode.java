/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.model.Enzyme;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * EnzymeNode is the visual representation of an enzyme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EnzymeNode extends PPath implements Observer {

    private Enzyme _enzyme;
    private ModelViewTransform _modelViewTransform;
    
    public EnzymeNode( Enzyme enzyme, ModelViewTransform modelViewTransform ) {
        super();
        
        _enzyme = enzyme;
        _enzyme.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        // ellipse, with origin at geometric center
        double w = _modelViewTransform.modelToView( _enzyme.getWidth() );
        double h = _modelViewTransform.modelToView( _enzyme.getHeight() );
        setPathTo( new Ellipse2D.Double( -w/2, -h/2, w, h ) );
        
        setPaint( Color.RED );
        setStroke( new BasicStroke( 1f ) );
        setStrokePaint( Color.BLACK );
        
        updatePosition();
    }
    
    public void cleanup() {
        _enzyme.deleteObserver( this );
    }
    
    private void updatePosition() {
        Point2D p = _modelViewTransform.modelToView( _enzyme.getPositionReference() );
        setOffset( p );
    }
    
    public void update( Observable o, Object arg ) {
        if ( o == _enzyme ) {
            // currently nothing to do, since the enzyme doesn't move
        }
    }

}
