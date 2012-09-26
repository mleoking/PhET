package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.F;
import fj.P2;
import fj.Unit;
import fj.data.List;
import fj.function.Doubles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.SpeedometerNode;
import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.RewindButton;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.StepButton;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas;
import edu.colorado.phet.forcesandmotionbasics.common.ForceArrowNode;
import edu.colorado.phet.forcesandmotionbasics.common.TextLocation;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.Context;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.ForcesNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.v;
import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createIdentity;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToHeight;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsApplication.BROWN;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsApplication.TOOLBOX_COLOR;
import static edu.colorado.phet.forcesandmotionbasics.motion.StackableNode._isOnSkateboard;
import static edu.colorado.phet.forcesandmotionbasics.motion.StackableNode._mass;
import static fj.function.Doubles.add;

/**
 * @author Sam Reid
 */
public class MotionCanvas extends AbstractForcesAndMotionBasicsCanvas implements StackableNodeContext {

    private final Property<Boolean> showSpeedometer = new Property<Boolean>( false );
    private final Property<Boolean> showValues = new Property<Boolean>( false );
    private final Property<Boolean> showForces = new Property<Boolean>( false );
    private final PNode skateboard;
    private final List<StackableNode> stackableNodes;
    private final Property<List<StackableNode>> stack = new Property<List<StackableNode>>( List.<StackableNode>nil() );
    private final boolean friction;

    private MotionModel model;

    public static final int FRIDGE_OFFSET_WITHIN_SKATEBOARD = 21;
    public static final int CRATE_OFFSET_WITHIN_SKATEBOARD = 27;
    private final PNode forcesNode;

    public static final double typicalDT = 0.033333333333333215;

    //Speed at which the bricks start to look as if they are going backwards
    public static final double STROBE_SPEED = 9.559393222711847;

    //Features only for Tab 3: Friction:
    private final Property<Boolean> showSumOfForces = new Property<Boolean>( true );
    private boolean playing = true;
    private final PusherNode pusherNode;
    private final SliderControl sliderControl;

