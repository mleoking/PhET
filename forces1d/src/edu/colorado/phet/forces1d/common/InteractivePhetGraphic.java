/** Sam Reid*/
package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.CompositeMouseInputListener;
import edu.colorado.phet.common.view.graphics.mousecontrols.CursorControl;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationControl;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Nov 13, 2004
 * Time: 10:48:16 AM
 * Copyright (c) Nov 13, 2004 by Sam Reid
 */
public class InteractivePhetGraphic extends CompositePhetGraphic implements InteractiveGraphic {
    private CompositeMouseInputListener mouseControl;
    private CursorControl handControl;

    public InteractivePhetGraphic( ApparatusPanel panel ) {
        super( panel );
        mouseControl = new CompositeMouseInputListener();
    }

    public void mouseClicked( MouseEvent e ) {
        if( isVisible() ) {
            mouseControl.mouseClicked( e );
        }
    }

    public void mousePressed( MouseEvent e ) {
        if( isVisible() ) {
            mouseControl.mousePressed( e );
        }
    }

    public void mouseReleased( MouseEvent e ) {
        if( isVisible() ) {
            mouseControl.mouseReleased( e );
        }
    }

    public void mouseEntered( MouseEvent e ) {
        if( isVisible() ) {
            mouseControl.mouseEntered( e );
        }
    }

    public void mouseExited( MouseEvent e ) {
        if( isVisible() ) {
            mouseControl.mouseExited( e );
        }
    }

    public void mouseDragged( MouseEvent e ) {
        if( isVisible() ) {
            mouseControl.mouseDragged( e );
        }
    }

    public void mouseMoved( MouseEvent e ) {
        if( isVisible() ) {
            mouseControl.mouseMoved( e );
        }
    }

    public boolean contains( int x, int y ) {
        return isVisible() && super.contains( x, y );
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

}
