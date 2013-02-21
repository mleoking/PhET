package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;
import fj.function.Doubles;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelComponentTypes;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.SpeedometerNode;
import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Strings;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.ModelComponents;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.ParameterKeys;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents;
import edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas;
import edu.colorado.phet.forcesandmotionbasics.common.ForceArrowNode;
import edu.colorado.phet.forcesandmotionbasics.common.TextLocation;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.ForcesNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.v;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendModelMessage;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendUserMessage;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.isSelected;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes.button;
import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createIdentity;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToHeight;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsApplication.BROWN;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsApplication.TOOLBOX_COLOR;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents.accelerometerCheckBoxIcon;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents.showForcesCheckBoxIcon;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents.speedCheckBoxIcon;
import static edu.colorado.phet.forcesandmotionbasics.motion.StackableNode._isOnSkateboard;
import static edu.colorado.phet.forcesandmotionbasics.motion.StackableNode._mass;
import static fj.data.Option.some;
import static fj.function.Doubles.add;

/**
 * Canvas for tab 2 "Motion" and tab 3 "Friction", shows everything as piccolo nodes (including the control panels).
 *
 * @author Sam Reid
 */
public class MotionCanvas extends AbstractForcesAndMotionBasicsCanvas implements StackableNodeContext {

    private final BooleanProperty showSpeedometer = new BooleanProperty( false );
    private final BooleanProperty showAccelerometer = new BooleanProperty( false );
    private final Property<Boolean> showValues = new Property<Boolean>( false );
    private final BooleanProperty showForces = new BooleanProperty( true );
    private final PNode skateboard;
    private final List<StackableNode> stackableNodes;
    private final Property<List<StackableNode>> stack = new Property<List<StackableNode>>( List.<StackableNode>nil() );
    private final boolean friction;

    private final MotionModel model;

    private static final int FRIDGE_OFFSET_WITHIN_SKATEBOARD = 21;
    private static final int CRATE_OFFSET_WITHIN_SKATEBOARD = 27;
    private final PNode forcesNode;

    private static final double typicalDT = 0.033333333333333215;

    //Speed at which the pusher should fall down, should be not much more than the strobe speed
    public static final double MAX_SPEED = 10.0 * 2.0;

    private final BooleanProperty playing = new BooleanProperty( true );
    private final PusherNode pusherNode;

    //Features only for Tab 3: Friction:
    private final BooleanProperty showSumOfForces = new BooleanProperty( false );
    private final BooleanProperty dragging = new BooleanProperty( false );
    private int lastNumSpecks = -1;
    private final boolean accelerometer;
    private final WaterBucketNode bucket;
    private final StackableNode fridge;
    private final StackableNode man;

    //For clouds
    private Image cloudTexture;
    private BufferedImage txtr;

