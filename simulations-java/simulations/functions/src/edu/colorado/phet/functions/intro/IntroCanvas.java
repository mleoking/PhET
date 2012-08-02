package edu.colorado.phet.functions.intro;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.GrayFilter;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.functions.FunctionsResources;
import edu.colorado.phet.functions.FunctionsResources.Images;
import edu.colorado.phet.functions.buildafunction2.AbstractFunctionsCanvas;
import edu.colorado.phet.functions.buildafunction2.UnaryNumberFunctionNode;
import edu.colorado.phet.functions.buildafunction2.ValueNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class IntroCanvas extends AbstractFunctionsCanvas {

    public static final Color BACKGROUND_COLOR = new Color( 236, 251, 251 );

    public IntroCanvas() {

        //Set a really light blue because there is a lot of white everywhere
        setBackground( BACKGROUND_COLOR );

        addChild( new Scene1() );
    }

    private class Scene1 extends PNode {
        private Scene1() {
            addChild( new UnaryNumberFunctionNode( new PImage( BufferedImageUtils.multiScaleToWidth( FunctionsResources.Images.ROTATE_RIGHT, 50 ) ), false ) {{
                setOffset( 390.72378138847836, 294.298375184638 );
            }} );

            final BufferedImage key = BufferedImageUtils.multiScaleToWidth( Images.KEY, 45 );
            addChild( new ValueNode( new PImage( BufferedImageUtils.getRotatedImage( key, -Math.PI / 2 ) ), new BasicStroke( 1 ), Color.white, Color.black, Color.black ) {{
                setOffset( 84.37223042836038, 315.3914327917278 );
            }} );

            addChild( new ValueNode( new PImage( GrayFilter.createDisabledImage( GrayFilter.createDisabledImage( key ) ) ), new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 10, 10 }, 0 ), new Color( 0, 0, 0, 0 ), Color.gray, Color.gray ) {{
                setOffset( 903.9881831610056, 318.4047267355978 );
            }} );
        }
    }

    private class Scene2 extends PNode {
        private Scene2() {
            addChild( new UnaryNumberFunctionNode( "*2", false ) {{
                setOffset( 390.72378138847836, 294.298375184638 );
            }} );

            addChild( new ValueNode( 3, new BasicStroke( 1 ), Color.white, Color.black, Color.black ) {{
                setOffset( 84.37223042836038, 315.3914327917278 );
            }} );

            addChild( new ValueNode( 6, new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 10, 10 }, 0 ), new Color( 0, 0, 0, 0 ), Color.gray, Color.gray ) {{
                setOffset( 903.9881831610056, 318.4047267355978 );
            }} );
        }
    }
}