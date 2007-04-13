package edu.colorado.phet.common.bernoulli.bernoulli.graphics.transform;

/**
 * Listens for changes in the model or view viewport.
 */
public interface TransformListener {
    public void transformChanged(ModelViewTransform2d mvt);
}
