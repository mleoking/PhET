/*, 2003.*/
package edu.colorado.phet.conductivity.oldphetgraphics;

import edu.colorado.phet.conductivity.oldphetgraphics.Boundary;
import edu.colorado.phet.common.phetcommon.util.MultiMap;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;

/**
 * User: Sam Reid
 * Date: Nov 3, 2003
 * Time: 2:14:33 AM
 */
public class MouseManager implements MouseInputListener /*, Boundary*/ {
    MultiMap am;
    MouseInputListener activeUnit;
//    BoundedMouseListener activeUnit;

    public MouseManager( MultiMap am ) {
        this.am = am;
    }

    public void mouseClicked( MouseEvent e ) {
        //Make sure we're over the active guy.
        mouseMoved( e );
    }

    public void mousePressed( MouseEvent e ) {
        handleEntranceAndExit( e );
        if( activeUnit != null ) {
            activeUnit.mousePressed( e );
        }
//            activeUnit.getMouseInputListener().mousePressed( e );
    }

    public void mouseReleased( MouseEvent e ) {
        handleEntranceAndExit( e );
        if( activeUnit != null ) {
            activeUnit.mouseReleased( e );
        }
//            activeUnit.getMouseInputListener().mouseReleased( e );
    }

    public void mouseEntered( MouseEvent e ) {
        handleEntranceAndExit( e );
    }

    public void mouseExited( MouseEvent e ) {
        handleEntranceAndExit( e );
    }

    public void mouseDragged( MouseEvent e ) {
        if( activeUnit != null ) {
            activeUnit.mouseDragged( e );
        }
//            activeUnit.getMouseInputListener().mouseDragged( e );
    }

    private MouseInputListener getHandler( MouseEvent e ) {
//    private BoundedMouseListener getHandler( MouseEvent e ) {
//        DefaultInteractiveGraphic[] units = (DefaultInteractiveGraphic[])am.toArray( new DefaultInteractiveGraphic[0] );
        Iterator it = am.reverseIterator();
        while( it.hasNext() ) {
            Object o = it.next();
            if( o instanceof Boundary && o instanceof MouseInputListener ) {
                Boundary boundary = (Boundary)o;
                if( boundary.contains( e.getX(), e.getY() ) ) {
                    return (MouseInputListener)boundary;
                }
            }
//        BoundedMouseListener[] units = (BoundedMouseListener[])am.toArray( new BoundedMouseListener[0] );
//        for( int i = units.length - 1; i >= 0; i-- ) {
//            DefaultInteractiveGraphic unit = units[i];
//            BoundedMouseListener unit = units[i];
//            if( unit.contains( e.getX(), e.getY() ) ) {
//            if( unit.getAbstractShape().contains( e.getX(), e.getY() ) ) {
//                return unit;
//            }
        }
        return null;
    }

    //Does nothing if we're already over the right handler.
    private void handleEntranceAndExit( MouseEvent e ) {
        MouseInputListener unit = getHandler( e );
//        BoundedMouseListener unit = getHandler( e );
        if( unit == null ) {
            if( activeUnit != null ) {
                activeUnit.mouseExited( e );
//                activeUnit.getMouseInputListener().mouseExited( e );
                activeUnit = null;
            }
        }
        else if( unit != null ) {
            if( activeUnit == unit ) {
                //same guy
            }
            else if( activeUnit == null ) {
                //Fire a mouse entered, set the active unit.
                activeUnit = unit;
                activeUnit.mouseEntered( e );
//                activeUnit.getMouseInputListener().mouseEntered( e );
            }
            else if( activeUnit != unit ) {
                //Switch active units.
                activeUnit.mouseExited( e );
//                activeUnit.getMouseInputListener().mouseExited( e );
                activeUnit = unit;
                activeUnit.mouseEntered( e );
//                activeUnit.getMouseInputListener().mouseEntered( e );
            }
        }
    }

    public void mouseMoved( MouseEvent e ) {
        //iterate down over the mouse handlers.
        handleEntranceAndExit( e );
        if( activeUnit != null ) {
            activeUnit.mouseMoved( e );
//            activeUnit.getMouseInputListener().mouseMoved( e );
        }
    }

}