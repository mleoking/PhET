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
import edu.colorado.phet.collision.Box2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * BoxGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BoxGraphic extends PNode {

    private float wallThickness = 15;
    private Stroke stroke = new BasicStroke( wallThickness );

    public BoxGraphic( Box2D box ) {
        Rectangle2D rect = new Rectangle2D.Double(
                box.getMinX() - wallThickness / 2,
                box.getMinY() - wallThickness / 2,
                box.getWidth(),
                box.getHeight() );
        PPath pPath = new PPath( rect, stroke );
        addChild( pPath );
    }
}
