/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.common;

import edu.colorado.phet.cck.elements.branch.AbstractBranchGraphic;
import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.TextDisplay2WithBackground;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Aug 30, 2003
 * Time: 1:07:36 AM
 * Copyright (c) Aug 30, 2003 by Sam Reid
 */
public class DragToCreate implements InteractiveGraphic {
    InteractiveGraphic icon;
    private InteractiveGraphicSource source;
    private Proxy proxy;
    InteractiveGraphic created;
    private TextDisplay2WithBackground textDisplay;

    public DragToCreate( InteractiveGraphic target, InteractiveGraphicSource source, String tipText, Point tipLocation, Proxy ps ) {
        this.icon = target;
        this.source = source;
        this.proxy = ps;
        textDisplay = new TextDisplay2WithBackground( tipText, tipLocation.x, tipLocation.y );
    }

    public static interface Proxy {
        void mousePressed( InteractiveGraphic created, MouseEvent event );

        void mouseDragged( InteractiveGraphic created, MouseEvent event );

        void mouseReleased( InteractiveGraphic created, MouseEvent event );
    }

    public static class BranchProxy implements Proxy {

        public void mousePressed( InteractiveGraphic created, MouseEvent event ) {
            AbstractBranchGraphic abg = (AbstractBranchGraphic)created;
            InteractiveGraphic main = abg.getMainBranchGraphic();
            main.mousePressed( event );
        }

        public void mouseDragged( InteractiveGraphic created, MouseEvent event ) {
            AbstractBranchGraphic abg = (AbstractBranchGraphic)created;
            InteractiveGraphic main = abg.getMainBranchGraphic();
            main.mouseDragged( event );
        }

        public void mouseReleased( InteractiveGraphic created, MouseEvent event ) {
            AbstractBranchGraphic abg = (AbstractBranchGraphic)created;
            InteractiveGraphic main = abg.getMainBranchGraphic();
            main.mouseReleased( event );
        }


    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return icon.contains( event.getX(), event.getY() );
    }

    public void mousePressed( MouseEvent event ) {
        if( created == null ) {
            create( event );
        }
    }

    private void create( MouseEvent event ) {
        Branch.ID_COUNTER++;
        created = source.newInteractiveGraphic();
        proxy.mousePressed( created, event );
//        System.out.println("Created.");
//        created.mousePressed(event);
    }

    public void mouseDragged( MouseEvent event ) {
//        System.out.println("Dragging: "+System.currentTimeMillis());
        if( created == null ) {
            create( event );
        }
        else {
            proxy.mouseDragged( created, event );
//            created.mouseDragged(event);
        }
    }

    public void mouseMoved( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent event ) {
//        System.out.println("Released dragtocreate.");
        if( created != null ) {
            proxy.mouseReleased( created, event );
//            created.mouseReleased(event);
            created = null;
        }
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void paint( Graphics2D g ) {
        icon.paint( g );
        textDisplay.paint( g );
    }

    public void setTipLocation( Point point ) {
//        this.tipLocation = point;
        textDisplay.setLocation( point.x - 120, point.y );
    }

    public boolean contains( int x, int y ) {
        return icon.contains( x, y );
    }
}
