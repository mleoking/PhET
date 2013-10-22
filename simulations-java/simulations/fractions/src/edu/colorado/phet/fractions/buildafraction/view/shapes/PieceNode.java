// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import fj.F;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.activities.AnimateToScale;
import edu.colorado.phet.common.piccolophet.activities.PActivityDelegateAdapter;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.SimSharingCanvasBoundedDragHandler;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.view.DisablePickingWhileAnimating;
import edu.colorado.phet.fractions.buildafraction.view.Stackable;
import edu.colorado.phet.fractions.buildafraction.view.UpdateAnimatingFlag;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.piece;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ParameterKeys.denominator;
import static java.awt.geom.AffineTransform.getTranslateInstance;

/**
 * Node for a single piece that can be dragged from the toolbox and dropped in a shape container.
 *
 * @author Sam Reid
 */
public abstract class PieceNode extends Stackable {
    public final Integer pieceSize;
    private static final PBounds TEMP_REPAINT_BOUNDS = new PBounds();
    double initialScale = Double.NaN;
    final PieceContext context;
    final PhetPPath pathNode;
    public static final BasicStroke stroke = new BasicStroke( 2 );
    double pieceRotation = 0.0;

    //Keep track of the container so we know whether to handle mouse events
    private SingleContainerNode container;

    //Keep track of attachment time because the last attached is always the first removed.
    public long attachmentTime = -1L;

    PieceNode( final Integer pieceSize, final PieceContext context, PhetPPath pathNode ) {
        this.pieceSize = pieceSize;
        this.context = context;
        this.pathNode = pathNode;
    }

    //Add mouse drag listeners.
    void installInputListeners() {
        addInputEventListener( new CursorHandler() );

        addInputEventListener( new SimSharingCanvasBoundedDragHandler( chain( piece, PieceNode.this.hashCode() ), PieceNode.this, false ) {
            @Override protected ParameterSet getParametersForAllEvents( final PInputEvent event ) {
                return super.getParametersForAllEvents( event ).with( denominator, pieceSize );
            }

            @Override public void mousePressed( final PInputEvent event ) {

                //See #3460 drop shadow bug
                if ( !event.isLeftMouseButton() ) { return; }
                if ( getNumberOfActiveActivities() > 0 ) {return;}

                //This node gets reparented, so only send mouse events if it is traveling freely (i.e. not in a container)
                if ( container != null ) { return; }
                super.mousePressed( event );

                dragStarted();
                PieceNode.this.moveToFront();
                final AnimateToScale activity = new AnimateToScale( context.getContainerScale(), PieceNode.this, BuildAFractionModule.ANIMATION_TIME );
                addActivity( activity );

                activity.setDelegate( new PActivityDelegateAdapter() {
                    public void activityStepped( final PActivity activity ) {
                        stepTowardMouse( event );
                    }
                } );

                context.startDrag( PieceNode.this );
                setPositionInStack( Option.<Integer>none() );
            }

            @Override public void mouseDragged( final PInputEvent event ) {
                //See #3460 drop shadow bug
                if ( !event.isLeftMouseButton() ) { return; }

                //This node gets reparented, so only send mouse events if it is traveling freely (i.e. not in a container)
                if ( container != null ) { return; }
                super.mouseDragged( event );
            }

            @Override protected void dragNode( final DragEvent event ) {
                //This node gets reparented, so only send mouse events if it is traveling freely (i.e. not in a container)
                if ( container != null ) { return; }
                Option<Double> originalAngle = context.getNextAngle( PieceNode.this );
                translate( event.delta.width / getScale(), event.delta.height / getScale() );
                Option<Double> newAngle = context.getNextAngle( PieceNode.this );
                if ( originalAngle.isSome() && newAngle.isSome() && !originalAngle.some().equals( newAngle.some() ) ) {
                    rotateTo( newAngle.some(), event.event );
                }
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                //See #3460 drop shadow bug
                if ( !event.isLeftMouseButton() ) { return; }

                //This node gets reparented, so only send mouse events if it is traveling freely (i.e. not in a container)
                if ( container != null ) { return; }
                super.mouseReleased( event );
                context.endDrag( PieceNode.this );

                //Protect against multiple copies accidentally being added
                dragEnded();
            }
        } );
    }

