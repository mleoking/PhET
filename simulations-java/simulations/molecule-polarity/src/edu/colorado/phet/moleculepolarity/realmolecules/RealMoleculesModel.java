// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.moleculepolarity.common.model.MPModel;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D.ImportMolecule;

/**
 * Model for the "Real Molecules" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealMoleculesModel extends MPModel {

    public final Property<Molecule3D> currentMolecule;

    private final ArrayList<Molecule3D> molecules;

    //TODO i18n of molecule names
    public RealMoleculesModel() {
        molecules = new ArrayList<Molecule3D>() {{

            add( new Molecule3D( "H2", "hydrogen", "mol2/h2.mol2" ) );
            add( new Molecule3D( "N2", "nitrogen", "mol2/n2.mol2" ) );
            add( new Molecule3D( "O2", "oxygen", "mol2/o2.mol2" ) );
            add( new Molecule3D( "F2", "fluorine", "mol2/f2.mol2" ) );
            add( new Molecule3D( "HF", "hydrogen fluoride", "mol2/hf.mol2" ) );
            add( new Molecule3D( "CO", "carbon monoxide", "mol2/co.mol2" ) );

            add( new Molecule3D( "H2O", "water", "mol2/h2o.mol2" ) );
            add( new Molecule3D( "CO2", "carbon dioxide", "mol2/co2.mol2" ) );
            add( new Molecule3D( "HCN", "hydrogen cyanide", "mol2/hcn.mol2" ) );
            add( new Molecule3D( "O3", "ozone", "mol2/o3.mol2" ) );

            add( new Molecule3D( "NH3", "ammonia", "mol2/nh3.mol2" ) );
            add( new Molecule3D( "BH3", "borane", "mol2/bh3.mol2" ) );
            add( new Molecule3D( "BF3", "boron trifluoride", "mol2/bf3.mol2" ) );
            add( new Molecule3D( "CH2O", "formaldehyde", "mol2/ch2o.mol2" ) );

            add( new Molecule3D( "CH4", "methane", "mol2/ch4.mol2" ) );
            add( new Molecule3D( "CH3F", "fluoromethane", "mol2/ch3f.mol2" ) );
            add( new Molecule3D( "CH2F2", "difluoromethane", "mol2/ch2f2.mol2" ) );
            add( new Molecule3D( "CHF3", "trifluoromethane", "mol2/chf3.mol2" ) );
            add( new Molecule3D( "CF4", "tetrafluoromethane", "mol2/cf4.mol2" ) );
            add( new Molecule3D( "CHCl3", "chloroform", "mol2/chcl3.mol2" ) );

            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                add( new ImportMolecule() );
            }
        }};

        currentMolecule = new Property<Molecule3D>( molecules.get( 0 ) );
    }

    public void reset() {
        super.reset();
        currentMolecule.reset();
    }

    public ArrayList<Molecule3D> getMolecules() {
        return new ArrayList<Molecule3D>( molecules );
    }
}
