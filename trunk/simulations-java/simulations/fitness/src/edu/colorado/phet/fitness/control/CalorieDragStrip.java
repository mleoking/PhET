package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Created by: Sam
 * May 26, 2008 at 10:14:57 AM
 */
public class CalorieDragStrip extends PNode {
    private static Random random = new Random();

    public CalorieDragStrip( final CalorieSet available ) {
        ArrayList nodes = new ArrayList();
        for ( int i = 0; i < 5; i++ ) {
            final PNode node = createNode( available.getItem( i ) );
            final int i1 = i;
            node.addInputEventListener( new PBasicInputEventHandler() {
                private PNode createdNode = null;

                public void mouseDragged( PInputEvent event ) {
                    if ( createdNode == null ) {
                        createdNode = createNode( available.getItem( i1 ) );
                        createdNode.setOffset( node.getOffset() );
                        addChild( createdNode );
                    }
                    createdNode.translate( event.getDelta().getWidth(), event.getDelta().getHeight() );
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

    private PNode createNode( CaloricItem item ) {
        if ( item.getImage() != null && item.getImage().trim().length() > 0 ) {
            return new PImage( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( item.getImage() ), 30 ) );
        }
        else {
            final Color color = new Color( random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 ) );
            return new PhetPPath( new Rectangle( 0, 0, 10, 10 ), color );
        }
    }
}
