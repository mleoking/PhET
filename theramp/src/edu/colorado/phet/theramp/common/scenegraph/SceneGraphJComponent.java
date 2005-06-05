package edu.colorado.phet.theramp.common.scenegraph;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphics2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Mar 8, 2005
 * Time: 9:06:36 PM
 * Copyright (c) Mar 8, 2005 by Sam Reid
 */

public class SceneGraphJComponent extends AbstractGraphic {

    private static JWindow offscreen;
    private static boolean inited = false;
    private static JPanel offscreenContentPane = new JPanel( null ) {
        public void invalidate() {
        }

        protected void paintComponent( Graphics g ) {
        }
    };

    private JComponent component;
    private boolean topLevel;
    private BufferedImage image;
    private KeyListener keyHandler;
    private static PhetJComponentRepaintManager repaintManagerPhet = new PhetJComponentRepaintManager();
    public static final AffineTransform IDENTITY = new AffineTransform();

    public static AbstractGraphic newInstance( JComponent jComponent ) {
        return newInstance( jComponent, true );
    }

    private static AbstractGraphic newInstance( JComponent jComponent, boolean topLevel ) {
        if( !inited ) {
            init( null );
//            new RuntimeException( "Focus traversal requires PhetJComponent.init(Window)" ).printStackTrace();
        }

        if( topLevel ) {
            offscreen.getContentPane().add( jComponent );
        }

        offscreen.getContentPane().validate();
        offscreen.getContentPane().doLayout();

        Dimension dim = jComponent.getPreferredSize();
        if( topLevel ) {
            jComponent.reshape( jComponent.getX(), jComponent.getY(), dim.width, dim.height );
        }
        else {
            Container container = jComponent.getParent();
            container.doLayout();
//            System.out.println( "container = " + container.getClass() + ", layout=" + container.getLayout().getClass() );
        }
        doNotifyAll( jComponent );//todo what is this for?
        if( jComponent.getComponentCount() > 0 ) {

            //This code attempted to create a Graphic Tree with the swing components we contain.
            GraphicListNode graphicLayerSet = new GraphicListNode();
            Component[] children = jComponent.getComponents();

            validateSuperTree( jComponent );
            graphicLayerSet.addGraphic( new SceneGraphJComponent( jComponent, topLevel ) );//the container is the background.

            for( int i = 0; i < children.length; i++ ) {
                if( !( children[i] instanceof JComponent ) ) {
                    System.out.println( "children[i] = " + children[i].getClass() );
                }
                else {
                    JComponent child = (JComponent)children[i];
                    Point location = child.getLocation();
//                System.out.println( "location@" + child.getClass() + " = " + location );
                    AbstractGraphic pj = SceneGraphJComponent.newInstance( child, false );
                    graphicLayerSet.addGraphic( pj );
                    //have to account for parent's locations.

//                    pj.setLocation( location.x - jComponent.getX(), location.y - jComponent.getY() );//TODO fixme
                }
            }

            return graphicLayerSet;
        }
        else {
            return new SceneGraphJComponent( jComponent, topLevel );
        }
    }

    public static void init( Window applicationWindow ) {


        if( inited ) {
            throw new RuntimeException( "Multiple inits." );
        }

//        System.out.println( "Setting repaintManagerPhet." );
        RepaintManager.setCurrentManager( repaintManagerPhet );

        offscreen = new JWindow( applicationWindow ) {
            public void invalidate() {
            }

            public void paint( Graphics g ) {
            }
        };       //this seems to work.  I thought you might have needed a visible component, though (maybe for some JVM implementations?)
//        System.out.println( "offscreen.getOwner() = " + offscreen.getOwner() );

//        offscreen.getOwner().setVisible( true );
        offscreen.setSize( 0, 0 );
        offscreen.setVisible( true );
        offscreenContentPane.setOpaque( false );
        offscreen.setContentPane( offscreenContentPane );
        inited = true;
    }

    private static void validateSuperTree( Component jComponent ) {
        jComponent.invalidate();
        jComponent.validate();
        jComponent.doLayout();
        Component parent = jComponent.getParent();
        if( parent != null ) {
            validateSuperTree( parent );
        }
    }

    private static void doNotifyAll( Component jComponent ) {
        jComponent.addNotify();
        if( jComponent instanceof Container ) {
            Container container = (Container)jComponent;
            Component[] c = container.getComponents();
            for( int i = 0; i < c.length; i++ ) {
                doNotifyAll( c[i] );
            }
        }
    }

    protected SceneGraphJComponent( JComponent component ) {
        this( component, true );
    }

