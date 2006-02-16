/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.help;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * SingleAtomModuleWiggleMe
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SingleAtomModuleWiggleMe extends CompositePhetGraphic {
    private Font font = new Font( "Lucida-sans", Font.BOLD, 18 );
    private Color color = new Color( 20, 140, 40 );
    private String text;
    private int x0;
    private int y0;
    private int wiggleWidth = 30;
    double tailWidth = 10;
    double headHeight = 10;
    double headWidth = 20;
    double arrowHeight = 20;
    private Wiggler wiggler;
    private boolean running = true;

    public SingleAtomModuleWiggleMe( Component component, Point2D location, int wiggleWidth ) {
        super( component );
        this.wiggleWidth = wiggleWidth;
        text = new String( SimStrings.get( "WiggleMeText" ) );
        x0 = (int)location.getX();
        y0 = (int)location.getY();
        PhetTextGraphic textGraphic = new PhetTextGraphic( component, font, text, color );
        this.addGraphic( textGraphic );
        textGraphic.setLocation( 0, (int)arrowHeight );
        Point2D tailLoc = new Point2D.Double( 0, arrowHeight );
        Point2D tipLoc = new Point2D.Double( 0, 0 );
        Arrow arrow = new Arrow( tailLoc, tipLoc, headHeight, headWidth, tailWidth );
        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( component, arrow.getShape(), color );
        arrowGraphic.setLocation( 10, 0 );
        this.addGraphic( arrowGraphic );

        // Kick off the thread that moves the graphic
        wiggler = new Wiggler();
        Thread t = new Thread( wiggler );
        t.start();
    }

    public void stop() {
        running = false;
        ( (ApparatusPanel)( getComponent() ) ).removeGraphic( this );
    }

    /**
     * Runnable that moves the graphic around
     */
    private class Wiggler implements Runnable {
        double t = 0;

        public void run() {
            while( running ) {
                t += 0.2;
                int x = (int)( x0 + Math.sin( t ) * wiggleWidth );
                setLocation( x, y0 );
                setBoundsDirty();
                repaint();

                try {
                    Thread.sleep( 50 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}
