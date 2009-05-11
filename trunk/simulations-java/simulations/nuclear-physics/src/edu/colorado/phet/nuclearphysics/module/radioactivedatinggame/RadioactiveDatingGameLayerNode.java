/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.umd.cs.piccolo.PNode;

public class RadioactiveDatingGameLayerNode extends PNode {
    private Stratum layer;
    private ModelViewTransform2D mvt;

    public RadioactiveDatingGameLayerNode( Stratum stratum, ModelViewTransform2D mvt, Color color ) {
        this.layer = stratum;
        this.mvt = mvt;
        StratumNode layerNode = new StratumNode( mvt.createTransformedShape( stratum.getTopLine() ),
                                                 mvt.createTransformedShape( stratum.getBottomLine() ),
                                                 color );
        addChild( layerNode );
    }
}
