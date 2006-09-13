/** Sam Reid*/
package edu.colorado.phet.common_cck.view.basicgraphics;

import edu.colorado.phet.common_cck.view.basicgraphics.repaint.Repaint;
import edu.colorado.phet.common_cck.view.graphics.Graphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 7:56:13 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public class RenderedGraphic implements Graphic {
    private RepaintDelegate repaintDelegate;//so we can switch strategies easily.
    private BasicGraphic basicGraphic;
    private Component component;
    private Rectangle lastBounds;
    private boolean unionRectangles = true;
    private boolean visible = true;

    public RenderedGraphic( BasicGraphic basicGraphic, Component component ) {
        this( basicGraphic, component, true );
    }

    public RenderedGraphic( BasicGraphic basicGraphic, Component component, boolean visible ) {
        this.basicGraphic = basicGraphic;
        this.component = component;
        this.lastBounds = basicGraphic.getBounds();
        this.visible = visible;
        basicGraphic.addGraphicListener( new BasicGraphicListener() {
            public void boundsChanged( BasicGraphic basicGraphic ) {
                RenderedGraphic.this.boundsChanged();
            }

            public void appearanceChanged( BasicGraphic basicGraphic ) {
                RenderedGraphic.this.paintChanged();
            }
        } );
        repaintDelegate = new Repaint();
    }

    public void setRepaintDelegate( RepaintDelegate repaintDelegate ) {
        this.repaintDelegate = repaintDelegate;
    }

    private void paintChanged() {
        repaintDelegate.repaint( component, lastBounds );
    }

    private void boundsChanged() {
        if( visible ) {
            Rectangle newBounds = basicGraphic.getBounds();
            if( unionRectangles ) {
                if( lastBounds == null ) {
                    repaintDelegate.repaint( component, newBounds );
                }
                else {
                    Rectangle union = lastBounds.union( newBounds );
                    repaintDelegate.repaint( component, union );
                }
            }
            else {
                if( lastBounds != null ) {
                    repaintDelegate.repaint( component, lastBounds );
                }
                repaintDelegate.repaint( component, newBounds );
            }
            lastBounds = newBounds;
        }
        else {
            lastBounds = null;
        }
    }

    public void paint( Graphics2D g ) {
        if( visible ) {
            basicGraphic.paint( g );
        }
    }

    public void setVisible( boolean visible ) {
        if( visible != this.visible ) {
            this.visible = visible;
            //need to paint over our region.
            repaintDelegate.repaint( component, basicGraphic.getBounds() );
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public RepaintDelegate getRepaintDelegate() {
        return repaintDelegate;
    }

    public Component getComponent() {
        return component;
    }

    public BasicGraphic getBasicGraphic() {
        return basicGraphic;
    }

    public boolean isUnionRectangles() {
        return unionRectangles;
    }

}
