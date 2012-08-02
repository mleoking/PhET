package edu.colorado.phet.functions.intro;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.functions.buildafunction2.AbstractFunctionsCanvas;
import edu.colorado.phet.functions.buildafunction2.NumberValueNode;
import edu.colorado.phet.functions.buildafunction2.UnaryNumberFunctionNode;
import edu.umd.cs.piccolo.PNode;

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
            addChild( new UnaryNumberFunctionNode( "*2", false ) {{
                setOffset( 390.72378138847836, 294.298375184638 );
            }} );
//            addChild( new UnaryNumberFunctionNode( "+1", false ) );
//            addChild( new UnaryNumberFunctionNode( "-1", false ) );
//            addChild( new UnaryNumberFunctionNode( "^2", false ) );

            addChild( new NumberValueNode( 3, new BasicStroke( 1 ), Color.white, Color.black, Color.black ) {{
                setOffset( 84.37223042836038, 315.3914327917278 );
            }} );

            addChild( new NumberValueNode( 6, new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 10, 10 }, 0 ), new Color( 0, 0, 0, 0 ), Color.gray, Color.gray ) {{
                setOffset( 903.9881831610056, 318.4047267355978 );
            }} );
        }
    }
}