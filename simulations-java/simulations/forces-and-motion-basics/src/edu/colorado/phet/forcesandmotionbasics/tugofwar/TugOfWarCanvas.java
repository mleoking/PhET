package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import fj.Effect;
import fj.F;
import fj.F2;
import fj.data.List;
import fj.data.Option;
import fj.function.Doubles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.functionaljava.FJUtils;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createIdentity;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsApplication.BROWN;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsApplication.TOOLBOX_COLOR;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.KnotNode.*;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.PullerNode.*;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor.BLUE;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor.RED;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PSize.*;

/**
 * @author Sam Reid
 */
public class TugOfWarCanvas extends AbstractForcesAndMotionBasicsCanvas implements PullerContext, ForcesNodeContext {

    public static final long ANIMATION_DURATION = 300;
    private final List<KnotNode> blueKnots;
    private final List<KnotNode> redKnots;
    private final ForcesNode forcesNode;
    public final ArrayList<VoidFunction0> forceListeners = new ArrayList<VoidFunction0>();
    private final PImage cartNode;
    private final Property<Boolean> showSumOfForces = new Property<Boolean>( false );
    private final Property<Boolean> showValues = new Property<Boolean>( false );
    private Property<Mode> mode = new Property<Mode>( Mode.WAITING );
    private Cart cart = new Cart();
    private ArrayList<PullerNode> pullers = new ArrayList<PullerNode>();
    private final PImage rope;
    private final double initialRopeX;
    private ArrayList<VoidFunction0> cartPositionListeners = new ArrayList<VoidFunction0>();
    private final ImageButtonNodeWithText stopButton;
    private final ImageButtonNodeWithText goButton;

    public static enum Mode {WAITING, GOING, COMPLETE}

