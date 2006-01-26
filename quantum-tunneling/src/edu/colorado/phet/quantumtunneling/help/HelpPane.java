/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.help;

import java.awt.Component;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;


/**
 * HelpPane
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HelpPane extends PGlassPane {

    public HelpPane( JFrame parentFrame ) {
        super( parentFrame );
    }
    
    public void clear() {
        getLayer().removeAllChildren();
    }
    
    public void add( HelpBubble helpItem, Point2D location ) {
        add( helpItem, location.getX(), location.getY() );
    }
    
    public void add( HelpBubble helpItem, double x, double y ) {
        System.out.println( "(" + x + "," + y + ") " + ((HelpBubble)helpItem).getText() );
        helpItem.setOffset( x, y );
        getLayer().addChild( helpItem );
    }
    
    public void add( HelpBubble helpItem, Component target ) {
        Point loc = SwingUtilities.convertPoint( target.getParent(), target.getLocation(), this );
        if ( helpItem.arrowVertical() ) {
            add( helpItem, loc.x + target.getWidth() / 2, loc.y );
        }
        else {
            add( helpItem, loc.x, loc.y + target.getHeight() / 2 );
        }
    }
    
    public void add( HelpBubble helpItem, PNode target, PCanvas canvas ) {
        Rectangle2D globalBounds = target.getGlobalBounds();
        Point globalPoint = new Point( (int) globalBounds.getX(), (int) globalBounds.getY() );
        Point loc = SwingUtilities.convertPoint( canvas.getParent(), globalPoint, this );
        if ( helpItem.arrowVertical() ) {
            add( helpItem, loc.x + target.getWidth() / 2, loc.y );
        }
        else {
            add( helpItem, loc.x, loc.y + target.getHeight() / 2 );
        }
    }
}
