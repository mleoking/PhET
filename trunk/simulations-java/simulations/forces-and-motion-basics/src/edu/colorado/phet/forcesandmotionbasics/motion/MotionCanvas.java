package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;
import fj.function.Doubles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JCheckBox;

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
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.SpeedometerNode;
import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.StepButton;
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
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsApplication.BROWN;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsApplication.TOOLBOX_COLOR;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images.ROCK_BROWN;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images.ROCK_GRAY;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents.showForcesCheckBoxIcon;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents.speedCheckBoxIcon;
import static edu.colorado.phet.forcesandmotionbasics.motion.StackableNode._isOnSkateboard;
import static edu.colorado.phet.forcesandmotionbasics.motion.StackableNode._mass;
import static fj.data.Option.some;
import static fj.function.Doubles.add;
import static java.awt.geom.AffineTransform.getTranslateInstance;

/**
 * Canvas for tab 2 "Motion" and tab 3 "Friction", shows everything as piccolo nodes (including the control panels).
 *
 * @author Sam Reid
 */
public class MotionCanvas extends AbstractForcesAndMotionBasicsCanvas implements StackableNodeContext {

    private final BooleanProperty showSpeedometer = new BooleanProperty( false );
    private final Property<Boolean> showValues = new Property<Boolean>( false );
    private final BooleanProperty showForces = new BooleanProperty( false );
    private final PNode skateboard;
    private final List<StackableNode> stackableNodes;
    private final Property<List<StackableNode>> stack = new Property<List<StackableNode>>( List.<StackableNode>nil() );
    private final boolean friction;

    private final MotionModel model;

    private static final int FRIDGE_OFFSET_WITHIN_SKATEBOARD = 21;
    private static final int CRATE_OFFSET_WITHIN_SKATEBOARD = 27;
    private final PNode forcesNode;

    private static final double typicalDT = 0.033333333333333215;

    //Speed at which the bricks start to look as if they are going backwards
    public static final double STROBE_SPEED = 9.559393222711847;

    private final BooleanProperty playing = new BooleanProperty( true );
    private final PusherNode pusherNode;

    //Features only for Tab 3: Friction:
    private final BooleanProperty showSumOfForces = new BooleanProperty( false );
    private final BooleanProperty dragging = new BooleanProperty( false );

