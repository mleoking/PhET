// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet.hud;

import java.awt.AWTEvent;
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

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.jmephet.JMETab;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.jmephet.input.JMEInputHandler;

import com.jme3.app.state.AbstractAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
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
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture2D;

/**
 * Creates a JME3 Spatial that displays Swing components within a rectangle. It also
 * listens to mouse events via JMERepaintManager, and will update itself as necessary.
 * <p/>
 * NOTE: call dispose() when you are done with this! (Don't leak memory and processor time)
 */
public class HUDNode extends Geometry {

    private final JComponent component; // our component that we render in our HUD
    private final PaintableImage image; // the image (JME3 texture) to which we render our component
    private final AffineTransform imageTransform;
    private final JMEInputHandler inputHandler;

    // the size of our canvas. this does not change
    private final int width;
    private final int height;

    private Material material;

    private RawInputListener inputListener; // our listener for mouse events, so that we can forward them
    private volatile boolean listenerAttached = false; // whether our listener is listening
    private AbstractAppState state; // our state listener. will get updates every frame until disposed

    private volatile boolean dirty = false; // whether the image needs to be repainted

    private final JMETab tab;
    /**
     * Basically whether this node should be antialiased. If it is set up in a position where the texture (image)
     * pixels are not 1-to-1 with the screen pixels (say, translated by fractions of a pixel, or any rotation),
     * this should be set to true.
     */
    public final Property<Boolean> antialiasing;

    public static final String ON_REPAINT_CALLBACK = "!@#%^&*"; // tag used in the repaint manager to notify this instance for repainting

    public HUDNode( final JComponent component, final int width, final int height,
                    final JMEInputHandler inputHandler, final JMETab tab ) {
        this( component, width, height, new AffineTransform(), inputHandler, tab, new Property<Boolean>( false ) );
    }

