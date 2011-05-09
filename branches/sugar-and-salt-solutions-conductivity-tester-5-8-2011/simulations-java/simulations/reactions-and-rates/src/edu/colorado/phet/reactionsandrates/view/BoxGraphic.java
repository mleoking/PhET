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

import edu.colorado.phet.common.collision.Box2D;
import edu.colorado.phet.common.piccolophet.nodes.RegisterablePNode;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * BoxGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BoxGraphic extends RegisterablePNode {
    private boolean glassBox = true;
    private float wallThickness = 15;
    private Stroke stroke = new BasicStroke( wallThickness );

    public BoxGraphic( Box2D box ) {

        if( glassBox ) {
            Dimension imageSize = new Dimension( (int)( box.getWidth() + 2 * wallThickness ),
                                                 (int)( box.getHeight() + 2 * wallThickness ) );
            PImage imageNode = PImageFactory.create( "reactions-and-rates/images/glass-box-B.png", imageSize );
            addChild( imageNode );
            setRegistrationPoint( wallThickness, wallThickness );
            setPickable( false );
            setChildrenPickable( false );
        }
        else {
            Rectangle2D rect = new Rectangle2D.Double( 0,
                                                       0,
                                                       box.getWidth() + wallThickness,
                                                       box.getHeight() + wallThickness );
            PPath pPath = new PPath( rect, stroke );
            addChild( pPath );
            setOffset( box.getMinX() - wallThickness / 2,
                       box.getMinY() - wallThickness / 2 );
        }
    }
}
