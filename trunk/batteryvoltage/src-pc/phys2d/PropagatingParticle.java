package phys2d;

public class PropagatingParticle extends Particle {
    Propagator p;

    public PropagatingParticle( Propagator p ) {
        this.p = p;
    }

    public void setPropagator( Propagator p ) {
        this.p = p;
    }

    public Propagator getPropagator() {
        return p;
    }
}