    // initialize from the EDT
    public HUDNode( final JComponent component, final int width, final int height, final AffineTransform imageTransform,
                    final JMEInputHandler inputHandler, final JMETab tab, final Property<Boolean> antialiasing ) {
        super( "HUD", new Quad( width, height, true ) ); // "true" flips it so our components are shown in the correct Y direction
        this.component = component;
        this.width = width;
        this.height = height;
        this.imageTransform = imageTransform;
        this.inputHandler = inputHandler;
        this.tab = tab;
        this.antialiasing = antialiasing;

        image = new PaintableImage( width, height, true, imageTransform ) {
            {
                component.setDoubleBuffered( false ); // not necessary. we are already essentially double-buffering it
                refreshImage(); // update it on construction
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
            }
        };

        // attach the property that our JMERepaintManager will look for. when we receive a repaint request, the dirty flag will be set
        component.putClientProperty( ON_REPAINT_CALLBACK, new VoidFunction0() {
            public void apply() {
                // mark as dirty from the render thread
                JMEUtils.invokeLater( new Runnable() {
                    public void run() {
                        repaint();
                    }
                } );
            }
        } );

        // listen to mouse events
        inputListener = new RawInputListener() {
            public void beginInput() {
            }

            public void endInput() {
            }

            public void onJoyAxisEvent( JoyAxisEvent evt ) {
            }

            public void onJoyButtonEvent( JoyButtonEvent evt ) {
            }

            public void onMouseMotionEvent( final MouseMotionEvent evt ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        Vector3f coordinates = transformEventCoordinates( evt.getX(), evt.getY() );
                        sendAWTMouseEvent( (int) coordinates.getX(), (int) coordinates.getY(), false, MouseEvent.NOBUTTON );
                    }
                } );
            }

            public void onMouseButtonEvent( final MouseButtonEvent evt ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        Vector3f coordinates = transformEventCoordinates( evt.getX(), evt.getY() );
                        sendAWTMouseEvent( (int) coordinates.getX(), (int) coordinates.getY(), evt.isPressed(), getSwingButtonIndex( evt.getButtonIndex() ) );
                    }
                } );
            }

            public void onKeyEvent( KeyInputEvent evt ) {
            }

            public void onTouchEvent( TouchEvent evt ) {
            }
        };

        material = new Material( tab.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
            setTexture( "ColorMap", new Texture2D() {{
                setImage( image );

                // set up and listen to any antialiasing changes
                antialiasing.addObserver( new SimpleObserver() {
                    public void update() {
                        // we actually modify the sampling, not the antialiasing if you want to be technical about it
                        if ( antialiasing.get() ) {
                            // allow general sampling
                            setMagFilter( MagFilter.Bilinear );
                            setMinFilter( MinFilter.BilinearNoMipMaps );
                        }
                        else {
                            // when the antialiasing hits this texture, make sure we use nearest-neighbor so that we don't get any blurring
                            setMagFilter( MagFilter.Nearest );
                            setMinFilter( MinFilter.NearestNoMipMaps );
                        }
                    }
                } );
            }} );

            getAdditionalRenderState().setDepthWrite( false );

            getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
            setTransparent( true );
        }};
        setMaterial( material );

        initRepaintManager();

        image.refreshImage();

        // get an update every frame. if our image is dirty, repaint it
        state = new AbstractAppState() {
            @Override public void update( float tpf ) {
                // make sure we acquire the swing thread before doing the repainting that needs to be done
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        if ( dirty ) {
                            image.refreshImage();
                            dirty = false;
                        }
                    }
                } );
            }
        };

        JMEUtils.invoke( new Runnable() {
            public void run() {
                inputHandler.addRawInputListener( inputListener );
                listenerAttached = true;
                tab.attachState( state );
            }
        } );
    }

    public static class HUDNodeCollision {
        public final Vector2f screenPoint;
        public final HUDNode hudNode;
        public final Vector3f hitPoint;
        public final CollisionResult collisionResult;

        public HUDNodeCollision( CollisionResult collisionResult, Vector3f hitPoint, HUDNode hudNode, Vector2f screenPoint ) {
            this.collisionResult = collisionResult;
            this.hitPoint = hitPoint;
            this.hudNode = hudNode;
            this.screenPoint = screenPoint;
        }

        /**
         * NOTE: only call this from the Swing EDT
         *
         * @return The leaf component that was clicked, assuming that it is planar
         */
        public Component getGuiPlaneComponent() {
            // TODO: extend this to work for nodes that aren't planar, etc.!
            return hudNode.componentAt( (int) hitPoint.x, (int) hitPoint.y );
        }
    }

    public JComponent getRootComponent() {
        return component;
    }

    /**
     * NOTE: only call this from the Swing EDT
     *
     * @param view  The view that contains the HUD nodes to look for
     * @param point The screen point at which we are casting the ray
     * @return Collision information
     */
    public static HUDNodeCollision getHUDCollisionUnderPoint( JMEView view, Vector2f point ) {
        CollisionResults results = view.hitsUnderPoint( point );
        for ( CollisionResult result : results ) {

            Geometry geometry = result.getGeometry();
            if ( geometry instanceof HUDNode ) {
                final HUDNode node = (HUDNode) geometry;
                final Vector3f hitPoint = node.transformEventCoordinates( point.x, point.y );

                return new HUDNodeCollision( result, hitPoint, node, point );
            }
        }
        return null;
    }

    /**
     * Runs a callback with either the topmost Component that was under the point, or null if there was none.
     *
     * @param view     The view that contains the HUD nodes to look for
     * @param point    The screen point at which we are casting the ray
     * @param callback Callback to be called, with the component if one exists, or null otherwise
     */
    public static void withComponentUnderPoint( JMEView view, Vector2f point, final VoidFunction1<Component> callback ) {
        final HUDNodeCollision collision = getHUDCollisionUnderPoint( view, point );
        if ( collision != null ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    callback.apply( collision.getGuiPlaneComponent() );
                }
            } );
        }
        else {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    callback.apply( null );
                }
            } );
        }
    }

    /**
     * Runs a callback with either the topmost Component that was under the current mouse location, or null if there was none.
     *
     * @param view         The view that contains the HUD nodes to look for
     * @param inputHandler Input handler
     * @param callback     Callback to be called, with the component if one exists, or null otherwise
     */
    public static void withComponentUnderPointer( JMEView view, JMEInputHandler inputHandler, final VoidFunction1<Component> callback ) {
        withComponentUnderPoint( view, inputHandler.getCursorPosition(), callback );
    }

    public void repaint() {
        dirty = true;
    }

    // All necessary cleanup that we need to do to never use this HUDNode again (don't leak memory). Shouldn't conflict with JME calls
    public void dispose() {
        ignoreInput();
        tab.detachState( state );

        // don't leak memory!
        JMEUtils.getApplication().getRenderer().deleteImage( image );
    }

    /**
     * Call this once and further mouse input will not be forwarded
     */
    public void ignoreInput() {
        if ( listenerAttached ) {
            listenerAttached = false;
            inputHandler.removeRawInputListener( inputListener );
        }
    }

    // ensure that we have the correct repaint manager so that we receive repaint events
    private void initRepaintManager() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                final RepaintManager repaintManager = RepaintManager.currentManager( component );
                if ( !( repaintManager instanceof JMERepaintManager ) ) {
                    RepaintManager.setCurrentManager( new JMERepaintManager() );
                }
            }
        } );
    }

    /**
     * Transform from screen coordinates (JME-based) into Swing-based coordinates for what we are showing
     *
     * @param x Screen X (JME)
     * @param y Screen Y (JME)
     * @return Transformed position, with x and y in the vector. Z is basically useless (we assume an orthographic display)
     */
    public Vector3f transformEventCoordinates( float x, float y ) {
        // do the coordinate transform
        Vector3f transformed = getWorldTransform().transformInverseVector( new Vector3f( x, y, 0 ), new Vector3f() );

        // invert our Y
        transformed.setY( height - transformed.getY() );

        // handle our custom transformation
        try {
            Point2D pt = imageTransform.inverseTransform( new Point2D.Float( transformed.getX(), transformed.getY() ), new Point2D.Float() );
            transformed.set( (float) pt.getX(), (float) pt.getY(), transformed.getZ() );
        }
        catch ( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }

        return transformed;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
        Component component = componentAt( x, y, HUDNode.this.component, true );
        if ( component != HUDNode.this.component ) {
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

    public void refresh() {
        image.refreshImage();
    }
}
