/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.AbstractEnzyme;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
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
    
    // tick mark on inner sphere, used to show rotation about the z-axis
    private static final Stroke TICK_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractEnzyme _enzyme;
    private ModelViewTransform _modelViewTransform;
    
    private PNode _innerNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
            
    public EnzymeNode( AbstractEnzyme enzyme, ModelViewTransform modelViewTransform, Paint outerPaint, Paint innerPaint, Color tickColor ) {
        super();
        
        _enzyme = enzyme;
        _enzyme.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        final double outerDiameter = _modelViewTransform.modelToView( _enzyme.getOuterDiameter() );
        SphericalNode outerSphere = new SphericalNode( true /* convertToImage */ );
        outerSphere.setDiameter( outerDiameter );
        outerSphere.setPaint( outerPaint );
        outerSphere.setStroke( null );
        
        final double innerDiameter = _modelViewTransform.modelToView( _enzyme.getInnerDiameter() );
        SphericalNode innerSphere = new SphericalNode( true /* convertToImage */ );
        innerSphere.setDiameter( innerDiameter );
        innerSphere.setPaint( innerPaint );
        innerSphere.setStroke(  null );

        Line2D tickPath = new Line2D.Double( 0, 0, 0, innerSphere.getFullBoundsReference().getHeight() / 2 );
        PPath tickMark = new PPath( tickPath );
        tickMark.setStroke( TICK_STROKE );
        tickMark.setStrokePaint( tickColor );
        
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
            if ( arg == AbstractEnzyme.PROPERTY_INNER_ORIENTATION ) {
                updateOrientation();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Utilites
    //----------------------------------------------------------------------------
    
    public static Icon createIcon( double outerDiameter, double innerDiameter, Paint outerPaint, Paint innerPaint, Color tickColor ) {
        AbstractEnzyme enzyme = new AbstractEnzyme( new Point2D.Double(0,0), outerDiameter, innerDiameter );
        ModelViewTransform modelViewTransform = new ModelViewTransform( 1 );
        EnzymeNode enzymeNode = new EnzymeNode( enzyme, modelViewTransform, outerPaint, innerPaint, tickColor );
        Image enzymeImage = enzymeNode.toImage();
        return new ImageIcon( enzymeImage );
    }
}
