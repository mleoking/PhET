/* Copyright 2004-2010, University of Colorado */
package edu.colorado.phet.motionseries.javastage.stage;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;
import edu.umd.cs.piccolo.PNode;

/**
 * Represents a node in model coordinates.  Ordinarily this class will be for internal use by PlayArea only,
 * and not used by client code; however, this class is left as public in case custom behavior is needed.
 *
 * @author Sam Reid
 * @see PlayArea
 * @see PlayArea#addModelNode(edu.umd.cs.piccolo.PNode)
 */
public class ModelNode extends PNode {
    /**
     * The transform that projects from model coordinates to stage coordinates, same as the one used in the PlayArea.
     *
     * @see PlayArea#transform
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
        updateTransform();
    }

    /*
     * Called when the underlying model -> stage transform changes.
     */
    protected void updateTransform() {
        setTransform(transform.getAffineTransform());
    }

    /**
     * Checks for equality based on the reference equality of the constituent node and transform object.  This is used in PlayArea's containment checking methods.
     *
     * @param o the object to check for equality
     * @return true if the this node and the specified object are considered equal
     * @see PlayArea#containsModelNode(edu.umd.cs.piccolo.PNode)
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelNode modelNode = (ModelNode) o;

        if (transform != modelNode.transform) return false;
        if (node != modelNode.node) return false;

        return true;
    }
}