    public MotionCanvas( final Resettable moduleContext, final IClock clock,

                         //True if tab 3 "friction"
                         final boolean friction,

                         //True if tab 4 "acceleration"
                         final boolean accelerometer ) {
        this.accelerometer = accelerometer;
        this.friction = friction;
        final CompositeDoubleProperty massOfObjectsOnSkateboard = new CompositeDoubleProperty( new Function0<Double>() {
            public Double apply() {
                return getMassOfObjectsOnSkateboard();
            }
        }, stack );
        this.model = new MotionModel( friction, massOfObjectsOnSkateboard );

        setBackground( BROWN );
        //use view coordinates since nothing compex happening in model coordinates.

        //for a canvas height of 710, the ground is at 452 down from the top
        final int width = 10000;

        //Reverse bottom and top because using view coordinates
        final int grassY = 425;
        addChild( new SkyNode( createIdentity(), new Rectangle2D.Double( -width / 2, -width / 2 + grassY, width, width / 2 ), grassY, SkyNode.DEFAULT_TOP_COLOR, SkyNode.DEFAULT_BOTTOM_COLOR ) );

        final BufferedImage mountains = new BufferedImage( 3250, Images.MOUNTAINS.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D mountainGraphics = mountains.createGraphics();
        mountainGraphics.drawRenderedImage( Images.MOUNTAINS, new AffineTransform() );
        mountainGraphics.dispose();

        //Development flag for testing performance with and without clouds
        final boolean showClouds = false;
        if ( showClouds ) {
            PNode clouds = new PNode() {{
                addChild( new HBox( 245, new PImage( Images.CLOUD1 ) {{
                    scale( 0.6 );
                }}, new PhetPPath( new Rectangle2D.Double( 0, 0, 1, 1 ), new Color( 0, 0, 0, 0 ) ) {{setVisible( false );}}
                ) );
            }};
            cloudTexture = clouds.toImage();
            txtr = BufferedImageUtils.toBufferedImage( cloudTexture );
        }
        PNode terrainLayer = new PNode() {
            {
                model.position.addObserver( new VoidFunction1<Double>() {
                    public void apply( final Double position ) {
                        updateBackgroundLayer( position );
                    }
                } );
                model.frictionValue.addObserver( new VoidFunction1<Double>() {
                    public void apply( final Double friction ) {
                        updateBackgroundLayer( model.position.get() );
                    }
                } );
            }

            private void updateBackgroundLayer( final Double position ) {
                removeAllChildren();

                //Show mountains as additional texture
                {
                    final double scale = 0.25;
                    final Rectangle2D.Double area = new Rectangle2D.Double( -STAGE_SIZE.width / scale, grassY / scale - mountains.getHeight(), STAGE_SIZE.width * 3 / scale, mountains.getHeight() );
                    final Rectangle2D.Double anchor = new Rectangle2D.Double( -position * 100 / scale / 50.0 + 150, area.getY() - 1, mountains.getWidth(), mountains.getHeight() );
                    PhetPPath path = new PhetPPath( area, new TexturePaint( mountains, anchor ) ) {{
                        scale( scale );
                    }};
                    path.setTransparency( 0.6f );
                    addChild( path );
                }

                //Extend the brick region an extra stage width to the left and right, in case it is a very odd aspect ratio.  (But no support for showing wider than that).
                {
                    final double brickScale = 0.4;
                    final Rectangle2D.Double area = new Rectangle2D.Double( -STAGE_SIZE.width / brickScale, grassY / brickScale, STAGE_SIZE.width * 3 / brickScale, Images.BRICK_TILE.getHeight() );
                    final Rectangle2D.Double anchor = new Rectangle2D.Double( -position * 100 / brickScale, area.getY(), Images.BRICK_TILE.getWidth(), Images.BRICK_TILE.getHeight() );
                    PhetPPath path = new PhetPPath( area, new TexturePaint( Images.BRICK_TILE, anchor ) ) {{
                        scale( brickScale );
                    }};
                    addChild( path );
                }

                //Show ice overlay
                if ( friction && model.frictionValue.get() == 0 ) {
                    final double iceScale = 1;
                    final Rectangle2D.Double area = new Rectangle2D.Double( -STAGE_SIZE.width / iceScale, grassY / iceScale - 1, STAGE_SIZE.width * 3 / iceScale, Images.ICE_OVERLAY.getHeight() );
                    final Rectangle2D.Double anchor = new Rectangle2D.Double( -position * 100 / iceScale, area.getY(), Images.ICE_OVERLAY.getWidth(), Images.ICE_OVERLAY.getHeight() );
                    PhetPPath path = new PhetPPath( area, new TexturePaint( Images.ICE_OVERLAY, anchor ) ) {{
                        scale( iceScale );
                    }};
                    path.setTransparency( 0.9f );
                    addChild( path );
                }

                //Show clouds
                if ( showClouds ) {
                    final Rectangle2D.Double area = new Rectangle2D.Double( -STAGE_SIZE.width, -50, STAGE_SIZE.width * 3, cloudTexture.getHeight( null ) );
                    final Rectangle2D.Double anchor = new Rectangle2D.Double( -position * 10, area.getY(), cloudTexture.getWidth( null ), cloudTexture.getHeight( null ) );

                    addChild( new PhetPPath( area, new TexturePaint( txtr, anchor ) ) );
                }

                //Show gravel overlay
                if ( friction && model.frictionValue.get() > 0 ) {
                    final double gravelScale = 1;
                    updateGravelImage();
                    final BufferedImage image = gravelImage;

                    //Move the gravel up so it is slightly above ground
                    final Rectangle2D.Double area = new Rectangle2D.Double( -STAGE_SIZE.width / gravelScale, grassY / gravelScale - 1.75, STAGE_SIZE.width * 3 / gravelScale, image.getHeight() );
                    final Rectangle2D.Double anchor = new Rectangle2D.Double( -position * 100 / gravelScale, area.getY(), image.getWidth(), image.getHeight() );
                    PhetPPath path = new PhetPPath( area, new TexturePaint( image, anchor ) ) {{ scale( gravelScale ); }};
                    addChild( path );
                }
            }
        };
        addChild( terrainLayer );

        final JCheckBox showForcesCheckBox = new PropertyCheckBox( UserComponents.forcesCheckBox, friction ? Strings.FORCES : Strings.FORCE, showForces ) {{
            setFont( DEFAULT_FONT );
        }};
        final JCheckBox showValuesCheckBox = new PropertyCheckBox( UserComponents.valuesCheckBox, Strings.VALUES, showValues ) {{
            setFont( DEFAULT_FONT );
        }};
        final JCheckBox showSumOfForcesCheckBox = new PropertyCheckBox( UserComponents.sumOfForcesCheckBox, Strings.SUM_OF_FORCES, showSumOfForces ) {{
            setFont( DEFAULT_FONT );
        }};
        showForces.addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean showForces ) {
                showValuesCheckBox.setEnabled( showForces );
                showSumOfForcesCheckBox.setEnabled( showForces );
            }
        } );
        final JCheckBox speedCheckBox = new PropertyCheckBox( UserComponents.speedCheckBox, Strings.SPEED, showSpeedometer ) {{
            setFont( DEFAULT_FONT );
        }};
        final BooleanProperty showMasses = new BooleanProperty( false );
        final JCheckBox massCheckBox = new PropertyCheckBox( UserComponents.massCheckBox, Strings.MASSES, showMasses ) {{
            setFont( DEFAULT_FONT );
        }};

        final PNode speedControlPanel = new HBox( 15, new PSwing( speedCheckBox ), new SpeedometerNode( Strings.SPEED, 125, model.speed, MAX_SPEED ) {{
            scale( 0.25 );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {
                    sendUserMessage( speedCheckBoxIcon, button, pressed, parameterSet( isSelected, !showSpeedometer.get() ) );
                    showSpeedometer.toggle();
                }
            } );
        }} );
        final PNode showForcesPanel = new HBox( 15, new PSwing( showForcesCheckBox ), new ForceArrowNode( false, ZERO, 20 * 5, "", ForcesNode.APPLIED_FORCE_COLOR, TextLocation.SIDE, false ) {{
            scale( 0.4 );
            setPickable( true );
            setChildrenPickable( true );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {
                    sendUserMessage( showForcesCheckBoxIcon, button, pressed, parameterSet( isSelected, !showForces.get() ) );
                    showForces.toggle();
                }
            } );
            addInputEventListener( new CursorHandler() );
        }} );

        final PNode showSumOfForcesPanel = new HBox( 15, new PSwing( showSumOfForcesCheckBox )
//                , new ForceArrowNode( false, ZERO, 20, "", ForcesNode.SUM_OF_FORCES_COLOR, TextLocation.SIDE, false ) {{
//            scale( 0.4 );
//            setPickable( true );
//            setChildrenPickable( true );
//            addInputEventListener( new PBasicInputEventHandler() {
//                @Override public void mousePressed( final PInputEvent event ) {
//                    sendUserMessage( showSumOfForcesCheckBoxIcon, button, pressed, parameterSet( isSelected, !showSumOfForces.get() ) );
//                    showSumOfForces.toggle();
//                }
//            } );
//            addInputEventListener( new CursorHandler() );
//        }}
        );
        final VBox vbox = friction ?
                          ( accelerometer ?
                            new VBox( 0, VBox.LEFT_ALIGNED, showForcesPanel, indent( showSumOfForcesPanel ), indent( showValuesCheckBox ),
                                      new PSwing( massCheckBox ),
                                      speedControlPanel,
                                      createAccelerometerCheckBox(),
                                      new FrictionSliderControl( model.frictionValue ) ) :
                            new VBox( 0, VBox.LEFT_ALIGNED, showForcesPanel, indent( showSumOfForcesPanel ), indent( showValuesCheckBox ),
                                      new PSwing( massCheckBox ),
                                      speedControlPanel,
                                      new FrictionSliderControl( model.frictionValue ) )
                          ) :
                          new VBox( 0, VBox.LEFT_ALIGNED, showForcesPanel, indent( showValuesCheckBox ),
                                    new PSwing( massCheckBox ),
                                    speedControlPanel );

        final ControlPanelNode controlPanelNode = new ControlPanelNode( vbox, new Color( 227, 233, 128 ), new BasicStroke( 2 ), Color.black );
        controlPanelNode.setOffset( STAGE_SIZE.width - controlPanelNode.getFullWidth() - INSET, INSET );
        addChild( controlPanelNode );

        addChild( new ResetAllButtonNode( new Resettable() {
            public void reset() {
                moduleContext.reset();
            }
        }, this, DEFAULT_FONT, Color.black, Color.orange ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getMaxY() + INSET );
            setConfirmationEnabled( false );
        }} );

        skateboard = friction ?
                     new PhetPPath( new Rectangle( 0, 0, Images.SKATEBOARD.getWidth(), 1 ), new Color( 0, 0, 0, 0 ), null, null ) :
                     new PImage( Images.SKATEBOARD );
        skateboard.setScale( 0.85 );
        skateboard.setOffset( STAGE_SIZE.getWidth() / 2 - skateboard.getFullBounds().getWidth() / 2, grassY - skateboard.getFullBounds().getHeight() );

        final RoundRectangle2D.Double toolboxShape = new RoundRectangle2D.Double( 0, 0, 270, 170, 20, 20 );
        PhetPPath leftToolbox = new PhetPPath( toolboxShape, TOOLBOX_COLOR, new BasicStroke( 1 ), Color.black );
        final double toolboxY = STAGE_SIZE.height - INSET - leftToolbox.getFullBounds().getHeight();
        leftToolbox.setOffset( INSET, toolboxY );
        addChild( leftToolbox );

        PhetPPath rightToolbox = new PhetPPath( toolboxShape, TOOLBOX_COLOR, new BasicStroke( 1 ), Color.black );
        rightToolbox.setOffset( STAGE_SIZE.width - INSET - rightToolbox.getFullBounds().getWidth(), toolboxY );
        addChild( rightToolbox );

        final AppliedForceSliderControl appliedForceSliderControl = new AppliedForceSliderControl( model.speedValue, model.appliedForce, stack, friction, playing, model );
        appliedForceSliderControl.setOffset( STAGE_SIZE.getWidth() / 2 - appliedForceSliderControl.getFullBounds().getWidth() / 2, grassY + 50 );
        addChild( appliedForceSliderControl );

        pusherNode = new PusherNode( model.fallen, skateboard, grassY, model.appliedForce, stack, model.speedValue, model.velocity, playing, model.movedSliderOnce );
        addChild( pusherNode );

        addChild( skateboard );

        final PlayPauseButton playPauseButton = new PlayPauseButton( 70 ) {{
            addListener( new Listener() {
                public void playbackStateChanged() {
                    MotionCanvas.this.playbackStateChanged( isPlaying() );
                }
            } );
        }};
        HBox timeControls = new HBox( -3,


//        new RewindButton( 60 ) {{
//            addListener( new Listener() {
//                public void buttonPressed() {
//                    rewind();
//                }
//            } );
//        }},

                                      playPauseButton
//                , new StepButton( 60 ) {{
//            addListener( new Listener() {
//                public void buttonPressed() {
//                    step( typicalDT * 5 );
//                }
//            } );
//
//            //Only enable the step button when the sim is paused
//            playing.addObserver( new VoidFunction1<Boolean>() {
//                public void apply( final Boolean playing ) {
//                    setEnabled( !playing );
//                }
//            } );
//        }}
        );
        timeControls.setOffset( STAGE_SIZE.width / 2 - playPauseButton.getFullBounds().getWidth() / 2, STAGE_SIZE.height - timeControls.getFullHeight() );
        addChild( timeControls );

        fridge = new StackableNode( UserComponents.fridge, this, Images.FRIDGE, 200, FRIDGE_OFFSET_WITHIN_SKATEBOARD, showMasses );
        StackableNode crate1 = new StackableNode( UserComponents.crate1, this, multiScaleToHeight( Images.CRATE, (int) ( 75 * 1.2 ) ), 50, CRATE_OFFSET_WITHIN_SKATEBOARD, showMasses );
        StackableNode crate2 = new StackableNode( UserComponents.crate2, this, multiScaleToHeight( Images.CRATE, (int) ( 75 * 1.2 ) ), 50, CRATE_OFFSET_WITHIN_SKATEBOARD, showMasses );

        double INTER_OBJECT_SPACING = 10;
        fridge.setInitialOffset( leftToolbox.getFullBounds().getX() + 10, leftToolbox.getFullBounds().getCenterY() - fridge.getFullBounds().getHeight() / 2 );
        crate1.setInitialOffset( fridge.getObjectMaxX() + INTER_OBJECT_SPACING, fridge.getFullBounds().getMaxY() - crate1.getFullBounds().getHeight() );
        crate2.setInitialOffset( crate1.getObjectMaxX() + INTER_OBJECT_SPACING, fridge.getFullBounds().getMaxY() - crate2.getFullBounds().getHeight() );

        addChild( fridge );
        addChild( crate1 );
        addChild( crate2 );

        //Weight for humans (but remember to round off the values to nearest 10 to make it easier to read): http://www.cdc.gov/growthcharts/data/set1clinical/cj41l021.pdf
        StackableNode girl = new StackableNode( UserComponents.girl, this, multiScaleToHeight( Images.GIRL_SITTING, 100 ), 40, friction ? 38 : 47, showMasses, true, multiScaleToHeight( Images.GIRL_STANDING, 150 ), multiScaleToHeight( Images.GIRL_HOLDING, 100 ) );
        final int manHeight = (int) ( 200 / 150.0 * 100.0 );
        man = new StackableNode( UserComponents.man, this, multiScaleToHeight( Images.MAN_SITTING, manHeight ), 80, 38, showMasses, true, multiScaleToHeight( Images.MAN_STANDING, 200 ), multiScaleToHeight( Images.MAN_HOLDING, manHeight ) );

        StackableNode trash = new StackableNode( UserComponents.trash, this, multiScaleToHeight( Images.TRASH_CAN, (int) ( 150 * 2.0 / 3.0 ) ), 50, 47, showMasses );
        bucket = new WaterBucketNode( UserComponents.bucket, this, multiScaleToHeight( Images.WATER_BUCKET, (int) ( 150 * 2.0 / 3.0 ) ), 50, 28, showMasses, model.acceleration );
        StackableNode gift = new StackableNode( UserComponents.gift, this, multiScaleToHeight( Images.MYSTERY_OBJECT_01, 60 ), 50, 40, showMasses ) {
            @Override protected Pair<Integer, String> getMassDisplayString( final double mass ) {

                //Add some padding on either side of the "?" for the gift to make it easier to read
                return new Pair<Integer, String>( 12, "?" );
            }
        };

        //Space things more evenly in the "acceleration" tab.
        double spacingX = accelerometer ? 29 : 18;
        girl.setInitialOffset( rightToolbox.getFullBounds().getX() + spacingX, rightToolbox.getFullBounds().getMaxY() - girl.getFullBounds().getHeight() - 5 );
        man.setInitialOffset( girl.getObjectMaxX() + spacingX, rightToolbox.getFullBounds().getMaxY() - man.getFullBounds().getHeight() - 5 );

        StackableNode trashOrBucket = accelerometer ? bucket : trash;
        trashOrBucket.setInitialOffset( man.getObjectMaxX() + spacingX, rightToolbox.getFullBounds().getMaxY() - trashOrBucket.getFullBounds().getHeight() - 5 );
        gift.setInitialOffset( trashOrBucket.getObjectMaxX() + spacingX - 5, rightToolbox.getFullBounds().getMaxY() - gift.getFullBounds().getHeight() - 5 );
        addChild( girl );
        addChild( man );
        addChild( trashOrBucket );

        stackableNodes = accelerometer ? fj.data.List.list( fridge, crate1, crate2, man, girl, trashOrBucket ) :
                         fj.data.List.list( fridge, crate1, crate2, man, girl, trashOrBucket, gift );

        //The bucket is so big that it should replace both the trash can and the gift on the acceleration tab.
        if ( !accelerometer ) {
            addChild( gift );
        }

        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                if ( playing.get() ) {
                    step( clockEvent.getSimulationTimeChange() );
                }
                model.clockStepped();
            }
        } );

        forcesNode = new PNode() {{
            final SimpleObserver updateForces = new SimpleObserver() {
                public void update() {
                    removeAllChildren();
                    if ( showForces.get() ) {
                        final double tailY = skateboard.getFullBounds().getCenterY() - 75;
                        final double centerX = skateboard.getFullBounds().getCenterX();

                        //separate arrow tails for friction vs applied
                        final int appliedForceOffsetX = friction && model.frictionForce.get() != 0 ? 3 * MathUtil.getSign( model.appliedForce.get() ) : 0;
                        final ForceArrowNode appliedForceArrowNode = new ForceArrowNode( false, v( centerX + appliedForceOffsetX, tailY ),
                                                                                         model.appliedForce.get(), Strings.APPLIED_FORCE, ForcesNode.APPLIED_FORCE_COLOR, TextLocation.SIDE, showValues.get() );
                        addChild( appliedForceArrowNode );

                        final int frictionForceOffsetX = friction ? 3 * MathUtil.getSign( model.frictionForce.get() ) : 0;
                        addChild( new ForceArrowNode( false, v( centerX + frictionForceOffsetX, tailY ),
                                                      model.frictionForce.get(), Strings.FRICTION_FORCE, Color.red, TextLocation.SIDE, showValues.get(), some( appliedForceArrowNode ) ) );
                        if ( friction && showSumOfForces.get() ) {
                            addChild( new ForceArrowNode( false, v( centerX, tailY - 70 ),
                                                          model.sumOfForces.get(), Strings.SUM_OF_FORCES, ForcesNode.SUM_OF_FORCES_COLOR, TextLocation.TOP, showValues.get() ) );
                        }
                    }
                }
            };
            showValues.addObserver( updateForces );
            model.appliedForce.addObserver( updateForces );
            model.frictionForce.addObserver( updateForces );
            model.sumOfForces.addObserver( updateForces );
            showForces.addObserver( updateForces );
            showSumOfForces.addObserver( updateForces );
        }};
        addChild( forcesNode );

        final SpeedometerNode speedometerNode = new SpeedometerNode( Strings.SPEED, 125, model.speed, MAX_SPEED ) {{
            showSpeedometer.addObserver( new VoidFunction1<Boolean>() {
                public void apply( final Boolean show ) {
                    setVisible( show );
                }
            } );

            //scale up so fonts and stroke thicknesses look good
            scale( 1.25 );
        }};
        addChild( speedometerNode );

        PNode accelerometerNode = new PNode() {{
            final AccelerometerNode a = new AccelerometerNode( model.acceleration );
            addChild( new VBox( 0, new PhetPText( Strings.ACCELERATION, new PhetFont( (int) ( 16 * 1.25 ) ) ), a ) );
            addChild( new PhetPText( "-20", new PhetFont( 15 ) ) {{
                setOffset( a.ticks.get( 0 ).getGlobalFullBounds().getCenterX() - getFullBounds().getWidth() / 2, a.getGlobalFullBounds().getMaxY() );
            }} );
            addChild( new PhetPText( "0", new PhetFont( 15 ) ) {{
                setOffset( a.ticks.get( 2 ).getGlobalFullBounds().getCenterX() - getFullBounds().getWidth() / 2, a.getGlobalFullBounds().getMaxY() );
            }} );
            addChild( new PhetPText( "20", new PhetFont( 15 ) ) {{
                setOffset( a.ticks.get( 4 ).getGlobalFullBounds().getCenterX() - getFullBounds().getWidth() / 2, a.getGlobalFullBounds().getMaxY() );
            }} );
            showAccelerometer.addObserver( new VoidFunction1<Boolean>() {
                public void apply( final Boolean show ) {
                    setVisible( show );
                }
            } );
        }};
        addChild( accelerometerNode );

        addChild( new VBox( 0, speedometerNode, accelerometerNode ) {{
            setOffset( STAGE_SIZE.width / 2 - getFullBounds().getWidth() / 2, 2 );

            stack.addObserver( new VoidFunction1<List<StackableNode>>() {
                public void apply( final List<StackableNode> stackableNodes ) {

                    //If the stack is too high, move the sensors to the side
                    if ( stackableNodes.length() >= 3 || ( stackableNodes.length() == 2 && stackContainsFridgeOrMan() ) ) {
                        animateToPositionScaleRotation( STAGE_SIZE.width / 2 - getFullBounds().getWidth() / 2 - getFullBounds().getWidth(), 2, 1, 0, 200 );
                    }
                    else {
                        animateToPositionScaleRotation( STAGE_SIZE.width / 2 - getFullBounds().getWidth() / 2, 2, 1, 0, 200 );
                    }
                }
            } );
        }} );

        //If the user removes all the objects, the applied forces should be set to zero.
        //Necessary because the user can apply a constant force by using the text box
        //In the friction tab, also stop the background motion
        stack.addObserver( new VoidFunction1<List<StackableNode>>() {
            public void apply( final List<StackableNode> stackableNodes ) {
                if ( stackableNodes.length() == 0 ) {
                    model.appliedForce.set( 0.0 );
                    model.velocity.set( 0.0 );
                }
            }
        } );

        stack.addObserver( new VoidFunction1<List<StackableNode>>() {
            public void apply( final List<StackableNode> stackableNodes ) {
                sendModelMessage( ModelComponents.stack, ModelComponentTypes.modelElement, ModelActions.changed, parameterSet( ParameterKeys.mass, getMassOfObjectsOnSkateboard() ).with( ParameterKeys.items, stackToString( stackableNodes ) ) );
            }
        } );
    }

    private boolean stackContainsFridgeOrMan() {
        return isInStack( this.fridge ) || isInStack( this.man );
    }

    //Creates the check box for turning the accelerometer on and off, only for the "acceleration" tab.
    private PNode createAccelerometerCheckBox() {
        final JCheckBox checkBox = new PropertyCheckBox( UserComponents.accelerometerCheckBox, Strings.ACCELERATION, showAccelerometer ) {{
            setFont( DEFAULT_FONT );
        }};
        return new HBox( 15, new PSwing( checkBox ), new AccelerometerNode( model.acceleration ) {{
            scale( 0.25 );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {
                    sendUserMessage( accelerometerCheckBoxIcon, button, pressed, parameterSet( isSelected, !showAccelerometer.get() ) );
                    showAccelerometer.toggle();
                }
            } );
        }} );
    }

    static final BufferedImage CLEAR = new BufferedImage( Images.ICE_OVERLAY.getWidth() / 8, 6, BufferedImage.TYPE_INT_ARGB_PRE );
    BufferedImage gravelImage = new BufferedImage( CLEAR.getWidth(), CLEAR.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );

    private static final Random random = new Random();

    private void updateGravelImage() {
        LinearFunction linearFunction = new LinearFunction( FrictionSliderControl.MAX * 0.1, FrictionSliderControl.MAX, 0, 500 * 1.15 );
        final int numSpecks = (int) linearFunction.evaluate( model.frictionValue.get() );
        //Save computation, esp. for older machines
        if ( numSpecks == lastNumSpecks ) {return;}

        //Clear a transparent buffered image as fast as possible: http://stackoverflow.com/questions/2367365/clear-a-transparent-bufferedimage-as-fast-as-possible
        gravelImage.setData( CLEAR.getRaster() );
        Graphics2D g2 = gravelImage.createGraphics();
        random.setSeed( 0L );

        g2.setPaint( Color.black );
        final int width = gravelImage.getWidth();
        final int height = gravelImage.getHeight();
        for ( int i = 0; i < numSpecks / 2; i++ ) {
            g2.fillRect( random.nextInt( width ), random.nextInt( height ), 1, 1 );
        }

        g2.setPaint( Color.darkGray );
        for ( int i = 0; i < numSpecks / 2; i++ ) {
            g2.fillRect( random.nextInt( width ), random.nextInt( height ), 1, 1 );
        }

        //Add white dots in the ratio 1:5
        random.setSeed( 11L );
        g2.setPaint( Color.white );
        for ( int i = 0; i < numSpecks / 10; i++ ) {
            g2.fillRect( random.nextInt( width ), random.nextInt( height ), 1, 1 );
        }

        g2.dispose();

        lastNumSpecks = numSpecks;
    }

    //Avoid an empty string in a parameter value by listing the ground or skateboard first
    private String stackToString( final List<StackableNode> stackableNodes ) {
        return stackableNodes.foldLeft( new F2<String, StackableNode, String>() {
            @Override public String f( final String s, final StackableNode stackableNode ) {
                return s + ", " + stackableNode.component.toString();
            }
        }, friction ? "ground" : "skateboard" );
    }

    private PNode indent( final JCheckBox component ) {
        return indent( new PSwing( component ) );
    }

    private PNode indent( final PNode component ) {
        return new HBox( 15, new PhetPPath( new Rectangle( 0, 0, 1, 1 ), new Color( 0, 0, 0, 0 ) ), component );
    }

    private void step( double dt ) {
        final boolean exceedsStrobeSpeedBefore = model.speed.get().get() >= MAX_SPEED;
        model.stepInTime( dt );
        final boolean exceedsStrobeSpeedAfter = model.speed.get().get() >= MAX_SPEED;

        if ( model.appliedForce.get() == 0.0 || exceedsStrobeSpeedBefore ) {
            final double delta = -model.velocity.get() * dt * 100;
            pusherNode.setOffset( pusherNode.getOffset().getX() + delta, pusherNode.getOffset().getY() );
        }

        //Show the pusher as fallen if they have exceeded the speed threshold within 1 second, or if the applied force is zero and fallen flag is set (which means instantaneous exceed of threshold in previous time step)
        if ( exceedsStrobeSpeedBefore || exceedsStrobeSpeedAfter || ( friction && model.speedValue.get() != SpeedValue.WITHIN_ALLOWED_RANGE ) ) {
            model.fallen.set( true );
        }
        else {
            model.fallen.set( false );
        }
        bucket.stepInTime();
    }

    private void playbackStateChanged( final boolean playing ) {
        this.playing.set( playing );
    }

