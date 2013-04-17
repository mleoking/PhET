// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.opticaltweezers.model.AbstractEnzyme;
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * AbstractEnzymeNode is base class for all visual representations of an enzyme.
 * It consists of 2 concentric spheres. The outer sphere remains stationary.
 * The inner sphere rotates about the z-axis (perpendicular to the screen),
 * at a speed proportion to the velocity with which the enzyme is pulling
 * through the DNA.  A tick mark is drawn on the inner sphere to make the
 * rotation more obvious, and to make it easier to count full rotations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractEnzymeNode extends PhetPNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // tick mark on inner sphere, used to show rotation about the z-axis
    private static final Stroke TICK_STROKE = new BasicStroke( 3f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractEnzyme _enzyme;
    private OTModelViewTransform _modelViewTransform;
    
    private PNode _innerNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
            
    public AbstractEnzymeNode( AbstractEnzyme enzyme, OTModelViewTransform modelViewTransform, Paint outerPaint, Paint innerPaint, Color tickColor ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _enzyme = enzyme;
        _enzyme.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        final double outerDiameter = _modelViewTransform.modelToView( _enzyme.getOuterDiameter() );
        SphericalNode outerSphere = new SphericalNode( outerDiameter, outerPaint, false /* convertToImage */ );
        outerSphere.setStroke( null );
        
        final double innerDiameter = _modelViewTransform.modelToView( _enzyme.getInnerDiameter() );
        SphericalNode innerSphere = new SphericalNode( innerDiameter, innerPaint, false /* convertToImage */ );
        innerSphere.setStroke(  null );

        // tick starts in center of inner sphere, extends not quite to the outer eddge
        Line2D tickPath = new Line2D.Double( 0, 0, 0, innerSphere.getFullBoundsReference().getHeight() / 3 );
        PPath tickMark = new PPath( tickPath );
        tickMark.setStroke( TICK_STROKE );
        tickMark.setStrokePaint( tickColor );
        
        _innerNode = new PComposite();
        _innerNode.addChild( innerSphere );
        _innerNode.addChild( tickMark );
       
        addChild( _innerNode );
        addChild( outerSphere );
        
        updatePosition();
        updateVisibility();
    }
    
    public void cleanup() {
        _enzyme.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public AbstractEnzyme getEnzyme() {
        return _enzyme;
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
    
    private void updateVisibility() {
        setVisible( _enzyme.isEnabled() );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _enzyme ) {
            if ( arg == AbstractEnzyme.PROPERTY_INNER_ORIENTATION ) {
                updateOrientation();
            }
            else if ( arg == AbstractEnzyme.PROPERTY_ENABLED ) {
                updateVisibility();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Utilites
    //----------------------------------------------------------------------------
    
    /**
     * Converts this node to an icon, scaled to the specified height.
     * This is useful for displaying an icon of the enzyme in a control panel.
     * 
     * @param height icon height, in pixels
     */
    public Icon createIcon( double height ) {
        boolean wasVisible = getVisible();
        setVisible( true );
        Image image = toImage();
        setVisible( wasVisible );
        PImage imageNode = new PImage( image );
        double imageHeight = imageNode.getFullBoundsReference().getHeight();
        imageNode.setScale( height / imageHeight );
        Image scaledImage = imageNode.toImage();
        Icon icon = new ImageIcon( scaledImage );
        return icon;
    }
}
