// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.TimerTask;

import javax.swing.*;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture2D;

/**
 * TODO: Avoid for now. This class is currently in "disaster zone" mode. many things to fix and clean up
 * TODO: Avoid for now. This class is currently in "disaster zone" mode. many things to fix and clean up
 * TODO: Avoid for now. This class is currently in "disaster zone" mode. many things to fix and clean up
 */
public class HUDNode extends Geometry {

    final JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT ) ) {
        @Override public void update( Graphics g ) {
            System.out.println( "update" );
            super.update( g );
        }

        @Override public boolean isOptimizedDrawingEnabled() {
            return false;
        }
    };

    private Component lastComponent;
    private Component grabbedMouse;
    private int grabbedMouseButton;
    private int downX = 0;
    private int downY = 0;
    private long lastClickTime = 0;
    private int clickCount = 0;
    private static final int MAX_CLICKED_OFFSET = 4;

    private static final int DOUBLE_CLICK_TIME = 300;

    public HUDNode( final int width, final int height, AssetManager assetManager, InputManager inputManager ) {
        super( "HUD", new Quad( width, height, true ) );
        panel.setPreferredSize( new Dimension( width, height ) );
        panel.setSize( panel.getPreferredSize() );
        panel.setDoubleBuffered( false ); // avoids having the RepaintManager attempt to get the containing window (and throw a NPE)

        System.out.println( panel.getBounds() );
        System.out.println( panel.isDisplayable() );

        final PaintableImage image = new PaintableImage( width, height, true ) {
            {
                panel.setDoubleBuffered( false );
                refreshImage();
            }

            @Override public void paint( Graphics2D g ) {
                g.setBackground( new Color( 0f, 0f, 0f, 0f ) );
                g.clearRect( 0, 0, getWidth(), getHeight() );
                panel.validate();
                layoutComponent( panel );
                panel.paint( g );
            }
        };

        // TODO: only update when repaint needed?
        new java.util.Timer().schedule( new TimerTask() {
                                            @Override public void run() {
                                                SwingUtilities.invokeLater( new Runnable() {
                                                    public void run() {
                                                        image.refreshImage();
                                                    }
                                                } );
                                            }
                                        }, 50 );

        inputManager.addRawInputListener( new RawInputListener() {
            public void beginInput() {
            }

            public void endInput() {
            }

            public void onJoyAxisEvent( JoyAxisEvent evt ) {
            }

            public void onJoyButtonEvent( JoyButtonEvent evt ) {
            }

            public void onMouseMotionEvent( final MouseMotionEvent evt ) {
//                System.out.println( "mouse " + evt.getX() + "," + evt.getY() );
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
//                        sendAWTMouseEvent( (int) ( evt.getX() ), (int) ( evt.getY() ), false, MouseEvent.NOBUTTON );
                        sendAWTMouseEvent( (int) ( evt.getX() - getXOffset() ), (int) ( height - evt.getY() + getYOffset() ), false, MouseEvent.NOBUTTON );
                        image.refreshImage();
                    }
                } );
            }

            public void onMouseButtonEvent( final MouseButtonEvent evt ) {
                System.out.println( "mousebutton " + evt.getX() + "," + evt.getY() );
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
//                        sendAWTMouseEvent( (int) ( evt.getX() ), (int) ( evt.getY() ), false, MouseEvent.NOBUTTON );
                        sendAWTMouseEvent( (int) ( evt.getX() - getXOffset() ), (int) ( height - evt.getY() + getYOffset() ), evt.isPressed(), getSwingButtonIndex( evt.getButtonIndex() ) );
                        image.refreshImage();
                    }
                } );
            }

            public void onKeyEvent( KeyInputEvent evt ) {
            }

            public void onTouchEvent( TouchEvent evt ) {
            }
        } );

        setMaterial( new Material( assetManager, "Common/MatDefs/Misc/Unshaded.j3md" ) {{
            setTexture( "ColorMap", new Texture2D() {{
                setImage( image );
            }} );

            getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
            setTransparent( true );
        }} );
