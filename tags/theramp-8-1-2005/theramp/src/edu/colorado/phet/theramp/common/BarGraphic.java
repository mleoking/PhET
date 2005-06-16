/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 9:32:14 AM
 * Copyright (c) Feb 12, 2005 by Sam Reid
 */

public class BarGraphic extends CompositePhetGraphic {
    private ModelViewTransform1D transform1D;
    private double value;
    private int x;
    private int width;
    private int y;
    private VerticalTextGraphic label;
    private Rectangle3DGraphic rectangle3DGraphic;

    public BarGraphic( Component component, String text, ModelViewTransform1D transform1D, double value, int x, int width, int y, int dx, int dy ) {
        super( component );
        this.transform1D = transform1D;
        this.value = value;
        this.x = x;
        this.y = y;
        this.width = width;

        Stroke stroke = new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        Color face = new Color( 200, 200, 255 );
        Color top = new Color( 150, 150, 255 );
        Color side = new Color( 10, 10, 210 );
        rectangle3DGraphic = new Rectangle3DGraphic( component, null, face, stroke, top, side, dx, dy, Color.black );
        label = new VerticalTextGraphic( component, new Font( "Lucida Sans", Font.BOLD, 20 ), text, Color.yellow, Color.black );
        addGraphic( rectangle3DGraphic );

        addGraphic( label );
        updateBar();
    }

    private void updateBar() {
        int height = computeHeight();
        Rectangle rect = new Rectangle( x, y - height, width, height );
        label.setLocation( rect.x + 5, (int)( -5 + rect.getMaxY() ) );
        rectangle3DGraphic.setRectangle( rect );
    }

    public void setValue( double value ) {
        if( value != this.value ) {
            this.value = value;
            if( value < 0 ) {
                rectangle3DGraphic.setLocation( 0, -computeHeight() );//a big hack to make negative values work.
            }
            else {
                rectangle3DGraphic.setLocation( 0, 0 );
            }
            this.value = Math.abs( value );
            updateBar();
        }
    }

    private int computeHeight() {
        return transform1D.modelToView( value );
    }
}
