/**
 * Class: RemoveMoleculeCmd
 * Class: edu.colorado.phet.idealgas.controller.command
 * User: Ron LeMaster
 * Date: Sep 16, 2004
 * Time: 7:00:27 PM
 */
package edu.colorado.phet.idealgas.controller.command;

import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.IdealGasModel;

import java.util.List;
import java.util.Random;

public class RemoveMoleculeCmd implements Command {
    private static Random random = new Random();
    private IdealGasModel idealGasModel;
    private Class currentGasSpecies;

    public RemoveMoleculeCmd( IdealGasModel idealGasModel, Class currentGasSpecies ) {
        this.idealGasModel = idealGasModel;
        this.currentGasSpecies = currentGasSpecies;
    }

    public void doIt() {
        List bodies = idealGasModel.getBodies();

        // Randomize which end of the list of bodies we start searching from,
        // just to make sure there is no non-random effect on the temperature
        // of the system
        Object obj = null;
        while( obj == null ) {
            boolean randomB = random.nextBoolean();
            if( randomB ) {
                for( int i = 0; i < bodies.size(); i++ ) {
                    obj = bodies.get( i );
                    if( currentGasSpecies.isInstance( obj ) ) {
                        break;
                    }
                }
            }
            else {
                for( int i = bodies.size() - 1; i >= 0; i-- ) {
                    obj = bodies.get( i );
                    if( currentGasSpecies.isInstance( obj ) ) {
                        break;
                    }
                }
            }
        }
        if( obj instanceof GasMolecule ) {
            GasMolecule molecule = (GasMolecule)obj;
            idealGasModel.removeModelElement( molecule );
        }
    }
}
