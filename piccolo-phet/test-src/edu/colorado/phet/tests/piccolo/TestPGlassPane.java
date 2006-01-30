/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.tests.piccolo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.Timer;

import edu.colorado.phet.piccolo.help.PGlassPane;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * TestGlassPane
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestPGlassPane extends PGlassPane {

    public TestPGlassPane( JFrame parentFrame ) {
        super( parentFrame );
        // Periodically mark certain components with colored circles...
        Timer timer = new Timer( 500, new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                getLayer().removeAllChildren();
                showAll( getParentFrame().getContentPane() );
            }
        } );
        timer.start();
    }

    /*
     * Recursively navigate through the Swing component hierachy.
     * For certain component types, draw a circle at their upper-left corner 
     * using these colors:
     * 
     * RED   = AbstractButton
     * BLUE  = JCheckBox
     * GREEN = JSlider
     * 
     * @param container
     */
    private void showAll( Container container ) {
        for ( int i = 0; i < container.getComponentCount(); i++ ) {
            Component c = container.getComponent( i );
            if ( c.isVisible() ) {
                if ( c instanceof AbstractButton ) {
                    Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                    PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                    path.setPaint( Color.RED );
                    path.setOffset( loc );
                    getLayer().addChild( path );
                }
                else if ( c instanceof JCheckBox ) {
                    Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                    PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                    path.setPaint( Color.BLUE );
                    path.setOffset( loc );
                    getLayer().addChild( path );
                }
                else if ( c instanceof JSlider ) {
                    Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                    PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                    path.setPaint( Color.GREEN );
                    path.setOffset( loc );
                    getLayer().addChild( path );
                }
                else if ( c instanceof Container ) {
                    showAll( (Container) c );
                }
            }
        }
    }


}
