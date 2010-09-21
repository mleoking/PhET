/*
 * Copyright (c) 2008-2010, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.umd.cs.piccolox.swt;

import java.awt.Color;
import java.awt.Shape;
import java.awt.event.InputEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.util.PLocator;
import edu.umd.cs.piccolox.util.PNodeLocator;

/**
 * <b>PSWTHandle</b> is used to modify some aspect of Piccolo when it is dragged.
 * Each handle has a PLocator that it uses to automatically position itself. See
 * PSWTBoundsHandle for an example of a handle that resizes the bounds of another
 * node.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PSWTHandle extends PSWTPath {
    private static final long serialVersionUID = 1L;
    /** The Default Size of a handle not including its border. */
    public static float DEFAULT_HANDLE_SIZE = 8;
    /** The default shape to use when drawing handles. Default is an ellipse. */
    public static Shape DEFAULT_HANDLE_SHAPE = new Ellipse2D.Float(0f, 0f, DEFAULT_HANDLE_SIZE, DEFAULT_HANDLE_SIZE);
    /** The default color to use when drawing a handle. (white) */
    public static Color DEFAULT_COLOR = Color.white;

    private PLocator locator;
    private PDragSequenceEventHandler handleDragger;

    /**
     * Construct a new handle that will use the given locator to locate itself
     * on its parent node.
     * 
     * @param aLocator locator to use when positioning this handle
     */
    public PSWTHandle(final PLocator aLocator) {
        super(DEFAULT_HANDLE_SHAPE);
        locator = aLocator;
        setPaint(DEFAULT_COLOR);
        installHandleEventHandlers();
    }

    /**
     * Installs the handler that will reposition the handle when it is dragged,
     * and invoke appropriate call backs.
     */
    protected void installHandleEventHandlers() {
        handleDragger = new HandleDragHandler();

        addPropertyChangeListener(PNode.PROPERTY_TRANSFORM, new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                relocateHandle();
            }
        });

        // so reject them so we don't consume them
        addInputEventListener(handleDragger);
    }

    /**
     * Return the event handler that is responsible for the drag handle
     * interaction.
     * 
     * @return handler responsible for responding to drag events
     */
    public PDragSequenceEventHandler getHandleDraggerHandler() {
        return handleDragger;
    }

    /**
     * Get the locator that this handle uses to position itself on its parent
     * node.
     * 
     * @return locator used to position this handle
     */
    public PLocator getLocator() {
        return locator;
    }

    /**
     * Set the locator that this handle uses to position itself on its parent
     * node.
     * 
     * @param aLocator used to position this handle
     */
    public void setLocator(final PLocator aLocator) {
        locator = aLocator;
        invalidatePaint();
        relocateHandle();
    }

    // ****************************************************************
    // Handle Dragging - These are the methods the subclasses should
    // normally override to give a handle unique behavior.
    // ****************************************************************

    /**
     * Override this method to get notified when the handle starts to get
     * dragged.
     * 
     * @param aLocalPoint point at which dragging was started relative to the
     *            handle's coordinate system
     * @param aEvent event representing the start of the drag
     */
    public void startHandleDrag(final Point2D aLocalPoint, final PInputEvent aEvent) {
    }

    /**
     * Override this method to get notified as the handle is dragged.
     * 
     * @param aLocalDimension magnitude of the dragHandle event in the
     *            dimensions of the handle's coordinate system.
     * @param aEvent event representing the drag
     */
    public void dragHandle(final PDimension aLocalDimension, final PInputEvent aEvent) {
    }

    /**
     * Override this method to get notified when the handle stops getting
     * dragged.
     * 
     * @param aLocalPoint point at which dragging was ended relative to the
     *            handle's coordinate system
     * @param aEvent event representing the end of the drag
     */
    public void endHandleDrag(final Point2D aLocalPoint, final PInputEvent aEvent) {
    }

    // ****************************************************************
    // Layout - When a handle's parent's layout changes the handle
    // invalidates its own layout and then repositions itself on its
    // parents bounds using its locator to determine that new
    // position.
    // ****************************************************************

    /** {@inheritDoc} */
    public void setParent(final PNode newParent) {
        super.setParent(newParent);
        relocateHandle();
    }

    /** {@inheritDoc} */
    public void parentBoundsChanged() {
        relocateHandle();
    }

    /**
     * Force this handle to relocate itself using its locator.
     */
    public void relocateHandle() {
        if (locator != null) {
            final PBounds b = getBoundsReference();
            final Point2D aPoint = locator.locatePoint(null);

            if (locator instanceof PNodeLocator) {
                final PNode located = ((PNodeLocator) locator).getNode();
                final PNode parent = getParent();

                located.localToGlobal(aPoint);
                globalToLocal(aPoint);

                if (parent != located && parent instanceof PCamera) {
                    ((PCamera) parent).viewToLocal(aPoint);
                }
            }

            final double newCenterX = aPoint.getX();
            final double newCenterY = aPoint.getY();

            if (newCenterX != b.getCenterX() || newCenterY != b.getCenterY()) {
                centerBoundsOnPoint(newCenterX, newCenterY);
            }
        }
    }

    // ****************************************************************
    // Serialization
    // ****************************************************************

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        installHandleEventHandlers();
    }

    private final class HandleDragHandler extends PDragSequenceEventHandler {
        public HandleDragHandler() {
            final PInputEventFilter filter = new PInputEventFilter(InputEvent.BUTTON1_MASK);
            setEventFilter(filter);
            filter.setMarksAcceptedEventsAsHandled(true);
            filter.setAcceptsMouseEntered(false);
            filter.setAcceptsMouseExited(false);
            filter.setAcceptsMouseMoved(false);
        }

        protected void startDrag(final PInputEvent event) {
            super.startDrag(event);
            startHandleDrag(event.getPositionRelativeTo(PSWTHandle.this), event);
        }

        protected void drag(final PInputEvent event) {
            super.drag(event);
            final PDimension aDelta = event.getDeltaRelativeTo(PSWTHandle.this);
            if (aDelta.getWidth() != 0 || aDelta.getHeight() != 0) {
                dragHandle(aDelta, event);
            }
        }

        protected void endDrag(final PInputEvent event) {
            super.endDrag(event);
            endHandleDrag(event.getPositionRelativeTo(PSWTHandle.this), event);
        }
    }
}