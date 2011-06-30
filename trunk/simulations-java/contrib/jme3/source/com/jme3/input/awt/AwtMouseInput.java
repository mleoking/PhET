/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme3.input.awt;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;

/**
 * <code>AwtMouseInput</code>
 *
 * @author Joshua Slack
 * @author MHenze (cylab)
 * 
 * @version $Revision$
 */
public class AwtMouseInput implements MouseInput, MouseListener, MouseWheelListener, MouseMotionListener {

    //public static int WHEEL_AMP = 40;   // arbitrary...  Java's mouse wheel seems to report something a lot lower than lwjgl's

    private static final Logger logger = Logger.getLogger(AwtMouseInput.class.getName());

    private boolean inited = false;

    private boolean visible = true;

    private RawInputListener listener;

    private Component component;

    private ArrayList<MouseButtonEvent> eventQueue = new ArrayList<MouseButtonEvent>();
    private int lastEventX;
    private int lastEventY;
    private int lastEventWheel;

    private Cursor transparentCursor;

    private Robot robot;
    private int wheelPos;
    private Point location;
    private Point centerLocation;
    private Point centerLocationOnScreen;
    private Point lastKnownLocation;
    private boolean isRecentering;
    private int eventsSinceRecenter;

    public AwtMouseInput(Component comp) {
        this.component = comp;
        location = new Point();
        centerLocation = new Point();
        centerLocationOnScreen = new Point();
        lastKnownLocation = new Point();

        try{
            robot = new Robot();
        }catch (java.awt.AWTException e){
            logger.log(Level.SEVERE, "Could not create a robot, so the mouse cannot be grabbed! ", e);
        }
    }

    public void initialize() {
        inited = true;
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addMouseWheelListener(this);

        logger.info("Mouse input initialized.");
    }

    public void destroy() {
        inited = false;
        robot = null;

        component.removeMouseListener(this);
        component.removeMouseMotionListener(this);
        component.removeMouseWheelListener(this);
        logger.info("Mouse input destroyed.");
    }

    public boolean isInitialized() {
        return inited;
    }

    public void setInputListener(RawInputListener listener){
        this.listener = listener;
    }

    public long getInputTimeNanos() {
        return System.nanoTime();
    }

    public void setCursorVisible(boolean visible){
        if (this.visible != visible){
            component.setCursor(visible ? null : getTransparentCursor());
            this.visible = visible;
            if (!visible)
                recenterMouse(component);
        }
    }

    public void update() {
        int newX = location.x;
        int newY = location.y;
        int newWheel = wheelPos;

        // invert DY
        MouseMotionEvent evt = new MouseMotionEvent(newX, newY,
                                                    newX - lastEventX,
                                                    lastEventY - newY,
                                                    wheelPos, lastEventWheel - wheelPos);
        listener.onMouseMotionEvent(evt);

        int size = eventQueue.size();
        for (int i = 0; i < size; i++){
            listener.onMouseButtonEvent(eventQueue.get(i));
        }
        eventQueue.clear();
        
        lastEventX = newX;
        lastEventY = newY;
        lastEventWheel = newWheel;
    }

    private final Cursor getTransparentCursor() {
        if (transparentCursor == null){
            BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            cursorImage.setRGB(0, 0, 0);
            transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "empty cursor");
        }
        return transparentCursor;
    }
