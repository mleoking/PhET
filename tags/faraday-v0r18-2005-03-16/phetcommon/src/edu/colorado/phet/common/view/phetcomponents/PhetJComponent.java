/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.view.phetcomponents;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Mar 8, 2005
 * Time: 9:06:36 PM
 * Copyright (c) Mar 8, 2005 by Sam Reid
 */

public class PhetJComponent extends PhetGraphic {

    private static final JWindow offscreen;

    private JComponent component;
    private BufferedImage image;
    private MouseInputAdapter mouseListener;
    private KeyListener keyHandler;

    static {
        offscreen = new JWindow();       //this seems to work.  I thought you might have needed a visible component, though (maybe for some JVM implementations?)
        offscreen.getContentPane().setLayout( null ); //so we can use reshape for absolute layout.
    }

//    public static PhetJComponent newInstance( ApparatusPanel apparatusPanel, JComponent jComponent ) {
    public static PhetGraphic newInstance( Component apparatusPanel, JComponent jComponent ) {
        doNotifyAll( jComponent );
        //some special cases.
        if( jComponent instanceof JTextComponent ) {
            return new PhetJTextComponent( apparatusPanel, (JTextComponent)jComponent );
        }
        else if( jComponent instanceof JToggleButton ) {
            return new PhetJToggleButton( apparatusPanel, (JToggleButton)jComponent );
        }
        else if( jComponent.getComponentCount() > 0 ) {
            return new PhetJComponent( apparatusPanel, jComponent );

            //This code attempted to create a Graphic Tree with the swing components we contain.
//            GraphicLayerSet graphicLayerSet = new GraphicLayerSet();
//            Component[] children = jComponent.getComponents();
//            jComponent.invalidate();
//            jComponent.validate();
//            jComponent.doLayout();
//            graphicLayerSet.addGraphic( new PhetJComponent( apparatusPanel, jComponent ) );//the container is the background.
//
//            for( int i = 0; i < children.length; i++ ) {
//                JComponent child = (JComponent)children[i];
//                Point location = child.getLocation();
//                System.out.println( "location = " + location );
//                PhetGraphic pj = PhetJComponent.newInstance( apparatusPanel, child );
//                graphicLayerSet.addGraphic( pj );
////                pj.setLocation( location.x + 120, location.y );
//                pj.setLocation( location.x, location.y  );
//            }
//
//            return graphicLayerSet;
        }
        else {
            return new PhetJComponent( apparatusPanel, jComponent );
        }
    }

    private static void doNotifyAll( JComponent jComponent ) {
        jComponent.addNotify();
        Component[] c = jComponent.getComponents();
        for( int i = 0; i < c.length; i++ ) {
            JComponent component = (JComponent)c[i];
            doNotifyAll( component );
        }
    }

