// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
import edu.colorado.phet.dilutions.DilutionsResources;
import edu.colorado.phet.dilutions.DilutionsResources.Symbols;
import edu.colorado.phet.dilutions.model.Solute.KoolAid;
import edu.colorado.phet.dilutions.model.Solution;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a beaker.
 * 3D perspective is provided by an image file.
 * Other elements (ticks,...) are added based on specific knowledge about the image file (size, location of spout, etc.)
 * If you change the image file, you may need to adjust this code.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeakerNode extends PComposite {

    // Layout depends on these properties of the image file.
    public static final PDimension CYLINDER_SIZE = new PDimension( 322, 339 ); // the cylindrical portion of the beaker.
    public static final double CYLINDER_END_HEIGHT = 39; // 2D height of the cylinder's ends
    private static final Point2D CYLINDER_OFFSET = new Point2D.Double( 75, 144 ); // the upper right corner of the cylinder, in the image's coordinate frame

    private static final boolean CYLINDER_VISIBLE = false; // for debugging alignment with beaker image file

    private static final String[] MAJOR_TICK_LABELS = { "0.5", "1" };

    private static final Color TICK_COLOR = Color.GRAY;
    private static final Color TICK_LABEL_COLOR = Color.DARK_GRAY;
    private static final double MINOR_TICK_SPACING = 0.1; // L
    private static final int MINOR_TICKS_PER_MAJOR_TICK = 5;
    private static final double MAJOR_TICK_LENGTH = 20;
    private static final double MINOR_TICK_LENGTH = 12;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Font TICK_LABEL_FONT = new PhetFont( 20 );
    private static final double TICK_LABEL_X_SPACING = 8;
    private static final PDimension LABEL_SIZE = new PDimension( 180, 70 );

    private final LabelNode labelNode;
    private final ArrayList<PText> tickLabelNodes;

    public BeakerNode( final double maxVolume, String units, final Solution solution, Property<Boolean> valuesVisible ) {

        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );

        // cylinder that defines the inner part of the beaker that can be filled. Use this to manually align with beaker image file.
        if ( CYLINDER_VISIBLE ) {
            addChild( new CylinderNode( CYLINDER_SIZE ) );
        }

        // inside bottom line
        PPath bottomNode = new PPath() {{
            setPathTo( new Arc2D.Double( 0, CYLINDER_SIZE.getHeight() - ( CYLINDER_END_HEIGHT / 2 ), CYLINDER_SIZE.getWidth(), CYLINDER_END_HEIGHT,
                                         5, 170, Arc2D.OPEN ) );
            setStroke( new BasicStroke( 2f ) );
            setStrokePaint( new Color( 150, 150, 150, 100 ) );
        }};
        addChild( bottomNode );

        // the glass beaker
        PImage imageNode = new PImage( DilutionsResources.Images.BEAKER_IMAGE ) {{
            scale( 0.75 ); //XXX
            setOffset( -CYLINDER_OFFSET.getX(), -CYLINDER_OFFSET.getY() );
        }};
        addChild( imageNode );

        // tick marks
        tickLabelNodes = new ArrayList<PText>();
        PComposite ticksNode = new PComposite();
        addChild( ticksNode );
        int numberOfTicks = (int) Math.round( maxVolume / MINOR_TICK_SPACING );
        final double bottomY = CYLINDER_SIZE.getHeight(); // don't use bounds or position will be off because of stroke width
        double deltaY = CYLINDER_SIZE.getHeight() / numberOfTicks;
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
                        setTextPaint( TICK_LABEL_COLOR );
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
        labelNode = new LabelNode( solution.solute.get().formula, CYLINDER_SIZE );
        addChild( labelNode );
        labelNode.setOffset( ( CYLINDER_SIZE.getWidth() / 2 ), ( 0.25 * CYLINDER_SIZE.getHeight() ) );

        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                // update solute label
                labelNode.setText( ( solution.getConcentration() == 0 ) ? Symbols.WATER : solution.solute.get().formula );
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

    //TODO relate this to SolutionNode
    // Cylinder that defines the shape that can be filled in the beaker, used for debugging alignment with beaker image file.
    private static class CylinderNode extends PPath {
        public CylinderNode( PDimension size ) {
            setStrokePaint( Color.RED );
            Area area = new Area( new Rectangle2D.Double( 0, 0, size.width, size.height ) );
            area.add( new Area( new Ellipse2D.Double( 0, -CYLINDER_END_HEIGHT / 2, size.width, CYLINDER_END_HEIGHT ) ) );
            area.add( new Area( new Ellipse2D.Double( 0, size.height - ( CYLINDER_END_HEIGHT / 2 ), size.width, CYLINDER_END_HEIGHT ) ) );
            setPathTo( area );
        }
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
                setPathTo( new RoundRectangle2D.Double( -LABEL_SIZE.getWidth() / 2, -LABEL_SIZE.getHeight() / 2, LABEL_SIZE.getWidth(), LABEL_SIZE.getHeight(), 10, 10 ) );
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
        final BeakerNode beakerNode = new BeakerNode( 1, "L", solution, valuesVisible ) {{
            setOffset( 200, 200 );
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
