// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class MediumNode extends PNode {
    public MediumNode( final ModelViewTransform transform, final Medium medium ) {
        addChild( new PhetPPath( Color.green ) {{
            setPathTo( transform.modelToView( medium.getShape() ) );
        }} );
    }
}
