package edu.colorado.phet.nuclearphysics.common.model;



public class Neutron extends Nucleon {

    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    public Neutron (double xPos, double yPos, boolean tunnelingEnabled){
        super(NucleonType.PROTON, xPos, yPos, tunnelingEnabled);
    }
    
    public Neutron (double xPos, double yPos, double xVel, double yVel, boolean tunnelingEnabled){
        super(NucleonType.PROTON, xPos, yPos, xVel, yVel, tunnelingEnabled);
    }
}
