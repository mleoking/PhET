// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.dilutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.dilutions.model.Solute;
import edu.colorado.phet.dilutions.model.Solute.KoolAid;
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

    private final HTMLNode labelNode;
    private final ArrayList<PText> tickLabelNodes;

    public BeakerNode( final PDimension size, final double maxVolume, String units, final Property<Solute> solute, Property<Boolean> valuesVisible ) {
        super();
        setPickable( false );
        setChildrenPickable( false );

        // outline
        final float width = (float) size.getWidth();
        final float height = (float) size.getHeight();

        GeneralPath beakerPath = new GeneralPath();
        beakerPath.reset();
        beakerPath.moveTo( (float) -BEAKER_LIP_OFFSET.getX(), (float) -( BEAKER_LIP_OFFSET.getY() + SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK ) );
        beakerPath.lineTo( 0f, (float) -SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK );
        beakerPath.lineTo( 0f, height );
        beakerPath.lineTo( width, height );
        beakerPath.lineTo( width, (float) -SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK );
        beakerPath.lineTo( (float) ( width + BEAKER_LIP_OFFSET.getX() ), (float) -( BEAKER_LIP_OFFSET.getY() + SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK ) );

        PPath beakerNode = new PPath( beakerPath );
        beakerNode.setPaint( null );
        beakerNode.setStroke( OUTLINE_STROKE );
        beakerNode.setStrokePaint( OUTLINE_COLOR );
        addChild( beakerNode );

        // tick marks
        tickLabelNodes = new ArrayList<PText>();
        PComposite ticksNode = new PComposite();
        addChild( ticksNode );
        int numberOfTicks = (int) Math.round( maxVolume / MINOR_TICK_SPACING );
        final double rightX = size.getWidth(); // don't use bounds or position will be off because of stroke width
        final double bottomY = size.getHeight(); // don't use bounds or position will be off because of stroke width
        double deltaY = size.getHeight() / numberOfTicks;
        for ( int i = 1; i <= numberOfTicks; i++ ) {
            final double y = bottomY - ( i * deltaY );
            if ( i % MINOR_TICKS_PER_MAJOR_TICK == 0 ) {
                // major tick
                Shape tickPath = new Line2D.Double( rightX - MAJOR_TICK_LENGTH, y, rightX - 2, y );
                PPath tickNode = new PPath( tickPath );
                tickNode.setStroke( MAJOR_TICK_STROKE );
                tickNode.setStrokePaint( TICK_COLOR );
                ticksNode.addChild( tickNode );

                int labelIndex = ( i / MINOR_TICKS_PER_MAJOR_TICK ) - 1;
                if ( labelIndex < MAJOR_TICK_LABELS.length ) {
                    String label = MAJOR_TICK_LABELS[labelIndex] + units;
                    PText textNode = new PText( label );
                    textNode.setFont( TICK_LABEL_FONT );
                    textNode.setTextPaint( TICK_COLOR );
                    ticksNode.addChild( textNode );
                    double xOffset = tickNode.getFullBounds().getMinX() - textNode.getFullBoundsReference().getWidth() - TICK_LABEL_X_SPACING;
                    double yOffset = tickNode.getFullBounds().getMinY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
                    textNode.setOffset( xOffset, yOffset );
                    tickLabelNodes.add( textNode );
                }
            }
            else {
                // minor tick
                Shape tickPath = new Line2D.Double( rightX - MINOR_TICK_LENGTH, y, rightX - 2, y );
                PPath tickNode = new PPath( tickPath );
                tickNode.setStroke( MINOR_TICK_STROKE );
                tickNode.setStrokePaint( TICK_COLOR );
                ticksNode.addChild( tickNode );
            }
        }

        labelNode = new HTMLNode() {{
            setFont( new PhetFont( Font.BOLD, 24 ) );
        }};
        addChild( labelNode );

        solute.addObserver( new SimpleObserver() {
            public void update() {
                // update solute label
                labelNode.setHTML( solute.get().formula );
                labelNode.setOffset( ( size.getWidth() / 2 ) - ( labelNode.getFullBoundsReference().getWidth() / 2 ),
                                     ( 035 * size.getHeight() ) - ( labelNode.getFullBoundsReference().getHeight() / 2 ) );
            }
        } );

        valuesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setValuesVisible( visible );
            }
        } );
    }

    // Controls visibility of tick mark values
    public void setValuesVisible( boolean visible ) {
        for ( PNode node : tickLabelNodes ) {
            node.setVisible( visible );
        }
    }

    public static Point2D getLipOffset() {
        return new Point2D.Double( BEAKER_LIP_OFFSET.getX(), BEAKER_LIP_OFFSET.getY() );
    }

    public static void main( String[] args ) {
        Property<Solute> solute = new Property<Solute>( new KoolAid() );
        Property<Boolean> valuesVisible = new Property<Boolean>( true );
        // beaker
        BeakerNode beakerNode = new BeakerNode( new PDimension( 300, 300 ), 1, "L", solute, valuesVisible );
        beakerNode.setOffset( 100, 100 );
        // red dot at beaker's origin
        PPath originNode = new PPath( new Ellipse2D.Double( -3, -3, 6, 6 ) );
        originNode.setPaint( Color.RED );
        originNode.setOffset( beakerNode.getOffset() );
        // canvas
        PCanvas canvas = new PCanvas();
        canvas.getLayer().addChild( beakerNode );
        canvas.getLayer().addChild( originNode );
        canvas.setPreferredSize( new Dimension( 600, 600 ) );
        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
