// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.simsharing.state.ImageFactory;
import edu.colorado.phet.common.phetcommon.simsharing.state.SimsharingApplication;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.gravityandorbits.module.CartoonModeList;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.colorado.phet.gravityandorbits.module.ModeListParameterList;
import edu.colorado.phet.gravityandorbits.module.RealModeList;
import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;

import static edu.colorado.phet.gravityandorbits.GAOSimSharing.UserComponents.cartoonTab;
import static edu.colorado.phet.gravityandorbits.GAOSimSharing.UserComponents.toScaleTab;
import static edu.colorado.phet.gravityandorbits.GAOStrings.CARTOON;
import static edu.colorado.phet.gravityandorbits.GAOStrings.TO_SCALE;

/**
 * The main application for Gravity and Orbits.
 *
 * @author Sam Reid
 * @see GravityAndOrbitsModule
 */
public class GravityAndOrbitsApplication extends PiccoloPhetApplication implements SimsharingApplication<GravityAndOrbitsApplicationState> {
    public static final String PROJECT_NAME = "gravity-and-orbits";
    public static final PhetResources RESOURCES = new PhetResources( GravityAndOrbitsApplication.PROJECT_NAME );

    private final GravityAndOrbitsModule intro;
    private final GravityAndOrbitsModule toScale;
    private final Property<Boolean> whiteBackgroundProperty = new Property<Boolean>( false );

    //For simsharing
    private final ImageFactory imageFactory = new ImageFactory();

    //Some features are disabled in teacher mode, such as showing the diameter of spherical bodies, since it is too expensive
    public static boolean teacherMode = false;
    private int index = 0;

    public GravityAndOrbitsApplication( PhetApplicationConfig config ) {
        super( config );
        //Modules are stored so the data can be read and set for simsharing
        intro = new IntroModule( getPhetFrame(), whiteBackgroundProperty );
        addModule( intro );

        toScale = new CartoonModule( getPhetFrame(), whiteBackgroundProperty );
        addModule( toScale );

        getPhetFrame().addMenu( new OptionsMenu() {{addWhiteBackgroundCheckBoxMenuItem( whiteBackgroundProperty );}} );
    }

    public void setTeacherMode( boolean b ) {
        teacherMode = b;
        intro.setTeacherMode( b );
        toScale.setTeacherMode( b );
    }

    public GravityAndOrbitsApplicationState getState() {
        return new GravityAndOrbitsApplicationState( this, imageFactory, index++ );
    }

    public void setState( GravityAndOrbitsApplicationState state ) {
        state.apply( this );
    }

    public void addModelSteppedListener( final VoidFunction0 updateSharing ) {

        getIntro().addModelSteppedListener( new SimpleObserver() {
            public void update() {
                updateSharing.apply();
            }
        } );
        getToScale().addModelSteppedListener( new SimpleObserver() {
            public void update() {
                updateSharing.apply();
            }
        } );
    }

    public boolean isPaused() {
        return getIntro().modeProperty.get().getModel().getClock().isPaused();
    }

    public void setPlayButtonPressed( boolean b ) {
        getIntro().playButtonPressed.set( true );
    }

    public static class IntroModule extends GravityAndOrbitsModule {
        public IntroModule( final PhetFrame phetFrame, Property<Boolean> whiteBackgroundProperty ) {
            super( cartoonTab, phetFrame, whiteBackgroundProperty, CARTOON, false, new Function1<ModeListParameterList, ArrayList<GravityAndOrbitsMode>>() {
                public ArrayList<GravityAndOrbitsMode> apply( ModeListParameterList p ) {
                    return new CartoonModeList( p.playButtonPressed, p.gravityEnabled, p.stepping, p.rewinding, p.timeSpeedScale );
                }
            }, 0, false );
        }
    }

    public static class CartoonModule extends GravityAndOrbitsModule {
        public CartoonModule( final PhetFrame phetFrame, Property<Boolean> whiteBackgroundProperty ) {
            super( toScaleTab, phetFrame, whiteBackgroundProperty, TO_SCALE, true, new Function1<ModeListParameterList, ArrayList<GravityAndOrbitsMode>>() {
                public ArrayList<GravityAndOrbitsMode> apply( ModeListParameterList p ) {
                    return new RealModeList( p.playButtonPressed, p.gravityEnabled, p.stepping, p.rewinding, p.timeSpeedScale );
                }
            }, 3,//Start Real tab in earth/satellite mode because it is more playful
                   true );
        }
    }

    public GravityAndOrbitsModule getIntro() {
        return intro;
    }

    public GravityAndOrbitsModule getToScale() {
        return toScale;
    }

    //Main method for the sim
    public static void main( final String[] args ) throws ClassNotFoundException {
        new PhetApplicationLauncher().launchSim( args, PROJECT_NAME, GravityAndOrbitsApplication.class );
    }
}