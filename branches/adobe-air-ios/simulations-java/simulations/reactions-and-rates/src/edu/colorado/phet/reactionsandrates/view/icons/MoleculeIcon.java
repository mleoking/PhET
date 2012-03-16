// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view.icons;

import edu.colorado.phet.reactionsandrates.model.*;
import edu.colorado.phet.reactionsandrates.view.ObservingMoleculeGraphic;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;

/**
 * SimpleMoleculeIcon
 * <p/>
 * An ImageIcon for a specified class of molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeIcon extends ImageIcon {
    // The amount the second component of a composite molecule icon needs
    // to be offset in order for it to look like it's touching the other component
    private int contactOffset = -8;

    public MoleculeIcon( Class moleculeClass, EnergyProfile profile ) {
        PNode pNode = null;
        Image image = null;
        if( moleculeClass == MoleculeA.class ) {
            pNode = new IconGraphic( new MoleculeA(), profile );
        }
        if( moleculeClass == MoleculeB.class ) {
            pNode = new IconGraphic( new MoleculeB(), profile );
        }
        if( moleculeClass == MoleculeC.class ) {
            pNode = new IconGraphic( new MoleculeC(), profile );
        }
        if( moleculeClass == MoleculeAB.class ) {
            PNode aNode = new IconGraphic( new MoleculeA(), profile );
            PNode bNode = new IconGraphic( new MoleculeB(), profile );
            pNode = new PNode();
            pNode.addChild( aNode );
            pNode.addChild( bNode );
            bNode.setOffset( aNode.getFullBounds().getWidth() + contactOffset, 0 );
        }
        if( moleculeClass == MoleculeBC.class ) {
            PNode bNode = new IconGraphic( new MoleculeB(), profile );
            PNode cNode = new IconGraphic( new MoleculeC(), profile );
            pNode = new PNode();
            pNode.addChild( cNode );
            pNode.addChild( bNode );
            cNode.setOffset( bNode.getFullBounds().getWidth() + contactOffset, 0 );
        }
        image = pNode.toImage();
        this.setImage( image );
    }


    private static class IconGraphic extends ObservingMoleculeGraphic {
        private final SimpleMolecule molecule;
        private final EnergyProfile profile;

        public IconGraphic( SimpleMolecule molecule, EnergyProfile profile ) {
            super( molecule, profile, true );

            this.molecule = molecule;
            this.profile = profile;
        }


        public Object clone() {
            return new IconGraphic( (SimpleMolecule)molecule.clone(), profile );
        }
    }
}
