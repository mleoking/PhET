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
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.IEnergyProfile;
import edu.colorado.phet.nuclearphysics.model.ProfileableNucleus;

import java.awt.image.ImageObserver;
import java.awt.*;

public class EnergyProfileGraphic extends PhetShapeGraphic {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    /**
     * An enumeration type that tells whether the profile shown is the old-style potential energy curve,
     * or the new square-sided profile with a total energy line on it.
     */
    public static class ProfileType {
        private ProfileType() {
        }
    }
    public static ProfileType TOTAL_ENERGY = new ProfileType();
    public static ProfileType POTENTIAL_ENERGY = new ProfileType();


    public static Color potentialProfileColor = new Color(150,25,255);
//    public static Color potentialProfileColor = Color.blue;
    public static Stroke potentialProfileStroke = new BasicStroke( 2f );
    private static float miterLimit = 10f;
    private static float[] dashPattern = {10f};
    private static float dashPhase = 0f;
//    public static Stroke totalEnergyStroke = new BasicStroke( 1f, BasicStroke.CAP_BUTT,
//                                                              BasicStroke.JOIN_MITER, miterLimit, dashPattern, dashPhase );
    public static Stroke totalEnergyStroke = new BasicStroke( 2f );
    public static Color totalEnergyColor = new Color( 0, 180, 0 );
//    public static Color totalEnergyColor = Color.green;

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private IEnergyProfile profile;
    private Nucleus nucleus;
    private ProfileType profileType;

    /**
     * Only constructor
     *
     * @param component
     * @param nucleus
     * @param profileType
     */
    public EnergyProfileGraphic( Component component, ProfileableNucleus nucleus, ProfileType profileType ) {
        super( component );
        this.nucleus = nucleus;
        this.profileType = profileType;
        if( profileType == TOTAL_ENERGY ) {
            this.profile = nucleus.getEnergyProfile();
        }
        else if( profileType == POTENTIAL_ENERGY ) {
            this.profile = nucleus.getPotentialProfile();
        }
    }

    public void setColor( Color color ) {
        this.potentialProfileColor = color;
    }

    public IEnergyProfile getProfile() {
        return profile;
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );

        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        g.setColor( potentialProfileColor );
        g.setStroke( potentialProfileStroke );
        g.draw( profile.getPotentialEnergyPath() );

        g.setColor( totalEnergyColor );
        g.setStroke( totalEnergyStroke );
//        if( profile.getTotalEnergyPath() != null ) {
//            g.draw( profile.getTotalEnergyPath() );
//        }

        // Debug
//        g.drawArc( -3, -3, 6,6, 0, 360 );
        gs.restoreGraphics();
    }
}
