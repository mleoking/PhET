// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomResources;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.IAtom;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class ChargeIndicatorNode extends PNode {
    private static final int BOX_DIMENSION = 80;

    private static final double WIDTH = 90;

    public ChargeIndicatorNode( final IDynamicAtom atom ) {
        final PImage chargeMeterImageNode = new PImage( BuildAnAtomResources.getImage( "atom_builder_charge_meter_no_window.png" ) );
        chargeMeterImageNode.setScale( WIDTH / chargeMeterImageNode.getFullBoundsReference().width );
        addChild ( chargeMeterImageNode );

        // Add the pie node now so it is on top of the image.
        final int arcInsetDX = 2;
        final PNode pieNode = new PNode() {{
            // See definition and sample usage in CircularGradientPaint.main
            final int pieHalfWidth = BOX_DIMENSION - arcInsetDX * 2;
            CircularGradientPaint rgp2 = new CircularGradientPaint( new Point2D.Double( pieHalfWidth / 2, ( BOX_DIMENSION ) / 2 ), Color.red, Color.white );
            final PhetPPath redSide = new PhetPPath( new Arc2D.Double( 0, 0, pieHalfWidth, BOX_DIMENSION, 0, 90, Arc2D.PIE ), rgp2 );
            addChild( redSide );

            CircularGradientPaint rgp1 = new CircularGradientPaint( new Point2D.Double( pieHalfWidth / 2, ( BOX_DIMENSION ) / 2 ), Color.white, Color.blue );
            final PhetPPath blueSide = new PhetPPath( new Arc2D.Double( 0, 0, pieHalfWidth, BOX_DIMENSION, 90, 90, Arc2D.PIE ), rgp1 );
            addChild( blueSide );

            //Workaround because CircularGradientPaint (or our usage of it) seems to be off by a pixel or so
            double seamPaintWidth=2;
            Rectangle2D.Double fixPaint = new Rectangle2D.Double( pieHalfWidth / 2 - seamPaintWidth/2, 0, seamPaintWidth, BOX_DIMENSION/2 );
            addChild( new PhetPPath( fixPaint, Color.white) );//Paint over the seam, couldn't get it fixed in CircularGradientPaint yet, only problematic on large window sizes and/or under 1.6
            final PhetPPath pieBorder = new PhetPPath( new Arc2D.Double( 0, 0, pieHalfWidth, BOX_DIMENSION, 0, 180, Arc2D.PIE ), new BasicStroke(2f), Color.GRAY );
            addChild( pieBorder );
        }};

        // Position the "pie" (which is the charge indicator) over the
        // background of the meter.  There is a "tweak factor" in here, so
        // tweak as needed.
        pieNode.setOffset( chargeMeterImageNode.getFullBoundsReference().width / 2 - pieNode.getFullBoundsReference().width / 2, 7 );
        addChild( pieNode );

        // Add the numeric indicator, which consists of a background and some text.
        addChild( new PhetPPath(Color.WHITE, new BasicStroke( 2f), Color.LIGHT_GRAY){{
            Shape shape = new RoundRectangle2D.Double(
                    0,
                    0,
                    pieNode.getFullBoundsReference().width * 0.6,
                    (chargeMeterImageNode.getFullBoundsReference().getMaxY() - pieNode.getFullBoundsReference().getMaxY()) * 0.7,
                    4,
                    4);
            setPathTo( shape );
            setOffset(
                    pieNode.getFullBoundsReference().getCenterX() - getFullBoundsReference().width / 2,
                    pieNode.getFullBoundsReference().getMaxY() + 5 );
        }} );

        final PText textNode = new PText( atom.getCharge() + "" ) {{setFont( BuildAnAtomConstants.WINDOW_TITLE_FONT );}};
        //center text below pie
        AtomListener updateText = new AtomListener.Adapter() {
            @Override
            public void configurationChanged() {
                textNode.setText( atom.getFormattedCharge() );
                textNode.setOffset( pieNode.getFullBounds().getCenterX() - textNode.getFullBounds().getWidth() / 2,
                        ( pieNode.getFullBounds().getMaxY() + chargeMeterImageNode.getFullBounds().getMaxY() ) / 2 - textNode.getFullBounds().getHeight() / 2 );
                textNode.setTextPaint( getTextPaint( atom ) );
            }
        };
        atom.addAtomListener( updateText );
        updateText.configurationChanged(); // Initial update.
        addChild( textNode );

        final PhetPPath arrowNode = new PhetPPath( Color.black );
        final AtomListener updateArrow = new AtomListener.Adapter() {
            @Override
            public void configurationChanged() {
                Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 12, -Math.PI / 2, 0 );//can only have 11 electrons, but map 12 to theta=0 so 11 looks maxed out
                double angle = linearFunction.evaluate( atom.getCharge() );
                arrowNode.setPathTo( new Arrow( new Point2D.Double( pieNode.getFullBounds().getCenterX(), pieNode.getFullBounds().getMaxY() ),
                                                ImmutableVector2D.parseAngleAndMagnitude( pieNode.getFullBounds().getHeight() * 0.98, angle ), 8, 8, 4, 4, false ).getShape() );
            }
        };
        atom.addAtomListener( updateArrow );
        updateArrow.configurationChanged();
        addChild( arrowNode );

        //+ and - labels on the pie part of the indicator
        addChild( new ShadowPText( "+" ) {{
            setFont( BuildAnAtomConstants.WINDOW_TITLE_FONT );
            setOffset( pieNode.getFullBounds().getWidth() * 3.0 / 4.0 - getFullBounds().getWidth() / 2, pieNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
            setTextPaint( Color.red );
        }} );

        addChild( new ShadowPText( "-" ) {{
            setFont( BuildAnAtomConstants.WINDOW_TITLE_FONT );
            setOffset( pieNode.getFullBounds().getWidth() * 1.0 / 4.0 - getFullBounds().getWidth() / 2, pieNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
            setTextPaint( new Color( 69, 94, 255 ) );//Blue that shows up against black
        }} );

        // Add the node that stacks charge icons horizontally next to one
        // another.
        addChild( new ChargePairingGraphNode( atom ){{
            setOffset(chargeMeterImageNode.getFullBoundsReference().getMaxX() + 15,
                    chargeMeterImageNode.getFullBoundsReference().getCenterY() - 5);
        }});
    }

    private Paint getTextPaint( IAtom atom ) {
        if ( atom.getCharge() == 0 ) {
            return Color.black;
        }
        else if ( atom.getCharge() > 0 ) {
            return Color.red;
        }
        else {
            return Color.blue;
        }
    }
}