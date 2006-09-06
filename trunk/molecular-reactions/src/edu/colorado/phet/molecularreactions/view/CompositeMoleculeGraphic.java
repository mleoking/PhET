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
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.Bond;
import edu.colorado.phet.molecularreactions.DebugFlags;

import java.awt.geom.Ellipse2D;
import java.awt.*;
import java.util.HashMap;

/**
 * CompositeMoleculeGraphic
 * <p/>
 * The only thing that this graphic adds to the view is a representation of the bonds between the
 * SimpleMolecules that make up the CompositeMolecule.
 * <p/>
 * If a debug flag is set, a red dot is displayed at the CM of the CompositeMolecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompositeMoleculeGraphic extends PNode implements SimpleObserver, CompositeMolecule.Listener {

    private CompositeMolecule compositeMolecule;
    private PPath cmNode;
    private PNode bondsLayer = new PNode();
//    private PNode simpleMoleculeLayer = new PNode();
//    private HashMap simpleMoleculeToGraphicMap = new HashMap();
    private HashMap bondToGraphicMap = new HashMap();
    private PPath boundingBox;

    /**
     * @param compositeMolecule
     */
    public CompositeMoleculeGraphic( CompositeMolecule compositeMolecule ) {
        compositeMolecule.addListener( this );
        this.compositeMolecule = compositeMolecule;
//        compositeMolecule.addObserver( this );

        addChild( bondsLayer );

        if( DebugFlags.SHOW_BOUNDING_BOX ) {
            boundingBox = new PPath( compositeMolecule.getBoundingBox() );
            addChild( boundingBox );
        }

        createBondGraphics( compositeMolecule.getBonds() );

        if( DebugFlags.SHOW_CM ) {
            double r = 2;
            cmNode = new PPath( new Ellipse2D.Double( -r, -r, r * 2, r * 2 ) );
            cmNode.setPaint( Color.red );
            addChild( cmNode );
            update();
        }

        compositeMolecule.addObserver( this );
    }

    private void createBondGraphics( Bond[] bonds ) {
        for( int i = 0; i < bonds.length; i++ ) {
            Bond bond = bonds[i];
            createBondGraphic( bond );
        }
    }

    private void createBondGraphic( Bond bond ) {
        PNode bondGraphic = new BondGraphic( bond );
        bondToGraphicMap.put( bond, bondGraphic );
        bondsLayer.addChild( bondGraphic );
    }

    public void update() {
        if( DebugFlags.SHOW_BOUNDING_BOX ) {
            boundingBox.setPathTo( compositeMolecule.getBoundingBox() );
        }
        if( DebugFlags.SHOW_CM ) {
            if( cmNode == null || compositeMolecule == null) {
                System.out.println( "CompositeMoleculeGraphic.update" );
            }
            cmNode.setOffset( compositeMolecule.getCM() );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of CompositeMolecule.Listener
    //--------------------------------------------------------------------------------------------------

    public void componentAdded( SimpleMolecule component, Bond bond ) {
//        createComponentGraphics( component );
        createBondGraphic( bond );
    }

    public void componentRemoved( SimpleMolecule component, Bond bond ) {
//        PNode moleculeGraphic = (PNode)simpleMoleculeToGraphicMap.get( component );
//        simpleMoleculeLayer.removeChild( moleculeGraphic );
        PNode bondGraphic = (PNode)bondToGraphicMap.get( bond );
        bondsLayer.removeChild( bondGraphic );
    }
}
