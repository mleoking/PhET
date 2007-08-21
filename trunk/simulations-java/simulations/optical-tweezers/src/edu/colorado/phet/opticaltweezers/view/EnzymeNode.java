/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.Enzyme;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;

/**
 * EnzymeNode is the visual representation of an enzyme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EnzymeNode extends PhetPNode implements Observer {
    
    private static final int OUTER_ALPHA = 80;
    private static final Color OUTER_PRIMARY_COLOR = new Color( 0, 0, 175, OUTER_ALPHA );
    private static final Color OUTER_HILITE_COLOR = new Color( 0, 0, 200, OUTER_ALPHA );
    private static final Stroke OUTER_STROKE = null;
    private static final Paint OUTER_STROKE_PAINT = Color.BLACK;
    
    private static final int INNER_ALPHA = 255;
    private static final Color INNER_PRIMARY_COLOR = new Color( 75, 210, 30, INNER_ALPHA );
    private static final Color INNER_HILITE_COLOR = new Color( 91, 255, 37, INNER_ALPHA );
    private static final Stroke INNER_STROKE = null;
    private static final Paint INNER_STROKE_PAINT = Color.BLACK;
    
    private Enzyme _enzyme;
    private ModelViewTransform _modelViewTransform;
    
    private SphericalNode _outerNode, _innerNode;
    
    public EnzymeNode( Enzyme enzyme, ModelViewTransform modelViewTransform ) {
        super();
        
        _enzyme = enzyme;
        _enzyme.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        final double outerDiameter = _modelViewTransform.modelToView( _enzyme.getOuterDiameter() );
        _outerNode = new SphericalNode( false );
        _outerNode.setDiameter( outerDiameter );
        Paint outerPaint = new RoundGradientPaint( 0, outerDiameter/6, OUTER_HILITE_COLOR, new Point2D.Double( outerDiameter/4, outerDiameter/4 ), OUTER_PRIMARY_COLOR );
        _outerNode.setPaint( outerPaint );
        _outerNode.setStroke( OUTER_STROKE );
        _outerNode.setStrokePaint( OUTER_STROKE_PAINT );
        
        final double innerDiameter = _modelViewTransform.modelToView( _enzyme.getInnerDiameter() );
        _innerNode = new SphericalNode( false );
        _innerNode.setDiameter( innerDiameter );
        Paint innerPaint = new RoundGradientPaint( 0, innerDiameter/6, INNER_HILITE_COLOR, new Point2D.Double( innerDiameter/4, innerDiameter/4 ), INNER_PRIMARY_COLOR );
        _innerNode.setPaint( innerPaint );
        _innerNode.setStroke(  INNER_STROKE );
        _innerNode.setStrokePaint( INNER_STROKE_PAINT );
       
        addChild( _innerNode );
        addChild( _outerNode );
        
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
            if ( arg == Enzyme.PROPERTY_INNER_ORIENTATION ) {
                _innerNode.setRotation( _enzyme.getInnerOrientation() );
            }
        }
    }

}
