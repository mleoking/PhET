/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.piccolo.event.BoundedDragHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 12:24:05 AM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class HalfOnscreenDragHandler extends BoundedDragHandler {
    private PPath dragBounds;
    private PCanvas component;

    public HalfOnscreenDragHandler( PCanvas component, PNode dragNode ) {
        super( dragNode, new PPath() );
        this.component = component;
        dragBounds = (PPath)super.getBoundingNode();
        component.getLayer().addChild( dragBounds );
        dragBounds.setPickable( false );
        dragBounds.setVisible( false );
        component.getLayer().addChild( dragBounds );
        component.addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                updateDragBounds();
            }


            public void componentShown( ComponentEvent e ) {
                updateDragBounds();
            }

        } );
        dragNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateDragBounds();
            }
        } );
        dragNode.addPropertyChangeListener( PNode.PROPERTY_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateDragBounds();
            }
        } );
    }

    private void updateDragBounds() {
        PNode dragNode = super.getDragNode();
        dragBounds.setPathTo( new Rectangle2D.Double( -dragNode.getFullBounds().getWidth() / 2, -dragNode.getFullBounds().getHeight() / 2,
                                                      component.getWidth() + dragNode.getFullBounds().getWidth(), component.getHeight() + dragNode.getFullBounds().getHeight() ) );
    }
}
