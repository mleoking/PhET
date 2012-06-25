package edu.colorado.phet.buildafraction.view.pictures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Divisions are shown in front of pieces so that you can see how the container is divided and how the pieces add up.
 *
 * @author Sam Reid
 */
public class ContainerFrontNode extends PNode {
    private final ContainerNode parent;

    public ContainerFrontNode( final ContainerNode parent ) {
        this.parent = parent;
        final PNode shapeNode = new PNode();
        final VoidFunction1<Integer> updateWithPieceSize = new VoidFunction1<Integer>() {
            public void apply( final Integer number ) {
                shapeNode.removeAllChildren();

                //Fill any seams
                shapeNode.addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, SimpleContainerNode.width * parent.getFractionValue().toDouble(), SimpleContainerNode.height ), Color.red ) );

                shapeNode.addChild( new SimpleContainerNode( number, null ) {{
                    for ( int i = 0; i < number; i++ ) {
                        final double pieceWidth = width / number;
                        shapeNode.addChild( new PhetPPath( new Rectangle2D.Double( pieceWidth * i, 0, pieceWidth, height ), new BasicStroke( 1 ), Color.black ) );
                    }
                    //Thicker outer stroke
                    shapeNode.addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), new BasicStroke( 2 ), Color.black ) );
                }} );
                ContainerFrontNode.this.moveToFront();
            }
        };
        parent.selectedPieceSize.addObserver( updateWithPieceSize );

        addChild( shapeNode );

        parent.addListener( new VoidFunction0() {
            public void apply() {
                updateWithPieceSize.apply( parent.selectedPieceSize.get() );
            }
        } );

        final PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange( final PropertyChangeEvent evt ) {
                setGlobalTranslation( parent.shapeNode.getGlobalTranslation() );
                setGlobalScale( parent.shapeNode.getGlobalScale() );
            }
        };
        parent.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, listener );
        listener.propertyChange( null );

        setPickable( false );
        setChildrenPickable( false );
    }
}