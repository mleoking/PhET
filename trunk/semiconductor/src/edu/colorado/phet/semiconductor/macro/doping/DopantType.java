package edu.colorado.phet.semiconductor.macro.doping;

/**
 * User: Sam Reid
 * Date: Feb 8, 2004
 * Time: 4:14:49 PM
 * Copyright (c) Feb 8, 2004 by Sam Reid
 */
public class DopantType {
    public static final DopantType P = new DopantType( "P" );
    public static final DopantType N = new DopantType( "N" );
    String name;

    public DopantType( String name ) {
        this.name = name;
    }

    public boolean equals( Object obj ) {
        return ( ( obj instanceof DopantType ) && ( (DopantType)obj ).name.equals( name ) );
    }

    public int hashCode() {
        return name.hashCode();
    }
}
