/**
 * Class: DefaultInteractiveGraphic
 * Package: edu.colorado.phet.common.view.graphics.paint
 * Author: Another Guy
 * Date: May 21, 2003
 */
package edu.colorado.phet.common.view.graphics;

import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.graphics.mousecontrols.CompositeMouseInputListener;
import edu.colorado.phet.common.view.graphics.mousecontrols.HandCursorControl;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationControl;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * This class facilitates adding behaviors to an interactive graphic.
 * This may be used as a decorator of your home-brewed Graphic and Boundary.
 */
public class DefaultInteractiveGraphic implements InteractiveGraphic {
    Graphic graphic;
    CompositeMouseInputListener mouseControl;
    Boundary boundary;
    private HandCursorControl handControl;

    public DefaultInteractiveGraphic( BoundedGraphic boundedGraphic ) {
        this( boundedGraphic, boundedGraphic );
    }

    public DefaultInteractiveGraphic( Graphic graphic, Boundary boundary ) {
        this.graphic = graphic;
        this.boundary = boundary;
        mouseControl = new CompositeMouseInputListener();
    }

    public void paint( Graphics2D g ) {
        graphic.paint( g );
    }

    public void mouseClicked( MouseEvent e ) {
        mouseControl.mouseClicked( e );
    }

    public void mousePressed( MouseEvent e ) {
        mouseControl.mousePressed( e );
    }

    public void mouseReleased( MouseEvent e ) {
        mouseControl.mouseReleased( e );
    }

    public void mouseEntered( MouseEvent e ) {
        mouseControl.mouseEntered( e );
    }

    public void mouseExited( MouseEvent e ) {
        mouseControl.mouseExited( e );
    }

    public void mouseDragged( MouseEvent e ) {
        mouseControl.mouseDragged( e );
    }

    public void mouseMoved( MouseEvent e ) {
        mouseControl.mouseMoved( e );
    }

    public boolean contains( int x, int y ) {
        return boundary.contains( x, y );
    }

    public void setGraphic( Graphic graphic ) {
        this.graphic = graphic;
    }

    public void setBoundary( Boundary boundary ) {
        this.boundary = boundary;
    }

    /**
     * Cause the cursor to turn into a hand when within the boundary.
     */
    public void addCursorHandBehavior() {
        if( handControl == null ) {
            handControl = new HandCursorControl();
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

    public void addTranslationBehavior( Translatable target ) {
        mouseControl.addMouseInputListener( new TranslationControl( target ) );
    }

    public Graphic getGraphic() {
        return graphic;
    }

    public void addPopupMenuBehavior( final JPopupMenu menu ) {
        MouseInputAdapter adapter = new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                if( SwingUtilities.isRightMouseButton( e ) ) {
                    menu.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        addMouseInputListener( adapter );
    }
}
