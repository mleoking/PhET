package edu.colorado.phet.coreadditions.bernoulli.graphics.transform;

/**
 * Listens for changes in the model or view viewport.
 */
public interface TransformListener {
    public void transformChanged(ModelViewTransform2d mvt);
}
