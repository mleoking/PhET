package edu.colorado.phet.curvefitting;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class CurveFittingApplication extends PiccoloPhetApplication {
    public CurveFittingApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new CurveFittingModule() );
    }

    public static void main( String[] args ) {
        new CurveFittingApplication( new PhetApplicationConfig( args, new FrameSetup.CenteredWithInsets( 100,100), PhetResources.forProject( "curve-fitting" ), "curve-fitting") ).startApplication();
    }
}
