package edu.colorado.phet.functions.buildafunction2;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class BuildAFunctionPrototype2Canvas extends AbstractFunctionsCanvas {

    public static final Color BACKGROUND_COLOR = new Color( 236, 251, 251 );

    public BuildAFunctionPrototype2Canvas() {

        //Set a really light blue because there is a lot of white everywhere
        setBackground( BACKGROUND_COLOR );

        addChild( new BinaryNumberFunctionNode( "+" ) );
        addChild( new BinaryNumberFunctionNode( "-" ) );
        addChild( new CopyNumberFunctionNode( "copy" ) );

        addChild( new UnaryNumberFunctionNode( "*2", true ) );
        addChild( new UnaryNumberFunctionNode( "+1", true ) );
        addChild( new UnaryNumberFunctionNode( "-1", true ) );
        addChild( new UnaryNumberFunctionNode( "^2", true ) );

        addChild( new ValueNode( new ValueContext() {
            @Override public void mouseDragged( final ValueNode valueNode, final PDimension delta ) {
                valueNode.translate( delta.width, delta.height );
            }
        }, 3, new BasicStroke(), Color.white, Color.black, Color.black ) );
    }
}