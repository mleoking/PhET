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
import edu.colorado.phet.nuclearphysics.model.Vessel;
import edu.colorado.phet.nuclearphysics.Config;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * VesselBackgroundPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class VesselBackgroundPanel extends PhetShapeGraphic implements Vessel.ChangeListener {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    private static int baseGray = Config.CONTROLLED_CHAIN_REACTION_BACKGROUND;
    private static double maxTemp = Vessel.MAX_TEMPERATURE;
    private static Color DEFAULT_COLOR = new Color( baseGray, baseGray, baseGray );
    private static Color[] lut = new Color[100];

    static {
        for( int i = 0; i < lut.length; i++ ) {
            int red = baseGray + (int)( ( 255 - baseGray ) * (double)i / lut.length );
            int blue = baseGray;
            int green = baseGray;
            lut[i] = new Color( red, green, blue );
        }
    }

    /**
     * @param component
     * @param vessel
     */
    public VesselBackgroundPanel( Component component, Vessel vessel ) {
        // The +1 is to handle round-off
        super( component, new Rectangle2D.Double( vessel.getBounds().getX(),
                                                  vessel.getBounds().getY(),
                                                  vessel.getBounds().getWidth() + 1,
                                                  vessel.getBounds().getHeight() ),
               DEFAULT_COLOR );
        vessel.addChangeListener( this );
    }


    public void temperatureChanged( Vessel.ChangeEvent event ) {
        int lutIdx = (int)( lut.length * ( event.getVessel().getTemperature() / maxTemp ) );
        lutIdx = Math.min( lutIdx, lut.length - 1 );
        setPaint( lut[lutIdx] );
        setBoundsDirty();
        repaint();
    }
}
