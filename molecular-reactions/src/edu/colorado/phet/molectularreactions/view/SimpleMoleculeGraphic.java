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
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.geom.Ellipse2D;
import java.awt.*;

/**
 * SimpleMoleculeGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleMoleculeGraphic extends PNode implements SimpleObserver
                                                           /*, Molecule.Listener*/ {
    private static Stroke defaultStroke = new BasicStroke( 1 );
    private static Stroke selectedStroke = new BasicStroke( 2 );

    private static Paint defaultStrokePaint = Color.black;
    private static Paint selectedStrokePaint = Color.red;
    private SimpleMolecule molecule;
    private PPath pPath;

    public SimpleMoleculeGraphic( SimpleMolecule molecule ) {
        this.molecule = molecule;
        molecule.addObserver( this );

        Shape s = new Ellipse2D.Double( -molecule.getRadius(),
                                        -molecule.getRadius(),
                                        molecule.getRadius() * 2,
                                        molecule.getRadius() * 2);
        pPath = new PPath( s, defaultStroke );
        pPath.setPaint( Color.green );
        pPath.setStrokePaint( defaultStrokePaint );
        addChild( pPath );
        update();
    }

    public void update() {
        setOffset( molecule.getPosition() );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Molecule.Listener
    //--------------------------------------------------------------------------------------------------

//    public void selected( Molecule.Event event ) {
//        pPath.setStroke( selectedStroke );
//        pPath.setStrokePaint( selectedStrokePaint );
//    }
//
//    public void unSelected( Molecule.Event event ) {
//        pPath.setStroke( defaultStroke );
//        pPath.setStrokePaint( defaultStrokePaint );
//    }
}
