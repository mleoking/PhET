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

public class RemoveMoleculeCmd implements Command {
    private IdealGasModel idealGasModel;
    private Class currentGasSpecies;

    public RemoveMoleculeCmd( IdealGasModel idealGasModel, Class currentGasSpecies ) {
        this.idealGasModel = idealGasModel;
        this.currentGasSpecies = currentGasSpecies;
    }

    public void doIt() {
        List bodies = idealGasModel.getBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            Object obj = bodies.get( i );
            if( currentGasSpecies.isInstance( obj ) ) {
                bodies.remove( i );
                GasMolecule molecule = (GasMolecule)obj;
                idealGasModel.removeModelElement( molecule );
                //                molecule.removeYourselfFromSystem();
                break;
            }
        }
    }
}
