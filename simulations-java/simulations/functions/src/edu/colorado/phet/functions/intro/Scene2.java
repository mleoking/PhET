// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import fj.F;
import fj.Unit;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.functions.buildafunction.UnaryFunctionNode;
import edu.colorado.phet.functions.buildafunction.ValueNode;

import static edu.colorado.phet.functions.model.Functions.INTEGER_PLUS_1;
import static edu.colorado.phet.functions.model.Functions.INTEGER_TIMES_2;
import static fj.data.List.list;

/**
 * @author Sam Reid
 */
public class Scene2 extends Scene {
    public Scene2( final IntroCanvas canvas ) {
        super( toStack( 3, new F<Unit, ValueNode>() {
            @Override public ValueNode f( final Unit unit ) {
                return new ValueNode( canvas, 3, new BasicStroke( 1 ), Color.white, Color.black, Color.black );
            }
        } ),
               list( new UnaryFunctionNode( "\u27152", false, INTEGER_TIMES_2, 390.72378138847836, 294.298375184638 - 80 ),
                     new UnaryFunctionNode( "+1", false, INTEGER_PLUS_1, 390.72378138847836, 444.298375184638 - 80 )
               ),
               createTargetNodeList( canvas, list( 4, 6, 8 ) ),
               canvas );
    }
}