    protected PhetJComponent( Component ap, final JComponent component ) {
        super( ap );
        this.component = component;
//        component.addNotify();
        offscreen.getContentPane().add( component );
        repaint();

        component.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                System.out.println( "evt = " + evt );
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        repaint();
                    }
                } );
            }
        } );

        //TODO see about InputMethodListener

        component.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
                repaint();
            }

            public void focusLost( FocusEvent e ) {
                repaint();
            }
        } );

        System.out.println( "component.getMouseListeners() = " + Arrays.asList( component.getMouseListeners() ) );
        mouseListener = new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mouseClicked( MouseEvent e ) {
                boolean handled = applyEventRecursive( component, e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseClicked( newEvent );
                    }

                }, toLocalFrame( e.getPoint() ) );
                if( handled ) {
                    repaint();
                }

            }

            // implements java.awt.event.MouseMotionListener
            public void mouseDragged( MouseEvent e ) {
                boolean handled = applyEventRecursive( component, e, new MouseMotionListenerMethod() {

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                        mouseMotionListener.mouseDragged( newEvent );
                    }
                }, toLocalFrame( e.getPoint() ) );
                if( handled ) {
                    repaint();
                }
            }

            // implements java.awt.event.MouseListener
            public void mouseEntered( MouseEvent e ) {
                boolean handled = applyEventRecursive( component, e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseEntered( newEvent );
                    }

                }, toLocalFrame( e.getPoint() ) );
                if( handled ) {
                    repaint();
                }
            }

            // implements java.awt.event.MouseListener
            public void mouseExited( MouseEvent e ) {
                boolean handled = applyEventRecursive( component, e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseExited( newEvent );
                    }

                }, toLocalFrame( e.getPoint() ) );
                if( handled ) {
                    repaint();
                }
            }

            // implements java.awt.event.MouseMotionListener
            public void mouseMoved( MouseEvent e ) {
                boolean handled = applyEventRecursive( component, e, new MouseMotionListenerMethod() {

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                        mouseMotionListener.mouseDragged( newEvent );
                    }
                }, toLocalFrame( e.getPoint() ) );
                if( handled ) {
                    repaint();
                }
            }

            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                boolean handled = applyEventRecursive( component, e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mousePressed( newEvent );
                    }

                }, toLocalFrame( e.getPoint() ) );
                if( handled ) {
                    repaint();
                }
            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                boolean handled = applyEventRecursive( component, e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseReleased( newEvent );
                    }

                }, toLocalFrame( e.getPoint() ) );
                if( handled ) {
                    repaint();
                }
            }
        };
        addMouseInputListener( mouseListener );

        keyHandler = new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                handleKeypress( e, new KeyMethod() {
                    public void invoke( KeyListener keyListener, KeyEvent ke ) {
                        keyListener.keyPressed( ke );
                    }
                } );
            }

            public void keyReleased( KeyEvent e ) {

                handleKeypress( e, new KeyMethod() {
                    public void invoke( KeyListener keyListener, KeyEvent ke ) {
                        keyListener.keyReleased( ke );
                    }
                } );
            }

            public void keyTyped( KeyEvent e ) {
                handleKeypress( e, new KeyMethod() {
                    public void invoke( KeyListener keyListener, KeyEvent ke ) {
                        keyListener.keyTyped( ke );
                    }
                } );
            }
        };
        addKeyListener( keyHandler );

    }

    private static interface KeyMethod {
        public void invoke( KeyListener keyListener, KeyEvent ke );
    }

    private void handleKeypress( KeyEvent e, KeyMethod km ) {
        KeyStroke stroke = KeyStroke.getKeyStrokeForEvent( e );
        ActionListener al = component.getActionForKeyStroke( stroke );
        ActionEvent ae = new ActionEvent( component, e.getID(), e.getKeyChar() + "", e.getWhen(), e.getModifiers() );

        if( al != null ) {
            al.actionPerformed( ae );
            repaint();
        }
        KeyListener[] kl = component.getKeyListeners();
        KeyEvent event = new KeyEvent( component, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar(), e.getKeyLocation() );
        for( int i = 0; i < kl.length; i++ ) {
            KeyListener listener = kl[i];
            km.invoke( listener, event );
        }
    }

    /**
     * This is the nonrecursive form, let's keep it around just in case.
     */
