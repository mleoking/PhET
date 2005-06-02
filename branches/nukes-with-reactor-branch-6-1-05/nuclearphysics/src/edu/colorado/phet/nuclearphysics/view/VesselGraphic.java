/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.nuclearphysics.model.Vessel;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * VesselGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class VesselGraphic extends CompositePhetGraphic {
//public class VesselGraphic extends PhetShapeGraphic {
    private static float wallThickness = 10;
    private Vessel vessel;

    public VesselGraphic( Component component, Vessel vessel ) {
        super( component );
        this.vessel = vessel;
//        super( component, new Rectangle2D.Double( vessel.getX() - wallThickness,
//                                                  vessel.getY() - wallThickness,
//                                                  vessel.getWidth(), vessel.getHeight() ),
//               new BasicStroke( wallThickness ), Color.black );
        PhetShapeGraphic boundary = new PhetShapeGraphic( component,
                                                          new Rectangle2D.Double( vessel.getX() - wallThickness,
                                                                                  vessel.getY() - wallThickness,
                                                                                  vessel.getWidth(), vessel.getHeight() ),
                                                          new BasicStroke( wallThickness ), Color.black );
        addGraphic( boundary );
        Rectangle2D[] channels = vessel.getChannels();
        for( int i = 0; i < channels.length; i++ ) {
            Rectangle2D channel = channels[i];
            PhetShapeGraphic channelGraphic = new PhetShapeGraphic( component,
                                                             new Rectangle2D.Double( channel.getX(),
                                                                                     channel.getY(),
                                                                                     channel.getWidth(),
                                                                                     channel.getHeight() ),
                                                             new BasicStroke( 1 ), Color.black );
            addGraphic( channelGraphic );
        }
        update();
    }

    private void update() {
        setBoundsDirty();
        repaint();
    }
}
