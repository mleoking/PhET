/**
 *
 * Class: CompositeInteractiveGraphic
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Dec 19, 2003
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.util.MultiMap;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class CompositeInteractiveGraphic implements InteractiveGraphic {

    private MultiMap graphicMap = new MultiMap();
    private MouseManager mouseManager;
    private boolean visible = true;

    public CompositeInteractiveGraphic() {
        mouseManager = new MouseManager();
    }

    public void paint( Graphics2D g ) {
        if( visible ) {
            Iterator it = graphicMap.iterator();
            while( it.hasNext() ) {
                Graphic graphic = (Graphic)it.next();
                graphic.paint( g );
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    /**
     * Used to see if the mouse is in component InteractiveGraphic
     *
     * @param x
     * @param y
     * @return
     */
    public boolean contains( int x, int y ) {
        if( visible ) {
            Iterator it = this.graphicMap.iterator();
            while( it.hasNext() ) {
                Object o = it.next();
                if( o instanceof Boundary ) {
                    Boundary boundary = (Boundary)o;
                    if( boundary.contains( x, y ) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void clear() {
        graphicMap.clear();
    }

    /**
     * @deprecated
     */
    public void remove( Graphic graphic ) {
        this.removeGraphic( graphic );
    }

    public void removeGraphic( Graphic graphic ) {
        graphicMap.removeValue( graphic );
    }

    public void addGraphic( Graphic graphic ) {
        addGraphic( graphic, 0 );
    }

    /**
     * Returns graphics from a forward iterator.
     */
    public Graphic[] getGraphics() {
        Iterator it = graphicMap.iterator();
        ArrayList graphics = new ArrayList();
        while( it.hasNext() ) {
            Graphic graphic = (Graphic)it.next();
            graphics.add( graphic );
        }
        return (Graphic[])graphics.toArray( new Graphic[0] );
    }

    public void addGraphic( Graphic graphic, double layer ) {
        this.graphicMap.add( new Double( layer ), graphic );
    }

    public void moveToTop( Graphic target ) {
        this.removeGraphic( target );
        graphicMap.add( graphicMap.lastKey(), target );
    }

    // Mouse-related behaviors
    public void mouseClicked( MouseEvent e ) {
        if( visible ) {
            mouseManager.mouseClicked( e );
        }
    }

    public void mousePressed( MouseEvent e ) {
        if( visible ) {
            mouseManager.mousePressed( e );
        }
    }

    public void mouseReleased( MouseEvent e ) {
        if( visible ) {
            mouseManager.mouseReleased( e );
        }
    }

    public void mouseEntered( MouseEvent e ) {
        if( visible ) {
            mouseManager.mouseEntered( e );
        }
    }

    public void mouseExited( MouseEvent e ) {
        if( visible ) {
            mouseManager.mouseExited( e );
        }
    }

    public void mouseDragged( MouseEvent e ) {
        if( visible ) {
            mouseManager.mouseDragged( e );
        }
    }

    public void mouseMoved( MouseEvent e ) {
        if( visible ) {
            mouseManager.mouseMoved( e );
        }
    }

    public void startDragging( InteractiveGraphic graphic, MouseEvent e ) {
        mouseManager.startDragging( graphic, e );
    }

    //
    // Inner Classes
    //

    /**
     * Manages delegation of mouse events to component InteractiveGraphics
     */
    private class MouseManager implements MouseInputListener {
        MouseInputListener activeUnit;

        public void mouseClicked( MouseEvent e ) {
            //Make sure we're over the active guy.
            handleEntranceAndExit( e );
            if( activeUnit != null ) {
                activeUnit.mouseClicked( e );
            }
        }

        public void mousePressed( MouseEvent e ) {
            handleEntranceAndExit( e );
            if( activeUnit != null ) {
                activeUnit.mousePressed( e );
            }
        }

        public void mouseReleased( MouseEvent e ) {
            if( activeUnit != null ) {
                activeUnit.mouseReleased( e );
            }
        }

        /**
         * This method is no-op because if the user is dragging a graphic,
         * and handleEntranceAndExit() gets
         * called, the boundary may be dropped.
         *
         * @param e
         */
        public void mouseEntered( MouseEvent e ) {
        }

        /**
         * This method is no-op because if the user is dragging a graphic,
         * and handleEntranceAndExit() gets
         * called, the boundary may be dropped.
         *
         * @param e
         */
        public void mouseExited( MouseEvent e ) {
        }

        public void mouseDragged( MouseEvent e ) {
            if( activeUnit != null ) {
                activeUnit.mouseDragged( e );
            }
        }

        /**
         * Find the topmost Boundary & MouseInputListener that contains this mouse event.
         */
        private MouseInputListener getHandler( MouseEvent e ) {
            Iterator it = graphicMap.reverseIterator();
            while( it.hasNext() ) {
                Object o = it.next();
                if( o instanceof Boundary && o instanceof MouseInputListener ) {
                    Boundary boundary = (Boundary)o;
                    if( boundary.contains( e.getX(), e.getY() ) ) {
                        return (MouseInputListener)boundary;
                    }
                }
            }
            return null;
        }

        /*

•	I think I’ve found the trouble spot.  The tree is like this:
•	TotalGraphic ? Circuit Graphic ? Junction graphics and branch graphics.
•	The totalGraphic :
1.	gets the mousemoved event
2.	Discovers that no graphic contains the new location.
3.	Calls a no-op on the CircuitGraphic.
4.	Sets activeUnit to null.
•	In step 3, the no-op on CircuitGraphic should be a call to CircuitGraphic.activeUnit.mouseExited if active unit is not null.  This suggests we need a different mouse listener to add to components than to leave in the tree.  Not sure what the best design is for this.  It is satisfactory to add CompositeInteractiveGraphics directly to components.
        */

        //Does nothing if we're already over the right handler.
        private void handleEntranceAndExit( final MouseEvent e ) {
            //What is this?  Why are we doing invoke later?  We should already be in the swing thread.
            //I verified this in one runtime.

            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    MouseInputListener unit = getHandler( e );
                    if( unit == null ) {
                        // If the mouse isn't over anything contained in the
                        // CompositeInteractiveGraphic...
//                        System.out.println( "Handler is null." + System.currentTimeMillis() );
                        getHandler( e );
                        if( activeUnit != null ) {
//                    if( activeUnit instanceof CompositeInteractiveGraphic ) {
//                        //traverse the tree.
//                        CompositeInteractiveGraphic cig = (CompositeInteractiveGraphic)activeUnit;
//                        if( cig.mouseManager.activeUnit != null ) {
//                            cig.mouseManager.activeUnit.mouseExited( e );
//                            cig.mouseManager.activeUnit=null;
//                        }
//                    }
                            activeUnit.mouseExited( e );
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
                        }
                        else if( activeUnit != unit ) {
                            //Switch active units.
                            activeUnit.mouseExited( e );
                            activeUnit = unit;
                            activeUnit.mouseEntered( e );
                        }
                    }
                }
            } );

        }

        public void mouseMoved( MouseEvent e ) {
            //iterate down over the mouse handlers.
            handleEntranceAndExit( e );
            if( activeUnit != null ) {
                activeUnit.mouseMoved( e );
            }
        }

        /**
         * temporarily transfer control to the specified graphic.
         *
         * @param inputListener
         * @param event
         */
        public void startDragging( MouseInputListener inputListener, MouseEvent event ) {
            if( activeUnit != null ) {
                activeUnit.mouseReleased( event );//could be problems if expected event==RELEASE_EVENT
            }
            activeUnit = inputListener;
            activeUnit.mouseDragged( event );
        }

    }

}
