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

public class EnergyProfileGraphic extends PhetImageGraphic implements SimpleObserver {

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
    private Color backgroundColor = new Color( 200, 200, 255 );
    private Stroke potentialProfileStroke = new BasicStroke( 2f );
    float miterLimit = 10f;
    float[] dashPattern = {10f};
    float dashPhase = 5f;
    Stroke totalEnergyStroke = new BasicStroke( 1f, BasicStroke.CAP_BUTT,
                                                BasicStroke.JOIN_MITER, miterLimit, dashPattern, dashPhase );

    private EnergyProfile profile;
//    private Point2D.Double origin;
    private AffineTransform profileTx = new AffineTransform();
    private Image image;
    private Nucleus nucleus;

    public EnergyProfileGraphic( Component component, Nucleus nucleus ) {
        super( component );
        this.nucleus = nucleus;
        this.profile = nucleus.getEnergylProfile();
        this.profile.addObserver( this );
        image = buildImage();
    }

    public void setColor( Color color ) {
        this.color = color;
        image = buildImage();
    }

    /**
     * Gets the location of the origin (0,0) within the bounds of the graphic
     *
     * @return a Point2D
     */
    public Point2D getOrigin() {
        return new Point2D.Double( profile.getWidth() / 2, profile.getMaxEnergy() );
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

    private Image buildImage() {
        AffineTransform atx = new AffineTransform();
        int imageHeight = (int)( profile.getMaxEnergy() - profile.getMinEnergy() );
//        int imageHeight = (int)Math.max( profile.getMaxEnergy(), profile.getMinEnergy() );
//        int imageHeight = (int)Math.max( profile.getMaxPotential(), profile.getWellPotential() );
        BufferedImage bi = new BufferedImage( (int)( profile.getWidth() ),
                                              imageHeight,
                                              BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)bi.getGraphics();
        GraphicsUtil.setAntiAliasingOn( g );

        // Note that the profile path is centered on the y axis, so half of it
        // has negative x coordinates. That's why is has to be translated
        g.setColor( backgroundColor );
        GraphicsUtil.setAlpha( g, 1 );
//        g.fill( atx.createTransformedShape( profile.getBackgroundPath() ) );
        GraphicsUtil.setAlpha( g, 1 );
        g.setColor( color );
        g.setStroke( potentialProfileStroke );
        atx.setToIdentity();
        atx.translate( profile.getWidth() / 2, profile.getMaxEnergy() );
        g.draw( atx.createTransformedShape( profile.getPotentialEnergyPath() ) );

        g.dispose();

        // To give the PhetImageGraphic and image
        setImage( bi );

        return bi;
    }

    public void update() {
        image = buildImage();
    }


}
