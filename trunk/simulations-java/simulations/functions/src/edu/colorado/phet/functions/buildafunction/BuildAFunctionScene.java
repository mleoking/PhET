// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.buildafunction;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.functions.common.view.TableNode;
import edu.colorado.phet.functions.model.Functions;
import edu.colorado.phet.functions.model.Type;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.functions.buildafunction.AbstractFunctionsCanvas.INSET;
import static edu.colorado.phet.functions.buildafunction.AbstractFunctionsCanvas.STAGE_SIZE;

/**
 * @author Sam Reid
 */
public class BuildAFunctionScene extends PNode {
    public BuildAFunctionScene() {
        addChild( new UnaryFunctionNode( "+1", true, Functions.INTEGER_PLUS_1, Type.NUMBER, Type.NUMBER, 20.088626292466763, 596.632200886265 ) {{setScale( 0.5 );}} );
        addChild( new UnaryFunctionNode( "-1", true, Functions.INTEGER_MINUS_1, Type.NUMBER, Type.NUMBER, 138.61152141802097, 595.6277695716408 ) {{setScale( 0.5 );}} );
        addChild( new UnaryFunctionNode( "\u27152", true, Functions.INTEGER_TIMES_2, Type.NUMBER, Type.NUMBER, 263.1610044313151, 596.6322008862643 ) {{setScale( 0.5 );}} );
        addChild( new UnaryFunctionNode( "^2", true, Functions.INTEGER_POWER_2, Type.NUMBER, Type.NUMBER, 393.7178729689809, 598.6410635155097 ) {{setScale( 0.5 );}} );

        addChild( new TwoInputFunctionNode( "+", true, Type.NUMBER, 521.2998522895131, 540.8862629246682 ) {{setScale( 0.5 );}} );
        addChild( new TwoInputFunctionNode( "-", true, Type.NUMBER, 662.9054652880341, 538.8774002954215 ) {{setScale( 0.5 );}} );
        addChild( new CopyFunctionNode( "copy", Type.NUMBER, 787.4549483013308, 542.895125553915 ) {{setScale( 0.5 );}} );

        addChild( new ValueNode( new ValueContext() {
            public void mouseDragged( final ValueNode valueNode, final PDimension delta ) {
                valueNode.translate( delta.width, delta.height );
            }

            public void mouseReleased( final ValueNode valueNode ) {
            }
        }, 3, new BasicStroke(), Color.white, Color.black, Color.black ) {{
            setOffset( 55.24372230428363, 280.23633677991086 );
        }} );

        addChild( new TableNode() {{setOffset( STAGE_SIZE.width - INSET - getFullBounds().getWidth(), INSET );}} );
    }
}