//    private void rewind() {
//        model.rewind();
//        model.fallen.set( false );
//        pusherNode.setOffset( 0, 0 );
//    }

    //Get the mass of the stack, returning 0.0 if the stack not yet initialized (for auto callback in constructor)
    private double getMassOfObjectsOnSkateboard() {
        return stackableNodes == null ? 0.0 : stackableNodes.filter( _isOnSkateboard ).map( _mass ).foldLeft( add, 0.0 );
    }

    public void stackableNodeDropped( final StackableNode stackableNode ) {
        PBounds bounds = skateboard.getGlobalFullBounds();
        bounds.add( skateboard.getGlobalFullBounds().getCenterX(), rootNode.globalToLocal( new Point2D.Double( STAGE_SIZE.width / 2, 0 ) ).getY() );
        if ( stackableNode.getGlobalFullBounds().getMinY() < skateboard.getGlobalFullBounds().getMaxY() &&

             //Limit stack size to 3 things
             stack.get().length() < 3 ) {

            stackableNode.onSkateboard.set( true );
            stack.set( stack.get().snoc( stackableNode ) );
            normalizeStack();
        }
        else {
            stackableNode.animateHome();
        }
        dragging.set( false );
    }

    public void stackableNodePressed( final StackableNode stackableNode ) {
        dragging.set( true );
        if ( stackableNode.onSkateboard.get() ) {
            final double dx = stackableNode.getFullBounds().getWidth() / 4;
            stackableNode.translate( dx, dx );
        }
        stackableNode.moveToFront();
        nodeMovedToFront();
        stackableNode.onSkateboard.set( false );
        stack.set( stack.get().filter( new F<StackableNode, Boolean>() {
            @Override public Boolean f( final StackableNode element ) {
                return element != stackableNode;
            }
        } ) );

        //Any other objects above it should fall down.
        normalizeStack();
    }

    public DoubleProperty getAppliedForce() { return model.appliedForce; }

    public BooleanProperty getUserIsDraggingSomething() { return dragging; }

    public boolean isInStackButNotInTop( final StackableNode s ) {
        return stack.get().reverse().tail().exists( new F<StackableNode, Boolean>() {
            @Override public Boolean f( final StackableNode stackableNode ) {
                return stackableNode == s;
            }
        } );
    }

    public boolean isInStack( final StackableNode node ) {
        return stack.get().exists( new F<StackableNode, Boolean>() {
            @Override public Boolean f( final StackableNode stackableNode ) {
                return stackableNode == node;
            }
        } );
    }

    public void addStackChangeListener( final SimpleObserver observer ) {
        stack.addObserver( observer );
    }

    public int getStackSize() { return stack.get().length(); }

    //Keep the force vector arrows in front of the objects
    private void nodeMovedToFront() { forcesNode.moveToFront(); }

    private void normalizeStack() {
        List<StackableNode> nodes = stack.get();
        for ( final P2<StackableNode, Integer> stackableNodeAndIndex : nodes.zipIndex() ) {
            int index = stackableNodeAndIndex._2();
            final StackableNode stackableNode = stackableNodeAndIndex._1();
            Rectangle2D skateboardBounds = stackableNode.getParent().globalToLocal( skateboard.getGlobalFullBounds() );
            final double skateboardY = skateboardBounds.getY() + ( friction ? 1 : 6 );
            double topY = -nodes.take( index ).map( new F<StackableNode, Double>() {
                @Override public Double f( final StackableNode n ) {
                    return n.getFullScaleHeight();
                }
            } ).foldLeft( Doubles.add, 0.0 ) + skateboardY;
            stackableNode.animateToPositionScaleRotation( skateboardBounds.getCenterX() - stackableNode.getFullScaleWidth() / 2 + stackableNode.getInset(),
                                                          topY - stackableNode.getFullScaleHeight(), 1, 0, 200 );
        }
    }
}