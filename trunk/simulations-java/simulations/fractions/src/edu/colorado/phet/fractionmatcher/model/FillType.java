package edu.colorado.phet.fractionmatcher.model;

/**
 * How a set of patterns should be filled in.
 *
 * @author Sam Reid
 */
public enum FillType {

    //REVIEW Instances of an enum are constants and should follow the Java conventions for constants, uppercase. Also inconsistent with what you've done elsewhere, eg Mode enum.

    //Just fills in order (left to right, etc)
    Sequential,

    //When >1, first shape will be completely filled and the 2nd shape will be random
    Mixed,

    //When >1 all shapes will be randomized
    Random
}