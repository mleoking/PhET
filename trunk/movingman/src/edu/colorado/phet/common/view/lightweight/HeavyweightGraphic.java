/** Sam Reid*/
package edu.colorado.phet.common.view.lightweight;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 7:56:13 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public class HeavyweightGraphic implements Graphic {
    RepaintDelegate repaintDelegate;//so we can switch strategies easily.
    LightweightGraphic lightweightGraphic;
    Component component;
    Rectangle lastBounds;
    boolean unionRectangles;
    boolean visible;

    public HeavyweightGraphic( LightweightGraphic lightweightGraphic, Component component ) {
        this( lightweightGraphic, component, true );
    }

    public HeavyweightGraphic( LightweightGraphic lightweightGraphic, Component component, boolean visible ) {
        this.lightweightGraphic = lightweightGraphic;
        this.component = component;
        this.lastBounds = lightweightGraphic.getBounds();
        this.visible = visible;
        lightweightGraphic.addGraphicListener( new GraphicListener() {
            public void boundsChanged( LightweightGraphic lightweightGraphic ) {
                HeavyweightGraphic.this.boundsChanged();
            }

            public void paintChanged( LightweightGraphic lightweightGraphic ) {
                HeavyweightGraphic.this.paintChanged();
            }
        } );
        repaintDelegate = new RepaintDelegate() {
            public void repaint( Component component, Rectangle rect ) {
                component.repaint( rect.x, rect.y, rect.width, rect.height );
            }
        };
//        repaintDelegate=new SyncedRepaintDelegate();
    }

    public void setRepaintDelegate( RepaintDelegate repaintDelegate ) {
        this.repaintDelegate = repaintDelegate;
    }

    private void paintChanged() {
        repaintDelegate.repaint( component, lastBounds );
    }

    private void boundsChanged() {
        Rectangle newBounds = lightweightGraphic.getBounds();
        if( unionRectangles ) {
            Rectangle union = lastBounds.union( newBounds );
            repaintDelegate.repaint( component, union );
        }
        else {
            repaintDelegate.repaint( component, lastBounds );
            repaintDelegate.repaint( component, newBounds );
        }
        lastBounds = newBounds;
    }

    public void paint( Graphics2D g ) {
        lightweightGraphic.paint( g );
    }

    public LightweightGraphic getLightweightGraphic() {
        return lightweightGraphic;
    }
}
