/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.tests.phetjcomponents;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.SpinnerUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.text.Caret;
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
    private JComponent component;
    private static final JWindow offscreen;
    private BufferedImage image;
    private boolean autorepaintCaret = true;

    static {
        offscreen = new JWindow();       //this seems to work.  I thought you might have needed a visible component, though (maybe for some JVM implementations?)
        offscreen.getContentPane().setLayout( null ); //so we can use reshape for absolute layout.
//        offscreen.setVisible( false );
    }

    public PhetJComponent( ApparatusPanel ap, JComponent jb ) {
        super( ap );
        this.component = jb;
        offscreen.getContentPane().add( component );
        redraw();
        if( jb instanceof JToggleButton ) {//good candidate for factory method on PhetJComponent.
            JToggleButton jtb = (JToggleButton)jb;
            jtb.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    redraw();
                }
            } );
        }
        jb.addPropertyChangeListener( new PropertyChangeListener() {
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

        jb.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
                redraw();
            }

            public void focusLost( FocusEvent e ) {
                redraw();
            }
        } );

        System.out.println( "component.getMouseListeners() = " + Arrays.asList( component.getMouseListeners() ) );
        addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mouseClicked( MouseEvent e ) {
                applyEvent( e, new MouseMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseClicked( newEvent );
                    }

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                    }
                } );

            }

            // implements java.awt.event.MouseMotionListener
            public void mouseDragged( MouseEvent e ) {
                applyEvent( e, new MouseMethod() {
                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                    }

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                        mouseMotionListener.mouseDragged( newEvent );
                    }
                } );
            }

            // implements java.awt.event.MouseListener
            public void mouseEntered( MouseEvent e ) {
                applyEvent( e, new MouseMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseEntered( newEvent );
                    }

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                    }
                } );
            }

            // implements java.awt.event.MouseListener
            public void mouseExited( MouseEvent e ) {
                applyEvent( e, new MouseMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseExited( newEvent );
                    }

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                    }
                } );
            }

            // implements java.awt.event.MouseMotionListener
            public void mouseMoved( MouseEvent e ) {
                applyEvent( e, new MouseMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {

                    }

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                        mouseMotionListener.mouseMoved( newEvent );
                    }
                } );
            }

            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                applyEvent( e, new MouseMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mousePressed( newEvent );
                    }

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                    }
                } );
            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                applyEvent( e, new MouseMethod() {
                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseReleased( newEvent );
                    }

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                    }
                } );

            }
        } );

        addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                KeyStroke stroke = KeyStroke.getKeyStrokeForEvent( e );
                ActionListener al = component.getActionForKeyStroke( stroke );
                ActionEvent ae = new ActionEvent( component, e.getID(), e.getKeyChar() + "", e.getWhen(), e.getModifiers() );

                if( al != null ) {
                    al.actionPerformed( ae );
                    redraw();
                }
            }

            public void keyReleased( KeyEvent e ) {
                KeyStroke stroke = KeyStroke.getKeyStrokeForEvent( e );
                ActionListener al = component.getActionForKeyStroke( stroke );
                if( e == null ) {
                    throw new RuntimeException( "Null keyEvent." );
                }
                ActionEvent ae = new ActionEvent( component, e.getID(), e.getKeyChar() + "", e.getWhen(), e.getModifiers() );
                if( al != null ) {
                    al.actionPerformed( ae );
                    redraw();
                }
            }

            public void keyTyped( KeyEvent e ) {
                KeyStroke stroke = KeyStroke.getKeyStrokeForEvent( e );
                ActionListener al = component.getActionForKeyStroke( stroke );
                if( e == null ) {
                    throw new RuntimeException( "Null keyEvent." );
                }
                ActionEvent ae = new ActionEvent( component, e.getID(), e.getKeyChar() + "", e.getWhen(), e.getModifiers() );

                if( al != null ) {
                    al.actionPerformed( ae );
                    redraw();
                }
            }
        } );
        if( component instanceof JTextComponent ) {
            JTextComponent jtc = (JTextComponent)component;
            Caret caret = jtc.getCaret();
            int blinkRate = caret.getBlinkRate();
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    redraw();//todo just redraw the caret region.
                }
            };
            Timer timer = new Timer( blinkRate, actionListener );
            if( autorepaintCaret ) {
                timer.start();
            }
        }
    }

    private void applyEvent( MouseEvent e, MouseMethod mouseMethod ) {
        MouseListener[] ml = component.getMouseListeners();

        //need an intelligent conversion of mouse point.
        Point pt = toLocalFrame( e.getPoint() );

//        System.out.println( "pt = " + pt );

        MouseEvent newEvent = new MouseEvent( component, e.getID(), System.currentTimeMillis(), e.getModifiers(), pt.x, pt.y, e.getClickCount(), e.isPopupTrigger(), e.getButton() );
        //pass to all listeners, some will be no-ops.//could be rewritten for understandability.
        for( int i = 0; i < ml.length; i++ ) {
            MouseListener mouseListener = ml[i];
            mouseMethod.invoke( mouseListener, newEvent );
        }
        MouseMotionListener[] mml = component.getMouseMotionListeners();
        for( int i = 0; i < mml.length; i++ ) {
            MouseMotionListener mouseMotionListener = mml[i];
            mouseMethod.invoke( mouseMotionListener, newEvent );
        }
        redraw();
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

    static interface MouseMethod {
        public void invoke( MouseListener mouseListener, MouseEvent newEvent );

        public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent );
    }

    private void redraw() {
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

        if( component instanceof JSpinner ) {
            JSpinner js = (JSpinner)component;
            SpinnerUI su = js.getUI();
            BasicSpinnerUI bsu = (BasicSpinnerUI)su;

//            js.getEditor().setPreferredSize( new Dimension( 10, 10 ) );
//            js.getEditor().paint( g2 );
//            js.getParent().paintComponents( g2 );
//            JComponent jc=js.
//            js.get
        }

        /**How to handle text..?*/
        if( component instanceof JTextComponent ) {
            JTextComponent jtc = (JTextComponent)component;
            Caret caret = jtc.getCaret();
            caret.setVisible( true );
            caret.setSelectionVisible( true );
            int blinkie = caret.getBlinkRate();
            long time = System.currentTimeMillis();
            long remainder = time % ( blinkie * 2 );
            if( remainder <= 500 ) {
                caret.paint( g2 );
            }
//            if( System.currentTimeMillis() % blinkie ) {
//                caret.paint( g2 );
//            }
        }

        setBoundsDirty();
        autorepaint();
    }

    /**
     * Forces a repaint of this graphic.
     */
    protected void forceRepaint() {
        syncBounds();
//        component.paintImmediately( getBounds() );
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

}