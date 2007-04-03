/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * BeamOutNode is the visual representation of the portion of the 
 * laser beam that is coming out of the microscope objective.
 * This part of the beam is shaped by the objective and shows the 
 * gradient field.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeamOutNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean SHOW_OUTLINE = true;
    
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color OUTLINE_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    
    private PPath _outlineNode;
    private PImage _gradientNode;
    private BufferedImage _gradientImage;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param laser
     * @param height height in view coordinates
     */
    public BeamOutNode( Laser laser, ModelViewTransform modelViewTransform ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;

        int bufferWidth = (int) _modelViewTransform.modelToView( laser.getDiameterAtObjective() );
        if ( bufferWidth % 2 == 0 ) {
            bufferWidth++;
        }
        int bufferHeight = (int) ( 2 * _laser.getDistanceFromObjectiveToWaist() );
        if ( bufferHeight % 2 == 0 ) {
            bufferHeight++;
        }
        _gradientImage = new BufferedImage( bufferWidth, bufferHeight, BufferedImage.TYPE_INT_ARGB );
        
        // Outline of beam shape
        if ( SHOW_OUTLINE ) {
            _outlineNode = new PPath();
            _outlineNode.setStroke( OUTLINE_STROKE );
            _outlineNode.setStrokePaint( OUTLINE_COLOR );
            _outlineNode.setPaint( null );
            addChild( _outlineNode );
            updateOutline();
        }
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
   
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POWER ) {
                updateGradient();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updateGradient() {
        //XXX
    }

    /*
     * Updates the shape of the beam's outline. 
     * The shape is calculated in model coordinates, then transformed to view coordinates.
     */
    private void updateOutline() {
        
        // Shape is symmetric, calculate points for one quarter of the outline, 1 point for each 1 nm
        final int numberOfPoints = (int) _laser.getDistanceFromObjectiveToWaist();
        Point2D[] points = new Point2D.Double[numberOfPoints];
        for ( int y = 0; y < points.length; y++ ) {
            double r = _laser.getBeamRadiusAt( y );
            points[y] = new Point2D.Double( r, y );
        }

        // Create path for entire outline.
        GeneralPath p = new GeneralPath();
        int iFirst = 0;
        int iLast = points.length - 1;
        // start at right center
        p.moveTo( (float) points[iFirst].getX(), (float) points[iFirst].getY() );
        // lower-right quadrant
        for ( int i = iFirst + 1; i < iLast - 1; i++ ) {
            p.lineTo( (float) points[i].getX(), (float) points[i].getY() );
        }
        // lower-left quadrant
        p.lineTo( -(float) points[iLast].getX(), (float) points[iLast].getY() );
        for ( int i = iLast - 2; i >= iFirst; i-- ) {
            p.lineTo( -(float) points[i].getX(), (float) points[i].getY() );
        }
        // upper-left quadrant
        for ( int i = iFirst + 1; i < iLast - 1; i++ ) {
            p.lineTo( -(float) points[i].getX(), -(float) points[i].getY() );
        }
        // upper-right quadrant
        p.lineTo( (float) points[iLast].getX(), -(float) points[iLast].getY() );
        for ( int i = iLast - 2; i >= iFirst; i-- ) {
            p.lineTo( (float) points[i].getX(), -(float) points[i].getY() );
        }
        p.closePath();
        
        // transform to view coordinates
        Shape shape = _modelViewTransform.createTransformedShapeModelToView( p );

        // set node's path
        _outlineNode.setPathTo( shape );
        
        // shape was drawn starting at right center, adjust so that origin is at center
        _outlineNode.setOffset( _outlineNode.getFullBounds().getWidth()/2, _outlineNode.getFullBounds().getHeight()/2 );
    }
}
