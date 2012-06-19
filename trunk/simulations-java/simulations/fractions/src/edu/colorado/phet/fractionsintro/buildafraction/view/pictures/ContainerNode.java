package edu.colorado.phet.fractionsintro.buildafraction.view.pictures;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.util.FJUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Some copied from NumberNode, may need to be remerged.
 *
 * @author Sam Reid
 */
public class ContainerNode extends PNode {
    private double initialX;
    private double initialY;
    private final PictureSceneNode parent;
    private int number;

    public static final double width = 130;
    public static final double height = 55;
    private PImage splitButton;

    public ContainerNode( PictureSceneNode parent, final int number, final ContainerContext context ) {
        this.parent = parent;
        this.number = number;
        PNode content = new PNode() {{
            for ( int i = 0; i < number; i++ ) {
                final double pieceWidth = width / number;
                addChild( new PhetPPath( new Rectangle2D.Double( pieceWidth * i, 0, pieceWidth, height ), Color.white, new BasicStroke( 1 ), Color.black ) );
            }
            //Thicker outer stroke
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), new BasicStroke( 2 ), Color.black ) );
            addInputEventListener( new SimSharingDragHandler( null, true ) {
                @Override protected void drag( final PInputEvent event ) {
                    super.drag( event );
                    final PDimension delta = event.getDeltaRelativeTo( getParent() );
                    ContainerNode.this.translate( delta.width, delta.height );
                }

                @Override protected void endDrag( final PInputEvent event ) {
                    super.endDrag( event );
                    context.endDrag( ContainerNode.this, event );
                }
            } );
            addInputEventListener( new CursorHandler() );
        }};

        addChild( content );

        splitButton = new PImage( Images.SPLIT_BLUE );
        addChild( splitButton );
        splitButton.setVisible( false );
        splitButton.setPickable( false );
        splitButton.translate( -splitButton.getFullBounds().getWidth(),
                               -splitButton.getFullBounds().getHeight() );
        splitButton.addInputEventListener( new CursorHandler() );
        splitButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( final PInputEvent event ) {
                SimSharingManager.sendButtonPressed( null );
                splitAll();
            }
        } );
    }

    private void splitAll() {
        for ( Object child : new ArrayList<Object>( getChildrenReference() ) ) {
            if ( child instanceof RectangularPiece ) {
                RectangularPiece p = (RectangularPiece) child;
                parent.splitPieceFromContainer( p, this );
            }
        }
        PInterpolatingActivity activity = splitButton.animateToTransparency( 0, 200 );
        activity.setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {
                splitButton.setVisible( false );
                splitButton.setPickable( false );
            }
        } );

    }

    public void setAllPickable( final boolean b ) {
        setPickable( b );
        setChildrenPickable( b );
    }

    public void setInitialPosition( final double x, final double y ) {
        this.initialX = x;
        this.initialY = y;
        setOffset( x, y );
    }

    public double getInitialX() { return initialX; }

    public double getInitialY() { return initialY; }

    public void animateHome() { animateToPositionScaleRotation( getInitialX(), getInitialY(), 1, 0, 1000 ); }

    public void addPiece( final RectangularPiece piece ) {
        Point2D offset = piece.getGlobalTranslation();
        addChild( piece );
        piece.setGlobalTranslation( offset );
        if ( !splitButton.getVisible() ) {
            splitButton.setVisible( true );
            splitButton.setPickable( true );
            splitButton.setTransparency( 0 );
            splitButton.animateToTransparency( 1, 200 );
        }
    }

    public static Rectangle2D.Double createRect( int number ) {
        final double pieceWidth = width / number;
        final Rectangle2D.Double shape = new Rectangle2D.Double( pieceWidth * number, 0, pieceWidth, height );
        return shape;
    }

    //How far over should a new piece be added in?
    public double getPiecesWidth() {
        ArrayList<RectangularPiece> children = new ArrayList<RectangularPiece>();
        for ( Object c : getChildrenReference() ) {
            if ( c instanceof RectangularPiece ) {
                children.add( (RectangularPiece) c );
            }
        }
        return children.size() == 0 ? 0 :
               fj.data.List.iterableList( children ).maximum( FJUtils.ord( new F<RectangularPiece, Double>() {
                   @Override public Double f( final RectangularPiece r ) {
                       return r.getFullBounds().getMaxX();
                   }
               } ) ).getFullBounds().getMaxX();
    }
}