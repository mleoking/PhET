// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Air;
import edu.umd.cs.piccolo.PNode;

/**
 * @author John Blanco
 */
public class AirNode extends PNode {

    private static final boolean SHOW_BOUNDS = true;

    public AirNode( Air air, ModelViewTransform mvt ) {
        if ( SHOW_BOUNDS ) {
            addChild( new PhetPPath( mvt.modelToView( air.getThermalContactArea().getBounds() ), new BasicStroke( 1 ), Color.RED ) );
        }
    }
}
