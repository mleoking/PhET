/**
 * Convenient storage for commonly-used constants.  A class which uses these
 * constants frequently should implement this interface to permit access via
 * unqualified names.
 */
interface PhysicsConstants {
    /**
     * Charge of an electron (absolute value)
     */
    public static final double e = 1.6e-19;

    /**
     * Boltzmann's constant
     */
    public static final double k = 1.38065e-23;

    /**
     * Charge of an electron divided by Boltzmann's constant
     * (occurs quite frequently when working in eV)
     */
    public static final double e_over_k = e / k;

    /**
     * Mass of an electron
     */
    public static final double m = 9.11e-31;

    /**
     * Planck's constant
     */
    public static final double h = 6.6e-34;

    /**
     * Planck's constant divided by 2*pi
     */
    public static final double h_bar = h / ( 2 * Math.PI );

    /**
     * Charge of an electron.  Synonym for 'e'.
     */
    public static final double q = e;

    /**
     * Permittivity of free space
     */
    public static final double epsilon0 = 8.8542e-12;

    /**
     * Relative permittivity of silicon
     */
    public static final double si_rel_perm = 11.7;

    /**    Effective permittivity (in silicon)	*/
    public static final double epsilon = epsilon0*si_rel_perm;
}