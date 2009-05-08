package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.umd.cs.piccolo.PNode;

public class RadioactiveDatingGameLayerNode extends PNode {
    private Layer layer;
    private ModelViewTransform2D mvt;

    public RadioactiveDatingGameLayerNode( Layer layer, ModelViewTransform2D mvt,Color color ) {
        this.layer = layer;
        this.mvt = mvt;
        Shape transformedShape = mvt.createTransformedShape( layer.getTopLine() );
        LayerNode layerNode = new LayerNode( transformedShape,
                                             mvt.createTransformedShape( layer.getBottomLine() ), color );
        addChild( layerNode );
    }
}
