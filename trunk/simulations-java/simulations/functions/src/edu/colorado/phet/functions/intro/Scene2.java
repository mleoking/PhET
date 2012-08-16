// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.functions.buildafunction.UnaryFunctionNode;
import edu.colorado.phet.functions.buildafunction.ValueContext;
import edu.colorado.phet.functions.buildafunction.ValueNode;
import edu.colorado.phet.functions.model.Functions;

/**
 * @author Sam Reid
 */
public class Scene2 extends Scene {

    Scene2( IntroCanvas canvas ) {
        super( valueNodeList( canvas ), unaryFunctionNodeList(), targetNodeList( canvas ), canvas );
    }

    private static List<ValueNode> targetNodeList( final IntroCanvas valueContext ) {
        final ValueNode valueNode = new ValueNode( valueContext, 6, new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 10, 10 }, 0 ), new Color( 0, 0, 0, 0 ), Color.gray, Color.gray ) {{
            setOffset( 903.9881831610056, 318.4047267355978 );
        }};
        return List.list( valueNode );
    }

    private static List<UnaryFunctionNode> unaryFunctionNodeList() {
        return List.list( new UnaryFunctionNode( "\u27152", false, Functions.INTEGER_TIMES_2 ) {{ setOffset( 390.72378138847836, 294.298375184638 ); }},
                          new UnaryFunctionNode( "+1", false, Functions.INTEGER_PLUS_1 ) {{ setOffset( 390.72378138847836, 444.298375184638 ); }}
        );
    }

    private static List<ValueNode> valueNodeList( final ValueContext valueContext ) {
        final ValueNode value = new ValueNode( valueContext, 3, new BasicStroke( 1 ), Color.white, Color.black, Color.black ) {{ setOffset( 84.37223042836038, 315.3914327917278 ); }};
        return List.list( value );
    }
}