// Copyright 2002-2011, University of Colorado

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
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
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
    private OTModelViewTransform _modelViewTransform;
    private PPath _dragBoundsNode;
    private Dimension2D _worldSize;

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
    public OTRulerNode( int majorTickInterval, int minorTicksBetweenMajors, Laser laser, OTModelViewTransform modelWorldTransform, PPath dragBoundsNode ) {
        super( DEFAULT_WORLD_SIZE.getWidth(), HEIGHT, null, OTResources.getString( "units.position" ), minorTicksBetweenMajors, FONT_SIZE );

        setUnitsAssociatedMajorTickLabel( "0" ); // attach units to the tick mark labeled "0"
        setBackgroundPaint( OTConstants.RULER_COLOR );

        /*
         * Use a large inset width, or the left and right tick mark labels may fall
         * off the edges of the ruler and mess up the calculation that aligns the
         * ruler's zero tick mark with the laser's position.
         */
        setInsetWidth( 100 );

        _majorTickInterval = majorTickInterval;

        _laser = laser;
        _laser.addObserver( this );

        _modelViewTransform = modelWorldTransform;
        _dragBoundsNode = dragBoundsNode;

        addInputEventListener( new CursorHandler( OTConstants.UP_DOWN_CURSOR ) );
        addInputEventListener( new BoundedDragHandler( this, dragBoundsNode ) );

        _worldSize = new PDimension( DEFAULT_WORLD_SIZE );

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

    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------

    /**
     * Updates the ruler's position when it's made visible.
     *
     * @param visible
     */
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            if ( visible ) {
                updatePosition();
            }
            super.setVisible( visible );
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
        if ( isVisible() ) {
            if ( o == _laser ) {
                if ( arg == Laser.PROPERTY_POSITION ) {
                    updatePosition();
                }
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
        final double xModel = _laser.getPositionReference().getX();
        final double xView = _modelViewTransform.modelToView( xModel ) - ( getFullBoundsReference().getWidth() / 2 );
        final double yView = getOffset().getY();
        setOffset( xView, yView );

        // adjust the drag bound for the new horizontal position
        PBounds bounds = getFullBoundsReference();
        Rectangle2D dragBounds = new Rectangle2D.Double( bounds.getX(), 0, bounds.getWidth(), _worldSize.getHeight() );
        _dragBoundsNode.setPathTo( dragBounds );
    }
}
