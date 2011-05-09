// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.view.phslider;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * PHSliderNode is a custom Piccolo slider for setting pH.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHSliderNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // whether the max is on top or bottom
    private static final boolean MAX_AT_TOP = true;
    
    // Track
    private static final float TRACK_STROKE_WIDTH = 1f;
    private static final Stroke TRACK_STROKE = new BasicStroke( TRACK_STROKE_WIDTH );
    private static final Color TRACK_COLOR = Color.BLACK;
    private static final Color ACID_COLOR = PHScaleConstants.H3O_COLOR;
    private static final Color BASE_COLOR = PHScaleConstants.OH_COLOR;
    
    // Knob
    private static final int KNOB_STROKE_WIDTH = 1;
    private static final Stroke KNOB_STROKE = new BasicStroke( KNOB_STROKE_WIDTH );
    private static final Color KNOB_FILL_COLOR = new Color( 255, 255, 255, 200 ); // translucent white
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;
    
    // Ticks
    private static final double TICK_LENGTH = 10;
    private static final int TICK_SPACING = 2;
    private static final Font TICK_FONT = new PhetFont( 16 );
    private static final float TICK_STROKE_WIDTH = 1f;
    private static final Stroke TICK_STROKE = new BasicStroke( TICK_STROKE_WIDTH );
    private static final Color TICK_COLOR = Color.BLACK;
    private static final double TICK_LABEL_SPACING = 2;
    private static final double TICK_TRACK_SPACING = 3; // space between ticks and the track

    // Labels
    private static final Font ACID_BASE_FONT = new PhetFont( 16 );
    private static final int ACID_BASE_TRACK_SPACING = 4; // space between acid/base labels and track
    private static final int ACID_BASE_MARGIN = 5; // acid/base labels are y-offset this amount from ends of track

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final IntegerRange _range;
    private final TrackNode _trackNode;
    private final KnobNode _knobNode;
    private PComposite _ticksNode;
    private final ArrayList _listeners;
    private double _pH;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PHSliderNode( IntegerRange range, double trackWidth, double ticksYSpacing, PDimension knobSize ) {
        super();
        
        _range = new IntegerRange( range );
        _listeners = new ArrayList();
            
        PDimension trackSize = new PDimension( trackWidth, range.getLength() * ticksYSpacing );
        
        _trackNode = new TrackNode( trackSize );
        _knobNode = new KnobNode( knobSize );
        
        _ticksNode = new PComposite();
        TickMarkNode tickMarkNode = null;
        final double xOffset = 0;
        double yOffset = 0;
        final double yDelta = trackSize.getHeight() / _range.getLength();
        if ( MAX_AT_TOP ) {
            for ( int i = _range.getMax(); i >= _range.getMin(); i-- ) {
                if ( i % TICK_SPACING == 0 ) {
                    tickMarkNode = new TickMarkNode( String.valueOf( i ) );
                }
                else {
                    tickMarkNode = new TickMarkNode();
                }
                _ticksNode.addChild( tickMarkNode );
                tickMarkNode.setOffset( xOffset, yOffset );
                yOffset += yDelta;
            }
        }
        else {
            for ( int i = _range.getMin(); i <= _range.getMax(); i++ ) {
                if ( i % TICK_SPACING == 0 ) {
                    tickMarkNode = new TickMarkNode( String.valueOf( i ) );
                }
                else {
                    tickMarkNode = new TickMarkNode();
                }
                _ticksNode.addChild( tickMarkNode );
                tickMarkNode.setOffset( xOffset, yOffset );
                yOffset += yDelta;
            }
        }
        
        PText acidLabelNode = new PText( PHScaleStrings.LABEL_ACID );
        acidLabelNode.setFont( ACID_BASE_FONT );
        acidLabelNode.rotate( -Math.PI / 2 );
        
        PText baseLabelNode = new PText( PHScaleStrings.LABEL_BASE );
        baseLabelNode.setFont( ACID_BASE_FONT );
        baseLabelNode.rotate( -Math.PI / 2 );

        // Rendering order
        addChild( _trackNode );
        addChild( acidLabelNode );
        addChild( baseLabelNode );
        addChild( _ticksNode );
        addChild( _knobNode );
        
        // Positions:
        // origin at the upper-left corner of the track
        _trackNode.setOffset( 0, 0 );
        // ticks to right of track
        _ticksNode.setOffset( _trackNode.getFullBoundsReference().getMaxX() + TICK_TRACK_SPACING, _trackNode.getYOffset() );
        // knob overlaps the track
        _knobNode.setOffset( _trackNode.getFullBoundsReference().getMaxX() + ( 0.25 * _knobNode.getFullBoundsReference().getWidth() ), 0 ); // y offset doesn't matter yet
        // acid/base labels at top-left and bottom-left of track
        if ( MAX_AT_TOP ) {
            acidLabelNode.setOffset( -( acidLabelNode.getFullBoundsReference().getWidth() + ACID_BASE_TRACK_SPACING ), _trackNode.getFullBoundsReference().getHeight() - ACID_BASE_MARGIN ); 
            baseLabelNode.setOffset( -( baseLabelNode.getFullBoundsReference().getWidth() + ACID_BASE_TRACK_SPACING ), baseLabelNode.getFullBoundsReference().getHeight() + ACID_BASE_MARGIN ); 
        }
        else {
            baseLabelNode.setOffset( -( baseLabelNode.getFullBoundsReference().getWidth() + ACID_BASE_TRACK_SPACING ), _trackNode.getFullBoundsReference().getHeight() - ACID_BASE_MARGIN );
            acidLabelNode.setOffset( -( acidLabelNode.getFullBoundsReference().getWidth() + ACID_BASE_TRACK_SPACING ), acidLabelNode.getFullBoundsReference().getHeight() + ACID_BASE_MARGIN );
        }
        
        initInteractivity();
        
        // initialize
        setPH( _range.getDefault() );
    }
    
    /*
     * Adds interactivity to the knob.
     */
    private void initInteractivity() {
        
        // hand cursor on knob
        _knobNode.addInputEventListener( new CursorHandler() );
        
        // Constrain the knob to be dragged vertically within the track
        _knobNode.addInputEventListener( new PDragEventHandler() {
            
            private double _globalClickYOffset; // y offset of mouse click from knob's origin, in global coordinates
            
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                // note the offset between the mouse click and the knob's origin
                Point2D pMouseLocal = event.getPositionRelativeTo( PHSliderNode.this );
                Point2D pMouseGlobal = PHSliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = PHSliderNode.this.localToGlobal( _knobNode.getOffset() );
                _globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY();
            }

            protected void drag(PInputEvent event) {
                
                // determine the knob's new offset
                Point2D pMouseLocal = event.getPositionRelativeTo( PHSliderNode.this );
                Point2D pMouseGlobal = PHSliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - _globalClickYOffset );
                Point2D pKnobLocal = PHSliderNode.this.globalToLocal( pKnobGlobal );
                
                // convert the offset to a pH value
                double yOffset = pKnobLocal.getY();
                double trackLength = _trackNode.getFullBoundsReference().getHeight();
                double pH = 0;
                if ( MAX_AT_TOP ) {
                    pH = _range.getMin() + _range.getLength() * ( trackLength - yOffset ) / trackLength;
                }
                else {
                    pH = _range.getMax() - _range.getLength() * ( trackLength - yOffset ) / trackLength;
                }
                
                if ( pH < _range.getMin() ) {
                    pH = _range.getMin();
                }
                else if ( pH > _range.getMax() ) {
                    pH = _range.getMax();
                }
                
                // set the pH (this will move the knob)
                setPH( pH );
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the pH value.
     * 
     * @return double
     */
    public double getPH() {
        return _pH;
    }
    
    /**
     * Sets the pH value and notifies all ChangeListeners.
     * 
     * @param pH
     */
    public void setPH( double pH ) {
        if ( !_range.contains( pH ) ) {
            throw new IllegalArgumentException( "pH is out of range: " + pH );
        }
        if ( pH != _pH ) {
            _pH = pH;
            double xOffset = _knobNode.getXOffset();
            double yOffset = _trackNode.getFullBoundsReference().getHeight() * ( ( _range.getMax() - pH ) / _range.getLength() );
            if ( MAX_AT_TOP ) {
                _knobNode.setOffset( xOffset, yOffset );
            }
            else {
                _knobNode.setOffset( xOffset, _trackNode.getFullBoundsReference().getHeight() - yOffset );
            }
            notifyChanged();
        }
    }
    
    /**
     * Enables and disables the slider.
     * When the beaker is empty, pH is meaningless, so we'll want to disable the slider.
     * 
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        setKnobVisible( enabled );
    }

    /**
     * Is this slider enabled?
     * It's enabled if the knob is visible.
     * 
     * @return
     */
    public boolean isEnabled() {
        return _knobNode.getVisible();
    }
    
    /*
     * Sets the visibility of the knob.
     * 
     * @param visible
     */
    private void setKnobVisible( boolean visible ) {
        _knobNode.setVisible( visible );
    }
    
    /**
     * Gets the offset used to vertically align the graph ticks with the pH slider ticks.
     * Offset is in global coordinates.
     * Only the y offset is meaningful.
     * 
     * @return
     */
    public Point2D getTickAlignmentGlobalOffset() {
        return localToGlobal( new Point2D.Double( 0, _ticksNode.getYOffset() ) );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * The slider track, vertical orientation, 
     * filled with a gradient paint that indicates the transition from acid to base.
     * Origin is at the upper left corner.
     */
    private static class TrackNode extends PNode {
        public TrackNode( PDimension size ) {
            super();
            setPickable( false );
            setChildrenPickable( false );
            
            PPath pathNode = new PPath();
            final double width = size.getWidth() - TRACK_STROKE_WIDTH;
            final double height = size.getHeight() - TRACK_STROKE_WIDTH;
            pathNode.setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
            Paint trackPaint = null;
            if ( MAX_AT_TOP ) {
                trackPaint = new GradientPaint( 0f, 0f, BASE_COLOR, 0f, (float)size.getHeight(), ACID_COLOR );
            }
            else {
                trackPaint = new GradientPaint( 0f, 0f, ACID_COLOR, 0f, (float)size.getHeight(), BASE_COLOR );
            }
            pathNode.setPaint( trackPaint );
            pathNode.setStroke( TRACK_STROKE );
            pathNode.setStrokePaint( TRACK_COLOR );
            addChild( pathNode );
        }
    }
    
    /*
     * The slider knob, points to the right.
     * Origin is at the knob's tip.
     */
    private static class KnobNode extends PNode {
        public KnobNode( PDimension size ) {

            float w = (float) size.getWidth();
            float h = (float) size.getHeight();
            GeneralPath knobPath = new GeneralPath();
            knobPath.moveTo( 0f, 0f );
            knobPath.lineTo( -0.35f * w, h / 2f );
            knobPath.lineTo( -w, h / 2f );
            knobPath.lineTo( -w, -h / 2f );
            knobPath.lineTo( -0.35f * w, -h / 2f );
            knobPath.closePath();

            PPath pathNode = new PPath();
            pathNode.setPathTo( knobPath );
            pathNode.setPaint( KNOB_FILL_COLOR );
            pathNode.setStroke( KNOB_STROKE );
            pathNode.setStrokePaint( KNOB_STROKE_COLOR );
            addChild( pathNode );
        }
    }
    
    /*
     * Tick mark, with optional label to the right of the tick.
     * Origin is at the left center of the tick.
     */
    private static class TickMarkNode extends PComposite {
        
        public TickMarkNode() {
            this( null );
        }
        
        public TickMarkNode( String label ) {
            super();
            setPickable( false );
            setChildrenPickable( false );
            
            PPath tickNode = new PPath( new Line2D.Double( 0, 0, TICK_LENGTH, 0 ) );
            tickNode.setStroke( TICK_STROKE );
            tickNode.setStrokePaint( TICK_COLOR );
            addChild( tickNode );
            
            PText labelNode = null;
            if ( label != null ) {
                labelNode = new PText( label );
                labelNode.setFont( TICK_FONT );
                labelNode.setOffset( tickNode.getFullBoundsReference().getMaxX() + TICK_LABEL_SPACING, -labelNode.getFullBoundsReference().getHeight() / 2 );
                addChild( labelNode );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    public void addChangeListener( ChangeListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyChanged() {
        ChangeEvent event = new ChangeEvent( this );
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ChangeListener) i.next() ).stateChanged( event );
        }
    }
}
