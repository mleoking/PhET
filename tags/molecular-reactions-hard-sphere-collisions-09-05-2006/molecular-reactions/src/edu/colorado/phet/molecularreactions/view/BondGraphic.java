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
import edu.colorado.phet.molecularreactions.model.Bond;
import edu.colorado.phet.molecularreactions.model.Molecule;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.geom.Line2D;
import java.awt.*;

/**
 * BondGraphic
 * <p>
 * A graphic that represents the bond between two simple molecules
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BondGraphic extends PPath implements SimpleObserver {
    private Stroke bondStroke = new BasicStroke( 8 );
    private Paint bondPaint = Color.gray;
    private Line2D bondLine;
    private SimpleMolecule m0;
    private SimpleMolecule m1;

    /**
     *
     * @param bond
     */
    public BondGraphic( Bond bond ) {
        m0 = bond.getParticipants()[0];
        m1 = bond.getParticipants()[1];
        bondLine = new Line2D.Double( m0.getPosition().getX(),
                                      m0.getPosition().getY(),
                                      m1.getPosition().getX(),
                                      m1.getPosition().getY() );
        setStrokePaint( bondPaint );
        setStroke( bondStroke );
        m0.addObserver( this );
        m1.addObserver( this );
    }

    public void update() {
        bondLine.setLine( m0.getPosition().getX(),
                          m0.getPosition().getY(),
                          m1.getPosition().getX(),
                          m1.getPosition().getY());
        setPathTo( bondLine );
    }
}
