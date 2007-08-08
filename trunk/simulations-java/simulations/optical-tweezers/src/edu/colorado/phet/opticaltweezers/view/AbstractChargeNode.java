/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * AbstractChargeNode is the base class for nodes that display charge on the bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractChargeNode extends PhetPNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color POSITIVE_COLOR = Color.RED;
    private static final Color NEGATIVE_COLOR = Color.BLUE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    private Point2D _pModel; // reusable point
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractChargeNode( Bead bead, Laser laser, ModelViewTransform modelViewTransform ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _bead = bead;
        _bead.addObserver( this );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _pModel = new Point2D.Double();
        
        initialize();
        updatePosition();
    }

    public void cleanup() {
        _bead.deleteObserver( this );
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Abstract methods
    //----------------------------------------------------------------------------
    
    protected abstract void initialize();
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    protected Bead getBead() {
        return _bead;
    }
    
    protected Laser getLaser() {
        return _laser;
    }
    
    protected ModelViewTransform getModelViewTransform() {
        return _modelViewTransform;
    }
    
    protected PNode createPositiveNode( double size, double thickness ) {
        Stroke stroke = new BasicStroke( (float)thickness );
        // Positive charge is a '+' sign
        Line2D horizontalLine = new Line2D.Double( -size/2, 0, size/2, 0 );
        PPath horizontalPathNode = new PPath( horizontalLine );
        horizontalPathNode.setStrokePaint( POSITIVE_COLOR );
        horizontalPathNode.setStroke( stroke );
        Line2D verticalLine = new Line2D.Double( 0, -size/2, 0, size/2 );
        PPath verticalPathNode = new PPath( verticalLine );
        verticalPathNode.setStrokePaint( POSITIVE_COLOR );
        verticalPathNode.setStroke( stroke );
        PComposite parentNode = new PComposite();
        parentNode.addChild( horizontalPathNode );
        parentNode.addChild( verticalPathNode );
        // Convert to an image
        PImage imageNode = new PImage( parentNode.toImage() );
        return imageNode;
    }
    
    protected PNode createNegativeNode( double size, double thickness ) {
        Stroke stroke = new BasicStroke( (float)thickness );
        // Negative charge is a horizontal line
        Line2D line = new Line2D.Double( 0, 0, size, 0 );
        PPath pathNode = new PPath( line );
        pathNode.setStrokePaint( NEGATIVE_COLOR );
        pathNode.setStroke( stroke );
        // Convert to an image
        PImage imageNode = new PImage( pathNode.toImage() );
        return imageNode;
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            if ( visible ) {
                updatePosition();
                updateCharge();
            }
            super.setVisible( visible );
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( isVisible() ) {
            if ( o == _bead ) {
                if ( arg == Bead.PROPERTY_POSITION ) {
                    updatePosition();
                }
            }
            else if ( o == _laser ) {
                if ( arg == Laser.PROPERTY_ELECTRIC_FIELD ) {
                    updateCharge();
                }
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        _modelViewTransform.modelToView( _bead.getPositionReference(), _pModel );
        setOffset( _pModel );
    }
    
    protected abstract void updateCharge();
}
