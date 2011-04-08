// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.*;
import java.awt.image.BufferedImage;

import edu.colorado.phet.bendinglight.view.ProtractorModel;
import edu.colorado.phet.bendinglight.view.ProtractorNode;
import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.getMaximizeButtonImage;
import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.getMinimizeButtonImage;

/**
 * In the "more tools" tab, the protractor can be expanded with a "+" button and returned to the original size with a "-" button.
 *
 * @author Sam Reid
 */
public class ExpandableProtractorNode extends ProtractorNode {
    private double originalScale;//The scale when the protractor node is not expanded
    private Property<Boolean> expanded = new Property<Boolean>( false );//True if the protractor has been made larger

    public ExpandableProtractorNode( final ModelViewTransform transform, final Property<Boolean> showProtractor, final ProtractorModel protractorModel, Function2<Shape, Shape, Shape> translateShape, Function2<Shape, Shape, Shape> rotateShape, double _scale ) {
        super( transform, showProtractor, protractorModel, translateShape, rotateShape, _scale );
        this.originalScale = _scale;

        //Button that allows the user to expand the protractor node in the more tools tab
        class MaxMinButton extends PImage {
            MaxMinButton( BufferedImage image, ObservableProperty<Boolean> expanded, final boolean expand ) {
                super( image );
                setOffset( innerBarShape.getX() + innerBarShape.getWidth() / 4 - getFullBounds().getWidth() / 2, innerBarShape.getCenterY() - getFullBounds().getHeight() / 2 );
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        setExpanded( expand );
                    }
                } );
                expanded.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( Boolean expanded ) {
                        setVisible( expanded );
                    }
                } );
            }
        }

        addChild( new MaxMinButton( getMaximizeButtonImage(), new Not( expanded ), true ) );
        addChild( new MaxMinButton( getMinimizeButtonImage(), expanded, false ) );
    }

    //Set whether the protractor should be shown as large (expanded) or regular
    private void setExpanded( boolean expanded ) {
        this.expanded.setValue( expanded );
        setProtractorScale( originalScale * ( expanded ? 2.6 : 1 ) );
        doUpdateTransform();
    }
}
