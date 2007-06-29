/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * OTRulerNode is a ruler that is specialized for this simulation.
 * The ruler always fills the canvas, and the zero mark on the ruler 
 * remains aligned with the horizontal position of the ruler.
 * The ruler can be vertically dragged, but it's horizontal position
 * is locked; the horizontal position only changes when the laser moves.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OTRulerNode extends RulerNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension2D DEFAULT_WORLD_SIZE = new PDimension( 600, 600 );
    private static final double HEIGHT = 40;
    private static final int FONT_SIZE = 12;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _majorTickInterval;
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    private PPath _dragBoundsNode;
    private Dimension2D _worldSize;
    private double _xOffsetFudgeFactor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructors.
     * 
     * @param majorTickInterval nm
     * @param minorTicksBetweenMajors
     * @param laser
     * @param modelWorldTransform
     * @param dragBoundsNode
     */
    public OTRulerNode( int majorTickInterval, int minorTicksBetweenMajors, Laser laser, ModelViewTransform modelWorldTransform, PPath dragBoundsNode ) {
        super( DEFAULT_WORLD_SIZE.getWidth(), HEIGHT, null, OTResources.getString( "units.position" ), minorTicksBetweenMajors, FONT_SIZE );
        
        setUnitsAssociatedMajorTickLabel( "0" ); // attach units to the tick mark labeled "0"
        
        _majorTickInterval = majorTickInterval;
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelWorldTransform;
        _dragBoundsNode = dragBoundsNode;
        
        addInputEventListener( new CursorHandler( OTConstants.UP_DOWN_CURSOR ) );
        addInputEventListener( new BoundedDragHandler( this, dragBoundsNode ) );
        
        _worldSize = new PDimension( DEFAULT_WORLD_SIZE );
        _xOffsetFudgeFactor = 0;
        
        updateWidth();
        updatePosition();
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _laser.deleteObserver( this );
    }

    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the size of the PhetPCanvas' world node.
     * This makes the ruler change it's width to fill the canvas,
     * giving the appearance of an infinitely wide ruler.
     * 
     * @param worldSize
     */
    public void setWorldSize( Dimension2D worldSize ) {
        _worldSize.setSize( worldSize );
        updateWidth();
    }
    
    /**
     * WORKAROUND for ruler alignment problems.
     * This amount is added to the ruler's xoffset to make it line up correctly.
     * I spent a lot of time trying to find the source the alignment problem,
     * and resorted to this workaround because of cost. It's possible that the 
     * laser view itself is slightly misaligned.
     * 
     * @param xOffsetFudgeFactor
     */
    public void setXOffsetFudgeFactor( double xOffsetFudgeFactor ) {
        if ( xOffsetFudgeFactor != _xOffsetFudgeFactor ) {
            _xOffsetFudgeFactor = xOffsetFudgeFactor;
            updatePosition();
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POSITION ) {
                updatePosition();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the ruler width to fill the canvas.
     */
    private void updateWidth() {
        
        // Convert canvas width to model coordinates
        final double modelCanvasWidth = _modelViewTransform.viewToModel( _worldSize.getWidth() );
        
        // Make the ruler 3x the width of the canvas
        final double modelRulerWidth = 3 * modelCanvasWidth;
        
        // Calculate the number of ticks on the ruler
        int numMajorTicks = (int)( modelRulerWidth / _majorTickInterval );
        if ( numMajorTicks % 2 == 0 ) {
            numMajorTicks++; // must be an odd number, with 0 at middle
        }
        
        // Distance between first and last tick
        final double viewDistance = ( numMajorTicks - 1 ) * _modelViewTransform.modelToView( _majorTickInterval );
        setDistanceBetweenFirstAndLastTick( viewDistance );
        
        // Create the tick labels
        String[] majorTickLabels = new String[ numMajorTicks ];
        int majorTick = -_majorTickInterval * ( numMajorTicks - 1 ) / 2;
        for ( int i = 0; i < majorTickLabels.length; i++ ) {
            majorTickLabels[i] = String.valueOf( majorTick );
            majorTick += _majorTickInterval;
        }
        setMajorTickLabels( majorTickLabels );
        
        // adjust position so the "0" is horizontally aligned with the laser
        updatePosition();
    }
    
    /*
     * Updates the ruler position so that it's zero mark is horizontally aligned 
     * with the laser's position. The ruler's vertical position is not changed.
     */
    private void updatePosition() {
        
        // horizontally align the ruler's center with the laser
        final double xModel = _laser.getPositionRef().getX();
        final double xView = _modelViewTransform.modelToView( xModel ) - ( getFullBounds().getWidth() / 2 ) + _xOffsetFudgeFactor;
        final double yView = getOffset().getY();
        setOffset( xView, yView );

        // adjust the drag bound for the new horizontal position
        Rectangle2D dragBounds = new Rectangle2D.Double( getFullBounds().getX(), 0, getFullBounds().getWidth(), _worldSize.getHeight() );
        _dragBoundsNode.setPathTo( dragBounds );
    }
}
