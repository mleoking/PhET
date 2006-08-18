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
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.model.CompoundMolecule;
import edu.colorado.phet.molecularreactions.model.Molecule;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.MRConfig;

import java.awt.geom.Ellipse2D;
import java.awt.*;

/**
 * CompoundMoleculeGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompoundMoleculeGraphic extends PNode implements SimpleObserver {
    private CompoundMolecule compoundMolecule;
    private PPath cmNode;

    public CompoundMoleculeGraphic( CompoundMolecule compoundMolecule ) {
        this.compoundMolecule = compoundMolecule;
        compoundMolecule.addObserver( this );
        addChild( createComponentGraphics( compoundMolecule ) );

        if( MRConfig.DEBUG ) {
            double r = 2;
            cmNode = new PPath( new Ellipse2D.Double( -r, -r, r * 2, r * 2 ) );
            cmNode.setPaint( Color.red );
            addChild( cmNode );
            update();
        }
    }

    private PNode createComponentGraphics( Molecule molecule ) {
        PNode pNode = null;
        if( molecule instanceof SimpleMolecule ) {
            pNode = new SpatialSimpleMoleculeGraphic( (SimpleMolecule)molecule );
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

    public void update() {
        if( MRConfig.DEBUG ) {
            cmNode.setOffset( compoundMolecule.getCM() );
        }
    }
}
