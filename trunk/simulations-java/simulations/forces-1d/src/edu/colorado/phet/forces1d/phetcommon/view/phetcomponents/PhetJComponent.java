package edu.colorado.phet.forces1d.phetcommon.view.phetcomponents;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetGraphics2D;

/**
 * User: Sam Reid
 * Date: Mar 8, 2005
 * Time: 9:06:36 PM
 */

public class PhetJComponent extends PhetGraphic {

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
    private MouseInputAdapter mouseListener;
    private KeyListener keyHandler;
    private static PhetJComponentRepaintManager repaintManagerPhet = new PhetJComponentRepaintManager();
    private static PhetJComponentManager manager = new PhetJComponentManager();

    static {
        getManager().addListener( new PJCFocusManager() );
    }

    public static PhetJComponentRepaintManager getRepaintManager() {
        return repaintManagerPhet;
    }

    public static PhetGraphic newInstance( Component apparatusPanel, JComponent jComponent ) {
        return newInstance( apparatusPanel, jComponent, true );
    }

    private static PhetGraphic newInstance( Component apparatusPanel, JComponent jComponent, boolean topLevel ) {
        if ( !inited ) {
            init( null );
//            new RuntimeException( "Focus traversal requires PhetJComponent.init(Window)" ).printStackTrace();
        }

        if ( topLevel ) {
            offscreen.getContentPane().add( jComponent );
        }

        offscreen.getContentPane().validate();
        offscreen.getContentPane().doLayout();

        Dimension dim = jComponent.getPreferredSize();
        if ( topLevel ) {
            jComponent.setBounds( jComponent.getX(), jComponent.getY(), dim.width, dim.height );
        }
        else {
            Container container = jComponent.getParent();
            container.doLayout();
//            System.out.println( "container = " + container.getClass() + ", layout=" + container.getLayout().getClass() );
        }
        doNotifyAll( jComponent );//todo what is this for?
        if ( jComponent.getComponentCount() > 0 ) {

            //This code attempted to create a Graphic Tree with the swing components we contain.
            GraphicLayerSet graphicLayerSet = new GraphicLayerSet();
            Component[] children = jComponent.getComponents();

            validateSuperTree( jComponent );
            graphicLayerSet.addGraphic( new PhetJComponent( apparatusPanel, jComponent, topLevel ) );//the container is the background.

            for ( int i = 0; i < children.length; i++ ) {
                if ( !( children[i] instanceof JComponent ) ) {
                    System.out.println( "children[i] = " + children[i].getClass() );
                }
                else {
                    JComponent child = (JComponent) children[i];
                    Point location = child.getLocation();
//                System.out.println( "location@" + child.getClass() + " = " + location );
                    PhetGraphic pj = PhetJComponent.newInstance( apparatusPanel, child, false );
                    graphicLayerSet.addGraphic( pj );
                    //have to account for parent's locations.

                    pj.setLocation( location.x - jComponent.getX(), location.y - jComponent.getY() );
                }
            }

            return graphicLayerSet;
        }
        else {
            return new PhetJComponent( apparatusPanel, jComponent, topLevel );
        }
    }

    public static void init( Window applicationWindow ) {


        if ( inited ) {
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
        if ( parent != null ) {
            validateSuperTree( parent );
        }
    }

    private static void doNotifyAll( Component jComponent ) {
        jComponent.addNotify();
        if ( jComponent instanceof Container ) {
            Container container = (Container) jComponent;
            Component[] c = container.getComponents();
            for ( int i = 0; i < c.length; i++ ) {
                doNotifyAll( c[i] );
            }
        }
    }

    protected PhetJComponent( Component ap, JComponent component ) {
        this( ap, component, true );
    }

    protected PhetJComponent( Component ap, final JComponent component, boolean topLevel ) {
        super( ap );
        this.component = component;
        this.topLevel = topLevel;
        repaint();

//        System.out.println( "component.getMouseListeners() = " + Arrays.asList( component.getMouseListeners() ) );
        mouseListener = new MouseInputAdapter() {

            public void mouseClicked( MouseEvent e ) {
                applyEvent( component, e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseClicked( newEvent );
                    }

                } );
            }

            // implements java.awt.event.MouseMotionListener
            public void mouseDragged( MouseEvent e ) {
                applyEvent( component, e, new MouseMotionListenerMethod() {

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                        mouseMotionListener.mouseDragged( newEvent );
                    }
                } );
            }

