package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.activities.AnimateToScale;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Strings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.view.util.RectangleUtils.expand;
import static edu.colorado.phet.common.phetcommon.view.util.RectangleUtils.round;

/**
 * For tabs 2-3, this represents an object that starts in the toolbox and can be dropped in the center of the play area.
 * Some objects can have others stacked upon them.  All objects can be stacked on something else.
 *
 * @author Sam Reid
 */
class StackableNode extends PNode {

    private Vector2D initialOffset;
    private final double initialScale;
    public final BooleanProperty onSkateboard = new BooleanProperty( false );
    public final IUserComponent component;
    private final double mass;

    public static final F<StackableNode, Boolean> _isOnSkateboard = new F<StackableNode, Boolean>() {
        @Override public Boolean f( final StackableNode stackableNode ) {
            return stackableNode.onSkateboard.get();
        }
    };
    public static final F<StackableNode, Double> _mass = new F<StackableNode, Double>() {
        @Override public Double f( final StackableNode stackableNode ) {
            return stackableNode.mass;
        }
    };
    public final int pusherOffset;
    private final BufferedImage toolboxImage;
    private final BufferedImage flippedStackedImage;

    //Remember the direction for the last image shown before applied force is set to 0.0 so that the character will keep facing the same direction.
    private double lastSign;
    private final PNode textLabel;
    private final BufferedImage flippedHandsUpImage;

    public StackableNode( IUserComponent component, final StackableNodeContext context, final BufferedImage image, final double mass, final int pusherOffset, BooleanProperty showMass ) {
        this( component, context, image, mass, pusherOffset, showMass, false, image, image );
    }

    public StackableNode( IUserComponent component, final StackableNodeContext context, final BufferedImage stackedImage, final double mass, final int pusherOffset, final BooleanProperty showMass,
                          final boolean faceDirectionOfAppliedForce, final BufferedImage toolboxImage,

                          //Show this image when something else is being dragged and this node will be shown underneath.  For the girl and man who will hold up the objects.
                          final BufferedImage handsUpImage ) {
        this.component = component;
        this.mass = mass;
        this.pusherOffset = pusherOffset;
        this.toolboxImage = toolboxImage;
        this.flippedStackedImage = BufferedImageUtils.flipX( stackedImage );
        this.flippedHandsUpImage = BufferedImageUtils.flipX( handsUpImage );
        final PImage imageNode = new PImage( toolboxImage ) {
            {
                onSkateboard.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( final Boolean aBoolean ) {
                        updateImage();
                    }
                } );
                if ( faceDirectionOfAppliedForce ) {
                    context.getAppliedForce().addObserver( new VoidFunction1<Double>() {
                        public void apply( final Double aDouble ) {
                            updateImage();
                        }
                    } );
                }
                final SimpleObserver updater = new SimpleObserver() {
                    public void update() {
                        updateImage();
                    }
                };
                context.getUserIsDraggingSomething().addObserver( updater );
                context.addStackChangeListener( updater );
            }

            private void updateImage() {
                final Image chosenImage = chooseImage();
                if ( onSkateboard.get() && faceDirectionOfAppliedForce && context.getAppliedForce().get() != 0 ) {
                    lastSign = MathUtil.getSign( context.getAppliedForce().get() );
                }
                setImage( chosenImage );
            }

            private Image chooseImage() {
                if ( !onSkateboard.get() ) {
                    return toolboxImage;
                }
                else {
                    if ( faceDirectionOfAppliedForce ) {
                        final boolean handsUp = ( context.getUserIsDraggingSomething().get() && context.getStackSize() < 3 ) || context.isInStackButNotInTop( StackableNode.this );
                        if ( context.getAppliedForce().get() > 0 ) {
                            return handsUp ? flippedHandsUpImage : flippedStackedImage;
                        }
                        else if ( context.getAppliedForce().get() < 0 ) {
                            return handsUp ? handsUpImage : stackedImage;
                        }
                        else {
                            if ( lastSign > 0 ) {
                                return handsUp ? flippedHandsUpImage : flippedStackedImage;
                            }
                            else {
                                return handsUp ? handsUpImage : stackedImage;
                            }
                        }
                    }
                    else {
                        return stackedImage;
                    }
                }
            }
        };
        addChild( imageNode );
        setScale( 0.8 );
        textLabel = new ZeroOffsetNode( new PNode() {{
            final Pair<Integer, String> massDisplayString = getMassDisplayString( mass );
            final PhetPText text = new PhetPText( massDisplayString._2, new PhetFont( 18, true ) );
            final PhetPPath textBackground = new PhetPPath( round( expand( text.getFullBounds(), 3 + massDisplayString._1, 3 ), 18, 18 ), Color.white, new BasicStroke( 1 ), Color.gray );
            addChild( textBackground );
            addChild( text );

            showMass.addObserver( new VoidFunction1<Boolean>() {
                public void apply( final Boolean visible ) {
                    setVisible( visible );
                }
            } );
        }} );
        final PropertyChangeListener updateTextLocation = new PropertyChangeListener() {
            public void propertyChange( final PropertyChangeEvent evt ) {
                textLabel.setOffset( imageNode.getFullBounds().getCenterX() - textLabel.getFullBounds().getWidth() / 2,
                                     imageNode.getFullBounds().getHeight() - textLabel.getFullBounds().getHeight() - 2 );
            }
        };
        updateTextLocation.propertyChange( null );
        imageNode.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updateTextLocation );

        addChild( textLabel );
        this.initialScale = getScale();
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( component, true ) {
            @Override protected void startDrag( final PInputEvent event ) {
                super.startDrag( event );
                addActivity( new AnimateToScale( 1.0, StackableNode.this, 200 ) );
                context.stackableNodePressed( StackableNode.this );
            }

            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                final PDimension delta = event.getDeltaRelativeTo( StackableNode.this.getParent() );
                translate( delta.width, delta.height );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );
                context.stackableNodeDropped( StackableNode.this );
            }
        } );
    }

    Pair<Integer, String> getMassDisplayString( final double mass ) {
        final String text = new DecimalFormat( "0" ).format( mass );
        return new Pair<Integer, String>( 0, new MessageFormat( Strings.MASS_DISPLAY__PATTERN ).format( new Object[] { text } ) );
    }

    public void animateHome() {
        animateToPositionScaleRotation( initialOffset.x, initialOffset.y, initialScale, 0, 200 );
    }

    public void setInitialOffset( final double x, final double y ) {
        this.initialOffset = Vector2D.v( x, y );
        setOffset( x, y );
    }

    //Return the width of the image part of the object (not including the width of the text overlays) for layout
    public double getObjectMaxX() {
        return getOffset().getX() + toolboxImage.getWidth() * getScale();
    }

    //The text label may go to the left of the object, factor it out when centering on the skateboard
    public double getInset() {
        if ( textLabel.getOffset().getX() < 0 ) {
            return Math.abs( textLabel.getOffset().getX() );
        }
        else { return 0; }
    }

    //Use the full scale height for computing layout on the stack, otherwise if it is still AnimateToScale'ing then it could appear sunken into the skateboard or previous object
    public double getFullScaleHeight() { return getFullBounds().getHeight() / getScale(); }

    public double getFullScaleWidth() { return getFullBounds().getWidth() / getScale(); }
}