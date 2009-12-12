/* Copyright 2004-2010, University of Colorado */
package edu.colorado.phet.motionseries.javastage.stage;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;
import edu.umd.cs.piccolo.PNode;

/**
 * Represents a node in model coordinates.  Ordinarily this class will be for internal use by StageCanvas only,
 * and not used by client code; however, this class is left as public in case custom behavior is needed.
 *
 * @see StageCanvas
 * @see StageCanvas#addModelNode(edu.umd.cs.piccolo.PNode)
 * @author Sam Reid
 */
public class ModelNode extends PNode {
    /**
     * The transform that projects from model coordinates to stage coordinates, same as the one used in the StageCanvas.
     *
     * @see StageCanvas#transform
     */
    private final ModelViewTransform2D transform;

    /**
     * The node depicted by this ModelNode.
     */
    private final PNode node;

    /**
     * Represents a PNode in the model coordinate frame.
     *
     * @param transform the transform that projects from model coordinates to stage coordinates.
     * @param node      the node depicted by this ModelNode.
     */
    public ModelNode(ModelViewTransform2D transform, PNode node) {
        if (transform == null) throw new IllegalArgumentException("Transform was null");
        if (node == null) throw new IllegalArgumentException("Node was null");
        this.transform = transform;
        this.node = node;
        addChild(node);
        transform.addTransformListener(new TransformListener() {
            public void transformChanged(ModelViewTransform2D mvt) {
                updateTransform();
            }
        });
    }

    /**
     * Called when the underlying model -> stage transform changes.
     */
    public void updateTransform() {
        setTransform(transform.getAffineTransform());
    }
}