            // implements java.awt.event.MouseListener
            public void mouseEntered( MouseEvent e ) {
                applyEvent( component, e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseEntered( newEvent );
                    }

                } );
            }

            // implements java.awt.event.MouseListener
            public void mouseExited( MouseEvent e ) {
                applyEvent( component, e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseExited( newEvent );
                    }

                } );
            }

            // implements java.awt.event.MouseMotionListener
            public void mouseMoved( MouseEvent e ) {
                applyEvent( component, e, new MouseMotionListenerMethod() {

                    public void invoke( MouseMotionListener mouseMotionListener, MouseEvent newEvent ) {
                        mouseMotionListener.mouseMoved( newEvent );
                    }
                } );
            }

            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                applyEvent( component, e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mousePressed( newEvent );
                    }

                } );
            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                applyEvent( component, e, new MouseListenerMethod() {

                    public void invoke( MouseListener mouseListener, MouseEvent newEvent ) {
                        mouseListener.mouseReleased( newEvent );
                    }

                } );
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
        component.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
//                System.out.println( "PhetJComponent.focusGained=" + component );
                repaint();
            }

            public void focusLost( FocusEvent e ) {
//                System.out.println( "PhetJComponent.focusLost, copmonent=" + component );
                repaint();
            }
        } );
        repaintManagerPhet.put( this );
        manager.phetJComponentCreated( this );
    }

    public static PhetJComponentManager getManager() {
        return manager;
    }

    private static interface KeyMethod {
        public void invoke( KeyListener keyListener, KeyEvent ke );
    }

    private void handleKeypress( KeyEvent e, KeyMethod km ) {
        KeyStroke stroke = KeyStroke.getKeyStrokeForEvent( e );
        ActionListener al = component.getActionForKeyStroke( stroke );
        ActionEvent ae = new ActionEvent( component, e.getID(), e.getKeyChar() + "", e.getWhen(), e.getModifiers() );

        if ( al != null ) {
            al.actionPerformed( ae );
        }
        KeyListener[] kl = component.getKeyListeners();
        KeyEvent event = new KeyEvent( component, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar(), e.getKeyLocation() );
        for ( int i = 0; i < kl.length; i++ ) {
            KeyListener listener = kl[i];
            km.invoke( listener, event );
        }
    }

    /**
     * This is the nonrecursive form, let's keep it around just in case.
     */
    private boolean applyEvent( JComponent component, MouseEvent e, MouseMethod mouseMethod ) {
        MouseListener[] ml = component.getMouseListeners();

        //need an intelligent conversion of mouse point.
        Point pt = toLocalFrame( e.getPoint() );

        MouseEvent newEvent = new MouseEvent( component, e.getID(), System.currentTimeMillis(), e.getModifiers(), pt.x, pt.y, e.getClickCount(), e.isPopupTrigger(), e.getButton() );
        //pass to all listeners, some will be no-ops.//could be rewritten for understandability.
        boolean changed = false;
        if ( mouseMethod instanceof PhetJComponent.MouseListenerMethod ) {
            MouseListenerMethod mle = (MouseListenerMethod) mouseMethod;
            for ( int i = 0; i < ml.length; i++ ) {
                MouseListener mouseListener = ml[i];
                mle.invoke( mouseListener, newEvent );
                changed = true;
            }
        }
        else if ( mouseMethod instanceof MouseMotionListenerMethod ) {
            MouseMotionListenerMethod mmlm = (MouseMotionListenerMethod) mouseMethod;

            MouseMotionListener[] mml = component.getMouseMotionListeners();
            for ( int i = 0; i < mml.length; i++ ) {
                MouseMotionListener mouseMotionListener = mml[i];
                mmlm.invoke( mouseMotionListener, newEvent );
                changed = true;
            }
        }
        else {
            throw new RuntimeException( "Illegal mouse handler class: " + mouseMethod );
        }
        if ( changed ) {
            redraw();
        }
        return changed;
    }

    private Point toLocalFrame( Point point ) {
        AffineTransform affineTransform = getNetTransform();
        try {
            Point2D dst = affineTransform.inverseTransform( point, null );
            return new Point( (int) dst.getX(), (int) dst.getY() );
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
        //the x and y are in the local frame (ie in the parent.)
//        System.out.println( "component = " + component.getClass() + ", x=" + component.getX() + ", y=" + component.getY() );
        Dimension dim = component.getPreferredSize();
        if ( !topLevel ) {
            dim = component.getSize();
        }
        if ( dim.width == 0 || dim.height == 0 ) {
            return;
        }
        Rectangle bounds = component.getBounds();
//        System.out.println( "component = " + component );
//        System.out.println( "bounds = " + bounds );
        if ( !bounds.equals( new Rectangle( dim ) ) ) {
            component.setBounds( 0, 0, dim.width, dim.height );//dimension is set by parent's layout manager.
        }

        if ( image == null ) {
            image = new BufferedImage( dim.width, dim.height, BufferedImage.TYPE_INT_ARGB );
        }
        else {
            if ( image.getWidth() != dim.width || image.getHeight() != dim.height ) {
                image = new BufferedImage( dim.width, dim.height, BufferedImage.TYPE_INT_ARGB );
            }
            else {//reuse the old buffered image.
                Graphics2D g2 = image.createGraphics();

                // Clear the background with the component's color (which may contain alpha).
                Composite saveComposite = g2.getComposite();
                g2.setComposite( AlphaComposite.Clear );
                g2.setColor( component.getBackground() );
                g2.fill( new Rectangle( 0, 0, image.getWidth(), image.getHeight() ) );
                g2.setComposite( saveComposite );
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

    protected Graphics2D createGraphics() {
        return image.createGraphics();
    }

    protected Rectangle determineBounds() {
        if ( image == null ) {
            return null;
        }
        return getNetTransform().createTransformedShape( new Rectangle( 0, 0, image.getWidth(), image.getHeight() ) ).getBounds();
    }

    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) {
            super.saveGraphicsState( g2 );
            RenderingHints hints = super.getRenderingHints();
            if ( hints != null ) {
                g2.setRenderingHints( hints );
            }
            if ( component != null && image != null ) {
                g2.transform( getNetTransform() );
                Object origHint = g2.getRenderingHint( RenderingHints.KEY_INTERPOLATION );
                g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
                g2.drawRenderedImage( image, new AffineTransform() );
                if ( origHint == null ) {
                    origHint = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
                }
                g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, origHint );
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

    public JComponent getSourceComponent() {
        return component;
    }

    public static class PhetJComponentRepaintManager extends RepaintManager {
        private Hashtable table = new Hashtable();//key=JComponent, value=PhetJComponent.
        private static ArrayList dirty = new ArrayList();

        public synchronized void addDirtyRegion( JComponent c, int x, int y, int w, int h ) {
            super.addDirtyRegion( c, x, y, w, h );
            if ( table.containsKey( c ) ) {
                PhetJComponent phetJComponent = (PhetJComponent) table.get( c );
                if ( c.getComponentCount() == 0 && new Exception().getStackTrace().length < 75 ) {
                    if ( !dirty.contains( c ) ) {
                        dirty.add( phetJComponent );
                        if ( dirty.size() > 1000 ) {
                            new RuntimeException( "Too many dirty components: " + dirty.size() ).printStackTrace();
                        }
                    }
                }
            }
        }

        public void put( PhetJComponent phetJComponent ) {
            table.put( phetJComponent.component, phetJComponent );
        }

        public void updateGraphics() {
            for ( int i = 0; i < dirty.size(); i++ ) {//todo coalesce the paint messages.  Can some code from RepaintManager be reused?
                PhetJComponent phetJComponent = (PhetJComponent) dirty.get( i );
                phetJComponent.repaint();
            }
            dirty.clear();
        }
    }
}