    public MotionCanvas( final Context context, final IClock clock, final boolean friction ) {
        this.friction = friction;
        this.model = new MotionModel( friction, new F<Unit, Double>() {
            @Override public Double f( final Unit unit ) {
                return getMassOfObjectsOnSkateboard();
            }
        } );

        setBackground( BROWN );
        //use view coordinates since nothing compex happening in model coordinates.

        //for a canvas height of 710, the ground is at 452 down from the top
        final int width = 10000;

        //Reverse bottom and top because using view coordinates
        final int grassY = 425;
        addChild( new SkyNode( createIdentity(), new Rectangle2D.Double( -width / 2, -width / 2 + grassY, width, width / 2 ), grassY, SkyNode.DEFAULT_TOP_COLOR, SkyNode.DEFAULT_BOTTOM_COLOR ) );

        PNode brickLayer = new PNode() {
            {
                model.position.addObserver( new VoidFunction1<Double>() {
                    public void apply( final Double position ) {
                        updateBrickLayer( position );
                    }
                } );
                model.frictionValue.addObserver( new VoidFunction1<Double>() {
                    public void apply( final Double friction ) {
                        updateBrickLayer( model.position.get() );
                    }
                } );
            }

            private void updateBrickLayer( final Double position ) {
                removeAllChildren();
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
            }
        };
        addChild( brickLayer );

        PNode clouds = new PNode() {{
            model.position.addObserver( new VoidFunction1<Double>() {
                public void apply( final Double position ) {
                    removeAllChildren();

                    for ( int i = -20; i <= 20; i++ ) {
                        final double cloudScale = 0.6;
                        final double offsetX = i * STAGE_SIZE.width * cloudScale - position * 10;
                        if ( offsetX > -STAGE_SIZE.width * 2 && offsetX < STAGE_SIZE.width * 3 ) {
                            addChild( new PImage( Images.CLOUD1 ) {{
                                scale( cloudScale );
                                setOffset( offsetX, -50 );
                            }} );
                        }
                    }
                }
            } );
        }};
        addChild( clouds );

        final JCheckBox showForcesCheckBox = new PropertyCheckBox( null, "Forces", showForces ) {{setFont( CONTROL_FONT );}};
        final JCheckBox showValuesCheckBox = new PropertyCheckBox( null, "Values", showValues ) {{setFont( CONTROL_FONT );}};
        final JCheckBox showSumOfForcesCheckBox = new PropertyCheckBox( null, "Sum of Forces", showSumOfForces ) {{setFont( CONTROL_FONT );}};
        showForces.addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean showForces ) {
                showValuesCheckBox.setEnabled( showForces );
                showSumOfForcesCheckBox.setEnabled( showForces );
            }
        } );
        final JCheckBox speedCheckBox = new PropertyCheckBox( null, "Speed", showSpeedometer ) {{ setFont( CONTROL_FONT ); }};

        //Nudge "show" to the right so it will align with checkboxes
        final HBox title = new HBox( 5, new PhetPPath( new Rectangle2D.Double( 0, 0, 0, 0 ) ), new PhetPText( "Show", CONTROL_FONT ) );

        final PNode speedControlPanel = new HBox( 15, new PSwing( speedCheckBox ), new SpeedometerNode( "Speed", 125, model.speed, STROBE_SPEED ) {{scale( 0.25 );}} );
        final VBox vbox = friction ?
                          new VBox( 2, VBox.LEFT_ALIGNED, title, new PSwing( showForcesCheckBox ), new PSwing( showValuesCheckBox ), new PSwing( showSumOfForcesCheckBox ), speedControlPanel, new FrictionSliderControl( model.frictionValue ) ) :
                          new VBox( 2, VBox.LEFT_ALIGNED, title, new PSwing( showForcesCheckBox ), new PSwing( showValuesCheckBox ), speedControlPanel );

        final ControlPanelNode controlPanelNode = new ControlPanelNode( vbox, new Color( 227, 233, 128 ), new BasicStroke( 2 ), Color.black );
        controlPanelNode.setOffset( STAGE_SIZE.width - controlPanelNode.getFullWidth() - INSET, INSET );
        addChild( controlPanelNode );

        addChild( new ResetAllButtonNode( new Resettable() {
            public void reset() {
                context.reset();
            }
        }, this, CONTROL_FONT, Color.black, Color.orange ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getMaxY() + INSET );
            setConfirmationEnabled( false );
        }} );

        skateboard = friction ?
                     new PhetPPath( new Rectangle( 0, 0, Images.SKATEBOARD.getWidth(), 1 ), new Color( 0, 0, 0, 0 ), null, null ) :
                     new PImage( Images.SKATEBOARD );
        skateboard.setScale( 0.85 );
        skateboard.setOffset( STAGE_SIZE.getWidth() / 2 - skateboard.getFullBounds().getWidth() / 2, grassY - skateboard.getFullBounds().getHeight() );
        addChild( skateboard );

        final RoundRectangle2D.Double toolboxShape = new RoundRectangle2D.Double( 0, 0, 270, 160, 20, 20 );
        PhetPPath leftToolbox = new PhetPPath( toolboxShape, TOOLBOX_COLOR, new BasicStroke( 1 ), Color.black );
        final double toolboxY = STAGE_SIZE.height - INSET - leftToolbox.getFullBounds().getHeight();
        leftToolbox.setOffset( INSET, toolboxY );
        addChild( leftToolbox );

        PhetPPath rightToolbox = new PhetPPath( toolboxShape, TOOLBOX_COLOR, new BasicStroke( 1 ), Color.black );
        rightToolbox.setOffset( STAGE_SIZE.width - INSET - rightToolbox.getFullBounds().getWidth(), toolboxY );
        addChild( rightToolbox );

        sliderControl = new SliderControl( model.appliedForce, stack, friction );
        sliderControl.setOffset( STAGE_SIZE.getWidth() / 2 - sliderControl.getFullBounds().getWidth() / 2, grassY + 50 );
        addChild( sliderControl );

        pusherNode = new PusherNode( skateboard, grassY, model.appliedForce, stack );
        addChild( pusherNode );

        HBox timeControls = new HBox( -3, new RewindButton( 60 ) {{
            addListener( new Listener() {
                public void buttonPressed() {
                    rewind();
                }
            } );
        }}, new PlayPauseButton( 70 ) {{
            addListener( new Listener() {
                public void playbackStateChanged() {
                    MotionCanvas.this.playbackStateChanged( isPlaying() );
                }
            } );
        }}, new StepButton( 60 ) {{
            addListener( new Listener() {
                public void buttonPressed() {
                    step( typicalDT * 5 );
                }
            } );
        }}
        );
        timeControls.setOffset( STAGE_SIZE.width / 2 - timeControls.getFullWidth() / 2, STAGE_SIZE.height - timeControls.getFullHeight() );
        addChild( timeControls );

        StackableNode fridge = new StackableNode( this, Images.FRIDGE, 200, FRIDGE_OFFSET_WITHIN_SKATEBOARD );
        StackableNode crate1 = new StackableNode( this, multiScaleToHeight( Images.CRATE, (int) ( 75 * 1.2 ) ), 50, CRATE_OFFSET_WITHIN_SKATEBOARD );
        StackableNode crate2 = new StackableNode( this, multiScaleToHeight( Images.CRATE, (int) ( 75 * 1.2 ) ), 50, CRATE_OFFSET_WITHIN_SKATEBOARD );

        double INTER_OBJECT_SPACING = 10;
        fridge.setInitialOffset( leftToolbox.getFullBounds().getX() + 10, leftToolbox.getFullBounds().getCenterY() - fridge.getFullBounds().getHeight() / 2 );
        crate1.setInitialOffset( fridge.getFullBounds().getMaxX() + INTER_OBJECT_SPACING, fridge.getFullBounds().getMaxY() - crate1.getFullBounds().getHeight() );
        crate2.setInitialOffset( crate1.getFullBounds().getMaxX() + INTER_OBJECT_SPACING, fridge.getFullBounds().getMaxY() - crate2.getFullBounds().getHeight() );

        addChild( fridge );
        addChild( crate1 );
        addChild( crate2 );

        //Weight for humans: http://www.cdc.gov/growthcharts/data/set1clinical/cj41l021.pdf
        StackableNode boy = new StackableNode( this, multiScaleToHeight( Images.BOY_SITTING, 100 ), 42, friction ? 38 : 47, true, multiScaleToHeight( Images.BOY_STANDING, 170 ) );
        StackableNode girl = new StackableNode( this, multiScaleToHeight( Images.GIRL_SITTING, 100 ), 38, friction ? 38 : 47, true, multiScaleToHeight( Images.GIRL_STANDING, 180 ) );
        StackableNode trash = new StackableNode( this, multiScaleToHeight( Images.TRASH_CAN, (int) ( 150 * 2.0 / 3.0 ) ), 50, 47 );
        StackableNode gift = new StackableNode( this, multiScaleToHeight( Images.MYSTERY_OBJECT_01, 80 ), 50, 40 );

        boy.setInitialOffset( rightToolbox.getFullBounds().getX() + 10, rightToolbox.getFullBounds().getMaxY() - boy.getFullBounds().getHeight() - 10 );
        girl.setInitialOffset( boy.getFullBounds().getMaxX() + 10, rightToolbox.getFullBounds().getMaxY() - girl.getFullBounds().getHeight() - 10 );
        trash.setInitialOffset( girl.getFullBounds().getMaxX() + 10, rightToolbox.getFullBounds().getMaxY() - trash.getFullBounds().getHeight() - 10 );
        gift.setInitialOffset( trash.getFullBounds().getMaxX() + 4, rightToolbox.getFullBounds().getMaxY() - gift.getFullBounds().getHeight() - 10 );
        addChild( boy );
        addChild( girl );
        addChild( trash );
        addChild( gift );

        stackableNodes = fj.data.List.list( fridge, crate1, crate2, boy, girl, trash, gift );

        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                if ( playing ) {
                    step( clockEvent.getSimulationTimeChange() );
                }
            }
        } );

        model.appliedForce.addObserver( new VoidFunction1<Double>() {
            public void apply( final Double appliedForce ) {
                if ( appliedForce != 0.0 ) {
                    pusherNode.setOffset( 0, 0 );
                }
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
                        addChild( new ForceArrowNode( false, v( centerX + appliedForceOffsetX, tailY ),
                                                      model.appliedForce.get(), "Applied Force", ForcesNode.APPLIED_FORCE_COLOR, TextLocation.SIDE, showValues.get() ) );
                        final int frictionForceOffsetX = friction && model.appliedForce.get() != 0 ? 3 * MathUtil.getSign( model.frictionForce.get() ) : 0;
                        addChild( new ForceArrowNode( false, v( centerX + frictionForceOffsetX, tailY ),
                                                      model.frictionForce.get(), "Friction Force", Color.red, TextLocation.SIDE, showValues.get() ) );
                        if ( friction && showSumOfForces.get() ) {
                            addChild( new ForceArrowNode( false, v( centerX, tailY - 70 ),
                                                          model.sumOfForces.get(), "Sum of Forces", ForcesNode.SUM_OF_FORCES_COLOR, TextLocation.TOP, showValues.get() ) );
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

        SpeedometerNode speedometerNode = new SpeedometerNode( "Speed", 125, model.speed, STROBE_SPEED ) {{
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
        stack.addObserver( new VoidFunction1<List<StackableNode>>() {
            public void apply( final List<StackableNode> stackableNodes ) {
                if ( stackableNodes.length() == 0 ) {
                    model.appliedForce.set( 0.0 );
                }
            }
        } );
    }

    private void step( double dt ) {
        final boolean exceedsStrobeSpeedBefore = model.speed.get().get() >= STROBE_SPEED;
        if ( exceedsStrobeSpeedBefore ) { model.appliedForce.set( 0.0 ); }
        model.stepInTime( dt );
        final boolean exceedsStrobeSpeedAfter = model.speed.get().get() >= STROBE_SPEED;
        if ( !exceedsStrobeSpeedBefore && exceedsStrobeSpeedAfter ) {
            sliderControl.releaseMouse();
        }

        if ( model.appliedForce.get() == 0.0 || exceedsStrobeSpeedBefore ) {
            final double delta = -model.velocity.get() * dt * 100;
            pusherNode.setOffset( pusherNode.getOffset().getX() + delta, pusherNode.getOffset().getY() );
        }
        if ( exceedsStrobeSpeedBefore || exceedsStrobeSpeedAfter ) {
            pusherNode.fallen.set( true );
        }
        else {
            pusherNode.fallen.set( false );
        }
    }

    private void playbackStateChanged( final boolean playing ) { this.playing = playing; }

    private void rewind() {
        model.rewind();
        pusherNode.fallen.set( false );
        pusherNode.setOffset( 0, 0 );
    }

    private double getMassOfObjectsOnSkateboard() { return stackableNodes.filter( _isOnSkateboard ).map( _mass ).foldLeft( add, 0.0 ); }

    public void stackableNodeDropped( final StackableNode stackableNode ) {
        PBounds bounds = skateboard.getGlobalFullBounds();
        bounds.add( skateboard.getGlobalFullBounds().getCenterX(), rootNode.globalToLocal( new Point2D.Double( STAGE_SIZE.width / 2, 0 ) ).getY() );
        if ( bounds.intersects( stackableNode.getGlobalFullBounds() ) ) {
            stackableNode.onSkateboard.set( true );
            stack.set( stack.get().snoc( stackableNode ) );
            normalizeStack();
        }
        else {
            stackableNode.animateHome();
        }
    }

    public void stackableNodePressed( final StackableNode stackableNode ) {
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

    //Keep the force vector arrows in front of the objects
    private void nodeMovedToFront() { forcesNode.moveToFront(); }

    private void normalizeStack() {
        List<StackableNode> nodes = stack.get();
        for ( final P2<StackableNode, Integer> stackableNodeAndIndex : nodes.zipIndex() ) {
            int index = stackableNodeAndIndex._2();
            final StackableNode stackableNode = stackableNodeAndIndex._1();
            Rectangle2D skateboardBounds = stackableNode.getParent().globalToLocal( skateboard.getGlobalFullBounds() );
            final double skateboardY = skateboardBounds.getY() + ( friction ? 1 : 8 );
            double topY = -nodes.take( index ).map( new F<StackableNode, Double>() {
                @Override public Double f( final StackableNode n ) {
                    return n.getFullBounds().getHeight();
                }
            } ).foldLeft( Doubles.add, 0.0 ) + skateboardY;
            stackableNode.animateToPositionScaleRotation( skateboardBounds.getCenterX() - stackableNode.getFullBounds().getWidth() / 2,
                                                          topY - stackableNode.getFullBounds().getHeight(), 1, 0, 200 );
        }
    }
}