// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.beaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a beaker.
 * 3D perspective is provided by an image (see BeakerImageNode).
 * Other elements (ticks, label, ...) are added programmatically.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeakerNode extends PNode {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( BeakerNode.class.getCanonicalName() );

    // tick mark properties
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final Color MINOR_TICK_COLOR = Color.DARK_GRAY;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );

    private final BeakerImageNode beakerImageNode;
    public final LabelNode labelNode;

    public BeakerNode( double maxVolume, final double imageScaleX, final double imageScaleY, String labelText, PDimension labelSize, Font labelFont, double MINOR_TICK_SPACING, int MINOR_TICKS_PER_MAJOR_TICK, final BufferedImage image ) {

        // the glass beaker
        beakerImageNode = new BeakerImageNode( image ) {{
            getTransformReference( true ).scale( imageScaleX, imageScaleY );
        }};
        final PDimension cylinderSize = beakerImageNode.getCylinderSize();
        final Point2D cylinderOffset = beakerImageNode.getCylinderOffset();
        final double cylinderEndHeight = beakerImageNode.getCylinderEndHeight();
        beakerImageNode.setOffset( -cylinderOffset.getX(), -cylinderOffset.getY() );

        // inside bottom line
        PPath bottomNode = new PPath() {{
            setPathTo( new Arc2D.Double( 0, cylinderSize.getHeight() - ( cylinderEndHeight / 2 ), cylinderSize.getWidth(), cylinderEndHeight, 5, 170, Arc2D.OPEN ) );
            setStroke( new BasicStroke( 2f ) );
            setStrokePaint( new Color( 150, 150, 150, 100 ) );
        }};

        addChild( bottomNode );
        addChild( beakerImageNode );

        // tick marks, arcs that wrap around the edge of the beaker's cylinder
        PComposite ticksNode = new PComposite();
        addChild( ticksNode );
        int numberOfTicks = (int) Math.round( maxVolume / MINOR_TICK_SPACING );
        final double bottomY = cylinderSize.getHeight(); // don't use bounds or position will be off because of stroke width
        double deltaY = cylinderSize.getHeight() / numberOfTicks;
        for ( int i = 1; i <= numberOfTicks; i++ ) {
            final double y = bottomY - ( i * deltaY ) - ( cylinderEndHeight / 2 );
            if ( i % MINOR_TICKS_PER_MAJOR_TICK == 0 ) {
                // major tick, no label
                PPath tickNode = new PPath( new Arc2D.Double( 0, y, cylinderSize.getWidth(), cylinderEndHeight, 195, 30, Arc2D.OPEN ) ) {{
                    setStroke( MAJOR_TICK_STROKE );
                    setStrokePaint( MAJOR_TICK_COLOR );
                }};
                ticksNode.addChild( tickNode );
            }
            else {
                // minor tick, no label
                PPath tickNode = new PPath( new Arc2D.Double( 0, y, cylinderSize.getWidth(), cylinderEndHeight, 195, 15, Arc2D.OPEN ) ) {{
                    setStroke( MINOR_TICK_STROKE );
                    setStrokePaint( MINOR_TICK_COLOR );
                }};
                ticksNode.addChild( tickNode );
            }
        }

        // label on the beaker
        labelNode = new LabelNode( labelText, labelSize, labelFont );
        addChild( labelNode );
        labelNode.setOffset( ( cylinderSize.getWidth() / 2 ), ( 0.25 * cylinderSize.getHeight() ) );
    }

    public PDimension getCylinderSize() {
        return beakerImageNode.getCylinderSize();
    }

    public double getCylinderEndHeight() {
        return beakerImageNode.getCylinderEndHeight();
    }

    public void setLabelVisible( boolean b ) {
        labelNode.setVisible( false );
    }

    /*
     * Label that appears on the beaker in a frosty, translucent frame.
     * Since we're very tight on horizontal space in the play area, and the label size is static, text is scaled to fit.
     * Origin at geometric center.
     */
    private static class LabelNode extends PComposite {

        private final HTMLNode htmlNode;
        private final PPath backgroundNode;

        public LabelNode( String text, final PDimension labelSize, final Font labelFont ) {

            // nodes
            htmlNode = new HTMLNode( "?" ) {{
                setFont( labelFont );
            }};
            backgroundNode = new PPath() {{
                setPaint( ColorUtils.createColor( Color.WHITE, 150 ) );
                setStrokePaint( Color.LIGHT_GRAY );
                setPathTo( new RoundRectangle2D.Double( -labelSize.getWidth() / 2, -labelSize.getHeight() / 2, labelSize.getWidth(), labelSize.getHeight(), 10, 10 ) );
            }};

            // rendering order
            addChild( backgroundNode );
            addChild( htmlNode );

            setText( text );
        }

        public void setText( String text ) {
            htmlNode.setHTML( text );
            // scale to fit the background with some margin
            final double margin = 2;
            final double scaleX = ( backgroundNode.getFullBoundsReference().getWidth() - ( 2 * margin ) ) / htmlNode.getFullBoundsReference().getWidth();
            final double scaleY = ( backgroundNode.getFullBoundsReference().getHeight() - ( 2 * margin ) ) / htmlNode.getFullBoundsReference().getHeight();
            if ( scaleX < 1 || scaleY < 1 ) {
                double scale = Math.min( scaleX, scaleY );
                LOGGER.info( "text \"" + text + "\" won't fit in beaker label, scaling by " + scale );
                htmlNode.setScale( scale );
            }
            // center in the background
            htmlNode.setOffset( -htmlNode.getFullBoundsReference().getWidth() / 2, -htmlNode.getFullBoundsReference().getHeight() / 2 );
        }
    }
}