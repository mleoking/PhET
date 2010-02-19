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
package edu.umd.cs.piccolo.event;

import java.awt.event.InputEvent;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * PDragEventHandler is a simple event handler for dragging a node on the
 * canvas.
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PDragEventHandler extends PDragSequenceEventHandler {

    private PNode draggedNode;
    private boolean moveToFrontOnPress;

    /**
     * Constructs a drag event handler which defaults to not moving the node to
     * the front on drag.
     */
    public PDragEventHandler() {
        draggedNode = null;
        moveToFrontOnPress = false;

        setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
    }

    /**
     * Returns the node that is currently being dragged, or null if none.
     * 
     * @return node being dragged or null
     */
    protected PNode getDraggedNode() {
        return draggedNode;
    }

    /**
     * Set's the node that is currently being dragged.
     * 
     * @param draggedNode node to be flagged as this handler's current drag node
     */
    protected void setDraggedNode(final PNode draggedNode) {
        this.draggedNode = draggedNode;
    }

    /**
     * Returns whether the given event should be start a drag interaction.
     * 
     * @param event the event being tested
     * 
     * @return true if event is a valid start drag event
     */
    protected boolean shouldStartDragInteraction(final PInputEvent event) {
        return super.shouldStartDragInteraction(event) && event.getPickedNode() != event.getTopCamera();
    }

    /**
     * Starts a drag event and moves the dragged node to the front if this
     * handler has been directed to do so with a call to setMoveToFrontOnDrag.
     * 
     * @param event The Event responsible for the start of the drag
     */
    protected void startDrag(final PInputEvent event) {
        super.startDrag(event);
        draggedNode = event.getPickedNode();
        if (moveToFrontOnPress) {
            draggedNode.moveToFront();
        }
    }

    /**
     * Moves the dragged node in proportion to the drag distance.
     * 
     * @param event event representing the drag
     */
    protected void drag(final PInputEvent event) {
        super.drag(event);
        final PDimension d = event.getDeltaRelativeTo(draggedNode);
        draggedNode.localToParent(d);
        draggedNode.offset(d.getWidth(), d.getHeight());
    }

    /**
     * Clears the current drag node.
     * 
     * @param event Event reponsible for the end of the drag. Usually a
     *            "Mouse Up" event.
     */
    protected void endDrag(final PInputEvent event) {
        super.endDrag(event);
        draggedNode = null;
    }

    /**
     * Returns whether this drag event handler has been informed to move nodes
     * to the front of all other on drag.
     * 
     * @return true if dragging a node will move it to the front
     */
    public boolean getMoveToFrontOnPress() {
        return moveToFrontOnPress;
    }

    /**
     * Informs this drag event handler whether it should move nodes to the front
     * when they are dragged. Default is false.
     * 
     * @param moveToFrontOnPress true if dragging a node should move it to the
     *            front
     */
    public void setMoveToFrontOnPress(final boolean moveToFrontOnPress) {
        this.moveToFrontOnPress = moveToFrontOnPress;
    }
}
