package edu.colorado.phet.densityjava.model;

//environment for computing applied forces and collisions
public interface BlockEnvironment {
    double getFloorY(Block block);

    double getAppliedForce(Block block);
}
