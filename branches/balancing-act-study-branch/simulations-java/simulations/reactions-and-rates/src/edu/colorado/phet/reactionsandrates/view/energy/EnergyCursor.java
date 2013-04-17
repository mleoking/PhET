// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactionsandrates.view.energy;

import edu.colorado.phet.common.piccolophet.nodes.RegisterablePNode;
import edu.colorado.phet.reactionsandrates.model.MRModel;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * EnergyCursor
 * <p/>
 * A vertical gray bar that moves around on the EnergyView to show where the reacting molecules
 * are on the energy profile
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyCursor extends RegisterablePNode {
    private double width = 4;

    EnergyCursor( double height, double minX, double maxX, MRModel model ) {
        setRegistrationPoint( width / 2, 0 );
        Rectangle2D cursorShape = new Rectangle2D.Double( 0, 0, width, height );
        PPath cursorPPath = new PPath( cursorShape );
        cursorPPath.setStroke( new BasicStroke( 1 ) );
        cursorPPath.setStrokePaint( new Color( 200, 200, 200 ) );
        cursorPPath.setPaint( new Color( 200, 200, 200, 200 ) );
        addChild( cursorPPath );
    }
}
