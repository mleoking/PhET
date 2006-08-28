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
import java.util.HashMap;

/**
 * CompoundMoleculeGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompositeMoleculeGraphic extends PNode implements SimpleObserver, CompositeMolecule.Listener {
    private CompositeMolecule compositeMolecule;
    private PPath cmNode;
    private PNode bondsLayer = new PNode();
    private PNode simpleMoleculeLayer = new PNode();
    private HashMap simpleMoleculeToGraphicMap = new HashMap();
    private HashMap participantsToBondGraphicsMap = new HashMap();

    /**
     *
     * @param compositeMolecule
     */
    public CompositeMoleculeGraphic( CompositeMolecule compositeMolecule ) {
        compositeMolecule.addListener( this );
        this.compositeMolecule = compositeMolecule;
        compositeMolecule.addObserver( this );

        addChild( bondsLayer );
        addChild( simpleMoleculeLayer );

        createComponentGraphics( compositeMolecule );
        createBondGraphics( compositeMolecule.getBonds() );

        if( MRConfig.DEBUG ) {
            double r = 2;
            cmNode = new PPath( new Ellipse2D.Double( -r, -r, r * 2, r * 2 ) );
            cmNode.setPaint( Color.red );
            addChild( cmNode );
            update();
        }
    }

    private void createComponentGraphics( Molecule molecule ) {
        if( molecule instanceof SimpleMolecule ) {
            SpatialSimpleMoleculeGraphic simpleMoleculeGraphic = new SpatialSimpleMoleculeGraphic( (SimpleMolecule)molecule );
            simpleMoleculeToGraphicMap.put( molecule, simpleMoleculeGraphic );
            simpleMoleculeLayer.addChild( simpleMoleculeGraphic );
        }
        else {
            Molecule[] componentMolecules = molecule.getComponentMolecules();
            for( int i = 0; i < componentMolecules.length; i++ ) {
                Molecule component = componentMolecules[i];
                createComponentGraphics( component );
            }
        }
    }

    private void createBondGraphics( Bond[] bonds ) {
        for( int i = 0; i < bonds.length; i++ ) {
            Bond bond = bonds[i];
            createBondGraphic( bond );
        }
    }

    private void createBondGraphic( Bond bond ) {
        PNode bondGraphic = new BondGraphic( bond );
        participantsToBondGraphicsMap.put( bond.getParticipants()[0], bondGraphic );
        participantsToBondGraphicsMap.put( bond.getParticipants()[1], bondGraphic );
        bondsLayer.addChild( bondGraphic );
    }

    public void update() {
        if( MRConfig.DEBUG ) {
            cmNode.setOffset( compositeMolecule.getCM() );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of CompositeMolecule.Listener
    //--------------------------------------------------------------------------------------------------

    public void componentAdded( SimpleMolecule component, Bond bond ) {
        createComponentGraphics( component );
        createBondGraphic( bond );
    }

    public void componentRemoved( SimpleMolecule component, Bond bond ) {
        // noop
    }
}
