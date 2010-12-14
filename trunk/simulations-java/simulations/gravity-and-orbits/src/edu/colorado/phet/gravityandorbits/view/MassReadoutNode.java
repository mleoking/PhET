package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * @author Sam Reid
 */
public abstract class MassReadoutNode extends PNode {

    protected BodyNode bodyNode;

    public MassReadoutNode( final BodyNode bodyNode, final Property<Boolean> visible ) {
        this.bodyNode = bodyNode;
        addChild( new PText( "1 million Earth masses" ) {{
            setPickable( false );
            setChildrenPickable( false );
            setFont( new PhetFont( 18, true ) );
            setTextPaint( Color.white );
            bodyNode.getBody().getMassProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setText( createText() );
                }
            } );
        }} );
        bodyNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                PBounds bounds = bodyNode.getBodyRenderer().getGlobalFullBounds();
                globalToLocal( bounds );
                localToParent( bounds );
                if ( bodyNode.getBody().isMassReadoutBelow() ) {
                    setOffset( bounds.getCenterX() - getFullBounds().getWidth() / 2, bounds.getMaxY() );
                }
                else {
                    setOffset( bounds.getCenterX() - getFullBounds().getWidth() / 2, bounds.getMinY() - getFullBounds().getHeight() );
                }
            }
        } );
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
    }

    abstract String createText();
}
