// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.moleculepolarity.common.model.MPModel;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D.Ammonia;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D.BoronTrifluoride;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D.Formaldehyde;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D.HydrogenFluoride;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D.ImportMolecule;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D.Methane;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D.MethylFluoride;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D.Water;

/**
 * Model for the "Real Molecules" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealMoleculesModel extends MPModel {

    private final ArrayList<Molecule3D> molecules;

    public RealMoleculesModel() {
        molecules = new ArrayList<Molecule3D>() {{
            add( new Ammonia() );
            add( new BoronTrifluoride() );
            add( new Formaldehyde() );
            add( new HydrogenFluoride() );
            add( new Methane() );
            add( new MethylFluoride() );
            add( new Water() );
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                add( new ImportMolecule() );
            }
        }};
    }

    public ArrayList<Molecule3D> getMolecules() {
        return new ArrayList<Molecule3D>( molecules );
    }
}
