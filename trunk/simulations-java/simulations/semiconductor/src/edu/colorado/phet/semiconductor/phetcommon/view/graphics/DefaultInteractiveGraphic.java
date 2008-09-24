/**
 * Class: DefaultInteractiveGraphic
 * Package: edu.colorado.phet.common.view.graphics.paint
 * Author: Another Guy
 * Date: May 21, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.view.graphics;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

import edu.colorado.phet.semiconductor.phetcommon.view.graphics.bounds.Boundary;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.mousecontrols.CompositeMouseInputListener;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.mousecontrols.HandCursorControl;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.mousecontrols.TranslationControl;

/**
 * This class facilitates adding behaviors to an interactive graphic.
 */
public class DefaultInteractiveGraphic implements InteractiveGraphic {
    Graphic graphic;
    CompositeMouseInputListener mouseControl;
    Boundary boundary;
    private HandCursorControl handControl;

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

    /**
     * Cause the cursor to turn into a hand when within the boundary.
     */
    public void addCursorHandBehavior() {
        if ( handControl == null ) {
            handControl = new HandCursorControl();
            mouseControl.addMouseInputListener( handControl );
        }
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

}
