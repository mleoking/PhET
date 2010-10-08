package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class ChargeIndicatorNode extends PNode {
    final int BOX_DIMENSION = 80;
    private static final Color purple = new Color( 112, 48, 160 );

    public ChargeIndicatorNode( final Atom atom ) {
        final PhetPPath boxNode = new PhetPPath( new Rectangle2D.Double( 0, 0, BOX_DIMENSION, BOX_DIMENSION ), BuildAnAtomConstants.READOUT_BACKGROUND_COLOR, new BasicStroke( 1 ), Color.black );
        addChild( boxNode );
        int arcOffsetY = 10;
        final int arcInsetDX = 2;
        final PNode pieNode = new PNode() {{
            //See definitation and sample usage in CircularGradientPaint.main
            CircularGradientPaint rgp2 = new CircularGradientPaint( new Point2D.Double( ( BOX_DIMENSION - arcInsetDX * 2 ) / 2, ( BOX_DIMENSION ) / 2 ), Color.red, Color.white );
            final PhetPPath redSide = new PhetPPath( new Arc2D.Double( 0, 0, BOX_DIMENSION - arcInsetDX * 2, BOX_DIMENSION, 0, 90, Arc2D.PIE ), rgp2 );
            addChild( redSide );

            CircularGradientPaint rgp1 = new CircularGradientPaint( new Point2D.Double( ( BOX_DIMENSION - arcInsetDX * 2 ) / 2, ( BOX_DIMENSION ) / 2 ), Color.white, Color.blue);
            final PhetPPath blueSide= new PhetPPath( new Arc2D.Double( 0, 0, BOX_DIMENSION - arcInsetDX * 2, BOX_DIMENSION, 90, 90, Arc2D.PIE ), rgp1 );
            addChild( blueSide );
        }};

        addChild( pieNode );
        final PText textNode = new PText( atom.getCharge() + "" ) {{setFont( BuildAnAtomConstants.READOUT_FONT );}};
        //center text below pie
        SimpleObserver updateText = new SimpleObserver() {
            public void update() {
                textNode.setOffset( pieNode.getFullBounds().getCenterX() - textNode.getFullBounds().getWidth() / 2, ( pieNode.getFullBounds().getMaxY() + boxNode.getFullBounds().getMaxY() ) / 2 - textNode.getFullBounds().getHeight() / 2 );
                textNode.setTextPaint( getTextPaint( atom ) );
                textNode.setText( atom.getFormattedCharge() );
            }
        };
        atom.addObserver( updateText );
        updateText.update();
        addChild( textNode );

        final PhetPPath arrowNode = new PhetPPath( Color.black);
        final SimpleObserver updateArrow = new SimpleObserver() {
            public void update() {
                Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 12, -Math.PI / 2, 0 );//can only have 11 electrons, but map 12 to theta=0 so 11 looks maxed out
                double angle = linearFunction.evaluate( atom.getCharge() );
                arrowNode.setPathTo( new Arrow( new Point2D.Double( pieNode.getFullBounds().getCenterX(), pieNode.getFullBounds().getMaxY() ),
                                                ImmutableVector2D.parseAngleAndMagnitude( pieNode.getFullBounds().getHeight() * 0.98, angle ), 8, 8, 4, 4, false ).getShape() );
            }
        };
        atom.addObserver( updateArrow );
        updateArrow.update();
        addChild( arrowNode );

        //+ and - labels on the pie part of the indicator
        addChild( new ShadowPText( "+" ) {{
            setFont( BuildAnAtomConstants.READOUT_FONT );
            setOffset( pieNode.getFullBounds().getWidth() * 3.0 / 4.0 - getFullBounds().getWidth() / 2, pieNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
            setTextPaint( Color.red );
        }} );

        addChild( new ShadowPText( "-" ) {{
            setFont( BuildAnAtomConstants.READOUT_FONT );
            setOffset( pieNode.getFullBounds().getWidth() * 1.0 / 4.0 - getFullBounds().getWidth() / 2, pieNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
            setTextPaint( new Color( 69, 94, 255 ) );//Blue that shows up against black
        }} );

        // TODO: i18n
        final PText atomText = new PText( "Atom" ) {{setFont( new PhetFont( 18, true ) );}};
        // TODO: i18n
        final PText ionText = new PText( "Ion" ) {{setFont( new PhetFont( 18, true ) );}};

        double xSpacing = 30;
        ionText.translate( boxNode.getFullBounds().getWidth() + xSpacing, boxNode.getFullBounds().getHeight() - ionText.getFullBounds().getHeight() );
        atomText.setOffset( ionText.getOffset().getX(), ionText.getOffset().getY() - atomText.getFullBounds().getHeight() );

        addChild( atomText );
        addChild( ionText );

        final SimpleObserver updateIconText = new SimpleObserver() {
            public void update() {
                if ( atom.getCharge() == 0 ) {
                    atomText.setTextPaint( getTextPaint( atom ) );
                    ionText.setTextPaint( Color.darkGray );
                }
                else {
                    atomText.setTextPaint( Color.darkGray );
                    ionText.setTextPaint( getTextPaint( atom ) );
                }
                //Overwrite if there are no particles in the atom
                //So as to not identify a nonexistent atom as "atom"
                //TODO: instead of overwriting these values, the logic above should be changed
                if ( atom.getNumParticles() == 0 ) {
                    atomText.setTextPaint( Color.darkGray );
                    ionText.setTextPaint( Color.darkGray );
                }
            }
        };
        atom.addObserver( updateIconText );
        updateIconText.update();

        //Add the check mark for "atom"
        addChild( new CheckMark() {{
            final SimpleObserver updateCheckMarkVisible = new SimpleObserver() {
                public void update() {
                    setVisible( atom.getCharge() == 0 &&
                                atom.getNumParticles() > 0 );//Don't show the check mark if the atom has nothing in it.
                }
            };
            atom.addObserver( updateCheckMarkVisible );
            updateCheckMarkVisible.update();

            setOffset( atomText.getFullBounds().getX() - getFullBounds().getWidth(), atomText.getFullBounds().getCenterY() );
        }} );

        // Add the check mark for "ion"
        addChild( new CheckMark() {{
            final SimpleObserver updateCheckMarkVisible = new SimpleObserver() {
                public void update() {
                    setVisible( atom.getCharge() != 0 &&
                                atom.getNumParticles() > 0 );//Don't show the check mark if the atom has nothing in it.
                    setPaint( atom.getCharge() > 0 ? Color.red : Color.blue );
                }
            };
            atom.addObserver( updateCheckMarkVisible );
            updateCheckMarkVisible.update();

            setOffset( ionText.getFullBounds().getX() - getFullBounds().getWidth(), ionText.getFullBounds().getCenterY() );
        }} );
    }

    private static class CheckMark extends PNode {
        private final PhetPPath atomCheckMark;

        private CheckMark() {
            int width = 5;
            int tailLength = 20;
            int headLength = 10;
            DoubleGeneralPath path = new DoubleGeneralPath( 0, 0 );
            path.lineToRelative( headLength, headLength );
            path.lineToRelative( tailLength, -tailLength );
            path.lineToRelative( -width, -width );
            path.lineToRelative( -( tailLength - width ), tailLength - width );
            path.lineToRelative( -( headLength - width ), -( headLength - width ) );
            path.lineTo( 0, 0 );
            path.closePath();
            atomCheckMark = new PhetPPath( path.getGeneralPath(), purple, new BasicStroke( 2 ), Color.black );
            atomCheckMark.scale( 0.7 );

            addChild( atomCheckMark );
        }

        @Override
        public void setPaint( Paint newPaint ) {
            super.setPaint( newPaint );
            atomCheckMark.setPaint( newPaint );
        }
    }

    private Paint getTextPaint( Atom atom ) {
        if ( atom.getCharge() == 0 ) {
            return purple;
        }
        else if ( atom.getCharge() > 0 ) {
            return Color.red;
        }
        else {
            return Color.blue;
        }
    }

    public double getBoxWidth() {
        return BOX_DIMENSION;
    }
}
