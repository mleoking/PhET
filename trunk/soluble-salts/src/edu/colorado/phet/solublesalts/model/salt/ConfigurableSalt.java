/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.salt;

import edu.colorado.phet.solublesalts.model.crystal.*;
import edu.colorado.phet.solublesalts.model.ion.Chlorine;
import edu.colorado.phet.solublesalts.model.ion.Sodium;
import edu.colorado.phet.solublesalts.model.ion.ConfigurableAnion;
import edu.colorado.phet.solublesalts.model.ion.ConfigurableCation;

import java.util.ArrayList;

/**
 * SodiumChloride
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ConfigurableSalt extends Salt {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    static private Lattice lattice = new OneToOneLattice( ConfigurableAnion.RADIUS + ConfigurableCation.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        components.add( new Component( ConfigurableAnion.class, new Integer( 1 ) ) );
        components.add( new Component( ConfigurableCation.class, new Integer( 1 ) ) );
    }

    public static void configure() {
        components.clear();
        components.add( new Component( ConfigurableAnion.class, new Integer( -ConfigurableCation.getClassCharge() ) ) );
        components.add( new Component( ConfigurableCation.class, new Integer( ConfigurableAnion.getClassCharge() ) ) );

        double spacing = ConfigurableAnion.RADIUS + ConfigurableCation.RADIUS;
        switch( ConfigurableCation.getClassCharge() ) {
            case -1:
                switch( ConfigurableAnion.getClassCharge() ) {
                    case 1:
                        lattice = new OneToOneLattice( spacing );
                        break;
                    case 2:
                        lattice = new TwoToOneLattice( ConfigurableAnion.class, ConfigurableCation.class, spacing );
                        break;
                    case 3:
                        lattice = new ThreeToOneLattice( ConfigurableAnion.class, ConfigurableCation.class, spacing );
                        break;
                    default:
                        throw new RuntimeException( "No lattice defined for ratio of ions in salt" );
                }
                break;
            case -2:
                switch( ConfigurableAnion.getClassCharge() ) {
                    case 1:
                        lattice = new TwoToOneLattice( ConfigurableCation.class, ConfigurableAnion.class, spacing );
                        break;
                    case 2:
                        lattice = new OneToOneLattice( spacing );
                        break;
                    case 3:
                        lattice = new ThreeToTwoLattice( ConfigurableAnion.class, ConfigurableCation.class, spacing );
                        break;
                    default:
                        throw new RuntimeException( "No lattice defined for ratio of ions in salt" );
                }
                break;
            case -3:
                switch( ConfigurableAnion.getClassCharge() ) {
                    case 1:
                        lattice = new ThreeToOneLattice( ConfigurableCation.class, ConfigurableAnion.class, spacing );
                        break;
                    case 2:
                        lattice = new ThreeToTwoLattice( ConfigurableCation.class, ConfigurableAnion.class, spacing );
                        break;
                    case 3:
                        lattice = new OneToOneLattice( spacing );
                        break;
                    default:
                        throw new RuntimeException( "No lattice defined for ratio of ions in salt" );
                }
                break;
            default:
                throw new RuntimeException( "No lattice defined for ratio of ions in salt" );
        }
    }


    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    public ConfigurableSalt() {
        super( components, lattice, ConfigurableAnion.class, ConfigurableCation.class, 36 );
    }

}
