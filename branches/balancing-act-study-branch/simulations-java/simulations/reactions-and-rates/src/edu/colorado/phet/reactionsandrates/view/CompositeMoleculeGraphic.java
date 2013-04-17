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

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.reactionsandrates.DebugFlags;
import edu.colorado.phet.reactionsandrates.model.CompositeMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Ellipse2D;

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
public class CompositeMoleculeGraphic extends PNode implements SimpleObserver {

    private CompositeMolecule compositeMolecule;
    private PPath cmNode;
    private PPath boundingBox;

    public Object clone() {
        return new CompositeMoleculeGraphic( (CompositeMolecule)compositeMolecule.clone() );
    }

    /**
     * @param compositeMolecule
     */
    public CompositeMoleculeGraphic( CompositeMolecule compositeMolecule ) {
        this.compositeMolecule = compositeMolecule;

        if( DebugFlags.SHOW_BOUNDING_BOX ) {
            boundingBox = new PPath( compositeMolecule.getBoundingBox() );
            addChild( boundingBox );
        }

        if( DebugFlags.SHOW_CM ) {
            double r = 2;
            cmNode = new PPath( new Ellipse2D.Double( -r, -r, r * 2, r * 2 ) );
            cmNode.setPaint( Color.red );
            addChild( cmNode );
            update();
        }
        compositeMolecule.addObserver( this );
    }

    public void update() {
        if( DebugFlags.SHOW_BOUNDING_BOX ) {
            boundingBox.setPathTo( compositeMolecule.getBoundingBox() );
        }
        if( DebugFlags.SHOW_CM ) {
            if( cmNode == null || compositeMolecule == null ) {
                System.out.println( "CompositeMoleculeGraphic.update" );
            }
            cmNode.setOffset( compositeMolecule.getCM() );
        }
    }
}