//    private void applyEventOrig( MouseEvent e, MouseMethod mouseMethod ) {
//        MouseListener[] ml = component.getMouseListeners();
//
//        //need an intelligent conversion of mouse point.
//        Point pt = toLocalFrame( e.getPoint() );
//
//        MouseEvent newEvent = new MouseEvent( component, e.getID(), System.currentTimeMillis(), e.getModifiers(), pt.x, pt.y, e.getClickCount(), e.isPopupTrigger(), e.getButton() );
//        //pass to all listeners, some will be no-ops.//could be rewritten for understandability.
//        boolean changed = false;
//        if( mouseMethod instanceof PhetJComponent.MouseListenerMethod ) {
//            MouseListenerMethod mle = (MouseListenerMethod)mouseMethod;
//            for( int i = 0; i < ml.length; i++ ) {
//                MouseListener mouseListener = ml[i];
//                mle.invoke( mouseListener, newEvent );
//                changed = true;
//            }
//        }
//        else if( mouseMethod instanceof MouseMotionListenerMethod ) {
//            MouseMotionListenerMethod mmlm = (MouseMotionListenerMethod)mouseMethod;
//
//            MouseMotionListener[] mml = component.getMouseMotionListeners();
//            for( int i = 0; i < mml.length; i++ ) {
//                MouseMotionListener mouseMotionListener = mml[i];
//                mmlm.invoke( mouseMotionListener, newEvent );
//                changed = true;
//            }
//        }
//        else {
//            throw new RuntimeException( "Illegal mouse handler class: " + mouseMethod );
//        }
//        if( changed ) {
//            redraw();
//        }
//    }

    private boolean applyEventRecursive( JComponent component, MouseEvent e, MouseMethod mouseMethod, Point pt ) {
        MouseListener[] ml = component.getMouseListeners();

        //need an intelligent conversion of mouse point.
//        Point pt = toLocalFrame( e.getPoint() );

        MouseEvent newEvent = new MouseEvent( component, e.getID(), System.currentTimeMillis(), e.getModifiers(), pt.x, pt.y, e.getClickCount(), e.isPopupTrigger(), e.getButton() );
        //pass to all listeners, some will be no-ops.//could be rewritten for understandability.
        boolean changed = false;
        if( mouseMethod instanceof PhetJComponent.MouseListenerMethod ) {
            MouseListenerMethod mle = (MouseListenerMethod)mouseMethod;
            for( int i = 0; i < ml.length; i++ ) {
                MouseListener mouseListener = ml[i];
                mle.invoke( mouseListener, newEvent );
                changed = true;
            }
        }
        else if( mouseMethod instanceof MouseMotionListenerMethod ) {
            MouseMotionListenerMethod mmlm = (MouseMotionListenerMethod)mouseMethod;

            MouseMotionListener[] mml = component.getMouseMotionListeners();
            for( int i = 0; i < mml.length; i++ ) {
                MouseMotionListener mouseMotionListener = mml[i];
                mmlm.invoke( mouseMotionListener, newEvent );
                changed = true;
            }
        }
        else {
            throw new RuntimeException( "Illegal mouse handler class: " + mouseMethod );
        }
//        if( changed ) {
//            redraw();
//        }
        if( component.getComponentCount() > 0 ) {
            Component[] children = component.getComponents();
            for( int i = 0; i < children.length; i++ ) {

                JComponent child = (JComponent)children[i];
                Point rel = new Point( pt.x - child.getX(), pt.y - child.getY() );
//                JComponent origComponent = component;
//                this.component = child;
                boolean c = applyEventRecursive( child, e, mouseMethod, rel );//transform it to child coordinates and set the source.
//                this.component = origComponent;
                if( c ) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    private Point toLocalFrame( Point point ) {
        AffineTransform affineTransform = getNetTransform();
        try {
            Point2D dst = affineTransform.inverseTransform( point, null );
            return new Point( (int)dst.getX(), (int)dst.getY() );
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    private static interface MouseMethod {
    }

    //TODO repaint should call redraw before super.repaint(), and all internal calls should be repaint().
    private void redraw() {
//        Rectangle origBounds = component.getBounds();
        component.reshape( 0, 0, component.getPreferredSize().width, component.getPreferredSize().height );
        if( image == null ) {
            image = new BufferedImage( component.getPreferredSize().width, component.getPreferredSize().height, BufferedImage.TYPE_INT_RGB );
        }
        else {
            if( image.getWidth() != component.getPreferredSize().width || image.getHeight() != component.getPreferredSize().height ) {
                image = new BufferedImage( component.getPreferredSize().width, component.getPreferredSize().height, BufferedImage.TYPE_INT_RGB );
            }
            else {//reuse the old buffered image.
                Graphics2D g2 = image.createGraphics();
                g2.setColor( component.getBackground() );
                g2.fill( new Rectangle( 0, 0, image.getWidth(), image.getHeight() ) );
            }
        }

        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
        g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

        component.paint( g2 );
//        component.reshape( origBounds.x, origBounds.y, origBounds.width, origBounds.height );
        Component[] children = component.getComponents();
        component.invalidate();
        component.validate();
        component.doLayout();
        for( int i = 0; i < children.length; i++ ) {
            JComponent child = (JComponent)children[i];
            Point loc = child.getLocation();
            System.out.println( "loc = " + loc );
            g2.translate( loc.x, loc.y );//this will need
            child.paint( g2 );
            g2.translate( -loc.x, -loc.y );
        }

        setBoundsDirty();
//        autorepaint();
    }

    protected Graphics2D createGraphics() {
        return image.createGraphics();
    }

    protected Rectangle determineBounds() {
        return getNetTransform().createTransformedShape( new Rectangle( 0, 0, image.getWidth(), image.getHeight() ) ).getBounds();
    }

    public void paint( Graphics2D g2 ) {
        if( isVisible() ) {
            super.saveGraphicsState( g2 );
            RenderingHints hints = super.getRenderingHints();
            if( hints != null ) {
                g2.setRenderingHints( hints );
            }
            if( component != null ) {
                g2.transform( getNetTransform() );
                g2.drawRenderedImage( image, new AffineTransform() );
            }
            super.restoreGraphicsState();
        }
    }

    public void repaint() {
        redraw();//TODO is this necessary?
        super.repaint();
    }

    private interface MouseListenerMethod extends MouseMethod {

        public void invoke( MouseListener mouseListener, MouseEvent newEvent );

    }

    private interface MouseMotionListenerMethod extends MouseMethod {

        public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent );

    }

}