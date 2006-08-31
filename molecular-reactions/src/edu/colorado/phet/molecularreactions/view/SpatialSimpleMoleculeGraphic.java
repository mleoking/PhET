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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeB;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.Selectable;
import edu.colorado.phet.molecularreactions.DebugFlags;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;

/**
 * SimpleMoleculeGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpatialSimpleMoleculeGraphic extends AbstractSimpleMoleculeGraphic {
    private PPath boundingBox;
    private PPath cmNode;
    private double r = 2;


    public SpatialSimpleMoleculeGraphic( SimpleMolecule molecule ) {
        super( molecule );

        if( DebugFlags.SHOW_BOUNDING_BOX ) {
            boundingBox = new PPath();
            boundingBox.setStrokePaint( Color.red );
            addChild( boundingBox );
            update();
        }

        if( DebugFlags.SHOW_CM ) {
            cmNode = new PPath( new Ellipse2D.Double( -r, -r, r * 2, r * 2 ) );
            cmNode.setPaint( Color.red );
            addChild( cmNode );
            update();
        }
    }

    public void update() {
        setOffset( getMolecule().getCM() );
        if( DebugFlags.SHOW_BOUNDING_BOX && boundingBox != null ) {
            boundingBox.setPathTo( getMolecule().getBoundingBox() );
            boundingBox.setOffset( -getMolecule().getPosition().getX(),
                                   -getMolecule().getPosition().getY() );
        }

        if( DebugFlags.SHOW_CM && cmNode != null ) {
            cmNode.setPathTo( new Ellipse2D.Double( -r, -r, r * 2, r * 2 ) );
        }
    }
}
