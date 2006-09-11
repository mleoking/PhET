package edu.colorado.phet.common_cck.view.basicgraphics;

import java.awt.*;
import java.util.ArrayList;

public class BasicCompositeGraphic extends BasicGraphic {
    private ArrayList graphics = new ArrayList();
    private BasicGraphicListener basicGraphicListener = new BasicGraphicListener() {
        public void boundsChanged( BasicGraphic basicGraphic ) {
            doBoundsChanged( basicGraphic );
        }

        public void appearanceChanged( BasicGraphic basicGraphic ) {
            doPaintChanged( basicGraphic );
        }
    };

    private void doPaintChanged( BasicGraphic basicGraphic ) {
        appearanceChanged();
    }

    private void doBoundsChanged( BasicGraphic basicGraphic ) {
        boundsChanged();
    }

    public void addBasicGraphic( BasicGraphic basicGraphic ) {
        graphics.add( basicGraphic );
        basicGraphic.addGraphicListener( basicGraphicListener );
    }

    public void removeGraphic( BasicGraphic basicGraphic ) {
        graphics.remove( basicGraphic );
        basicGraphic.removeGraphicListener( basicGraphicListener );
    }

    public int numGraphics() {
        return graphics.size();
    }

    public Rectangle getBounds() {
        Rectangle bounds = null;
        for( int i = 0; i < graphics.size(); i++ ) {
            BasicGraphic basicGraphic = (BasicGraphic)graphics.get( i );
            if( bounds == null ) {
                bounds = basicGraphic.getBounds();
            }
            else {
                bounds = bounds.union( basicGraphic.getBounds() );
            }
        }
        return bounds;
    }

    public void paint( Graphics2D g ) {
        for( int i = 0; i < graphics.size(); i++ ) {
            BasicGraphic basicGraphic = (BasicGraphic)graphics.get( i );
            basicGraphic.paint( g );
        }
    }
}
