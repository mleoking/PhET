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

        rectangle3DGraphic = new Rectangle3DGraphic( component, null, Color.blue, stroke, Color.green, Color.red, dx, dy, Color.black );
        label = new VerticalTextGraphic( component, new Font( "Lucida Sans", 0, 16 ), text, Color.black );
        addGraphic( rectangle3DGraphic );

        addGraphic( label );
        updateBar();
    }

    private void updateBar() {
        int height = transform1D.modelToView( value );
        Rectangle rect = new Rectangle( x, y - height, width, height );
        label.setLocation( rect.x + 5, (int)( -5 + rect.getMaxY() ) );
        rectangle3DGraphic.setRectangle( rect );
    }

    public void setValue( double value ) {
//        System.out.println( "value = " + value );
        this.value = value;
        updateBar();
    }
}
