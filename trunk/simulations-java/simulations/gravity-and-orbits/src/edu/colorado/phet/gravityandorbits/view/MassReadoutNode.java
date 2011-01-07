// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.And;
import edu.colorado.phet.common.phetcommon.model.Not;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.gravityandorbits.GAOStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Abstract class provides functionality for displaying the mass readout (in text) of a Body node.
 *
 * @author Sam Reid
 */
public abstract class MassReadoutNode extends PNode {

    protected BodyNode bodyNode;

    public MassReadoutNode( final BodyNode bodyNode, final Property<Boolean> visible ) {
        this.bodyNode = bodyNode;
        addChild( new PText( MessageFormat.format( GAOStrings.PATTERN_VALUE_UNITS, 1, GAOStrings.MILLION_EARTH_MASSES ) ) {{
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
        new And( visible, new Not( bodyNode.getBody().getCollidedProperty() ) ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( getValue() );
                }
            } );
        }};
    }

    abstract String createText();
}
