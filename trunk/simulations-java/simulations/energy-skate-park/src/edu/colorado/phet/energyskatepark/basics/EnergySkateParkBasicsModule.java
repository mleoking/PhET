// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkOptions;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.model.Planet;
import edu.colorado.phet.energyskatepark.serialization.EnergySkateParkIO;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.returnSkaterButton;

/**
 * Module for Energy Skate Park Basics.
 *
 * @author Sam Reid
 */
public class EnergySkateParkBasicsModule extends AbstractEnergySkateParkModule {
    public static final double INSET = 5;
    public final ControlPanelNode controlPanel;
    private final String PARABOLA_TRACK = "parabola";
    public static final Font CONTROL_FONT = new PhetFont( 15 );
    public static final Font TITLE_FONT = new PhetFont( Font.BOLD, 16 );

    //Flag to indicate that friction has been enabled
    public final BooleanProperty frictionEnabled = new BooleanProperty( false );

    //Flag to indicate whether the skater should stick to the track
    public final BooleanProperty stickToTrack = new BooleanProperty( true );

    //Property that indicates currently loaded track.
    public final Property<String> currentTrackFileName = new Property<String>( "" );

    public final Property<Double> frictionAmount = new Property<Double>( 0.0 );

    //Shift the tracks so they have a good location with respect to origin and control panels
    public static final Vector2D PARABOLA_OFFSET = new Vector2D( -0.5, 0 );
    public static final Vector2D DOUBLE_WELL_OFFSET = new Vector2D( 6.0 - 5.88033509545687 - 1.0, 0.02 );
    public static final Vector2D RAMP_TO_FLOOR_OFFSET = Vector2D.ZERO;

    //Developer flag to help create good initial conditions for the skater.
    private static final boolean SAMPLE_POSITIONS_FOR_INITIAL_CONDITIONS = false;