    private int getNumberOfActiveActivities() {
        int numberActiveActivities = 0;
        for ( PActivity activity : activities ) {
            if ( activity.isStepping() ) { numberActiveActivities++; }
        }
        return numberActiveActivities;
    }

    //For circular pie pieces, rotate to align with the closest open site in any ContainerNode's SingleContainerNodes.
    void rotateTo( final double angle, final PInputEvent event ) {}

    //As the object gets bigger, it should move so that it is centered on the mouse.  To compute how to move it, we must rotate it and scale it immediately
    //and invisibly to the user, then set back its settings and animate them.
    void stepTowardMouse( final PInputEvent event ) {

        //We want path node to move toward the mouse center.
        Point2D pt = event.getPositionRelativeTo( this );
        Point2D center = pathNode.getGlobalFullBounds().getCenter2D();
        center = globalToLocal( center );

        Vector2D delta = new Vector2D( center, pt ).times( 0.75 );
        translate( delta.x, delta.y );
    }

    void dragEnded() { }

    void dragStarted() { }

    protected double getAnimateToScale() { return this.initialScale; }

    AffineTransform getShadowOffset() {return getTranslateInstance( 6, 6 );}

    public void setInitialScale( double s ) {
        this.initialScale = s;
        setScale( s );
    }

    //Get the fraction value represented by this single piece
    public Fraction toFraction() { return new Fraction( 1, pieceSize );}

    //Function that gets the fraction value represented by this single piece
    public static final F<PieceNode, Fraction> _toFraction = new F<PieceNode, Fraction>() {
        @Override public Fraction f( final PieceNode r ) {
            return r.toFraction();
        }
    };

    //Animate this piece card to the top of its stack.
    @SuppressWarnings("unchecked") public void animateToTopOfStack() { stack.animateToTopOfStack( this, context.isFractionLab() ); } //unchecked warning

    //Show drop shadow when moving back to toolbox
    public void animateToStackLocation( Vector2D v, final boolean deleteOnArrival ) {
        container = null;
        attachmentTime = -1L;
        animateToPositionScaleRotation( v.x, v.y, getAnimateToScale(), 0, BuildAFractionModule.ANIMATION_TIME ).
                setDelegate( new CompositeDelegate( new DisablePickingWhileAnimating( this, true ),
                                                    new PActivityDelegateAdapter() {
                                                        public void activityStarted( final PActivity activity ) {
                                                            showShadow();
                                                        }

                                                        public void activityFinished( final PActivity activity ) {
                                                            hideShadow();
                                                        }
                                                    },
                                                    new UpdateAnimatingFlag( animating ),
                                                    new PActivityDelegateAdapter() {
                                                        @Override public void activityFinished( final PActivity activity ) {
                                                            if ( deleteOnArrival ) {
                                                                PieceNode.this.delete();
                                                            }
                                                        }
                                                    }
                ) );
    }

    protected void delete() {
        super.delete();
        removeFromParent();
    }

    protected abstract void hideShadow();

    protected abstract void showShadow();

    public void setInContainer( final SingleContainerNode singleContainerNode ) {
        this.container = singleContainerNode;
        this.attachmentTime = System.currentTimeMillis();
    }

    //When joining a container, all other animations must be stopped or it can result in very buggy behavior (such as the piece ending up in the wrong location)
    public void terminateActivities() {
        for ( PActivity activity : new ArrayList<PActivity>( activities ) ) {
            activities.remove( activity );
            activity.terminate( PActivity.TERMINATE_WITHOUT_FINISHING );
        }
        activities.clear();
    }

    //List of activities that have been scheduled.  See terminateActivities
    private final ArrayList<PActivity> activities = new ArrayList<PActivity>();

    @Override public boolean addActivity( final PActivity activity ) {
        activities.add( activity );
        return super.addActivity( activity );
    }

    public abstract PieceNode copy();

    //Workaround for dirty rectangle problem
    @Override public void repaint() {
        TEMP_REPAINT_BOUNDS.setRect( RectangleUtils.expand( getFullBoundsReference(), 2, 2 ) );
        repaintFrom( TEMP_REPAINT_BOUNDS, this );
    }
}