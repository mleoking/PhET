// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.MessageFormat;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPSimSharing.Parameters;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.colorado.phet.moleculepolarity.common.model.Molecule2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Slider control for electronegativity.
 * Dragging the slider continuously updates an atom's electronegativity.
 * When the slider's thumb is released, it snaps to the closest tick mark.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ElectronegativityControlNode extends PhetPNode {

    // track
    private static final PDimension TRACK_SIZE = new PDimension( 125, 5 );
    private static final Color TRACK_FILL_COLOR = Color.LIGHT_GRAY;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );

    // thumb
    private static final PDimension THUMB_SIZE = new PDimension( 15, 20 );
    private static final Stroke THUMB_STROKE = new BasicStroke( 1f );
    private static final Color THUMB_STROKE_COLOR = Color.BLACK;
    private static final Color THUMB_HIGHLIGHT_COLOR = Color.GREEN;
    private static final Color THUMB_NORMAL_COLOR = THUMB_HIGHLIGHT_COLOR.darker();

    // ticks
    private static final double MAJOR_TICK_LENGTH = 12;
    private static final double MINOR_TICK_LENGTH = 7;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 1f );
    private static final double MAJOR_TICK_INTERVAL = 1.0;

    // background
    private static final double BACKGROUND_X_MARGIN = 10;
    private static final double BACKGROUND_Y_MARGIN = 5;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 2f );
    private static final Color BACKGROUND_STROKE_COLOR = Color.GRAY;

    /**
     * Constructor
     *
     * @param userComponent
     * @param atom          the atom whose electronegativity we're controlling
     * @param molecule      molecule that the atom belongs to, for pausing animation while this control is used
     * @param range         range of electronegativity
     * @param snapInterval  thumb will snap to this increment when released, also determines the tick mark spacing
     */
    public ElectronegativityControlNode( IUserComponent userComponent, final Atom atom, Molecule2D molecule, DoubleRange range, double snapInterval ) {

        final PanelNode panelNode = new PanelNode( userComponent, atom, molecule, range, snapInterval );
        String title = MessageFormat.format( MPStrings.PATTERN_0ATOM_NAME, atom.getName() );
        TitledBackgroundNode backgroundNode = new TitledBackgroundNode( title, atom.getColor(), panelNode, BACKGROUND_X_MARGIN, BACKGROUND_Y_MARGIN );
        addChild( backgroundNode );

        atom.electronegativity.addObserver( new SimpleObserver() {
            public void update() {
                panelNode.updateControl();
            }
        } );
    }

    /*
     * A titled background for any node.
     * The title is centered at the top of the background.
     * If the title is narrower than the node, the title looks like it's in a tab.
     */
    private static class TitledBackgroundNode extends PhetPNode {

        private static final double Y_SPACING = 10;
        private static final double CORNER_RADIUS = 10;

        public TitledBackgroundNode( String title, final Color fillColor, PNode child, double xMargin, double yMargin ) {

            PText titleNode = new PText( title ) {{
                setFont( new PhetFont( 20 ) );
            }};

            double panelWidth = Math.max( titleNode.getFullBoundsReference().getWidth(), child.getFullBoundsReference().getWidth() ) + ( 2 * xMargin );
            double panelHeight = ( titleNode.getFullBoundsReference().getHeight() / 2 ) + child.getFullBoundsReference().getHeight() + Y_SPACING;
            Shape panelShape = new RoundRectangle2D.Double( 0, 0, panelWidth, panelHeight, CORNER_RADIUS, CORNER_RADIUS );

            double titleX = ( panelWidth / 2 ) - ( titleNode.getFullBoundsReference().getWidth() / 2 ) - xMargin;
            double titleY = -( titleNode.getFullBoundsReference().getHeight() / 2 ) - yMargin;
            double titleWidth = titleNode.getFullBoundsReference().getWidth() + ( 2 * xMargin );
            double titleHeight = titleNode.getFullBoundsReference().getHeight() + ( 2 * yMargin );
            Shape titleShape = new RoundRectangle2D.Double( titleX, titleY, titleWidth, titleHeight, CORNER_RADIUS, CORNER_RADIUS );

            Area area = new Area( panelShape );
            area.add( new Area( titleShape ) );

            PPath backgroundNode = new PPath( area ) {{
                setStroke( BACKGROUND_STROKE );
                setStrokePaint( BACKGROUND_STROKE_COLOR );
                setPaint( fillColor );
            }};

            addChild( backgroundNode );
            addChild( child );
            addChild( titleNode );

            double x = ( panelWidth / 2 ) - ( child.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( child );
            double y = -PNodeLayoutUtils.getOriginYOffset( child ) + ( titleNode.getFullBoundsReference().getHeight() / 2 );
            child.setOffset( x, y );
            x = ( panelWidth / 2 ) - ( titleNode.getFullBoundsReference().getWidth() / 2 );
            y = -( titleNode.getFullBoundsReference().getHeight() / 2 );
            titleNode.setOffset( x, y );
        }
    }

    /*
     * The panel that contains all of the control's components - the slider and the label.
     * Origin is at the track's origin.
     */
    private static class PanelNode extends PhetPNode {

        private final Atom atom;
        private final TrackNode trackNode;
        private final ThumbNode thumbNode;
        private final DoubleRange range;

        public PanelNode( IUserComponent userComponent, final Atom atom, Molecule2D molecule, DoubleRange range, double snapInterval ) {

            this.atom = atom;
            this.range = range;

            // origin is at the track's upper-left corner
            trackNode = new TrackNode();
            addChild( trackNode );
            trackNode.setOffset( 0, 0 );

            // label, placed above the track
            PText labelNode = new PText( MPStrings.ELECTRONEGATIVITY ) {{
                setFont( new PhetFont( 14 ) );
            }};
            addChild( labelNode );
            labelNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 ),
                                 trackNode.getFullBoundsReference().getMinY() - labelNode.getFullBoundsReference().getHeight() - 15 );

            // tick marks below the track, left to right
            int numberOfTicks = (int) ( range.getLength() / snapInterval ) + 1;
            double xSpacing = TRACK_SIZE.getWidth() / ( numberOfTicks - 1 );
            for ( int i = 0; i < numberOfTicks; i++ ) {
                TickMarkNode tickMarkNode;
                if ( i == 0 ) {
                    tickMarkNode = new MajorTickMarkNode( MPStrings.LESS );
                }
                else if ( i == numberOfTicks - 1 ) {
                    tickMarkNode = new MajorTickMarkNode( MPStrings.MORE );
                }
                else {
                    if ( ( i * snapInterval ) % MAJOR_TICK_INTERVAL == 0 ) {
                        tickMarkNode = new MajorTickMarkNode();
                    }
                    else {
                        tickMarkNode = new MinorTickMarkNode();
                    }
                }
                addChild( tickMarkNode );
                tickMarkNode.setOffset( trackNode.getXOffset() + ( i * xSpacing ),
                                        trackNode.getFullBoundsReference().getMaxY() );
            }

            // start with the thumb centered in the track
            thumbNode = new ThumbNode( userComponent, molecule, this, trackNode, range, snapInterval, atom );
            addChild( thumbNode );
            thumbNode.setOffset( trackNode.getFullBoundsReference().getCenterX(),
                                 trackNode.getFullBoundsReference().getCenterY() + ( thumbNode.getFullBoundsReference().getHeight() / 3 ) );

            atom.electronegativity.addObserver( new SimpleObserver() {
                public void update() {
                    updateControl();
                }
            } );
        }

        // Updates the control to match the model.
        public void updateControl() {
            // knob location
            LinearFunction f = new LinearFunction( range.getMin(), range.getMax(), trackNode.getXOffset(), trackNode.getXOffset() + TRACK_SIZE.getWidth() );
            double x = f.evaluate( atom.electronegativity.get() );
            double y = thumbNode.getYOffset();
            thumbNode.setOffset( x, y );
        }
    }

    /*
     * The track that the bar moves in.
     * Origin is at upper-left corner.
     */
    private static class TrackNode extends PPath {

        public TrackNode() {
            setPathTo( new Rectangle2D.Double( 0, 0, TRACK_SIZE.width, TRACK_SIZE.height ) );
            setPaint( TRACK_FILL_COLOR );
            setStrokePaint( TRACK_STROKE_COLOR );
            setStroke( TRACK_STROKE );
        }
    }

    /*
     * The slider thumb (aka knob), points down.
     * Origin is at the thumb's tip.
     */
    private static class ThumbNode extends PPath {

        public ThumbNode( IUserComponent userComponent, Molecule2D molecule, PNode relativeNode, PNode trackNode, DoubleRange range, double snapInterval, final Atom atom ) {

            float w = (float) THUMB_SIZE.getWidth();
            float h = (float) THUMB_SIZE.getHeight();
            GeneralPath path = new GeneralPath();
            path.moveTo( 0f, 0f );
            path.lineTo( w / 2f, 0.35f * -h );
            path.lineTo( w / 2f, -h );
            path.lineTo( -w / 2f, -h );
            path.lineTo( -w / 2f, 0.35f * -h );
            path.closePath();

            setPathTo( path );
            setPaint( THUMB_NORMAL_COLOR );
            setStroke( THUMB_STROKE );
            setStrokePaint( THUMB_STROKE_COLOR );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PaintHighlightHandler( this, THUMB_NORMAL_COLOR, THUMB_HIGHLIGHT_COLOR ) );
            addInputEventListener( new ThumbDragHandler( userComponent, molecule, atom, relativeNode, trackNode, this, range, snapInterval ) );
        }
    }

    // Drag handler for the thumb, snaps to closest tick mark.
    private static class ThumbDragHandler extends SliderThumbDragHandler {

        private final Molecule2D molecule;
        private final Atom atom;
        private final double snapInterval; // slider snaps to closet model value in this interval

        // see superclass for constructor params
        public ThumbDragHandler( IUserComponent userComponent, Molecule2D molecule, final Atom atom, PNode relativeNode, PNode trackNode, PNode thumbNode, DoubleRange range, double snapInterval ) {
            super( userComponent, false, Orientation.HORIZONTAL, relativeNode, trackNode, thumbNode, range,
                   new VoidFunction1<Double>() {
                       public void apply( Double value ) {
                           atom.electronegativity.set( value );
                       }
                   } );
            this.molecule = molecule;
            this.atom = atom;
            this.snapInterval = snapInterval;
        }

        // Add some custom parameters
        @Override protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
            return super.getParametersForAllEvents( event ).add( Parameters.atom, atom.getName() ).add( Parameters.electronegativity, atom.electronegativity.get() );
        }

        // snaps to the closest value
        @Override protected double adjustValue( double value ) {
            return Math.floor( ( value / snapInterval ) + 0.5d ) * snapInterval;
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            molecule.setDragging( true );
        }

        @Override protected void endDrag( PInputEvent event ) {
            super.endDrag( event );
            molecule.setDragging( false );
        }
    }

    /*
    * Base class for tick marks.
    * A tick mark is a vertical line with optional label below it.
    * Origin is at the top center of line.
    */
    private abstract static class TickMarkNode extends PComposite {

        public TickMarkNode( double length, final Stroke stroke, String label ) {

            PPath tickNode = new PPath( new Line2D.Double( 0, 0, 0, length ) ) {{
                setStroke( stroke );
            }};
            addChild( tickNode );

            if ( label != null && label.trim().length() != 0 ) {
                PText labelNode = new PText( label ) {{
                    setFont( new PhetFont( 12 ) );
                }};
                addChild( labelNode );
                double x = tickNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 );
                double y = tickNode.getFullBoundsReference().getMaxY() + 2;
                labelNode.setOffset( x, y );
            }
        }
    }

    // Minor tick marks have no label.
    private static class MinorTickMarkNode extends TickMarkNode {
        public MinorTickMarkNode() {
            super( MINOR_TICK_LENGTH, MINOR_TICK_STROKE, null );
        }
    }

    // Major tick marks have an optional label.
    private static class MajorTickMarkNode extends TickMarkNode {

        public MajorTickMarkNode() {
            this( null );
        }

        public MajorTickMarkNode( String label ) {
            super( MAJOR_TICK_LENGTH, MAJOR_TICK_STROKE, label );
        }
    }

    // test

    public static void main( String[] args ) {

        DiatomicMolecule molecule = new DiatomicMolecule( new ImmutableVector2D(), 0 );
        Atom atom = molecule.atomA;
        atom.electronegativity.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                System.out.println( "electronegativity=" + value );
            }
        } );

        ElectronegativityControlNode controlNode = new ElectronegativityControlNode( new UserComponent( "enControl" ), atom, molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        controlNode.setOffset( 100, 100 );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 400, 300 ) );
        canvas.getLayer().addChild( controlNode );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
