/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.solublesalts.model.Vessel;

import java.awt.geom.*;
import java.awt.*;

/**
 * Vessel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class VesselGraphic extends PNode {

    private PPath shape;
    private DoubleGeneralPath walls;
    private Vessel vessel;

    public VesselGraphic( Vessel vessel ) {
        this.vessel = vessel;
        determineShape();
    }

    private void determineShape() {
        if( shape != null ) {
            this.removeChild( shape );
        }
        float thickness = 20;
        Rectangle2D rect = vessel.getShape();
        walls = new DoubleGeneralPath( );
        walls.moveTo( rect.getMinX() - thickness / 2, rect.getMinY() );
        walls.lineToRelative( 0, rect.getHeight() + thickness / 2 );
        walls.lineToRelative( rect.getWidth() + thickness / 2, 0 );
        walls.lineToRelative( 0, -( rect.getHeight() + thickness / 2  ));
        shape = new PPath( walls.getGeneralPath() );
        shape.setStroke( new BasicStroke( thickness ) );
        this.addChild( shape );
    }

}
