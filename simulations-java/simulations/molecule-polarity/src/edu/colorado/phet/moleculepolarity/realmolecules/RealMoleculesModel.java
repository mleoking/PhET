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
            add( new Molecule3D( "H2", "hydrogen", "sdf/h2.sdf" ) );
            add( new Molecule3D( "HF", "hydrogen fluoride", "sdf/hf.sdf" ) );
            add( new Molecule3D( "H2O", "water", "sdf/h2o.sdf" ) );
            add( new Molecule3D( "CO2", "carbon dioxide", "sdf/co2.sdf" ) );
            add( new Molecule3D( "HCN", "hydrogen cyanide", "sdf/hcn.sdf" ) );
            add( new Molecule3D( "NH3", "ammonia", "sdf/nh3.sdf" ) );
            add( new Molecule3D( "BF3", "boron trifluoride", "sdf/bf3.sdf" ) );
            add( new Molecule3D( "CH2O", "formaldehyde", "sdf/ch2o.sdf" ) );
            add( new Molecule3D( "CH4", "methane", "sdf/ch4.sdf" ) );
            add( new Molecule3D( "CH3F", "fluoromethane", "sdf/ch3f.sdf" ) );
            add( new Molecule3D( "CHCl3", "chloroform", "sdf/chcl3.sdf" ) );
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                add( new ImportMolecule() );
            }
        }};
    }

    public ArrayList<Molecule3D> getMolecules() {
        return new ArrayList<Molecule3D>( molecules );
    }
}
