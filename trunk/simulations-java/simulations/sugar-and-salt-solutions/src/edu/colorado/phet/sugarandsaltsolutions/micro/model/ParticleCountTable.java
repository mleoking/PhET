package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * To get the same ratio (100 g/1 L) of salt and sugar in the micro tab, use the number of particles in the ParticleCountTable
 *
 * @author Sam Reid
 */
public class ParticleCountTable {
    //Amounts are given for 1E-23L of water, then multiplied by 2 since the max fluid volume is 2E-23L
    public static final int MAX_SODIUM_CHLORIDE = (int) ( 10.30 * 2 );

    //Increase the requested amount of CaCl2 because otherwise it is too difficult to get it to crystallize before the water is almost completely evaporated
    public static final int MAX_CALCIUM_CHLORIDE = (int) ( 5.43 * 2 ) * 3;
    public static final int MAX_SODIUM_NITRATE = (int) ( 7.00 * 2 );
    public static final int MAX_SUCROSE = (int) ( 1.76 * 2 );
    public static final int MAX_ETHANOL = (int) ( 13.07 * 2 );
}