//            setQueueBucket( Bucket.Transparent );

        setLocalTranslation( new Vector3f( (float) getXOffset(), (float) getYOffset(), 0 ) );
    }

    public JPanel getPanel() {
        return panel;
    }

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

    private double getXOffset() {
        return 10;
    }

    private double getYOffset() {
        return 10;
    }

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

    private boolean useConvertPoint = true;

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
        Component comp = componentAt( x, y, panel, false );

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
                final Point pos = convertPoint( panel, x, y, lastComponent );
                sendExitedEvent( lastComponent, getCurrentModifiers( swingButton ), pos );
                lastComponent = lastComponent.getParent();
            }
            final Point pos = convertPoint( panel, x, y, lastComponent );
            if ( lastComponent == null ) {
                lastComponent = panel;
            }
            sendEnteredEvent( comp, lastComponent, getCurrentModifiers( swingButton ), pos );
            lastComponent = comp;
            downX = Integer.MIN_VALUE;
            downY = Integer.MIN_VALUE;
            lastClickTime = 0;
        }

        if ( comp != null ) {
            boolean clicked = false;
            if ( swingButton > MouseEvent.NOBUTTON ) {
                if ( pressed ) {
                    grabbedMouse = comp;
                    grabbedMouseButton = swingButton;
                    downX = x;
                    downY = y;
//                    setFocusOwner( componentAt( x, y, panel, true ) );
                    dispatchEvent( panel, new FocusEvent( panel,        // TODO: remove this HACK
                                                          FocusEvent.FOCUS_GAINED, false, null ) );
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

            final Point pos = convertPoint( panel, x, y, comp );
//            System.out.println( "sending mouseevent: " + pos.x + "," + pos.y );
            final MouseEvent event = new MouseEvent( comp,
                                                     eventType,
                                                     time, getCurrentModifiers( swingButton ), pos.x, pos.y, clickCount,
                                                     swingButton == MouseEvent.BUTTON2 && pressed, // todo: should this be platform dependent? (e.g. mac)
                                                     swingButton >= 0 ? swingButton : 0 );
            dispatchEvent( comp, event );
            if ( clicked ) {
                // CLICKED seems to need special glass pane handling o_O
                comp = componentAt( x, y, panel, true );
                final Point clickedPos = convertPoint( panel, x, y, comp );

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
        if ( isKeyDown( KeyInput.KEY_LMENU ) ) {
            modifiers |= InputEvent.ALT_DOWN_MASK;
            modifiers |= InputEvent.ALT_MASK;
        }
        if ( isKeyDown( KeyInput.KEY_RMENU ) ) {
            modifiers |= InputEvent.ALT_GRAPH_DOWN_MASK;
            modifiers |= InputEvent.ALT_GRAPH_MASK;
        }
        if ( isKeyDown( KeyInput.KEY_LCONTROL ) || isKeyDown( KeyInput.KEY_RCONTROL ) ) {
            modifiers |= InputEvent.CTRL_DOWN_MASK;
            modifiers |= InputEvent.CTRL_MASK;
        }
        if ( isKeyDown( KeyInput.KEY_LSHIFT ) || isKeyDown( KeyInput.KEY_RSHIFT ) ) {
            modifiers |= InputEvent.SHIFT_DOWN_MASK;
            modifiers |= InputEvent.SHIFT_MASK;
        }
        return modifiers | getButtonMask( swingBtton );
    }

    private boolean isKeyDown( int key ) {
//        return KeyInput.get().isKeyDown( key );
        return false;
    }


    private int getButtonMask( int swingButton ) {
        int buttonMask = 0;
//        if ( MouseInput.get().isButtonDown( 0 ) || swingButton == MouseEvent.BUTTON1 ) {
//            buttonMask |= InputEvent.BUTTON1_MASK;
//            buttonMask |= InputEvent.BUTTON1_DOWN_MASK;
//        }
//        if ( MouseInput.get().isButtonDown( 1 ) || swingButton == MouseEvent.BUTTON2 ) {
//            buttonMask |= InputEvent.BUTTON2_MASK;
//            buttonMask |= InputEvent.BUTTON2_DOWN_MASK;
//        }
//        if ( MouseInput.get().isButtonDown( 2 ) || swingButton == MouseEvent.BUTTON3 ) {
//            buttonMask |= InputEvent.BUTTON3_MASK;
//            buttonMask |= InputEvent.BUTTON3_DOWN_MASK;
//        }
        return buttonMask;
    }

    public Component componentAt( int x, int y ) {
        Component component = componentAt( x, y, panel, true );
        if ( component != panel ) {
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
}
