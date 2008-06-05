package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Created by: Sam
 * May 26, 2008 at 10:14:57 AM
 */
public class CalorieDragStrip extends PNode {
    private static Random random = new Random();
    private ArrayList listeners = new ArrayList();
    private static final int HEIGHT = 22;

    public CalorieDragStrip( final CalorieSet available ) {
        ArrayList nodes = new ArrayList();
        for ( int i = 0; i < 20; i++ ) {
            final PNode node = createNode( available.getItem( i ) );
            final int i1 = i;
            node.addInputEventListener( new PDragSequenceEventHandler() {
                private DefaultDragNode createdNode = null;

                protected void startDrag( PInputEvent e ) {
                    super.startDrag( e );
                    createdNode = createNode( available.getItem( i1 ) );
                    createdNode.addDragHandler();
                    createdNode.getPNode().setOffset( node.getOffset() );
                    addChild( createdNode.getPNode() );
                }

                protected void drag( PInputEvent event ) {
                    super.drag( event );
                    createdNode.getPNode().translate( event.getDelta().getWidth(), event.getDelta().getHeight() );
                    notifyDragged( createdNode );
                }

                protected void endDrag( PInputEvent e ) {
                    super.endDrag( e );
                    notifyDropped( createdNode );
                }
            } );

            nodes.add( node );
        }
        for ( int i = 1; i < nodes.size(); i++ ) {
            int row = i / 4;
            int col = i % 4;
            PNode pNode = (PNode) nodes.get( i );
            PNode prev = (PNode) nodes.get( i - 1 );
            pNode.setOffset( col == 0 ? 0 : prev.getFullBounds().getMaxX(), row * prev.getFullBounds().getHeight() );

        }
        for ( int i = 0; i < nodes.size(); i++ ) {
            addChild( (PNode) nodes.get( i ) );
        }
    }

    public void removeItem( DragNode droppedNode ) {
        removeChild( droppedNode.getPNode() );
    }

    private class DefaultDragNode extends PNode implements DragNode {
        private CaloricItem item;
        private PNode node;

        public DefaultDragNode( PNode node, CaloricItem item ) {
            this.item = item;
            this.node = node;
            addChild( node );
            node.addInputEventListener( new CursorHandler() );
        }

        public void addDragHandler() {
            node.addInputEventListener( new PDragSequenceEventHandler() {
                protected void drag( PInputEvent event ) {
                    super.drag( event );
                    getPNode().translate( event.getDelta().getWidth(), event.getDelta().getHeight() );
                    notifyDragged( DefaultDragNode.this );
                }

                protected void endDrag( PInputEvent e ) {
                    notifyDropped( DefaultDragNode.this );
                }
            } );
        }

        public PNode getPNode() {
            return this;
        }

        public CaloricItem getItem() {
            return item;
        }
    }

    private DefaultDragNode createNode( CaloricItem item ) {
        if ( item.getImage() != null && item.getImage().trim().length() > 0 ) {
            return new DefaultDragNode( new PImage( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( item.getImage() ), HEIGHT ) ), item );
        }
        else {
            final Color color = new Color( random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 ) );
            return new DefaultDragNode( new PhetPPath( new Rectangle( 0, 0, 10, 10 ), color ), item );
        }
    }

    public static interface DragNode {
        PNode getPNode();

        CaloricItem getItem();
    }

    public static interface Listener {
        void nodeDropped( DragNode node );

        void notifyDragged( DragNode createdNode );
    }

    public static class Adapter implements Listener {
        public void nodeDropped( DragNode node ) {
        }

        public void notifyDragged( DragNode createdNode ) {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyDropped( DragNode createdNode ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).nodeDropped( createdNode );
        }
    }

    public void notifyDragged( DragNode createdNode ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).notifyDragged( createdNode );
        }
    }
}
