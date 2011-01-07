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
import edu.colorado.phet.reactionsandrates.model.Bond;
import edu.colorado.phet.reactionsandrates.model.SimpleMolecule;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * BondGraphic
 * <p/>
 * A graphic that represents the bond between two simple molecules
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BondGraphic extends PPath implements SimpleObserver {
    private Stroke bondStroke = new BasicStroke( 3 );
    //    private Paint bondPaint = new Color( 0, 160, 0, 180 );
    private Paint bondPaint = new Color( 255, 0, 0, 180 );
    private Line2D bondLine = new Line2D.Double();
    private Bond bond;

    /**
     * @param bond
     */
    public BondGraphic( Bond bond ) {
        this.bond = bond;
        SimpleMolecule m0 = bond.getParticipants()[0];
        SimpleMolecule m1 = bond.getParticipants()[1];
        setStrokePaint( bondPaint );
        setStroke( bondStroke );
        bond.addObserver( this );

        setPickable( false );

        update();
    }

    protected Line2D getBondLine() {
        return bondLine;
    }

    public void update() {
        SimpleMolecule m0 = bond.getParticipants()[0];
        SimpleMolecule m1 = bond.getParticipants()[1];
        bondLine.setLine( m0.getPosition().getX(),
                          m0.getPosition().getY(),
                          m1.getPosition().getX(),
                          m1.getPosition().getY() );
        setPathTo( bondLine );
    }
}
