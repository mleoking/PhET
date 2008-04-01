package edu.colorado.phet.nuclearphysics2.model;


public class Neutron extends Nucleon {

    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    Neutron (double xPos, double yPos, boolean tunnelingEnabled){
        super(xPos, yPos, tunnelingEnabled);
    }
    
    Neutron (double xPos, double yPos, double xVel, double yVel, boolean tunnelingEnabled){
        super(xPos, yPos, xVel, yVel, tunnelingEnabled);
    }
}
