/**
 * Class: VerticalGuideline
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common_sound.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common_sound.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common_sound.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common_sound.view.graphics.mousecontrols.TranslationEvent;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class VerticalGuideline extends CompositePhetGraphic {
    private int xLocation;

    public VerticalGuideline( final Component component, Color color, int position ) {
        super( null );
        final VerticalLine verticalLine = new VerticalLine( component, color, position );
        addGraphic( verticalLine );
        this.setCursor( Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR ) );
        this.xLocation = position;
        this.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent event) {
                xLocation += event.getDx();
                verticalLine.setLocation( xLocation );
                component.repaint();
            }
        } );
    }

    private static class VerticalLine extends PhetShapeGraphic {

        private Rectangle2D.Double line = new Rectangle2D.Double();
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
            setBoundsDirty();
            super.repaint();
        }
    }
}