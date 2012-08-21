// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import fj.F;
import fj.Unit;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.functions.FunctionsResources.Images;
import edu.colorado.phet.functions.buildafunction.UnaryFunctionNode;
import edu.colorado.phet.functions.buildafunction.ValueNode;
import edu.colorado.phet.functions.model.Functions;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
class Scene1 extends Scene {

    Scene1( final IntroCanvas introCanvas ) {
        super( valueNodes( introCanvas ),
               List.list( functionNode() ),
               List.list( targetNode( introCanvas ) ), introCanvas );
    }

    private static List<ValueNode> valueNodes( final IntroCanvas introCanvas ) {
        return Scene.toStack( 3, new F<Unit, ValueNode>() {
            @Override public ValueNode f( final Unit unit ) {
                return new ValueNode( introCanvas, new Graphic( 0 ), new BasicStroke( 1 ), Color.white, Color.black, Color.black );
            }
        } );
    }

    private static TargetNode targetNode( final IntroCanvas introCanvas ) {
        return new TargetNode( introCanvas, new Graphic( 1 ),
                               new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 10, 10 }, 0 ), new Color( 0, 0, 0, 0 ), Color.gray, Color.black ) {{
            setOffset( 903.9881831610056, 318.4047267355978 );
        }};
    }

    private static UnaryFunctionNode functionNode() {
        return new UnaryFunctionNode( new PImage( BufferedImageUtils.multiScaleToWidth( Images.ROTATE_RIGHT, 60 ) ), false, Functions.ROTATE_GRAPHIC_RIGHT ) {{
            setOffset( 390.72378138847836, 294.298375184638 );
        }};
    }
}