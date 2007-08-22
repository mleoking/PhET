/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.Enzyme;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * EnzymeNode is the visual representation of an enzyme.
 * It consists of 2 concentric spheres. The outer sphere remains stationary.
 * The inner sphere rotates about the z-axis (perpendicular to the screen),
 * at a speed proportion to the velocity with which the enzyme is pulling
 * through the DNA.  A tick mark is drawn on the inner sphere to make the
 * rotation more obvious, and to make it easier to count full rotations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EnzymeNode extends PhetPNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // outer sphere properties
    private static final int OUTER_ALPHA = 80;
    private static final Color OUTER_PRIMARY_COLOR = new Color( 0, 0, 175, OUTER_ALPHA );
    private static final Color OUTER_HILITE_COLOR = new Color( 0, 0, 200, OUTER_ALPHA );
    private static final Stroke OUTER_STROKE = null;
    private static final Paint OUTER_STROKE_PAINT = Color.BLACK;
    
    // inner sphere properties
    private static final int INNER_ALPHA = 200;
    private static final Color INNER_PRIMARY_COLOR = new Color( 75, 210, 30, INNER_ALPHA );
    private static final Color INNER_HILITE_COLOR = new Color( 91, 255, 37, INNER_ALPHA );
    private static final Stroke INNER_STROKE = null;
    private static final Paint INNER_STROKE_PAINT = Color.BLACK;
    
    // tick mark on inner sphere, used to show rotation about the z-axis
    private static final Stroke TICK_STROKE = new BasicStroke( 1f );
    private static final Color TICK_STROKE_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Enzyme _enzyme;
    private ModelViewTransform _modelViewTransform;
    
    private PNode _innerNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public EnzymeNode( Enzyme enzyme, ModelViewTransform modelViewTransform ) {
        super();
        
        _enzyme = enzyme;
        _enzyme.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        final double outerDiameter = _modelViewTransform.modelToView( _enzyme.getOuterDiameter() );
        SphericalNode outerSphere = new SphericalNode( true /* convertToImage */ );
        outerSphere.setDiameter( outerDiameter );
        Paint outerPaint = new RoundGradientPaint( 0, outerDiameter/6, OUTER_HILITE_COLOR, new Point2D.Double( outerDiameter/4, outerDiameter/4 ), OUTER_PRIMARY_COLOR );
        outerSphere.setPaint( outerPaint );
        outerSphere.setStroke( OUTER_STROKE );
        outerSphere.setStrokePaint( OUTER_STROKE_PAINT );
        
        final double innerDiameter = _modelViewTransform.modelToView( _enzyme.getInnerDiameter() );
        SphericalNode innerSphere = new SphericalNode( true /* convertToImage */ );
        innerSphere.setDiameter( innerDiameter );
        Paint innerPaint = new RoundGradientPaint( 0, innerDiameter/6, INNER_HILITE_COLOR, new Point2D.Double( innerDiameter/4, innerDiameter/4 ), INNER_PRIMARY_COLOR );
        innerSphere.setPaint( innerPaint );
        innerSphere.setStroke(  INNER_STROKE );
        innerSphere.setStrokePaint( INNER_STROKE_PAINT );

        Line2D tickPath = new Line2D.Double( 0, 0, 0, innerSphere.getFullBoundsReference().getHeight() / 2 );
        PPath tickMark = new PPath( tickPath );
        tickMark.setStroke( TICK_STROKE );
        tickMark.setStrokePaint( TICK_STROKE_COLOR );
        
        _innerNode = new PComposite();
        _innerNode.addChild( innerSphere );
        _innerNode.addChild( tickMark );
       
        addChild( _innerNode );
        addChild( outerSphere );
        
        updatePosition();
    }
    
    public void cleanup() {
        _enzyme.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        Point2D p = _modelViewTransform.modelToView( _enzyme.getPositionReference() );
        setOffset( p );
    }
    
    private void updateOrientation() {
        _innerNode.setRotation( _enzyme.getInnerOrientation() );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _enzyme ) {
            if ( arg == Enzyme.PROPERTY_INNER_ORIENTATION ) {
                updateOrientation();
            }
        }
    }

}
