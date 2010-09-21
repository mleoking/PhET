/**
 * Class: DefaultInteractiveGraphic
 * Package: edu.colorado.phet.common.view.graphics.paint
 * Author: Another Guy
 * Date: May 21, 2003
 */
package edu.colorado.phet.radiowaves.common_1200.graphics;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import edu.colorado.phet.common.phetgraphics.view.util.CompositeMouseInputListener;
import edu.colorado.phet.common.phetgraphics.view.util.CursorControl;

/**
 * This class facilitates adding behaviors to an interactive graphic.
 * This may be used as a decorator of your home-brewed Graphic and Boundary.
 */
public class DefaultInteractiveGraphic implements InteractiveGraphic {
    private Graphic graphic;
    private CompositeMouseInputListener mouseControl;
    private Boundary boundary;
    private CursorControl handControl;
    private boolean visible = true;

    public DefaultInteractiveGraphic( BoundedGraphic boundedGraphic ) {
        this( boundedGraphic, boundedGraphic );
    }

    public DefaultInteractiveGraphic( Graphic graphic, Boundary boundary ) {
        this.graphic = graphic;
        this.boundary = boundary;
        mouseControl = new CompositeMouseInputListener();
    }

    public void paint( Graphics2D g ) {
        if( visible ) {
            graphic.paint( g );
        }
    }

    public void mouseClicked( MouseEvent e ) {
        if( visible ) {
            mouseControl.mouseClicked( e );
        }
    }

    public void mousePressed( MouseEvent e ) {
        if( visible ) {
            mouseControl.mousePressed( e );
        }
    }

    public void mouseReleased( MouseEvent e ) {
        if( visible ) {
            mouseControl.mouseReleased( e );
        }
    }

    public void mouseEntered( MouseEvent e ) {
        if( visible ) {
            mouseControl.mouseEntered( e );
        }
    }

    public void mouseExited( MouseEvent e ) {
        if( visible ) {
            mouseControl.mouseExited( e );
        }
    }

    public void mouseDragged( MouseEvent e ) {
        if( visible ) {
            mouseControl.mouseDragged( e );
        }
    }

    public void mouseMoved( MouseEvent e ) {
        if( visible ) {
            mouseControl.mouseMoved( e );
        }
    }

    public boolean contains( int x, int y ) {
        return visible && boundary.contains( x, y );
    }

    public void setGraphic( Graphic graphic ) {
        this.graphic = graphic;
    }

    public void setBoundary( Boundary boundary ) {
        this.boundary = boundary;
    }

    public void setBoundedGraphic( BoundedGraphic boundedGraphic ) {
        setGraphic( boundedGraphic );
        setBoundary( boundedGraphic );
    }

    /**
     * Cause the cursor to turn into a hand when within the boundary.
     */
    public void addCursorHandBehavior() {
        if( handControl == null ) {
            handControl = new CursorControl( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
            mouseControl.addMouseInputListener( handControl );
        }
    }

    public void addCursorBehavior( Cursor customCursor ) {
        if( handControl == null ) {
            handControl = new CursorControl( customCursor );
            mouseControl.addMouseInputListener( handControl );
        }
    }

    public void removeCursorHandBehavior() {
        mouseControl.removeMouseInputListener( handControl );
        handControl = null;
    }

    /**
     * Add an arbitrary MouseInputListener.
     *
     * @param mouseInputAdapter
     */
    public void addMouseInputListener( MouseInputListener mouseInputAdapter ) {
        mouseControl.addMouseInputListener( mouseInputAdapter );
    }

    public void removeMouseInputListener( MouseInputListener mouseInputListener ) {
        mouseControl.removeMouseInputListener( mouseInputListener );
    }

    public void addTranslationBehavior( Translatable target ) {
        mouseControl.addMouseInputListener( new TranslationControl( target ) );
    }

    public Graphic getGraphic() {
        return graphic;
    }

    public void addPopupMenuBehavior( final JPopupMenu menu ) {
        MouseInputAdapter adapter = new MouseInputAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( SwingUtilities.isRightMouseButton( e ) ) {
                    menu.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        addMouseInputListener( adapter );
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
