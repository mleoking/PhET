package edu.colorado.phet.functions.intro;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.functions.FunctionsResources.Images;
import edu.colorado.phet.functions.buildafunction2.AbstractFunctionsCanvas;
import edu.colorado.phet.functions.buildafunction2.BuildAFunctionPrototype2Canvas;
import edu.colorado.phet.functions.buildafunction2.UnaryNumberFunctionNode;
import edu.colorado.phet.functions.buildafunction2.ValueContext;
import edu.colorado.phet.functions.buildafunction2.ValueNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.functions.FunctionModule.radioButton;
import static javax.swing.GrayFilter.createDisabledImage;

/**
 * @author Sam Reid
 */
public class IntroCanvas extends AbstractFunctionsCanvas implements ValueContext {

    private Scene scene;
    private final ResetAllButtonNode resetAllButtonNode;

    public IntroCanvas() {

        setBackground( BuildAFunctionPrototype2Canvas.BACKGROUND_COLOR );
        addScreenChild( new HBox( radioButton( "Shapes", false ), radioButton( "Text", true ), radioButton( "Numbers", false ) ) {{
            setOffset( INSET, INSET );
        }} );
        resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
            }
        }, this, new PhetFont( 18, true ), Color.black, Color.orange ) {{
            setOffset( 1024 - this.getFullWidth() - INSET - INSET - INSET, 768 - this.getFullHeight() - INSET - 40 - 40 - 10 - INSET );
        }};
        addScreenChild( resetAllButtonNode );

        scene = new Scene1();
        addChild( scene );
    }

    @Override public void mouseDragged( final ValueNode valueNode, final PDimension delta ) {
        scene.mouseDragged( valueNode, delta );
    }

    @Override public void mouseReleased( final ValueNode valueNode ) {
        scene.mouseReleased( valueNode );
    }

    private class Scene1 extends Scene {

        private final UnaryNumberFunctionNode functionNode;
        private final ValueNode valueNode;
        private final PImage valueNodeImage;
        private final ValueNode targetNode;

        private Scene1() {
            final BufferedImage key = BufferedImageUtils.multiScaleToWidth( Images.KEY, 45 );
            targetNode = new ValueNode( IntroCanvas.this, new PImage( createDisabledImage( createDisabledImage( key ) ) ), new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 10, 10 }, 0 ), new Color( 0, 0, 0, 0 ), Color.gray ) {{
                setOffset( 903.9881831610056, 318.4047267355978 );
            }};
            addChild( targetNode );

            valueNodeImage = new PImage( BufferedImageUtils.getRotatedImage( key, -Math.PI / 2 ) );
            valueNode = new ValueNode( IntroCanvas.this, valueNodeImage, new BasicStroke( 1 ), Color.white, Color.black ) {{
                setOffset( 84.37223042836038, 315.3914327917278 );
            }};
            addChild( valueNode );

            functionNode = new UnaryNumberFunctionNode( new PImage( BufferedImageUtils.multiScaleToWidth( Images.ROTATE_RIGHT, 50 ) ), false ) {{
                setOffset( 390.72378138847836, 294.298375184638 );
            }};
            addChild( functionNode );
        }

        public void mouseDragged( final ValueNode valueNode, final PDimension delta ) {
            PBounds valueBounds = valueNode.getGlobalFullBounds();
            PBounds functionBounds = functionNode.getGlobalFullBounds();
            boolean leftBefore = valueNode.getGlobalFullBounds().getCenterX() < functionNode.getGlobalFullBounds().getCenterX();
            double distanceFromOpening = functionBounds.getMinX() - valueBounds.getMaxX();
            PBounds functionBox = new PBounds( functionBounds.getMinX() - 50, functionBounds.getY(), functionBounds.getWidth() + 50, functionBounds.getHeight() );
            if ( delta.width >= 0 && functionBox.intersects( valueBounds ) ) {
                if ( distanceFromOpening > 0 && distanceFromOpening < 50 ) {
                    double valueCenterY = valueBounds.getCenterY();
                    double functionCenterY = functionBounds.getCenterY();
                    double deltaY = functionCenterY - valueCenterY;
                    LinearFunction f = new LinearFunction( 0, 50, 1, 0 );
                    valueNode.translate( delta.width, f.evaluate( distanceFromOpening ) * deltaY );
                }
                else if ( valueBounds.getMinX() < functionBounds.getMaxX() && valueBounds.getMaxX() > functionBounds.getMinX() ) {
                    double valueCenterY = valueBounds.getCenterY();
                    double functionCenterY = functionBounds.getCenterY();
                    double deltaY = functionCenterY - valueCenterY;
                    valueNode.translate( delta.width, deltaY );
                }
                else {
                    valueNode.translate( delta.width, delta.height );
                }
                boolean leftAfter = valueNode.getGlobalFullBounds().getCenterX() < functionNode.getGlobalFullBounds().getCenterX();
                if ( leftBefore == true && leftAfter == false ) {
                    valueNodeImage.setImage( BufferedImageUtils.getRotatedImage( BufferedImageUtils.toBufferedImage( valueNodeImage.getImage() ), Math.PI / 2 ) );
                    valueNode.centerContent();
                    valueNode.rotateRight();
                }
            }

            else {
                valueNode.translate( delta.width, delta.height );
            }

//            System.out.println( "LB = " + leftBefore + ", leftAfter = " + leftAfter );
        }

        public void mouseReleased( final ValueNode valueNode ) {
            if ( valueNode.getGlobalFullBounds().intersects( targetNode.getGlobalFullBounds() ) && valueNode.getNumberRotations() % 4 == 1 ) {
                valueNode.setStrokePaint( Color.red );
                valueNode.centerFullBoundsOnPoint( targetNode.getFullBounds().getCenterX(), targetNode.getFullBounds().getCenterY() );
                IntroCanvas.this.showNextButton();
            }
        }
    }

    private void showNextButton() {
        final HTMLImageButtonNode nextButton = new HTMLImageButtonNode( "Next" ) {{
            setFont( resetAllButtonNode.getFont() );
            setBackground( Color.green );
            setOffset( resetAllButtonNode.getFullBounds().getX(), resetAllButtonNode.getFullBounds().getMinY() - getFullBounds().getHeight() - 10 );
        }};
        nextButton.addActionListener( new ActionListener() {
            @Override public void actionPerformed( final ActionEvent e ) {
                IntroCanvas.this.removeChild( scene );
                scene = new Scene2();
                IntroCanvas.this.addChild( scene );
                IntroCanvas.this.removeChild( nextButton );
            }
        } );
        addChild( nextButton );
    }

    private class Scene2 extends Scene {
        private Scene2() {
            addChild( new UnaryNumberFunctionNode( "*2", false ) {{
                setOffset( 390.72378138847836, 294.298375184638 );
            }} );

            addChild( new UnaryNumberFunctionNode( "+1", false ) {{
                setOffset( 390.72378138847836, 444.298375184638 );
            }} );

            addChild( new ValueNode( IntroCanvas.this, 3, new BasicStroke( 1 ), Color.white, Color.black, Color.black ) {{
                setOffset( 84.37223042836038, 315.3914327917278 );
            }} );

            addChild( new ValueNode( IntroCanvas.this, 6, new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 10, 10 }, 0 ), new Color( 0, 0, 0, 0 ), Color.gray, Color.gray ) {{
                setOffset( 903.9881831610056, 318.4047267355978 );
            }} );
        }

        @Override public void mouseDragged( final ValueNode valueNode, final PDimension delta ) {
        }

        @Override public void mouseReleased( final ValueNode valueNode ) {
        }
    }
}