/**
 * Class: ProfileModificationModule
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Mar 17, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;

public class ProfileModificationModule extends ProfiledNucleusModule {

    //
    // Statics
    //
    private static PotentialProfile defaultProfile = new PotentialProfile( 300, 400, 75 );


    private PotentialProfile potentialProfile;

    public ProfileModificationModule( AbstractClock clock ) {
        super( "Profile Shaping", clock );
        potentialProfile = defaultProfile;
        super.addControlPanelElement( new ProfileShapingControlPanel( this ) );
    }

    public void activate( PhetApplication app ) {
//        super.activate( app );
//        potentialProfile = defaultProfile;
//        super.addControlPanelElement( new ProfileShapingControlPanel( this ) );
    }


    public void setProfileMaxHeight( double modelValue ) {
        potentialProfile.setMaxPotential( modelValue );
        getPotentialProfilePanel().repaint();
    }

    public void setProfileWellDepth( double wellDepth ) {
        potentialProfile.setWellDepth( wellDepth );
        getPotentialProfilePanel().repaint();
    }

    public void setProfileWidth( double profileWidth ) {
        potentialProfile.setWidth( profileWidth );
        getPotentialProfilePanel().repaint();
    }

    public PotentialProfile getPotentialProfile() {
        return potentialProfile;
    }
}
