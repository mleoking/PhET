/**
 * Class: VerticalGuideline
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound.view;


import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class VerticalGuideline extends DefaultInteractiveGraphic {
    private int xLocation;

    public VerticalGuideline( final Component component, Color color, int position ) {
        super( null );
        final VerticalLine verticalLine = new VerticalLine( component, color, position );
        setBoundedGraphic( verticalLine );
        this.addCursorHandBehavior();
        this.xLocation = position;
        this.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                xLocation += dx;
                verticalLine.setLocation( xLocation );
                //                component.repaint();
            }
        } );
    }

    private static class VerticalLine extends PhetShapeGraphic {

        private Rectangle2D.Double line = new Rectangle2D.Double();
        //        private Line2D.Double line = new Line2D.Double();
        private Color color;

        public VerticalLine( Component component, Color color, int position ) {
            super( component, null, color );
            setShape( line );
            setLocation( position );
            this.color = color;
        }

        public void paint( Graphics2D g ) {
            Color oldColor = g.getColor();
            g.setColor( color );
            g.draw( line );
            g.setColor( oldColor );
        }

        public void setLocation( int xLocation ) {
            line.setRect( xLocation, 0, 1, 800 );
            //            line.setLine( xLocation, 0, 1, 800 );
            //            line.setLine( xLocation, 0, xLocation, 800 );
            //            line.setLine( xLocation, 0, xLocation, getComponent().getBounds().getMaxY() );
            super.repaint();
        }
    }
}