    public MotionCanvas( final Resettable moduleContext, final IClock clock,

                         //True if tab 3 "friction"
                         final boolean friction ) {
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

        final BufferedImage rocksOverlay = new BufferedImage( 2000, 200, BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D g2 = rocksOverlay.createGraphics();
        rock( g2, ROCK_GRAY, 10, 0, 14 );
        rock( g2, ROCK_BROWN, 40, 10, 22 );
        rock( g2, ROCK_BROWN, 220, 25, 28 );
        rock( g2, ROCK_GRAY, 380, -10, 28 );
        rock( g2, ROCK_BROWN, 1500, -10, 28 );
        rock( g2, ROCK_GRAY, 1700, 25, 15 );
        rock( g2, ROCK_GRAY, 1900, 12, 14 );
        rock( g2, ROCK_BROWN, 1950, 12, 28 );
        g2.dispose();

        final BufferedImage mountains = new BufferedImage( 3250, Images.MOUNTAINS.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D mountainGraphics = mountains.createGraphics();
        mountainGraphics.drawRenderedImage( Images.MOUNTAINS, new AffineTransform() );
        mountainGraphics.dispose();

        PNode clouds = new PNode() {{
            addChild( new HBox( 245, new PImage( Images.CLOUD1 ) {{
                scale( 0.6 );
            }}, new PhetPPath( new Rectangle2D.Double( 0, 0, 1, 1 ), new Color( 0, 0, 0, 0 ) ) {{setVisible( false );}}
            ) );
        }};
        final Image cloudTexture = clouds.toImage();

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

                //Show rocks as additional texture
                //TODO: Prototype code, being left during interviews.
                boolean showRocks = false;
                if ( showRocks ) {
                    final double scale = 0.5;
                    final Rectangle2D.Double area = new Rectangle2D.Double( -STAGE_SIZE.width / scale, grassY / scale + Images.BRICK_TILE.getHeight() - 22, STAGE_SIZE.width * 3 / scale, rocksOverlay.getHeight() );
                    final Rectangle2D.Double anchor = new Rectangle2D.Double( -position * 100 / scale, area.getY() - 1, rocksOverlay.getWidth(), rocksOverlay.getHeight() );
                    PhetPPath path = new PhetPPath( area, new TexturePaint( rocksOverlay, anchor ) ) {{
                        scale( scale );
                    }};
                    path.setTransparency( 0.7f );
                    addChild( path );
                }

                //Show mountains as additional texture
                //TODO: Prototype code, being left during interviews.
                boolean showMountains = true;
                if ( showMountains ) {
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
                {
                    final Rectangle2D.Double area = new Rectangle2D.Double( -STAGE_SIZE.width, -50, STAGE_SIZE.width * 3, cloudTexture.getHeight( null ) );
                    final Rectangle2D.Double anchor = new Rectangle2D.Double( -position * 10, area.getY(), cloudTexture.getWidth( null ), cloudTexture.getHeight( null ) );
                    addChild( new PhetPPath( area, new TexturePaint( BufferedImageUtils.toBufferedImage( cloudTexture ), anchor ) ) );
                }
            }
        };
        addChild( terrainLayer );

        final JCheckBox showForcesCheckBox = new PropertyCheckBox( UserComponents.forcesCheckBox, Strings.FORCES, showForces ) {{
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

        final PNode speedControlPanel = new HBox( 15, new PSwing( speedCheckBox ), new SpeedometerNode( Strings.SPEED, 125, model.speed, STROBE_SPEED ) {{
            scale( 0.25 );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {
                    sendUserMessage( speedCheckBoxIcon, button, pressed, parameterSet( isSelected, !showSpeedometer.get() ) );
                    showSpeedometer.toggle();
                }
            } );
        }} );
        final PNode showForcesPanel = new HBox( 15, new PSwing( showForcesCheckBox ), new ForceArrowNode( false, ZERO, 20, "", ForcesNode.APPLIED_FORCE_COLOR, TextLocation.SIDE, false ) {{
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
                          new VBox( 0, VBox.LEFT_ALIGNED, showForcesPanel, indent( showValuesCheckBox ), indent( showSumOfForcesPanel ),
                                    new PSwing( massCheckBox ),
                                    speedControlPanel,
                                    new FrictionSliderControl( model.frictionValue ) ) :
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

        final AppliedForceSliderControl appliedForceSliderControl = new AppliedForceSliderControl( model.speedValue, model.appliedForce, stack, friction, playing );
        appliedForceSliderControl.setOffset( STAGE_SIZE.getWidth() / 2 - appliedForceSliderControl.getFullBounds().getWidth() / 2, grassY + 50 );
        addChild( appliedForceSliderControl );

        pusherNode = new PusherNode( model.fallen, skateboard, grassY, model.appliedForce, stack, model.speedValue, model.speed, playing, model.movedSliderOnce );
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

                                      playPauseButton, new StepButton( 60 ) {{
            addListener( new Listener() {
                public void buttonPressed() {
                    step( typicalDT * 5 );
                }
            } );

            //Only enable the step button when the sim is paused
            playing.addObserver( new VoidFunction1<Boolean>() {
                public void apply( final Boolean playing ) {
                    setEnabled( !playing );
                }
            } );
        }}
        );
        timeControls.setOffset( STAGE_SIZE.width / 2 - playPauseButton.getFullBounds().getWidth() / 2, STAGE_SIZE.height - timeControls.getFullHeight() );
        addChild( timeControls );

        StackableNode fridge = new StackableNode( UserComponents.fridge, this, Images.FRIDGE, 200, FRIDGE_OFFSET_WITHIN_SKATEBOARD, showMasses );
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
        StackableNode man = new StackableNode( UserComponents.man, this, multiScaleToHeight( Images.MAN_SITTING, manHeight ), 80, 38, showMasses, true, multiScaleToHeight( Images.MAN_STANDING, 200 ), multiScaleToHeight( Images.MAN_HOLDING, manHeight ) );
        StackableNode trash = new StackableNode( UserComponents.trash, this, multiScaleToHeight( Images.TRASH_CAN, (int) ( 150 * 2.0 / 3.0 ) ), 50, 47, showMasses );
        StackableNode gift = new StackableNode( UserComponents.gift, this, multiScaleToHeight( Images.MYSTERY_OBJECT_01, 60 ), 50, 40, showMasses ) {
            @Override protected Pair<Integer, String> getMassDisplayString( final double mass ) {

                //Add some padding on either side of the "?" for the gift to make it easier to read
                return new Pair<Integer, String>( 12, "?" );
            }
        };

        double spacingX = 18;
        girl.setInitialOffset( rightToolbox.getFullBounds().getX() + spacingX, rightToolbox.getFullBounds().getMaxY() - girl.getFullBounds().getHeight() - 5 );
        man.setInitialOffset( girl.getObjectMaxX() + spacingX, rightToolbox.getFullBounds().getMaxY() - man.getFullBounds().getHeight() - 5 );
        trash.setInitialOffset( man.getObjectMaxX() + spacingX, rightToolbox.getFullBounds().getMaxY() - trash.getFullBounds().getHeight() - 5 );
        gift.setInitialOffset( trash.getObjectMaxX() + spacingX - 5, rightToolbox.getFullBounds().getMaxY() - gift.getFullBounds().getHeight() - 5 );
        addChild( girl );
        addChild( man );
        addChild( trash );
        addChild( gift );

        stackableNodes = fj.data.List.list( fridge, crate1, crate2, man, girl, trash, gift );

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

        SpeedometerNode speedometerNode = new SpeedometerNode( Strings.SPEED, 125, model.speed, STROBE_SPEED ) {{
            showSpeedometer.addObserver( new VoidFunction1<Boolean>() {
                public void apply( final Boolean show ) {
                    setVisible( show );
                }
            } );

            //scale up so fonts and stroke thicknesses look good
            scale( 1.25 );

            setOffset( STAGE_SIZE.width / 2 - getFullBounds().getWidth() / 2, 10 );

            stack.addObserver( new VoidFunction1<List<StackableNode>>() {
                public void apply( final List<StackableNode> stackableNodes ) {
                    if ( stackableNodes.length() >= 3 ) {
                        animateToPositionScaleRotation( STAGE_SIZE.width / 2 - getFullBounds().getWidth() / 2 - getFullBounds().getWidth(), 10, 1.25, 0, 200 );
                    }
                    else {
                        animateToPositionScaleRotation( STAGE_SIZE.width / 2 - getFullBounds().getWidth() / 2, 10, 1.25, 0, 200 );
                    }
                }
            } );
        }};
        addChild( speedometerNode );

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

    //Avoid an empty string in a parameter value by listing the ground or skateboard first
    private String stackToString( final List<StackableNode> stackableNodes ) {
        return stackableNodes.foldLeft( new F2<String, StackableNode, String>() {
            @Override public String f( final String s, final StackableNode stackableNode ) {
                return s + ", " + stackableNode.component.toString();
            }
        }, friction ? "ground" : "skateboard" );
    }

    private void rock( final Graphics2D g2, final BufferedImage im, final int x, final int y, final int width ) {
        g2.drawRenderedImage( multiScaleToWidth( im, width ), getTranslateInstance( x, y ) );
    }

    private PNode indent( final JCheckBox component ) {
        return indent( new PSwing( component ) );
    }

    private PNode indent( final PNode component ) {
        return new HBox( 15, new PhetPPath( new Rectangle( 0, 0, 1, 1 ), new Color( 0, 0, 0, 0 ) ), component );
    }

    private void step( double dt ) {
        final boolean exceedsStrobeSpeedBefore = model.speed.get().get() >= STROBE_SPEED;
        model.stepInTime( dt );
        final boolean exceedsStrobeSpeedAfter = model.speed.get().get() >= STROBE_SPEED;

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