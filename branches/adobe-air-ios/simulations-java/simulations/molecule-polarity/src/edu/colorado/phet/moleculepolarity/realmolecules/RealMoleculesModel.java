// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D.ImportMolecule;

/**
 * Model for the "Real Molecules" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealMoleculesModel implements Resettable {

    private final ArrayList<Molecule3D> molecules; // the supported set of real 3D molecules
    public final Property<Molecule3D> currentMolecule; // the molecule that is currently visible

    public RealMoleculesModel() {

        molecules = new ArrayList<Molecule3D>() {{

            add( new Molecule3D( "H2", MPStrings.HYDROGEN, "mol2/h2.mol2" ) );
            add( new Molecule3D( "N2", MPStrings.NITROGEN, "mol2/n2.mol2" ) );
            add( new Molecule3D( "O2", MPStrings.OXYGEN, "mol2/o2.mol2" ) );
            add( new Molecule3D( "F2", MPStrings.FLUORINE, "mol2/f2.mol2" ) );
            add( new Molecule3D( "HF", MPStrings.HYDROGEN_FLUORIDE, "mol2/hf.mol2" ) );

            add( new Molecule3D( "H2O", MPStrings.WATER, "mol2/h2o.mol2" ) );
            add( new Molecule3D( "CO2", MPStrings.CARBON_DIOXIDE, "mol2/co2.mol2" ) );
            add( new Molecule3D( "HCN", MPStrings.HYDROGEN_CYANIDE, "mol2/hcn.mol2" ) );
            add( new Molecule3D( "O3", MPStrings.OZONE, "mol2/o3.mol2" ) );

            add( new Molecule3D( "NH3", MPStrings.AMMONIA, "mol2/nh3.mol2" ) );
            add( new Molecule3D( "BH3", MPStrings.BORANE, "mol2/bh3.mol2" ) );
            add( new Molecule3D( "BF3", MPStrings.BORON_TRIFLUORIDE, "mol2/bf3.mol2" ) );
            add( new Molecule3D( "CH2O", MPStrings.FORMALDEHYDE, "mol2/ch2o.mol2" ) );

            add( new Molecule3D( "CH4", MPStrings.METHANE, "mol2/ch4.mol2" ) );
            add( new Molecule3D( "CH3F", MPStrings.FLUOROMETHANE, "mol2/ch3f.mol2" ) );
            add( new Molecule3D( "CH2F2", MPStrings.DIFLUOROMETHANE, "mol2/ch2f2.mol2" ) );
            add( new Molecule3D( "CHF3", MPStrings.TRIFLUOROMETHANE, "mol2/chf3.mol2" ) );
            add( new Molecule3D( "CF4", MPStrings.TETRAFLUOROMETHANE, "mol2/cf4.mol2" ) );
            add( new Molecule3D( "CHCl3", MPStrings.CHLOROFORM, "mol2/chcl3.mol2" ) );

            // in developer mode, this provides a file chooser for selecting and loading a data file
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                add( new ImportMolecule() );
            }
        }};

        currentMolecule = new Property<Molecule3D>( molecules.get( 4 ) );
    }

    public void reset() {
        currentMolecule.reset();
    }

    public ArrayList<Molecule3D> getMolecules() {
        return new ArrayList<Molecule3D>( molecules );
    }
}
