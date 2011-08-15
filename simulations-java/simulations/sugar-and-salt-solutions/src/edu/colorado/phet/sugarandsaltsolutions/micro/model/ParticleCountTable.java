package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * To get the same ratio (100 g/1 L) of salt and sugar in the micro tab, use the number of particles in the ParticleCountTable
 *
 * @author Sam Reid
 */
public class ParticleCountTable {

    //The number of formula (such as NaCl or CaCl3) that the user can add to the solution, chosen to be this value so that the user can only add 1.0 mol / L by default
    private static final int NUMBER_SOLUTE_FORMULAE = 6;

    public static final int MAX_SODIUM_CHLORIDE = NUMBER_SOLUTE_FORMULAE;
    public static final int MAX_SODIUM_NITRATE = NUMBER_SOLUTE_FORMULAE;
    public static final int MAX_SUCROSE = NUMBER_SOLUTE_FORMULAE;
    public static final int MAX_GLUCOSE = NUMBER_SOLUTE_FORMULAE;

    //Half as much since different formula ratio, this number limits the concentration to 1.00mol/L on startup
    public static final int MAX_CALCIUM_CHLORIDE = NUMBER_SOLUTE_FORMULAE / 2;
}