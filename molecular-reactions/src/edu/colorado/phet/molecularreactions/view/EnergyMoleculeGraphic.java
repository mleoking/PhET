/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.umd.cs.piccolo.PNode;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.common.util.SimpleObserver;

/**
 * EnergyMoleculeGraphic
 * <p/>
 * A graphic that represents a molecule on the EnergyView. It is composed
 * of one or two EnergySimpleMoleculeGraphics, depending on the nature of the
 * molecule it is representing
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyMoleculeGraphic extends PNode implements SimpleObserver {
    private boolean showComposite;
    private AbstractMolecule molecule;

    public EnergyMoleculeGraphic( AbstractMolecule molecule ) {
        this.molecule = molecule;
        molecule.addObserver( this );
        showComposite = molecule.isPartOfComposite();
        createChildNodes();
    }

    private void createChildNodes() {
        if( !showComposite ) {
            addChild( new EnergySimpleMoleculeGraphic( (SimpleMolecule)molecule ) );
        }
        else {
            // Keep the B molecule in the middle of the display by putting the other molecule
            // on top if it's an A molecule, and below if it's a C molecule
            SimpleMolecule[] components = ( (CompositeMolecule)molecule.getParentComposite() ).getComponentMolecules();
            SimpleMolecule smB = components[0] instanceof MoleculeB ? components[0] : components[1];
            SimpleMolecule smX = components[0] instanceof MoleculeB ? components[1] : components[0];
            PNode mgB = new EnergySimpleMoleculeGraphic( smB );
            mgB.setOffset( 0, 0 );
            addChild( mgB );
            PNode mgX = new EnergySimpleMoleculeGraphic( smX );
            int direction = smX instanceof MoleculeA ? -1 : 1;
            mgX.setOffset( 0, direction * mgB.getFullBounds().getHeight() );
            addChild( mgX );
        }
    }

    /**
     * Checks to see if the molecule we are representing has become part of
     * a composite, or left one
     */
    public void update() {

        // Check to see if the molecule we are representing has become part of
        // a composite, or left one
        boolean recreateChildren = false;
        if( showComposite && !molecule.isPartOfComposite() ) {
            this.removeAllChildren();
            recreateChildren = true;
            showComposite = false;
        }
        else if( !showComposite && molecule.isPartOfComposite() ) {
            this.removeAllChildren();
            recreateChildren = true;
            showComposite = true;
        }

        if( recreateChildren ) {
            createChildNodes();
        }
    }
}
