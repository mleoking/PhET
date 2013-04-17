// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view;

import edu.colorado.phet.reactionsandrates.DebugFlags;
import edu.colorado.phet.reactionsandrates.model.*;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;

/**
 * SpatialSimpleMoleculeGraphic
 * <p/>
 * Graphic for simple molecules for use in the SpatialView. The additional behavior
 * this class adds to the base class is that instances are selectable with the mouse.
 * with the mouse.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpatialSimpleMoleculeGraphic extends ObservingMoleculeGraphic {
    private PPath boundingBox;

    private final SimpleMolecule molecule;
    private final EnergyProfile profile;

    /**
     * @param molecule
     * @param profile
     */
    public SpatialSimpleMoleculeGraphic( SimpleMolecule molecule, EnergyProfile profile ) {
        super( molecule, profile );

        this.molecule = molecule;
        this.profile = profile;

        // Catch mouse clicks that select this graphic's molecule
        if( molecule instanceof MoleculeA || molecule instanceof MoleculeC ) {
            this.addInputEventListener( new PBasicInputEventHandler() {
                public void mouseClicked( PInputEvent event ) {
                    super.mouseClicked( event );
                    getMolecule().setSelectionStatus( Selectable.SELECTED );
                }
            } );
        }

        if( DebugFlags.SHOW_BOUNDING_BOX ) {
            boundingBox = new PPath();
            boundingBox.setStrokePaint( Color.red );
            addChild( boundingBox );
            update();
        }
    }


    public Object clone() {
        return new SpatialSimpleMoleculeGraphic( (SimpleMolecule)molecule.clone(), profile );
    }

    public void update() {
        super.update();
        setOffset( getMolecule().getCM() );
        if( DebugFlags.SHOW_BOUNDING_BOX && boundingBox != null ) {
            boundingBox.setPathTo( getMolecule().getBoundingBox() );
            boundingBox.setOffset( -getMolecule().getPosition().getX(),
                                   -getMolecule().getPosition().getY() );
        }

//        if( getMolecule().isPartOfComposite() && debugNode == null ) {
//            debugNode = new PPath( new Ellipse2D.Double(0,0,4,4));
//            debugNode.setPaint( Color.green);
//            addChild( debugNode );
//        }
//        else if( !getMolecule().isPartOfComposite() && debugNode != null) {
//            removeChild( debugNode );
//            debugNode = null;
//        }
    }
}
