package edu.colorado.phet.semiconductor.macro.doping;


/**
 * User: Sam Reid
 * Date: Feb 8, 2004
 * Time: 4:14:49 PM
 */
public class DopantType {

    public static final DopantType P = new DopantType( "P", 4, 1 );
    public static final DopantType N = new DopantType( "N", 6, 2 );
    String name;
    private int height;
    private int dopingBand;

    private DopantType( String name, int height, int dopingBand ) {
        this.name = name;
        this.height = height;
        this.dopingBand = dopingBand;
    }

    public boolean equals( Object obj ) {
        return ( ( obj instanceof DopantType ) && ( (DopantType) obj ).name.equals( name ) );
    }

    public int hashCode() {
        return name.hashCode();
    }

    public int getNumFilledLevels() {
        return height;
    }

    public int getDopingBand() {
        return dopingBand;
    }

    public String toString() {
        return name;
    }
}
