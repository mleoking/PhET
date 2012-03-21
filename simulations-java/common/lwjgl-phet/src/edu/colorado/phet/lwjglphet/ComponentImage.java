// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;

// TODO: event handling (listen to events). use similar to HUDNode model
public class ComponentImage extends TextureImage {
    private final JComponent component;

    // owned by the EDT
    private volatile boolean dirty = true;

    public static final String ON_REPAINT_CALLBACK = "!@#%^&*"; // tag used in the repaint manager to notify this instance for repainting

    public ComponentImage( int width, int height, boolean hasAlpha, int magFilter, int minFilter, JComponent component ) {
        super( width, height, hasAlpha, magFilter, minFilter );
        this.component = component;
        init();
    }

    public ComponentImage( int width, int height, boolean hasAlpha, int magFilter, int minFilter, AffineTransform imageTransform, JComponent component ) {
        super( width, height, hasAlpha, magFilter, minFilter, imageTransform );
        this.component = component;
        init();
    }

    private void init() {
        initRepaintManager();

        component.setDoubleBuffered( false ); // not necessary. we are already essentially double-buffering it
        refreshImage(); // update it on construction

        // attach the property that our JMERepaintManager will look for. when we receive a repaint request, the dirty flag will be set
        component.putClientProperty( ON_REPAINT_CALLBACK, new VoidFunction0() {
            public void apply() {
                // mark as dirty from the render thread
                dirty = true;
            }
        } );
    }

