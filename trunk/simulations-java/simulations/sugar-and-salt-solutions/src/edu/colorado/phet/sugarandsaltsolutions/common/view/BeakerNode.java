// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.umd.cs.piccolo.PNode;

/**
 * Graphical display of the beaker
 *
 * @author Sam Reid
 */
public class BeakerNode extends PNode {
    public BeakerNode( ModelViewTransform transform, Beaker beaker ) {
        addChild( new PhetPPath( transform.modelToView( beaker.getWallShape() ), Color.gray ) );
    }
}
