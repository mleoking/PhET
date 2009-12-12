package edu.colorado.phet.motionseries.javastage.stage;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;
import edu.umd.cs.piccolo.PNode;

class ModelNode extends PNode {
    private ModelViewTransform2D transform;
    private PNode node;

    public ModelNode(ModelViewTransform2D transform, PNode node) {
        this.transform = transform;
        this.node = node;
        addChild(node);
        transform.addTransformListener(new TransformListener() {
            public void transformChanged(ModelViewTransform2D mvt) {
                updateTransform();
            }
        });
    }

    public void updateTransform() {
        setTransform(transform.getAffineTransform());
    }
}
