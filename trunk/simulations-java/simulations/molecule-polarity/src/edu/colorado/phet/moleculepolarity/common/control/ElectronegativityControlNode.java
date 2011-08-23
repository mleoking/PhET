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
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.colorado.phet.moleculepolarity.common.model.IMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Slider control for electronegativity.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ElectronegativityControlNode extends PhetPNode {

    // track
    private static final PDimension TRACK_SIZE = new PDimension( 125, 5 );
    private static final Color TRACK_FILL_COLOR = Color.LIGHT_GRAY;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );

    // knob
    private static final PDimension KNOB_SIZE = new PDimension( 15, 20 );
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    private static final Color KNOB_HIGHLIGHT_COLOR = Color.GREEN;
    private static final Color KNOB_NORMAL_COLOR = KNOB_HIGHLIGHT_COLOR.darker();
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;

    // ticks
    private static final double MAJOR_TICK_LENGTH = 10;
    private static final double MINOR_TICK_LENGTH = 5;

    // background
    private static final double BACKGROUND_X_MARGIN = 10;
    private static final double BACKGROUND_Y_MARGIN = 5;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 2f );
    private static final Color BACKGROUND_STROKE_COLOR = Color.GRAY;

    /**
     * Constructor
     *
     * @param atom         the atom whose electronegativity we're controlling
     * @param molecule     molecule that the atom belongs to, for pausing animation while this control is used
     * @param range        range of electronegativity
     * @param snapInterval knob will snap to this increment when released
     */
    public ElectronegativityControlNode( final Atom atom, IMolecule molecule, DoubleRange range, double snapInterval ) {

        final PanelNode panelNode = new PanelNode( atom, molecule, range, snapInterval );
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
        private final KnobNode knobNode;
        private final DoubleRange range;

        public PanelNode( final Atom atom, IMolecule molecule, DoubleRange range, double snapInterval ) {

            this.atom = atom;
            this.range = range;

            trackNode = new TrackNode();
            knobNode = new KnobNode( molecule, this, trackNode, range, snapInterval, atom );
            PText labelNode = new PText( MPStrings.ELECTRONEGATIVITY ) {{
                setFont( new PhetFont( 14 ) );
            }};
            TickMarkNode minTickNode = new TickMarkNode( MAJOR_TICK_LENGTH, MPStrings.LESS );
            TickMarkNode maxTickNode = new TickMarkNode( MAJOR_TICK_LENGTH, MPStrings.MORE );

            // rendering order
            addChild( trackNode );
            addChild( minTickNode );
            addChild( maxTickNode );
            addChild( knobNode );
            addChild( labelNode );

            // layout
            {
                // track at origin
                double x = 0;
                double y = 0;
                trackNode.setOffset( x, y );
                // min tick at left end of track
                x = trackNode.getFullBoundsReference().getMinX();
                y = trackNode.getFullBoundsReference().getMaxY();
                minTickNode.setOffset( x, y );
                // max tick at right end of track
                x = trackNode.getFullBoundsReference().getMaxX() - 1;
                y = trackNode.getFullBoundsReference().getMaxY();
                maxTickNode.setOffset( x, y );
                // knob centered in track
                x = trackNode.getFullBoundsReference().getCenterX();
                y = trackNode.getFullBoundsReference().getCenterY() + ( knobNode.getFullBoundsReference().getHeight() / 2 );
                knobNode.setOffset( x, y );
                // label centered above the track
                x = trackNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 );
                y = trackNode.getFullBoundsReference().getMinY() - labelNode.getFullBoundsReference().getHeight() - 15;
                labelNode.setOffset( x, y );
            }

            atom.electronegativity.addObserver( new SimpleObserver() {
                public void update() {
                    updateControl();
                }
            } );
        }

        // Updates the control to match the capacitor model.
        public void updateControl() {
            // knob location
            LinearFunction f = new LinearFunction( range.getMin(), range.getMax(), trackNode.getXOffset(), trackNode.getFullBoundsReference().getMaxX() - 1 );
            double x = f.evaluate( atom.electronegativity.get() );
            double y = knobNode.getYOffset();
            knobNode.setOffset( x, y );
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
     * The slider knob, points down.
     * Origin is at the knob's tip.
     */
    private static class KnobNode extends PPath {

        public KnobNode( IMolecule molecule, PNode relativeNode, PNode trackNode, DoubleRange range, double snapInterval, final Atom atom ) {

            float w = (float) KNOB_SIZE.getWidth();
            float h = (float) KNOB_SIZE.getHeight();
            GeneralPath path = new GeneralPath();
            path.moveTo( 0f, 0f );
            path.lineTo( w / 2f, 0.35f * -h );
            path.lineTo( w / 2f, -h );
            path.lineTo( -w / 2f, -h );
            path.lineTo( -w / 2f, 0.35f * -h );
            path.closePath();

            setPathTo( path );
            setPaint( KNOB_NORMAL_COLOR );
            setStroke( KNOB_STROKE );
            setStrokePaint( KNOB_STROKE_COLOR );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PaintHighlightHandler( this, KNOB_NORMAL_COLOR, KNOB_HIGHLIGHT_COLOR ) );
            addInputEventListener( new KnobDragHandler( molecule, relativeNode, trackNode, this, range, snapInterval,
                                                        new VoidFunction1<Double>() {
                                                            public void apply( Double value ) {
                                                                atom.electronegativity.set( value );
                                                            }
                                                        } ) );
        }
    }

    // Drag handler for the knob, snaps to closet value.
    private static class KnobDragHandler extends HorizontalSliderDragHandler {

        private final IMolecule molecule;
        private final double snapInterval; // slider snaps to closet model value in this interval

        // see superclass for constructor params
        public KnobDragHandler( IMolecule molecule, PNode relativeNode, PNode trackNode, PNode knobNode, DoubleRange range, double snapInterval, VoidFunction1<Double> updateFunction ) {
            super( relativeNode, trackNode, knobNode, range, updateFunction );
            this.molecule = molecule;
            this.snapInterval = snapInterval;
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
     * Tick mark, a vertical line (tick) with optional label below it.
     * Origin at top center of tick.
     */
    private static class TickMarkNode extends PComposite {

        public TickMarkNode( double length ) {
            this( length, null );
        }

        public TickMarkNode( double length, String label ) {

            PPath tickNode = new PPath( new Line2D.Double( 0, 0, 0, length ) );
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

    // test
    public static void main( String[] args ) {

        DiatomicMolecule molecule = new DiatomicMolecule( new ImmutableVector2D() );
        Atom atom = molecule.atomA;
        atom.electronegativity.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                System.out.println( "electronegativity=" + value );
            }
        } );

        ElectronegativityControlNode controlNode = new ElectronegativityControlNode( atom, molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
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
