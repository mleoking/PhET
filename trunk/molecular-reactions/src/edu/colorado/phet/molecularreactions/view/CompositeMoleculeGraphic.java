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
import edu.colorado.phet.molecularreactions.model.CompositeMolecule;
import edu.colorado.phet.molecularreactions.model.Molecule;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.Bond;
import edu.colorado.phet.molecularreactions.MRConfig;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.*;

/**
 * CompoundMoleculeGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompositeMoleculeGraphic extends PNode implements SimpleObserver {
    private CompositeMolecule compositeMolecule;
    private PPath cmNode;

    /**
     *
     * @param compositeMolecule
     */
    public CompositeMoleculeGraphic( CompositeMolecule compositeMolecule ) {
        this.compositeMolecule = compositeMolecule;
        compositeMolecule.addObserver( this );
        addChild( createBondGraphics( compositeMolecule ) );
        addChild( createComponentGraphics( compositeMolecule ) );

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

    private PNode createBondGraphics( CompositeMolecule molecule ) {
        PNode layer = new PNode();
        Bond[] bonds = molecule.getBonds();
        for( int i = 0; i < bonds.length; i++ ) {
            Bond bond = bonds[i];
            PNode bondGraphic = new BondGraphic( bond );
            layer.addChild( bondGraphic );
        }
        return layer;
    }

    public void update() {
        if( MRConfig.DEBUG ) {
            cmNode.setOffset( compositeMolecule.getCM() );
        }
    }
}
