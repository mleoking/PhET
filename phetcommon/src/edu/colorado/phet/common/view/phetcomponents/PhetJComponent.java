/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.view.phetcomponents;

import edu.colorado.phet.common.view.ApparatusPanel;
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

    public static PhetJComponent newInstance( ApparatusPanel apparatusPanel, JComponent jComponent ) {
        //some special cases.
        if( jComponent instanceof JTextComponent ) {
            return new PhetJTextComponent( apparatusPanel, (JTextComponent)jComponent );
        }
        else if( jComponent instanceof JToggleButton ) {
            return new PhetJToggleButton( apparatusPanel, (JToggleButton)jComponent );
        }
        else {
            return new PhetJComponent( apparatusPanel, jComponent );
        }
    }

    protected PhetJComponent( ApparatusPanel ap, final JComponent component ) {
        super( ap );
        this.component = component;
        offscreen.getContentPane().add( component );
        redraw();

        component.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                System.out.println( "evt = " + evt );
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        redraw();
                    }
                } );
            }
        } );

        //TODO see about InputMethodListener

        component.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
                redraw();
            }

            public void focusLost( FocusEvent e ) {
                redraw();
            }
        } );

        System.out.println( "component.getMouseListeners() = " + Arrays.asList( component.getMouseListeners() ) );
        mouseListener = new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mouseClicked( MouseEvent e ) {
                applyEvent( e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseClicked( newEvent );
                    }

                } );

            }

            // implements java.awt.event.MouseMotionListener
            public void mouseDragged( MouseEvent e ) {
                applyEvent( e, new MouseMotionListenerMethod() {

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                        mouseMotionListener.mouseDragged( newEvent );
                    }
                } );
            }

            // implements java.awt.event.MouseListener
            public void mouseEntered( MouseEvent e ) {
                applyEvent( e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseEntered( newEvent );
                    }

                } );
            }

            // implements java.awt.event.MouseListener
            public void mouseExited( MouseEvent e ) {
                applyEvent( e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseExited( newEvent );
                    }

                } );
            }

            // implements java.awt.event.MouseMotionListener
            public void mouseMoved( MouseEvent e ) {
                applyEvent( e, new MouseMotionListenerMethod() {

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                        mouseMotionListener.mouseMoved( newEvent );
                    }
                } );
            }

            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                applyEvent( e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mousePressed( newEvent );
                    }

                } );

            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                applyEvent( e, new MouseListenerMethod() {
                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseReleased( newEvent );
                    }

                } );

            }
        };
        addMouseInputListener( mouseListener );

        keyHandler = new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                handleKeypress( e );
            }

            public void keyReleased( KeyEvent e ) {
                handleKeypress( e );
            }

            public void keyTyped( KeyEvent e ) {
                handleKeypress( e );
            }
        };
        addKeyListener( keyHandler );

    }

    private void handleKeypress( KeyEvent e ) {
        KeyStroke stroke = KeyStroke.getKeyStrokeForEvent( e );
        ActionListener al = component.getActionForKeyStroke( stroke );
        ActionEvent ae = new ActionEvent( component, e.getID(), e.getKeyChar() + "", e.getWhen(), e.getModifiers() );

        if( al != null ) {
            al.actionPerformed( ae );
            redraw();
        }
    }

    private void applyEvent( MouseEvent e, MouseMethod mouseMethod ) {
        MouseListener[] ml = component.getMouseListeners();

        //need an intelligent conversion of mouse point.
        Point pt = toLocalFrame( e.getPoint() );

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
        if( changed ) {
            redraw();
        }
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

    protected void redraw() {
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
        setBoundsDirty();
        autorepaint();
    }

    protected Graphics2D createGraphics() {
        return image.createGraphics();
    }

    /**
     * Forces a repaint of this graphic.
     */
    protected void forceRepaint() {
        syncBounds();
        Rectangle bounds = getBounds();
        getComponent().repaint( bounds.x, bounds.y, bounds.width, bounds.height );
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

    private interface MouseListenerMethod extends MouseMethod {

        public void invoke( MouseListener mouseListener, MouseEvent newEvent );

    }

    private interface MouseMotionListenerMethod extends MouseMethod {

        public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent );

    }

}