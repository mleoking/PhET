package edu.colorado.phet.statesofmatter.model.container;

public abstract class AbstractParticleContainer implements ParticleContainer {
    private double rotation;

    public final double getRotation() {
        return rotation;
    }

    public final void setRotation(double rotation) {
        this.rotation = rotation;

        rotateImpl(rotation);
    }

    protected abstract void rotateImpl(double rotation);
}
