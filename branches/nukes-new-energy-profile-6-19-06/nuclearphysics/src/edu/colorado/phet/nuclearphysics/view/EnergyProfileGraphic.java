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

import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.EnergyProfile;
import edu.colorado.phet.nuclearphysics.model.Uranium235;

import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

public class EnergyProfileGraphic extends PhetShapeGraphic {
//public class EnergyProfileGraphic extends PhetImageGraphic {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static ImageObserver imgObs = new ImageObserver() {
        public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
            return false;
        }
    };

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private Color color = Color.blue;
    private Stroke potentialProfileStroke = new BasicStroke( 2f );
    float miterLimit = 10f;
    float[] dashPattern = {10f};
    float dashPhase = 5f;
    Stroke totalEnergyStroke = new BasicStroke( 1f, BasicStroke.CAP_BUTT,
                                                BasicStroke.JOIN_MITER, miterLimit, dashPattern, dashPhase );

    private EnergyProfile profile;
    private AffineTransform profileTx = new AffineTransform();
    private Nucleus nucleus;

    /**
     * Sole constructor
     * @param component
     * @param nucleus
     */
    public EnergyProfileGraphic( Component component, Nucleus nucleus ) {
        super( component );
        this.nucleus = nucleus;
        this.profile = nucleus.getEnergylProfile();
    }

    public void setColor( Color color ) {
        this.color = color;
    }

    public EnergyProfile getProfile() {
        return profile;
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );

        profileTx.setToIdentity();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        g.setColor( color );
        g.setStroke( potentialProfileStroke );
        g.draw( profile.getPotentialEnergyPath() );

        g.setColor( Color.red );
        g.setStroke( totalEnergyStroke );
        g.draw( profile.getTotalEnergyPath() );

        // Debug
//        g.drawArc( -3, -3, 6,6, 0, 360 );
        gs.restoreGraphics();
    }
}
