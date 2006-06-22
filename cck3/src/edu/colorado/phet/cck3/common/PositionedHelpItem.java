/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.cck3.debug.SimpleObservableDebug;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.graphics.shapes.Arrow;
import edu.colorado.phet.common_cck.view.help.HelpItem;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetMultiLineTextGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Area;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 12:04:51 AM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class PositionedHelpItem extends CCKCompositePhetGraphic {
    private Target target;
    private PhetMultiLineTextGraphic textGraphic;
    private PhetShapeGraphic arrowGraphic;
    private Arrow arrow;
    private Color arrowColor = Color.blue;
    private boolean noTarget = false;
    private boolean enabled;

    public PositionedHelpItem( String text, Target target, Font font, Component component ) {
        super( component );
        this.target = target;
        String[] lines = HelpItem.tokenizeString( text );
        Point location = target.getTextLocation();
        if( location == null ) {
            location = new Point();
        }
        int x = location.x;
        int y = location.y;
        textGraphic = new PhetMultiLineTextGraphic( component, lines, font, x, y, Color.blue, 1, 1, Color.yellow );
        target.addObserver( new SimpleObserver() {
            public void update() {
                changed();
            }
        } );
        arrowGraphic = new PhetShapeGraphic( component, new Area(), arrowColor );
        addGraphic( arrowGraphic );
        addGraphic( textGraphic );
        setVisible( false );
    }

    public void changed() {
        Point location = target.getTextLocation();
        if( location == null ) {
            noTarget = true;
        }
        else {
            noTarget = false;
        }
//        System.out.println( "PHI.location = " + location );
        if( location != null ) {
            int x = location.x;
            int y = location.y;
            textGraphic.setPosition( x, y );
            arrow = target.getArrow( textGraphic );
            arrowGraphic.setShape( arrow.getShape() );
        }
        if( isVisible() && !noTarget ) {
            super.setBoundsDirty();
            super.repaint();
        }
        setVisible( enabled && !noTarget );
        super.setBoundsDirty();
    }

    public Rectangle getTextBounds() {
        return textGraphic.getBounds();
    }

    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
        changed();
    }

    public abstract static class Target extends SimpleObservableDebug {
        public abstract Point getTextLocation();

        public abstract Arrow getArrow( PhetMultiLineTextGraphic textGraphic );
    }
}
