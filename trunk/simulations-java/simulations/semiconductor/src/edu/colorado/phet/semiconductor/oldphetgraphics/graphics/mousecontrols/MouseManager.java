/*, 2003.*/
package edu.colorado.phet.semiconductor.oldphetgraphics.graphics.mousecontrols;

import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.event.MouseInputListener;

import edu.colorado.phet.semiconductor.phetcommon.util.MultiMap;
import edu.colorado.phet.semiconductor.oldphetgraphics.graphics.bounds.Boundary;

/**
 * User: Sam Reid
 * Date: Nov 3, 2003
 * Time: 2:14:33 AM
 */
public class MouseManager implements MouseInputListener {
    MultiMap am;
    MouseInputListener activeUnit;

    public MouseManager( MultiMap am ) {
        this.am = am;
    }

    public void mouseClicked( MouseEvent e ) {
        //Make sure we're over the active guy.
//        mouseMoved(e);
        handleEntranceAndExit( e );
        if ( activeUnit != null ) {
            activeUnit.mouseClicked( e );
        }
    }

    public void mousePressed( MouseEvent e ) {
        handleEntranceAndExit( e );
        if ( activeUnit != null ) {
            activeUnit.mousePressed( e );
        }
    }

    public void mouseReleased( MouseEvent e ) {
//        handleEntranceAndExit(e);
        if ( activeUnit != null ) {
            activeUnit.mouseReleased( e );
        }
    }

    public void mouseEntered( MouseEvent e ) {
        handleEntranceAndExit( e );
    }

    public void mouseExited( MouseEvent e ) {
        handleEntranceAndExit( e );
    }

    public void mouseDragged( MouseEvent e ) {
        if ( activeUnit != null ) {
            activeUnit.mouseDragged( e );
        }
    }

    private MouseInputListener getHandler( MouseEvent e ) {
        Iterator it = am.reverseIterator();
        while ( it.hasNext() ) {
            Object o = it.next();
            if ( o instanceof Boundary && o instanceof MouseInputListener ) {
                Boundary boundary = (Boundary) o;
                if ( boundary.contains( e.getX(), e.getY() ) ) {
                    return (MouseInputListener) boundary;
                }
            }
        }
        return null;
    }

    //Does nothing if we're already over the right handler.
    private void handleEntranceAndExit( MouseEvent e ) {
        MouseInputListener unit = getHandler( e );
        if ( unit == null ) {
            if ( activeUnit != null ) {
                activeUnit.mouseExited( e );
                activeUnit = null;
            }
        }
        else if ( unit != null ) {
            if ( activeUnit == unit ) {
                //same guy
            }
            else if ( activeUnit == null ) {
                //Fire a grabber entered, set the active unit.
                activeUnit = unit;
                activeUnit.mouseEntered( e );
            }
            else if ( activeUnit != unit ) {
                //Switch active units.
                activeUnit.mouseExited( e );
                activeUnit = unit;
                activeUnit.mouseEntered( e );
            }
        }
    }

    public void mouseMoved( MouseEvent e ) {
        //iterate down over the grabber handlers.
        handleEntranceAndExit( e );
        if ( activeUnit != null ) {
            activeUnit.mouseMoved( e );
        }
    }

    //temporarily transfer control to the specified graphic.
    //May not be safe to give control to a mouseinputlistener not in our multimap...
    public void startDragging( MouseInputListener inputListener, MouseEvent event ) {
        if ( activeUnit != null ) {
            activeUnit.mouseReleased( event );//could be problems if expected event==RELEASE_EVENT
        }
        activeUnit = inputListener;
        activeUnit.mouseDragged( event );
    }

}