    public ImmutableVector2F localToComponentCoordinates( ImmutableVector2F localCoordinates ) {
        Point2D swingPoint;
        try {
            swingPoint = getImageTransform().inverseTransform( new Point2D.Float( localCoordinates.x, localCoordinates.y ), new Point2D.Float() );
        }
        catch ( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
        final double x = swingPoint.getX();
        final double y = swingPoint.getY();
        return new ImmutableVector2F( x, y );
    }

    public void handleMouseEvent( int rawX, int rawY ) {
        ImmutableVector2F swingPoint = localToComponentCoordinates( new ImmutableVector2F( rawX, rawY ) );
        final double x = swingPoint.x;
        final double y = swingPoint.y;
//        System.out.println( "swingPoint: " + swingPoint );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                if ( Mouse.getEventButton() == -1 ) {
                    sendAWTMouseEvent( (int) x, (int) y, false, MouseEvent.NOBUTTON );
                }
                else {
                    sendAWTMouseEvent( (int) x, (int) y, Mouse.getEventButtonState(), getSwingButtonIndex( Mouse.getEventButton() ) );
                }
            }
        } );
    }

    // TODO: run this every frame
    public void update() {
        // make sure we acquire the swing thread before doing the repainting that needs to be done
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                if ( dirty ) {
                    refreshImage();
                    dirty = false;
                }
            }
        } );
    }

    public void repaint() {
        dirty = true;
    }

    @Override public void paint( Graphics2D g ) {
        // background is transparent, to support transparent graphics
        g.setBackground( new Color( 0f, 0f, 0f, 0f ) );
        g.clearRect( 0, 0, getWidth(), getHeight() );

        // validating, so that Swing will accept non-frame-connected component trees
        component.validate();

        // set the specific size to the preferred size. otherwise size stays at 0,0
        component.setSize( component.getPreferredSize() );

        // run the full layout now that we have the size
        layoutComponent( component );

        //Fix for rendering problems on 1.5, see #3122
        component.printAll( g );

        /*---------------------------------------------------------------------------*
        * debugging visuals
        *----------------------------------------------------------------------------*/
//        g.setStroke( new BasicStroke( 0 ) );
//        g.setPaint( new Color( 0f, 0f, 1f, 0.25f ) );
//        debuggingRectangle( g, component.getPreferredSize().width, component.getPreferredSize().height );
//
//        g.setTransform( new AffineTransform() );
//        g.setPaint( new Color( 1f, 0f, 0f, 0.5f ) );
//        debuggingRectangle( g, getWidth(), getHeight() );
    }

    private void debuggingRectangle( Graphics2D g, int w, int h ) {
        g.drawRect( 0, 0, w, 1 );
        g.drawRect( 0, 0, 1, h );
        g.drawRect( w - 1, 0, 1, h );
        g.drawRect( 0, h - 1, w, 1 );
    }

    private static void layoutComponent( Component component ) {
        synchronized ( component.getTreeLock() ) {
            component.doLayout();

            if ( component instanceof Container ) {
                for ( Component child : ( (Container) component ).getComponents() ) {
                    layoutComponent( child );
                }
            }
        }
    }

    private void initRepaintManager() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                final RepaintManager repaintManager = RepaintManager.currentManager( component );
                if ( !( repaintManager instanceof LWJGLRepaintManager ) ) {
                    RepaintManager.setCurrentManager( new LWJGLRepaintManager() );
                }
            }
        } );
    }

    /*---------------------------------------------------------------------------*
    * Here follows a lot of JMEDesktop code, listed with the relevant license information
    * see https://code.google.com/p/jmonkeyengine/source/browse/trunk/src/com/jmex/awt/swingui/JMEDesktop.java?r=4078
    *
    * WARNING: This code has not been verified to work flawlessly, only handles mouse motion and clicks,
    *          ignores and does not handle focus, and sometimes does not send events far enough into
    *          PSwing handlers. Modify with caution. (Dragons be here)
    *----------------------------------------------------------------------------*/

    /*
    * Copyright (c) 2003-2008 jMonkeyEngine
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

    private Component lastComponent;
    private Component grabbedMouse;
    private int grabbedMouseButton;
    private int downX = 0;
    private int downY = 0;
    private long lastClickTime = 0;
    private int clickCount = 0;
    private static final int MAX_CLICKED_OFFSET = 4;
    private static final int DOUBLE_CLICK_TIME = 300;

    private boolean useConvertPoint = true;

    private int getSwingButtonIndex( int jmeButtonIndex ) {
        switch( jmeButtonIndex ) {
            case 0:
                return MouseEvent.BUTTON1;
            case 1:
                return MouseEvent.BUTTON2;
            case 2:
                return MouseEvent.BUTTON3;
            default:
                return MouseEvent.NOBUTTON; //todo: warn here?
        }
    }

    private Point convertPoint( Component parent, int x, int y, Component comp ) {
        if ( useConvertPoint && parent != null && comp != null ) {
            try {
                return SwingUtilities.convertPoint( parent, x, y, comp );
            }
            catch ( Throwable e ) {
                useConvertPoint = false;
            }
        }
        if ( comp != null ) {
            while ( comp != parent ) {
                x -= comp.getX();
                y -= comp.getY();
                if ( comp.getParent() == null ) {
                    break;
                }
                comp = comp.getParent();
            }
        }
        return new Point( x, y );
    }


    private void sendAWTMouseEvent( int x, int y, boolean pressed, int swingButton ) {
        Component comp = componentAt( x, y, component, false );
        if ( comp == null ) {
            // added this check so that we send the event anyways, even if the mouse isn't over that portion.
            // this will mean we send events so that Piccolo won't generate false events on re-entry!!! (see
            // the checking for button-down-ness in PCanvas)
            comp = component;
        }

        final int eventType;
        if ( swingButton > MouseEvent.NOBUTTON ) {
            eventType = pressed ? MouseEvent.MOUSE_PRESSED : MouseEvent.MOUSE_RELEASED;
        }
        else {
            eventType = getButtonMask( MouseEvent.NOBUTTON ) == 0 ? MouseEvent.MOUSE_MOVED : MouseEvent.MOUSE_DRAGGED;
        }

        final long time = System.currentTimeMillis();
        if ( lastComponent != comp ) {
            //enter/leave events
            while ( lastComponent != null && ( comp == null || !SwingUtilities.isDescendingFrom( comp, lastComponent ) ) ) {
                final Point pos = convertPoint( component, x, y, lastComponent );
                sendExitedEvent( lastComponent, getCurrentModifiers( swingButton ), pos );
                lastComponent = lastComponent.getParent();
            }
            final Point pos = convertPoint( component, x, y, lastComponent );
            if ( lastComponent == null ) {
                lastComponent = component;
            }
            sendEnteredEvent( comp, lastComponent, getCurrentModifiers( swingButton ), pos );
            lastComponent = comp;
            downX = Integer.MIN_VALUE;
            downY = Integer.MIN_VALUE;
            lastClickTime = 0;
        }

        if ( comp != null ) {
            boolean clicked = false;
            Component compWithPanes = componentAt( x, y, component, true );
            if ( compWithPanes == null ) {
                compWithPanes = comp;
            }
            if ( swingButton > MouseEvent.NOBUTTON ) {
                if ( pressed ) {
                    grabbedMouse = comp;
                    grabbedMouseButton = swingButton;
                    downX = x;
                    downY = y;
                    // we don't use setFocusOwner, since it needs components to be frame-attached
//                    setFocusOwner( componentAt( x, y, panel, true ) );
                    compWithPanes.requestFocus();
//                    dispatchEvent( panel, new FocusEvent( panel,        // TODO: remove this HACK
//                                                          FocusEvent.FOCUS_GAINED, false, null ) );
                }
                else if ( grabbedMouseButton == swingButton && grabbedMouse != null ) {
                    comp = grabbedMouse;
                    grabbedMouse = null;
                    if ( Math.abs( downX - x ) <= MAX_CLICKED_OFFSET && Math.abs( downY - y ) < MAX_CLICKED_OFFSET ) {
                        if ( lastClickTime + DOUBLE_CLICK_TIME > time ) {
                            clickCount++;
                        }
                        else {
                            clickCount = 1;
                        }
                        clicked = true;
                        lastClickTime = time;
                    }
                    downX = Integer.MIN_VALUE;
                    downY = Integer.MIN_VALUE;
                }
            }
            else if ( grabbedMouse != null ) {
                comp = grabbedMouse;
            }

            final Point pos = convertPoint( component, x, y, comp );
            final MouseEvent event = new MouseEvent( comp,
                                                     eventType,
                                                     time, getCurrentModifiers( swingButton ), pos.x, pos.y, clickCount,
                                                     swingButton == MouseEvent.BUTTON2 && pressed, // todo: should this be platform dependent? (e.g. mac)
                                                     swingButton >= 0 ? swingButton : 0 );
            dispatchEvent( comp, event );
            if ( clicked ) {
                // CLICKED seems to need special glass pane handling o_O
                comp = compWithPanes;
                final Point clickedPos = convertPoint( component, x, y, comp );

                final MouseEvent clickedEvent = new MouseEvent( comp,
                                                                MouseEvent.MOUSE_CLICKED,
                                                                time, getCurrentModifiers( swingButton ), clickedPos.x, clickedPos.y, clickCount,
                                                                false, swingButton );
                dispatchEvent( comp, clickedEvent );
            }
        }
        else if ( pressed ) {
            // clicked no component at all
//            setFocusOwner( null );
        }
    }

    private void sendEnteredEvent( Component comp, Component lastComponent, int buttonMask, Point pos ) {
        if ( comp != null && comp != lastComponent ) {
            sendEnteredEvent( comp.getParent(), lastComponent, buttonMask, pos );

            pos = convertPoint( lastComponent, pos.x, pos.y, comp );
            final MouseEvent event = new MouseEvent( comp,
                                                     MouseEvent.MOUSE_ENTERED,
                                                     System.currentTimeMillis(), buttonMask, pos.x, pos.y, 0, false, 0 );
            dispatchEvent( comp, event );
        }

    }

    private void sendExitedEvent( Component lastComponent, int buttonMask, Point pos ) {
        final MouseEvent event = new MouseEvent( lastComponent,
                                                 MouseEvent.MOUSE_EXITED,
                                                 System.currentTimeMillis(), buttonMask, pos.x, pos.y, 1, false, 0 );
        dispatchEvent( lastComponent, event );
    }

    private boolean focusCleared = false;

//    public void setFocusOwner( Component comp ) {
//        if ( comp == null || comp.isFocusable() ) {
//            for ( Component p = comp; p != null; p = p.getParent() ) {
//                if ( p instanceof JInternalFrame ) {
//                    try {
//                        ( (JInternalFrame) p ).setSelected( true );
//                    }
//                    catch ( PropertyVetoException e ) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            awtWindow.setFocusableWindowState( true );
//            Component oldFocusOwner = getFocusOwner();
//            if ( comp == desktop ) {
//                comp = null;
//            }
//            if ( oldFocusOwner != comp ) {
//                if ( oldFocusOwner != null ) {
//                    dispatchEvent( oldFocusOwner, new FocusEvent( oldFocusOwner,
//                                                                  FocusEvent.FOCUS_LOST, false, comp ) );
//                }
//                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
//                if ( comp != null ) {
//                    dispatchEvent( comp, new FocusEvent( comp,
//                                                         FocusEvent.FOCUS_GAINED, false, oldFocusOwner ) );
//                }
//            }
//            awtWindow.setFocusableWindowState( false );
//        }
//        focusCleared = comp == null;
//    }

    private int getCurrentModifiers( int swingBtton ) {
        int modifiers = 0;
        if ( isKeyDown( Keyboard.KEY_LMENU ) ) {
            modifiers |= InputEvent.ALT_DOWN_MASK;
            modifiers |= InputEvent.ALT_MASK;
        }
        if ( isKeyDown( Keyboard.KEY_RMENU ) ) {
            modifiers |= InputEvent.ALT_GRAPH_DOWN_MASK;
            modifiers |= InputEvent.ALT_GRAPH_MASK;
        }
        if ( isKeyDown( Keyboard.KEY_LCONTROL ) || isKeyDown( Keyboard.KEY_RCONTROL ) ) {
            modifiers |= InputEvent.CTRL_DOWN_MASK;
            modifiers |= InputEvent.CTRL_MASK;
        }
        if ( isKeyDown( Keyboard.KEY_LSHIFT ) || isKeyDown( Keyboard.KEY_RSHIFT ) ) {
            modifiers |= InputEvent.SHIFT_DOWN_MASK;
            modifiers |= InputEvent.SHIFT_MASK;
        }
        return modifiers | getButtonMask( swingBtton );
    }

    private boolean isKeyDown( int key ) {
        return Keyboard.isKeyDown( key );
//        return false;
    }


    private int getButtonMask( int swingButton ) {
        int buttonMask = 0;
        if ( Mouse.isButtonDown( 0 ) || swingButton == MouseEvent.BUTTON1 ) {
            buttonMask |= InputEvent.BUTTON1_MASK;
            buttonMask |= InputEvent.BUTTON1_DOWN_MASK;
        }
        if ( Mouse.isButtonDown( 1 ) || swingButton == MouseEvent.BUTTON2 ) {
            buttonMask |= InputEvent.BUTTON2_MASK;
            buttonMask |= InputEvent.BUTTON2_DOWN_MASK;
        }
        if ( Mouse.isButtonDown( 2 ) || swingButton == MouseEvent.BUTTON3 ) {
            buttonMask |= InputEvent.BUTTON3_MASK;
            buttonMask |= InputEvent.BUTTON3_DOWN_MASK;
        }
        return buttonMask;
    }

    public Component componentAt( final int x, final int y ) {
        // wrap the necessary parts within a Swing lock so that we know we don't tromp over parts
        Component component = componentAt( x, y, ComponentImage.this.component, true );
        if ( component != ComponentImage.this.component ) {
            return component;
        }
        return null;
    }

    private Component componentAt( int x, int y, Component parent, boolean scanRootPanes ) {
        if ( scanRootPanes && parent instanceof JRootPane ) {
            JRootPane rootPane = (JRootPane) parent;
            parent = rootPane.getContentPane();
        }

        Component child = parent;
        if ( !parent.contains( x, y ) ) {
            child = null;
        }
        else {
            synchronized ( parent.getTreeLock() ) {
                if ( parent instanceof Container ) {
                    Container container = (Container) parent;
                    int ncomponents = container.getComponentCount();
                    for ( int i = 0; i < ncomponents; i++ ) {
                        Component comp = container.getComponent( i );
                        if ( comp != null
                             && comp.isVisible()
//                             && ( dragAndDropSupport == null || !dragAndDropSupport.isDragPanel( comp ) )
                             && comp.contains( x - comp.getX(), y - comp.getY() ) ) {
                            child = comp;
                            break;
                        }
                    }
                }
            }
        }

        if ( child != null ) {
            if ( parent instanceof JTabbedPane && child != parent ) {
                child = ( (JTabbedPane) parent ).getSelectedComponent();
            }
            x -= child.getX();
            y -= child.getY();
        }
        return child != parent && child != null ? componentAt( x, y, child, scanRootPanes ) : child;
    }

    private void dispatchEvent( final Component receiver, final AWTEvent event ) {
//        if ( getModalComponent() == null || SwingUtilities.isDescendingFrom( receiver, getModalComponent() ) ) {
        if ( !SwingUtilities.isEventDispatchThread() ) {
            throw new IllegalStateException( "not in swing thread!" );
        }
        receiver.dispatchEvent( event );
//        }
    }
}