    public EnergySkateParkBasicsModule( IUserComponent userComponent, String name, final PhetFrame phetFrame, boolean splinesMovable ) {
        super( userComponent, name, phetFrame, new EnergySkateParkOptions(), splinesMovable,

               //Only bump up the splines if the user can move them
               splinesMovable,

               //Use the skater friction only in *Basics
               0.0,

               //No zoom controls in basics
               false,

               //Move the x-origin to the right so it doesn't overlap the thumbnails
               3.0, true, false );

        //Don't allow users to apply rocket force with the keyboard
        energySkateParkSimulationPanel.setThrustEnabled( false );

        //Show the sky for the earth background
        new Planet.Earth().apply( this );

        //Control panels are floating not docked
        setControlPanel( null );

        //When the user changes the "stick to track" setting, enable/disable the roller coaster mode
        stickToTrack.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean stickToTrack ) {
                getEnergySkateParkModel().setRollerCoasterMode( stickToTrack );
            }
        } );

        //Add the energy graph control panel
        controlPanel = createControlPanel();

        //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
        energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
            public void apply() {
                controlPanel.setOffset( energySkateParkSimulationPanel.getWidth() - controlPanel.getFullBounds().getWidth() - EnergySkateParkBasicsModule.INSET, EnergySkateParkBasicsModule.INSET );
            }
        } );

        energySkateParkSimulationPanel.getRootNode().controlLayer.addChild( controlPanel );

        //Move the legend out from behind the control panel
        energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
            public void apply() {
                energySkateParkSimulationPanel.getRootNode().getLegend().translate( -controlPanel.getFullBounds().getWidth() - INSET, 0 );
            }
        } );
    }

    protected ControlPanelNode createControlPanel() {
        return new ViewControlPanel( this );
    }

    //Load the specified track and set its roller coaster mode, used when the user presses different track selection buttons
    public void loadTrack( String trackName, Vector2D offset ) {
        EnergySkateParkIO.open( "energy-skate-park/tracks/basics/" + trackName + ".esp", this );
        currentTrackFileName.set( trackName );
        getEnergySkateParkModel().setRollerCoasterMode( stickToTrack.get() );

        //Move it so it sits on the ground with y=0
        adjustSplines( offset );

        //Initialize the skater beneath the track so the user has to move it to start the sim
        initSkater();
    }

    //Initialize the skater beneath the track so the user has to move it to start the sim
    public void initSkater() {
        getEnergySkateParkModel().getBody( 0 ).setPosition( 10, 0 );
        getEnergySkateParkModel().getBody( 0 ).setVelocity( 0, 0 );
        getEnergySkateParkModel().getBody( 0 ).setFrictionCoefficient( getCoefficientOfFriction() );

        //Make the "Return Skater" button return the skater to the original location, if the skater hasn't been moved yet
        getEnergySkateParkModel().getBody( 0 ).setRestorePoint( getEnergySkateParkModel().getBody( 0 ) );

        //Use this code to sample alpha positions from a spline to come up with good initial conditions
        if ( SAMPLE_POSITIONS_FOR_INITIAL_CONDITIONS ) {
            getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
                @Override public void preStep() {
                    System.out.println( getEnergySkateParkModel().getBody( 0 ).getSpline() + ", " + getEnergySkateParkModel().getBody( 0 ).getParticle().getParticle1D().getAlpha() );
                }
            } );
        }
        getEnergySkateParkModel().getBody( 0 ).switchToTrack( getEnergySkateParkModel().getFloor().getParametricFunction2D(), false, 0.36666702272486873 );
        getEnergySkateParkModel().getBody( 0 ).updateStateFromParticle();
    }

    protected void loadDefaultTrack() {

        //Move the pre-set parabola to the left so it doesn't encroach on the control panel
        //Interacts with grid location epsilon in EnergySkateParkRootNode
        loadTrack( PARABOLA_TRACK, PARABOLA_OFFSET );
    }

    //Move splines so they sit on the ground with y=0 and move by the specified x amount
    private void adjustSplines( Vector2D offset ) {
        for ( int i = 0; i < getEnergySkateParkModel().getNumSplines(); i++ ) {
            EnergySkateParkSpline spline = getEnergySkateParkModel().getSpline( i );
            spline.translate( offset.getX(), -spline.getMinControlPointY() + offset.getY() );
        }
    }

    //Show the "restart skater" and "reset all" button below the bottom control panel
    public void addResetAndRestartButtons( final PNode parent ) {

        //Add the "Restart skater" button
        final TextButtonNode restartSkaterButton = new TextButtonNode( EnergySkateParkResources.getString( "controls.reset-character" ) ) {{
            setUserComponent( returnSkaterButton );
            setFont( CONTROL_FONT );
            setBackground( Color.ORANGE );

            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    returnOrResetSkater();
                }
            } );
        }};

        //Add the "Reset all" button
        final TextButtonNode resetAllButton = new TextButtonNode( PhetCommonResources.getInstance().getLocalizedString( "ControlPanel.button.resetAll" ) ) {{
            setUserComponent( UserComponents.resetAllButton );
            setFont( CONTROL_FONT );
            setBackground( Color.YELLOW );

            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    reset();
                }
            } );
        }};

        //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
        energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
            public void apply() {
                restartSkaterButton.setOffset( controlPanel.getFullBounds().getCenterX() - restartSkaterButton.getFullBounds().getWidth() / 2, parent.getFullBounds().getMaxY() + INSET * 2 );
                resetAllButton.setOffset( controlPanel.getFullBounds().getCenterX() - resetAllButton.getFullBounds().getWidth() / 2, restartSkaterButton.getFullBounds().getMaxY() + INSET * 2 );
            }
        } );
        energySkateParkSimulationPanel.getRootNode().addChild( restartSkaterButton );
        energySkateParkSimulationPanel.getRootNode().addChild( resetAllButton );
    }

    //Show selection panel that allows the user to choose different tracks
    public void addTrackSelectionControlPanel() {
        final ControlPanelNode trackSelectionNode = new ControlPanelNode( new VBox(
                new TrackButton( this, PARABOLA_TRACK, PARABOLA_OFFSET ),
                new TrackButton( this, "rampToFloor", RAMP_TO_FLOOR_OFFSET ),

                //Move the double-well to the right so it will have its lowest peak directly on a discrete meter mark for the grid, but also make sure it doesn't overlap the control panel
                new TrackButton( this, "doubleWell", DOUBLE_WELL_OFFSET )
        ), EnergySkateParkLookAndFeel.backgroundColor ) {{

            //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
            energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
                public void apply() {
                    setOffset( INSET, INSET );
                }
            } );
        }};
        energySkateParkSimulationPanel.getRootNode().addChild( trackSelectionNode );
    }

    @Override public void reset() {
        super.reset();

        //The friction amount can only be restored if the friction is enabled, see TrackFrictionSlider
        frictionEnabled.set( true );
        frictionAmount.reset();

        frictionEnabled.reset();
        stickToTrack.reset();

        loadDefaultTrack();
    }
}