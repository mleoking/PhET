// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Abstract class provides functionality for displaying the mass readout (in text) of a Body node.
 *
 * @author Sam Reid
 */
public abstract class MassReadoutNode extends PNode {

    //REVIEW all subclasses use bodyNode.getBody().getMass(), so why not store the body?
    protected final BodyNode bodyNode;

    public MassReadoutNode( final BodyNode bodyNode, final Property<Boolean> visible ) {
        this.bodyNode = bodyNode;
        addChild( new PText( createText() ) {{
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
        final PropertyChangeListener updateLocation = new PropertyChangeListener() {
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
        };
        bodyNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, updateLocation );
        //Updating on visibility changes ensures that the labels appear in the correct place initially
        visible.addObserver( new SimpleObserver() {
            public void update() {
                updateLocation.propertyChange( null );
            }
        } );
        new And( visible, new Not( bodyNode.getBody().getCollidedProperty() ) ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( getValue() );
                }
            } );
        }};
    }

    //REVIEW defaults to package public, shouldn't this be protected?
    abstract String createText();
}
