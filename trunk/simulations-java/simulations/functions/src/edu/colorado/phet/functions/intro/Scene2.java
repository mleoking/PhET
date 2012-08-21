// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import fj.F;
import fj.Unit;
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

    private static List<TargetNode> targetNodeList( final IntroCanvas valueContext ) {
        return Scene.createTargetNodeList( valueContext, List.list( 4, 6, 8 ) );
    }

    private static List<UnaryFunctionNode> unaryFunctionNodeList() {
        return List.list( new UnaryFunctionNode( "\u27152", false, Functions.INTEGER_TIMES_2 ) {{ setOffset( 390.72378138847836, 294.298375184638 - 80 ); }},
                          new UnaryFunctionNode( "+1", false, Functions.INTEGER_PLUS_1 ) {{ setOffset( 390.72378138847836, 444.298375184638 - 80 ); }}
        );
    }

    private static List<ValueNode> valueNodeList( final ValueContext valueContext ) {
        return Scene.toStack( 3, new F<Unit, ValueNode>() {
            @Override public ValueNode f( final Unit unit ) {
                return new ValueNode( valueContext, 3, new BasicStroke( 1 ), Color.white, Color.black, Color.black );
            }
        } );
    }
}