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
import edu.colorado.phet.molecularreactions.model.Molecule;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.CompositeMolecule;
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
    private Molecule molecule;

    public EnergyMoleculeGraphic( Molecule molecule ) {
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
//        if( molecule instanceof CompositeMolecule ) {
            SimpleMolecule[] components = ( (CompositeMolecule)molecule.getParentComposite() ).getComponentMolecules();
            PNode mg1 = new EnergySimpleMoleculeGraphic( (SimpleMolecule)components[0] );
            mg1.setOffset( 0, -mg1.getFullBounds().getHeight() / 2 );
            addChild( mg1 );
            PNode mg2 = new EnergySimpleMoleculeGraphic( (SimpleMolecule)components[1] );
            mg2.setOffset( 0, mg1.getFullBounds().getHeight() / 2 );
            addChild( mg2 );
        }
    }

    public void update() {
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
