// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.view.MinusNode;
import edu.colorado.phet.capacitorlab.view.PlusNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * "Zoom" button, used for changing the scale on meters.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ZoomButton extends JButton {

    public static class ZoomInButton extends ZoomButton {
        public ZoomInButton() {
            super( true );
        }
    }

    public static class ZoomOutButton extends ZoomButton {
        public ZoomOutButton() {
            super( false );
        }
    }

    public ZoomButton( boolean zoomIn ) {
        setIcon( new ImageIcon( new ZoomImageNode( zoomIn ).toImage() ) );
    }

    private static class ZoomImageNode extends PComposite {

        // all other dimensions are derived from the glass diameter
        private static final double GLASS_DIAMETER = 13;

        private static final Color COLOR = Color.BLACK;

        private static final float GLASS_STROKE_WIDTH = (float) ( GLASS_DIAMETER / 7f );
        private static final Stroke GLASS_STROKE = new BasicStroke( GLASS_STROKE_WIDTH );

        private static final double HANDLE_WIDTH = 0.2 * GLASS_DIAMETER;
        private static final double HANDLE_HEIGHT = 0.65 * GLASS_DIAMETER;

        private static final double PLUS_MINUS_WIDTH = 0.6 * GLASS_DIAMETER;
        private static final double PLUS_MINUS_HEIGHT = 0.1 * GLASS_DIAMETER;

        public ZoomImageNode( boolean zoomIn ) {

            PPath glassNode = new PPath( new Ellipse2D.Double( 0, 0, GLASS_DIAMETER, GLASS_DIAMETER ) );
            glassNode.setPaint( new Color( 0, 0, 0, 0 ) ); // transparent, so that clicking in the center of the glass works
            glassNode.setStroke( GLASS_STROKE );
            glassNode.setStrokePaint( COLOR );
            addChild( glassNode );

            PPath handleNode = new PPath( new Rectangle2D.Double( 0, 0, HANDLE_WIDTH, HANDLE_HEIGHT ) );
            handleNode.setStroke( null );
            handleNode.setPaint( COLOR );
            addChild( handleNode );

            // plus or minus sign
            PNode signNode = null;
            if ( zoomIn ) {
                signNode = new PlusNode( PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT, COLOR );
            }
            else {
                signNode = new MinusNode( PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT, COLOR );
            }
            addChild( signNode );

            // layout
            double x = 0;
            double y = 0;
            glassNode.setOffset( x, y );
            x = glassNode.getFullBoundsReference().getCenterX() - ( handleNode.getFullBoundsReference().getWidth() / 2 );
            y = glassNode.getFullBoundsReference().getMaxY() - ( GLASS_STROKE_WIDTH / 2 );
            handleNode.setOffset( x, y );
            x = glassNode.getFullBoundsReference().getCenterX();
            y = glassNode.getFullBoundsReference().getCenterY();
            signNode.setOffset( x, y );
        }
    }
}
