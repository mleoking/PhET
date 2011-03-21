// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.gravityandorbits.module.*;

import static edu.colorado.phet.gravityandorbits.GAOStrings.CARTOON;
import static edu.colorado.phet.gravityandorbits.GAOStrings.TO_SCALE;

/**
 * The main application for Gravity and Orbits.
 *
 * @see GravityAndOrbitsModule
 */
public class GravityAndOrbitsApplication extends PiccoloPhetApplication {
    public static final String PROJECT_NAME = "gravity-and-orbits";
    public static final PhetResources RESOURCES = new PhetResources( GravityAndOrbitsApplication.PROJECT_NAME );

    private final GravityAndOrbitsModule intro;
    private final GravityAndOrbitsModule toScale;
    private final Property<Boolean> whiteBackgroundProperty = new Property<Boolean>( false );

    public GravityAndOrbitsApplication( PhetApplicationConfig config ) {
        super( config );
        intro = new GravityAndOrbitsModule( getPhetFrame(), whiteBackgroundProperty, CARTOON, false, new Function1<ModeListParameter, ArrayList<GravityAndOrbitsMode>>() {
            public ArrayList<GravityAndOrbitsMode> apply( ModeListParameter p ) {
                return new CartoonModeList( p.clockPausedProperty, p.gravityEnabledProperty, p.stepping, p.rewinding, p.timeSpeedScaleProperty, 22.4 );
            }
        }, 0, false );
        addModule( intro );
        toScale = new GravityAndOrbitsModule( getPhetFrame(), whiteBackgroundProperty, TO_SCALE, true, new Function1<ModeListParameter, ArrayList<GravityAndOrbitsMode>>() {
            public ArrayList<GravityAndOrbitsMode> apply( ModeListParameter p ) {
                return new RealModeList( p.clockPausedProperty, p.gravityEnabledProperty, p.stepping, p.rewinding, p.timeSpeedScaleProperty );
            }
        }, 3, true );//Start Real tab in earth/satellite mode because it is more playful
        addModule( toScale );
        getPhetFrame().addMenu( new OptionsMenu() {{addWhiteBackgroundCheckBoxMenuItem( whiteBackgroundProperty );}} );
    }

    public GravityAndOrbitsModule getIntro() {
        return intro;
    }

    public GravityAndOrbitsModule getToScale() {
        return toScale;
    }

    public static void main( final String[] args ) throws ClassNotFoundException {
        new PhetApplicationLauncher().launchSim( args, PROJECT_NAME, GravityAndOrbitsApplication.class );
    }
}
