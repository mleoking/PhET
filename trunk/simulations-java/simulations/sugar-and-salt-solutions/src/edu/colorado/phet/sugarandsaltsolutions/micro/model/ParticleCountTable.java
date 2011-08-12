package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * To get the same ratio (100 g/1 L) of salt and sugar in the micro tab, use the number of particles in the ParticleCountTable
 *
 * @author Sam Reid
 */
public class ParticleCountTable {

    //Increase the requested amount because otherwise it is too difficult to get it to crystallize before the water is almost completely evaporated
    public static final int scaleFactor = 2;

    //Amounts are given for 1E-23L of water, then multiplied by 2 since the max fluid volume is 2E-23L
    public static final int MAX_SODIUM_CHLORIDE = (int) ( 10.30 * 2 ) * scaleFactor;
    public static final int MAX_CALCIUM_CHLORIDE = (int) ( 5.43 * 2 ) * scaleFactor;
    public static final int MAX_SODIUM_NITRATE = (int) ( 7.00 * 2 ) * scaleFactor;
    public static final int MAX_SUCROSE = (int) ( 1.76 * 2 ) * scaleFactor;

    //Since glucose is half as big as sucrose, you can have twice as many
    public static final int MAX_GLUCOSE = MAX_SUCROSE * 2;
}