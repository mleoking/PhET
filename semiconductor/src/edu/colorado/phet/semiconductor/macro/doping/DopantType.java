package edu.colorado.phet.semiconductor.macro.doping;

import edu.colorado.phet.semiconductor.macro.energy.bands.SemiconductorBandSet;
import edu.colorado.phet.semiconductor.macro.energy.bands.Band;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;

/**
 * User: Sam Reid
 * Date: Feb 8, 2004
 * Time: 4:14:49 PM
 * Copyright (c) Feb 8, 2004 by Sam Reid
 */
public class DopantType {

//    public static final int NUM_DOPING_LEVELS = 6;
    public static final DopantType P = new DopantType( "P" ,4);
    public static final DopantType N = new DopantType( "N" ,6);
    String name;
    private int height;

    private DopantType( String name ,int height) {
        this.name = name;
        this.height = height;
    }

    public boolean equals( Object obj ) {
        return ( ( obj instanceof DopantType ) && ( (DopantType)obj ).name.equals( name ) );
    }

    public int hashCode() {
        return name.hashCode();
    }

    public int getNumFilledLevels() {
        return height;
    }

}