    protected SceneGraphJComponent( final JComponent component, boolean topLevel ) {
//        super( ap );
        this.component = component;
        this.topLevel = topLevel;
        repaint();

        SceneGraphMouseHandler mouseListener = new SceneGraphMouseHandler() {

            public void mouseEntered( SceneGraphMouseEvent event ) {

            }

            public void mouseExited( SceneGraphMouseEvent event ) {
            }

            public void mouseDragged( SceneGraphMouseEvent event ) {
            }

            public void mousePressed( SceneGraphMouseEvent event ) {
                applyEvent( component, event, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseMotionListener, MouseEvent newEvent ) {
                        mouseMotionListener.mousePressed( newEvent );
                    }
                } );
            }

            public void mouseReleased( SceneGraphMouseEvent event ) {
                applyEvent( component, event, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseMotionListener, MouseEvent newEvent ) {
                        mouseMotionListener.mouseReleased( newEvent );
                    }
                } );
            }

            public void mouseClicked( SceneGraphMouseEvent event ) {
            }
        };
        addMouseListener( mouseListener );

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
//        addKeyListener( keyHandler );//TODO do

        repaintManagerPhet.put( this );
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
    private boolean applyEvent( JComponent component, SceneGraphMouseEvent scme, MouseMethod mouseMethod ) {
        MouseListener[] ml = component.getMouseListeners();

        //need an intelligent conversion of mouse point.
        MouseEvent mouseEvent = scme.getMouseEvent();
        MouseEvent newEvent = new MouseEvent( component, scme.getID(), System.currentTimeMillis(), mouseEvent.getModifiers(), (int)scme.getX(), (int)scme.getY(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), mouseEvent.getButton() );
        //pass to all listeners, some will be no-ops.//could be rewritten for understandability.
        boolean changed = false;
        if( mouseMethod instanceof SceneGraphJComponent.MouseListenerMethod ) {
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
        return changed;
    }

    private static interface MouseMethod {
    }

    //TODO repaint should call redraw before super.repaint(), and all internal calls should be repaint().
    private void redraw() {
        //the x and y are in the local frame (ie in the parent.)
//        System.out.println( "component = " + component.getClass() + ", x=" + component.getX() + ", y=" + component.getY() );
        Dimension dim = component.getPreferredSize();
        if( !topLevel ) {
            dim = component.getSize();
        }
        if( dim.width == 0 || dim.height == 0 ) {
            return;
        }
        component.reshape( 0, 0, dim.width, dim.height );//dimension is set by parent's layout manager.

        if( image == null ) {
            image = new BufferedImage( dim.width, dim.height, BufferedImage.TYPE_INT_RGB );
        }
        else {
            if( image.getWidth() != dim.width || image.getHeight() != dim.height ) {
                image = new BufferedImage( dim.width, dim.height, BufferedImage.TYPE_INT_RGB );
            }
            else {//reuse the old buffered image.
                Graphics2D g2 = image.createGraphics();
                g2.setColor( component.getBackground() );
                g2.fill( new Rectangle( 0, 0, image.getWidth(), image.getHeight() ) );
            }
        }

        Graphics2D g2 = image.createGraphics();
        g2 = new PhetGraphics2D( g2 );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        component.paint( g2 );
        setBoundsDirty();
    }

    private void setBoundsDirty() {
        //TODO do
    }

    protected Graphics2D createGraphics() {
        return image.createGraphics();
    }

    public void paint( Graphics2D g2 ) {
        if( isVisible() ) {
            super.setup( g2 );
            if( component != null && image != null ) {
                g2.drawRenderedImage( image, IDENTITY );
            }
            super.restore( g2 );
        }
    }

    public Rectangle2D getLocalBounds() {
        return new Rectangle( 0, 0, image.getWidth(), image.getHeight() );
    }

    public void repaint() {
        redraw();//TODO is this necessary?
        super.repaint();//TODO fixme
    }

    private interface MouseListenerMethod extends MouseMethod {

        public void invoke( MouseListener mouseListener, MouseEvent newEvent );

    }

    private interface MouseMotionListenerMethod extends MouseMethod {

        public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent );

    }

    public JComponent getSourceComponent() {
        return component;
    }

    public static class PhetJComponentRepaintManager extends RepaintManager {
        private Hashtable table = new Hashtable();//key=JComponent, value=PhetJComponent.

        public synchronized void addDirtyRegion( JComponent c, int x, int y, int w, int h ) {
            super.addDirtyRegion( c, x, y, w, h );
            if( table.containsKey( c ) ) {
                SceneGraphJComponent phetJComponent = (SceneGraphJComponent)table.get( c );
                if( c.getComponentCount() == 0 ) {
                    phetJComponent.repaint(); // queue up repaint request
                }
            }
        }

        public void put( SceneGraphJComponent phetJComponent ) {
            table.put( phetJComponent.component, phetJComponent );
        }

    }
}