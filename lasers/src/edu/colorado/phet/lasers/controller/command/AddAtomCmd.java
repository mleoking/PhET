/**
 * Class: AddAtomCmd
 * Package: edu.colorado.phet.lasers.controller.command
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.controller.command;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.lasers.physics.atom.Atom;
import edu.colorado.phet.lasers.physics.LaserSystem;
import edu.colorado.phet.lasers.physics.ResonatingCavity;
import edu.colorado.phet.lasers.physics.CavityMustContainAtom;
import edu.colorado.phet.physics.Constraint;

public class AddAtomCmd extends LaserApplicationCmd {

    private Atom atom;

    public AddAtomCmd( Atom atom ) {
        this.atom = atom;
    }

    public Object doIt() {
        PhetApplication.instance().addBody( atom );
        ResonatingCavity cavity = getLaserSystem().getResonatingCavity();
        Constraint constraintSpec = new CavityMustContainAtom( cavity, atom );
        cavity.addConstraint( constraintSpec );
        return null;
    }
}
