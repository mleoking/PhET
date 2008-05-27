package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
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

    public CalorieDragStrip( final CalorieSet available ) {
        ArrayList nodes = new ArrayList();
        for ( int i = 0; i < 5; i++ ) {
            final PNode node = createNode( available.getItem( i ) );
            final int i1 = i;
            node.addInputEventListener( new PDragSequenceEventHandler() {
                DragNode createdNode = null;
//                private PNode createdNode = null;

                protected void startDrag( PInputEvent e ) {
                    super.startDrag( e );
                    createdNode = createNode( available.getItem( i1 ) );
                    createdNode.getPNode().setOffset( node.getOffset() );
                    addChild( createdNode.getPNode() );
                }

                protected void drag( PInputEvent event ) {
                    super.drag( event );
                    createdNode.getPNode().translate( event.getDelta().getWidth(), event.getDelta().getHeight() );
                }

                protected void endDrag( PInputEvent e ) {
                    super.endDrag( e );
                    notifyDropped( createdNode );
                }
            } );

            nodes.add( node );
        }
        for ( int i = 1; i < nodes.size(); i++ ) {
            PNode pNode = (PNode) nodes.get( i );
            PNode prev = (PNode) nodes.get( i - 1 );
            pNode.setOffset( 0, prev.getFullBounds().getMaxY() );
        }
        for ( int i = 0; i < nodes.size(); i++ ) {
            PNode pNode = (PNode) nodes.get( i );
            addChild( pNode );
        }
    }

    private static class DefaultDragNode extends PNode implements DragNode {
        private CaloricItem item;

        public DefaultDragNode( PNode node, CaloricItem item ) {
            this.item = item;
            addChild( node );
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
            return new DefaultDragNode( new PImage( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( item.getImage() ), 30 ) ), item );
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
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyDropped( DragNode createdNode ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).nodeDropped( createdNode );
        }
    }
}
