package edu.colorado.phet.forcesandmotionbasics;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.forcesandmotionbasics.touch.ThumbnailTabModulePane;

public class ForcesAndMotionBasicsThumbnailTabs {
    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, ForcesAndMotionBasicsResources.PROJECT_NAME, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new ForcesAndMotionBasicsApplication( config, new ThumbnailTabModulePane() );
            }
        } );
    }
}