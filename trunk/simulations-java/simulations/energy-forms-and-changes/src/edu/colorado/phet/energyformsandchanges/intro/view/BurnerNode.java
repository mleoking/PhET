// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Burner;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents the burner in the view.
 *
 * @author John Blanco
 */
public class BurnerNode extends PNode {

    public BurnerNode( Burner burner, ModelViewTransform mvt ) {
        addChild( new PhetPPath( mvt.modelToViewRectangle( burner.getOutlineRect() ) ) );
    }
}
