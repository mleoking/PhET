// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.moleculepolarity.common.model.MPModel;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D.ImportMolecule;

/**
 * Model for the "Real Molecules" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealMoleculesModel extends MPModel {

    private final ArrayList<Molecule3D> molecules;

    //TODO i18n of molecule names
    public RealMoleculesModel() {
        molecules = new ArrayList<Molecule3D>() {{
            add( new Molecule3D( "H2", "hydrogen", "sdf/X-2D-hydrogen.sdf" ) );
            add( new Molecule3D( "HF", "hydrogen fluoride", "sdf/hydrogen-fluoride.sdf" ) );
            add( new Molecule3D( "H2O", "water", "sdf/water.sdf" ) );
            add( new Molecule3D( "CO2", "carbon dioxide", "sdf/carbon-dioxide.sdf" ) );
            add( new Molecule3D( "HCN", "hydrogen cyanide", "jmol/hydrogen-cyanide.sdf" ) );
            add( new Molecule3D( "NH3", "ammonia", "sdf/ammonia.sdf" ) );
            add( new Molecule3D( "BF3", "boron trifluoride", "sdf/X-2D-boron-trifluoride.sdf" ) );
            add( new Molecule3D( "CH2O", "formaldehyde", "sdf/formaldehyde.sdf" ) );
            add( new Molecule3D( "CH4", "methane", "sdf/X-3D-methane.sdf" ) );
            add( new Molecule3D( "CH3F", "fluoromethane", "sdf/fluoromethane.sdf" ) );
            add( new Molecule3D( "CHCl3", "chloroform", "sdf/chloroform.sdf" ) );
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                add( new ImportMolecule() );
            }
        }};
    }

    public ArrayList<Molecule3D> getMolecules() {
        return new ArrayList<Molecule3D>( molecules );
    }
}
