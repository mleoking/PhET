/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molectularreactions.view;

import edu.umd.cs.piccolo.PNode;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.model.CompoundMolecule;
import edu.colorado.phet.molecularreactions.model.Molecule;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;

/**
 * CompoundMoleculeGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompoundMoleculeGraphic extends PNode {

    public CompoundMoleculeGraphic( CompoundMolecule compoundMolecule ) {
        addChild( createComponentGraphics( compoundMolecule ) );
    }

    private PNode createComponentGraphics( Molecule molecule ) {
        PNode pNode = null;
        if( molecule instanceof SimpleMolecule ) {
            pNode = new SimpleMoleculeGraphic( (SimpleMolecule)molecule );
        }
        else {
            pNode = new PNode();
            Molecule[] componentMolecules = molecule.getComponentMolecules();
            for( int i = 0; i < componentMolecules.length; i++ ) {
                Molecule component = componentMolecules[i];
                pNode.addChild( createComponentGraphics( component ) );
            }
        }
        return pNode;
    }
}