    public TugOfWarCanvas( final Context context, final IClock clock ) {

        setBackground( BROWN );
        //use view coordinates since nothing compex happening in model coordinates.

        //for a canvas height of 710, the ground is at 452 down from the top
        final int width = 10000;

        //Reverse bottom and top because using view coordinates
        final int grassY = 452;
        addChild( new SkyNode( createIdentity(), new Rectangle2D.Double( -width / 2, -width / 2 + grassY, width, width / 2 ), grassY, SkyNode.DEFAULT_TOP_COLOR, SkyNode.DEFAULT_BOTTOM_COLOR ) );

        final PImage grassNode = new PImage( Images.GRASS );
        grassNode.setOffset( -2, grassY - 2 );
        addChild( grassNode );

        final JCheckBox sumOfForcesCheckBox = new PropertyCheckBox( null, "Sum of Forces", showSumOfForces ) {{
            setFont( CONTROL_FONT );
        }};
        final JCheckBox showValuesCheckBox = new PropertyCheckBox( null, "Values", showValues ) {{setFont( CONTROL_FONT );}};
        final ControlPanelNode controlPanelNode = new ControlPanelNode(
                new VBox( 2, VBox.LEFT_ALIGNED,

                          //Nudge "show" to the right so it will align with checkboxes
                          new HBox( 5, new PhetPPath( new Rectangle2D.Double( 0, 0, 0, 0 ) ), new PhetPText( "Show", CONTROL_FONT ) ),
                          new PSwing( showValuesCheckBox ), new PSwing( sumOfForcesCheckBox ) ), new Color( 227, 233, 128 ), new BasicStroke( 2 ), Color.black );
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

        cartNode = new PImage( Images.CART );
        cartNode.setOffset( STAGE_SIZE.width / 2 - cartNode.getFullBounds().getWidth() / 2, grassY - cartNode.getFullBounds().getHeight() + 4 );


        rope = new PImage( Images.ROPE );
        initialRopeX = STAGE_SIZE.width / 2 - rope.getFullBounds().getWidth() / 2;
        rope.setOffset( initialRopeX, cartNode.getFullBounds().getCenterY() - rope.getFullBounds().getHeight() / 2 );

        blueKnots = ImageMetrics.blueKnots.map( new F<Double, KnotNode>() {
            @Override public KnotNode f( final Double knotLocation ) {
                return new KnotNode( knotLocation, Color.blue, rope.getFullBounds() );
            }
        } );
        redKnots = ImageMetrics.redKnots.map( new F<Double, KnotNode>() {
            @Override public KnotNode f( final Double knotLocation ) {
                return new KnotNode( knotLocation, Color.red, rope.getFullBounds() );
            }
        } );

        addChildren( blueKnots.append( redKnots ) );

        addChild( rope );
        addChild( cartNode );

        final double IMAGE_SCALE = 0.75;
        Vector2D largePosition = Vector2D.v( 88.38995568685374, 488 + 1 );
        Vector2D mediumPosition = Vector2D.v( 155.66912850812423, 515 + 1 + 1 );
        Vector2D smallPosition1 = Vector2D.v( 215.9527326440175, 559 - 1 );
        Vector2D smallPosition2 = Vector2D.v( 263.1610044313148, 559 - 1 );
        final PullerNode largeRedPuller = puller( BLUE, LARGE, IMAGE_SCALE, largePosition );
        addPuller( largeRedPuller );
        addPuller( puller( BLUE, MEDIUM, IMAGE_SCALE, mediumPosition ) );
        addPuller( puller( BLUE, SMALL, IMAGE_SCALE, smallPosition1 ) );
        addPuller( puller( BLUE, SMALL, IMAGE_SCALE, smallPosition2 ) );

        final double offset = largeRedPuller.getFullBounds().getWidth();
        addPuller( puller( RED, LARGE, IMAGE_SCALE, reflect( largePosition, offset ) ) );
        addPuller( puller( RED, MEDIUM, IMAGE_SCALE, reflect( mediumPosition, offset ) ) );
        addPuller( puller( RED, SMALL, IMAGE_SCALE, reflect( smallPosition1, offset ) ) );
        addPuller( puller( RED, SMALL, IMAGE_SCALE, reflect( smallPosition2, offset ) ) );

        final PhetPPath blueToolbox = new PhetPPath( getBounds( _isBlue ), TOOLBOX_COLOR, new BasicStroke( 1 ), Color.black );
        addChild( blueToolbox );
        final PhetPPath redToolbox = new PhetPPath( getBounds( _isRed ), TOOLBOX_COLOR, new BasicStroke( 1 ), Color.black );
        addChild( redToolbox );
        blueToolbox.moveToBack();
        redToolbox.moveToBack();

        forcesNode = new ForcesNode();
        addChild( forcesNode );

        goButton = new ImageButtonNodeWithText( Images.GO_UP, Images.GO_HOVER, Images.GO_PRESSED, "Go!", new VoidFunction0() {
            public void apply() {
                mode.set( Mode.GOING );
                stopButton.hover();
            }
        } ) {{
            setOffset( getButtonLocation( this ) );

            final VoidFunction0 update = new VoidFunction0() {
                public void apply() {
                    boolean visible = redKnots.append( blueKnots ).filter( new F<KnotNode, Boolean>() {
                        @Override public Boolean f( final KnotNode knotNode ) {
                            return knotNode.getPullerNode() != null;
                        }
                    } ).length() > 0 && mode.get() == Mode.WAITING;
                    setVisible( visible );
                    setChildrenPickable( visible );
                }
            };
            forceListeners.add( update );
            update.apply();
        }};
        addChild( goButton );

        stopButton = new ImageButtonNodeWithText( Images.STOP_UP, Images.STOP_HOVER, Images.STOP_PRESSED, "STOP", new VoidFunction0() {
            public void apply() {
                mode.set( Mode.WAITING );
                goButton.hover();
            }
        } ) {{
            setOffset( getButtonLocation( this ) );
            mode.addObserver( new VoidFunction1<Mode>() {
                public void apply( final Mode mode ) {
                    boolean visible = mode == Mode.GOING;
                    setVisible( visible );
                    setChildrenPickable( visible );
                }
            } );
        }};
        addChild( stopButton );

        addChild( new TextButtonNode( "Restart", CONTROL_FONT, Color.orange ) {{
            setOffset( stopButton.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, stopButton.getFullBounds().getMaxY() + INSET );
            mode.addObserver( new VoidFunction1<Mode>() {
                public void apply( final Mode mode ) {

                    //leave "restart" button showing after "stop" pressed
                    boolean visible = mode == Mode.GOING || mode == Mode.COMPLETE || ( mode == Mode.WAITING && !cartIsInCenter() );
                    setVisible( visible );
                    setPickable( visible );
                    setChildrenPickable( visible );
                }
            } );
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    restart();
                }
            } );
        }} );

        mode.addObserver( new VoidFunction1<Mode>() {
            public void apply( final Mode mode ) {
                updateForceListeners();
            }
        } );

        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {

                //all motion is done through deltas
                if ( mode.get() == Mode.GOING ) {
                    double originalCartPosition = cart.getPosition();
                    final double dt = clockEvent.getSimulationTimeChange();
                    double acceleration = getSumOfForces() / ( cart.weight + getAttachedPullers().map( PullerNode._weight ).foldLeft( Doubles.add, 0.0 ) );
                    cart.stepInTime( dt, acceleration );
                    final double delta = cart.getPosition() - originalCartPosition;
                    moveSystem( delta );
                    notifyCartPositionListeners();

                    //stop when the opposite rope passes the middle of the screen
                    if ( cart.getPosition() > 180 || cart.getPosition() < -180 ) {
                        mode.set( Mode.COMPLETE );
                    }
                }
            }
        } );

        addChild( new CaretNode() {{
            setOffset( STAGE_SIZE.width / 2, grassY + 9 );
        }} );

        showSumOfForces.addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean showSumOfForces ) {
                updateForceArrows();
            }
        } );
        showValues.addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean showValues ) {
                updateForceArrows();
            }
        } );
    }

    private Shape getBounds( final F<PullerNode, Boolean> color ) {
        final PBounds bounds = List.iterableList( pullers ).filter( color ).map( _getFullBounds ).foldLeft( new F2<PBounds, PBounds, PBounds>() {
            @Override public PBounds f( final PBounds a, final PBounds b ) {
                if ( a == null ) { return b; }
                if ( b == null ) { return a; }
                return new PBounds( a.createUnion( b ) );
            }
        }, null );
        Rectangle2D expanded = RectangleUtils.expand( bounds, 15, 10 );
        return new RoundRectangle2D.Double( expanded.getX(), expanded.getY(), expanded.getWidth(), expanded.getHeight(), 20, 20 );
    }

    private void notifyCartPositionListeners() {
        for ( VoidFunction0 listener : cartPositionListeners ) {
            listener.apply();
        }
    }

    private void moveSystem( final double delta ) {
        cartNode.translate( delta, 0 );
        rope.translate( delta, 0 );
        getAttachedPullers().foreach( new Effect<PullerNode>() {
            @Override public void e( final PullerNode pullerNode ) {
                pullerNode.translate( delta / pullerNode.getScale(), 0 );
            }
        } );
    }

    private void restart() {
        mode.set( Mode.WAITING );
        cart.restart();
        double ropeOffset = rope.getOffset().getX() - initialRopeX;
        moveSystem( -ropeOffset );
        updateForceListeners();
        notifyCartPositionListeners();
    }

    private void addPuller( final PullerNode puller ) {
        addChild( puller );
        pullers.add( puller );
    }

    public List<PullerNode> getAttachedPullers() {
        return blueKnots.append( redKnots ).bind( new F<KnotNode, List<PullerNode>>() {
            @Override public List<PullerNode> f( final KnotNode k ) {
                return k.getPullerNode() == null ? List.<PullerNode>nil() : List.single( k.getPullerNode() );
            }
        } );
    }

    private Point2D getButtonLocation( PNode buttonNode ) {
        return new Point2D.Double( STAGE_SIZE.width / 2 - buttonNode.getFullBounds().getWidth() / 2, cartNode.getFullBounds().getMaxY() + INSET * 2 );
    }

    private Vector2D reflect( final Vector2D position, final double width ) {
        double distanceFromCenter = STAGE_SIZE.width / 2 - position.x;
        double newX = STAGE_SIZE.width / 2 + distanceFromCenter - width;
        return new Vector2D( newX, position.y );
    }

    public PullerNode puller( PColor color, PSize size, final double scale, final Vector2D offset ) {
        return new PullerNode( color, size, 0, scale, offset, this, mode );
    }

    public void drag( final PullerNode pullerNode ) {
        //find closest knot node
        List<KnotNode> knots = pullerNode.color == BLUE ? blueKnots : redKnots;
        knots.foreach( _unhighlight );
        Option<KnotNode> attachNode = getAttachNode( pullerNode );
        attachNode.foreach( new Effect<KnotNode>() {
            @Override public void e( final KnotNode knotNode ) {
                knotNode.setHighlighted( true );
            }
        } );
    }

    public void endDrag( final PullerNode pullerNode ) {
        blueKnots.append( redKnots ).foreach( _unhighlight );
        Option<KnotNode> attachNode = getAttachNode( pullerNode );
        if ( attachNode.isSome() ) {
            Point2D hands = pullerNode.getGlobalAttachmentPoint();
            Point2D knot = attachNode.some().getGlobalFullBounds().getCenter2D();
            Vector2D delta = new Vector2D( hands, knot );
            Dimension2D localDelta = rootNode.globalToLocal( new Dimension2DDouble( delta.x, delta.y ) );
            pullerNode.animateToPositionScaleRotation( pullerNode.getOffset().getX() + localDelta.getWidth(), pullerNode.getOffset().getY() + localDelta.getHeight(), pullerNode.scale, 0, ANIMATION_DURATION );

            //attach everything
            attachNode.some().setPullerNode( pullerNode );
            pullerNode.setKnot( attachNode.some() );
            updateForceListeners();
        }
        else {
            detach( pullerNode );
            pullerNode.animateHome();
        }
    }

    private void detach( final PullerNode pullerNode ) {
        KnotNode node = pullerNode.getKnot();
        if ( node != null ) {
            node.setPullerNode( null );
        }
        pullerNode.setKnot( null );
        updateForceListeners();
    }

    private void updateForceListeners() {
        updateForceArrows();

        for ( VoidFunction0 forceListener : forceListeners ) {
            forceListener.apply();
        }
    }

    private void updateForceArrows() {forcesNode.setForces( mode.get() == Mode.WAITING || mode.get() == Mode.COMPLETE, getLeftForce(), getRightForce(), showSumOfForces.get(), showValues.get() );}

    private double getRightForce() {return redKnots.map( _force ).foldLeft( Doubles.add, 0.0 );}

    private double getLeftForce() {return -blueKnots.map( _force ).foldLeft( Doubles.add, 0.0 );}

    private double getSumOfForces() {return getRightForce() + getLeftForce();}

    public void startDrag( final PullerNode pullerNode ) {
        detach( pullerNode );
    }

    public boolean cartIsInCenter() { return Math.abs( cart.getPosition() ) < 1;}

    public void addCartPositionChangeListener( final VoidFunction0 voidFunction0 ) {
        cartPositionListeners.add( voidFunction0 );
    }

    private Option<KnotNode> getAttachNode( final PullerNode pullerNode ) {
        List<KnotNode> knots = pullerNode.color == BLUE ? blueKnots : redKnots;
        List<KnotNode> free = knots.filter( _free ).filter( new F<KnotNode, Boolean>() {
            @Override public Boolean f( final KnotNode knotNode ) {
                return knotPullerDistance( knotNode, pullerNode ) < 80;
            }
        } );
        if ( free.length() > 0 ) {
            KnotNode closest = free.minimum( FJUtils.ord( new F<KnotNode, Double>() {
                @Override public Double f( final KnotNode k ) {
                    return knotPullerDistance( k, pullerNode );
                }
            } ) );
            return Option.some( closest );
        }
        else { return Option.none(); }
    }

    private double knotPullerDistance( final KnotNode k, final PullerNode p ) {return k.getGlobalFullBounds().getCenter2D().distance( p.getGlobalAttachmentPoint() );}

    public static enum PColor {BLUE, RED}

    public static enum PSize {SMALL, MEDIUM, LARGE}
}