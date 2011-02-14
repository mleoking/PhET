// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PrismNode extends PNode {
    public PrismNode( final ModelViewTransform transform, final Prism prism ) {
        addChild( new PhetPPath( new Color( 60, 214, 214 ), new BasicStroke(), Color.darkGray ) {{
            setPathTo( transform.modelToView( prism.getShape() ) );
        }} );
    }
}
