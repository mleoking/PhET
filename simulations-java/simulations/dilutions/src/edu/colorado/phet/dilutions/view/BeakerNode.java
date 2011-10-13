// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.dilutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.dilutions.DilutionsResources.Symbols;
import edu.colorado.phet.dilutions.model.Solute.KoolAid;
import edu.colorado.phet.dilutions.model.Solution;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * BeakerNode is the visual representation of a beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeakerNode extends PComposite {

    private static final String[] MAJOR_TICK_LABELS = { "\u00bd", "1" }; // 1/2L, 1L

    private static final Color TICK_COLOR = Color.BLACK;
    private static final double MINOR_TICK_SPACING = 0.1; // L
    private static final int MINOR_TICKS_PER_MAJOR_TICK = 5;
    private static final double MAJOR_TICK_LENGTH = 20;
    private static final double MINOR_TICK_LENGTH = 12;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Font TICK_LABEL_FONT = new PhetFont( 20 );
    private static final double TICK_LABEL_X_SPACING = 8;

    public static final float STROKE_WIDTH = 6f;
    private static final Stroke OUTLINE_STROKE = new BasicStroke( STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final Color OUTLINE_COLOR = Color.BLACK;

    private static final double SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK = 0;
    private static final Point2D BEAKER_LIP_OFFSET = new Point2D.Double( 20, 20 );

    private final LabelNode labelNode;
    private final ArrayList<PText> tickLabelNodes;

    public BeakerNode( final PDimension beakerSize, final double maxVolume, String units, final Solution solution, Property<Boolean> valuesVisible ) {
        super();
        setPickable( false );
        setChildrenPickable( false );

        // outline
        final float width = (float) beakerSize.getWidth();
        final float height = (float) beakerSize.getHeight();

        // counterclockwise from top left
        GeneralPath beakerPath = new GeneralPath();
        beakerPath.reset();
        beakerPath.moveTo( (float) -BEAKER_LIP_OFFSET.getX(), (float) -( BEAKER_LIP_OFFSET.getY() + SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK ) );
        beakerPath.lineTo( 0f, (float) -SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK );
        beakerPath.lineTo( 0f, height );
        beakerPath.lineTo( width, height );
        beakerPath.lineTo( width, (float) -SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK );
        beakerPath.lineTo( (float) ( width + BEAKER_LIP_OFFSET.getX() ), (float) -( BEAKER_LIP_OFFSET.getY() + SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK ) );

        PPath beakerNode = new PPath( beakerPath ) {{
            setPaint( null );
            setStroke( OUTLINE_STROKE );
            setStrokePaint( OUTLINE_COLOR );
        }};
        addChild( beakerNode );

        // tick marks
        tickLabelNodes = new ArrayList<PText>();
        PComposite ticksNode = new PComposite();
        addChild( ticksNode );
        int numberOfTicks = (int) Math.round( maxVolume / MINOR_TICK_SPACING );
        final double bottomY = beakerSize.getHeight(); // don't use bounds or position will be off because of stroke width
        double deltaY = beakerSize.getHeight() / numberOfTicks;
        for ( int i = 1; i <= numberOfTicks; i++ ) {
            final double y = bottomY - ( i * deltaY );
            if ( i % MINOR_TICKS_PER_MAJOR_TICK == 0 ) {

                // major tick line
                PPath tickNode = new PPath( new Line2D.Double( 0, y, MAJOR_TICK_LENGTH, y ) ) {{
                    setStroke( MAJOR_TICK_STROKE );
                    setStrokePaint( TICK_COLOR );
                }};
                ticksNode.addChild( tickNode );

                // major tick label
                int labelIndex = ( i / MINOR_TICKS_PER_MAJOR_TICK ) - 1;
                if ( labelIndex < MAJOR_TICK_LABELS.length ) {
                    String label = MAJOR_TICK_LABELS[labelIndex] + units;
                    PText textNode = new PText( label ) {{
                        setFont( TICK_LABEL_FONT );
                        setTextPaint( TICK_COLOR );
                    }};
                    ticksNode.addChild( textNode );
                    textNode.setOffset( tickNode.getFullBounds().getMaxX() + TICK_LABEL_X_SPACING,
                                        tickNode.getFullBounds().getMinY() - ( textNode.getFullBoundsReference().getHeight() / 2 ) );
                    tickLabelNodes.add( textNode );
                }
            }
            else {
                // minor tick, no label
                PPath tickNode = new PPath( new Line2D.Double( 0, y, MINOR_TICK_LENGTH, y ) ) {{
                    setStroke( MINOR_TICK_STROKE );
                    setStrokePaint( TICK_COLOR );
                }};
                ticksNode.addChild( tickNode );
            }
        }

        // label on the beaker
        labelNode = new LabelNode( solution.solute.get().formula, beakerSize );
        addChild( labelNode );

        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                // update solute label
                labelNode.setText( ( solution.getConcentration() == 0 ) ? Symbols.WATER : solution.solute.get().formula );
                labelNode.setOffset( ( beakerSize.getWidth() / 2 ), ( 0.35 * beakerSize.getHeight() ) );
            }
        };
        solution.addConcentrationObserver( observer );
        solution.solute.addObserver( observer );

        valuesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setValuesVisible( visible );
            }
        } );
    }

    // Label that appears on the beaker in a frost, translucent frame. Origin at geometric center.
    private static class LabelNode extends PComposite {

        private final HTMLNode htmlNode;
        private final PPath backgroundNode;

        public LabelNode( String text, final PDimension beakerSize ) {

            // nodes
            htmlNode = new HTMLNode( "?" ) {{
                setFont( new PhetFont( Font.BOLD, 28 ) );
            }};
            backgroundNode = new PPath() {{
                setPaint( ColorUtils.createColor( Color.WHITE, 150 ) );
                setStrokePaint( Color.LIGHT_GRAY );
                double width = 0.65 * beakerSize.getWidth();
                double height = 2 * htmlNode.getFullBoundsReference().getHeight();
                setPathTo( new RoundRectangle2D.Double( -width / 2, -height / 2, width, height, 10, 10 ) );
            }};

            // rendering order
            addChild( backgroundNode );
            addChild( htmlNode );

            setText( text );
        }

        public void setText( String text ) {
            // label, centered
            htmlNode.setHTML( text );
            htmlNode.setOffset( -htmlNode.getFullBoundsReference().getWidth() / 2, -htmlNode.getFullBoundsReference().getHeight() / 2 );
        }
    }

    // Controls visibility of tick mark values
    public void setValuesVisible( boolean visible ) {
        for ( PNode node : tickLabelNodes ) {
            node.setVisible( visible );
        }
    }

    // test
    public static void main( String[] args ) {
        Solution solution = new Solution( new KoolAid(), 1, 0.5 );
        Property<Boolean> valuesVisible = new Property<Boolean>( true );
        // beaker
        final BeakerNode beakerNode = new BeakerNode( new PDimension( 300, 300 ), 1, "L", solution, valuesVisible ) {{
            setOffset( 100, 100 );
        }};
        // red dot at beaker's origin
        final PPath originNode = new PPath( new Ellipse2D.Double( -3, -3, 6, 6 ) ) {{
            setPaint( Color.RED );
            setOffset( beakerNode.getOffset() );
        }};
        // canvas
        final PCanvas canvas = new PCanvas() {{
            getLayer().addChild( beakerNode );
            getLayer().addChild( originNode );
            setPreferredSize( new Dimension( 600, 600 ) );
        }};
        // frame
        JFrame frame = new JFrame() {{
            setContentPane( canvas );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}