//
//    public boolean isCursorVisible() {
//        return( isCursorVisible );
//    }
//	public void setHardwareCursor(URL file, int xHotspot, int yHotspot) {
//	    //Create the image from the provided url
//	    java.awt.Image cursorImage = new ImageIcon( file ).getImage( );
//	    //Create a custom cursor with this image
//	    opaqueCursor = Toolkit.getDefaultToolkit().createCustomCursor( cursorImage , new Point( xHotspot , yHotspot ) , "custom cursor" );
//	    //Use this cursor
//	    setCursorVisible( isCursorVisible );
//	}


    public int getButtonCount() {
        return 3;
    }

    public void mouseClicked(MouseEvent arg0) {
//        MouseButtonEvent evt = new MouseButtonEvent(getJMEButtonIndex(arg0), false);
//        listener.onMouseButtonEvent(evt);
    }

    public void mousePressed(MouseEvent arg0) {
        MouseButtonEvent evt = new MouseButtonEvent(getJMEButtonIndex(arg0), true, arg0.getX(), arg0.getY());
        evt.setTime(arg0.getWhen());
        eventQueue.add(evt);
    }

    public void mouseReleased(MouseEvent arg0) {
        MouseButtonEvent evt = new MouseButtonEvent(getJMEButtonIndex(arg0), false, arg0.getX(), arg0.getY());
        evt.setTime(arg0.getWhen());
        eventQueue.add(evt);
    }

    public void mouseEntered(MouseEvent arg0) {
        if (!visible)
            recenterMouse(arg0.getComponent());
    }

    public void mouseExited(MouseEvent arg0) {
        if (!visible)
            recenterMouse(arg0.getComponent());
    }

    public void mouseWheelMoved(MouseWheelEvent arg0) {
        int dwheel = arg0.getUnitsToScroll();
        wheelPos -= dwheel;
    }

    public void mouseDragged(MouseEvent arg0) {
        mouseMoved(arg0);
    }

    public void mouseMoved(MouseEvent arg0) {
        if (isRecentering) {
            // MHenze (cylab) Fix Issue 35:
            // As long as the MouseInput is in recentering mode, nothing is done until the mouse is entered in the component
            // by the events generated by the robot. If this happens, the last known location is resetted.
            if ((centerLocation.x == arg0.getX() && centerLocation.y == arg0.getY()) || eventsSinceRecenter++ == 5) {
                lastKnownLocation.x = arg0.getX();
                lastKnownLocation.y = arg0.getY();
                isRecentering = false;
            }
        } else {
            // MHenze (cylab) Fix Issue 35:
            // Compute the delta and absolute coordinates and recenter the mouse if necessary
            int dx = arg0.getX() - lastKnownLocation.x;
            int dy = arg0.getY() - lastKnownLocation.y;
            location.x += dx;
            location.y += dy;
            if (!visible) {
                recenterMouse(arg0.getComponent());
            }
            lastKnownLocation.x = arg0.getX();
            lastKnownLocation.y = arg0.getY();
// MHenze (cylab) Fix Issue 35: all accesses to the swingEvents list have to be synchronized for StandardGame to work...
                // TODO MHenze (cylab): Cleanup. this members seem obsolete by the newly introduced above.
//                absPoint.setLocation(location);
// 		if (lastPoint.x == Integer.MIN_VALUE) {
// 			lastPoint.setLocation(absPoint.x, absPoint.y);
// 		}
//                lastPoint.setLocation(arg0.getPoint());
//                currentDeltaPoint.x = absPoint.x - lastPoint.x;
//                currentDeltaPoint.y = -(absPoint.y - lastPoint.y);
//                lastPoint.setLocation(location);
        }
    }

    // MHenze (cylab) Fix Issue 35: A method to generate recenter the mouse to allow the InputSystem to "grab" the mouse
    private void recenterMouse(final Component component) {
        if (robot != null) {
            eventsSinceRecenter = 0;
            isRecentering = true;
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
            centerLocation.setLocation(component.getWidth()/2, component.getHeight()/2);
            centerLocationOnScreen.setLocation(centerLocation);
            SwingUtilities.convertPointToScreen(centerLocationOnScreen, component);
            robot.mouseMove(centerLocationOnScreen.x, centerLocationOnScreen.y);
//                }
//            });
        }
    }

    private int getJMEButtonIndex( MouseEvent arg0 ) {
        int index;
        switch (arg0.getButton()) {
            default:
            case MouseEvent.BUTTON1: //left
                index = 0;
                break;
            case MouseEvent.BUTTON2: //middle
                index = 2;
                break;
            case MouseEvent.BUTTON3: //right
                index = 1;
                break;
        }
        return index;
    }
}
