/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 9:32:14 AM
 * Copyright (c) Feb 12, 2005 by Sam Reid
 */

public class BarGraphic2D extends CompositePhetGraphic {
    private ModelViewTransform1D transform1D;
    private double value;
    private int x;
    private int width;
    private int y;
    private VerticalTextGraphic label;
    private PhetShapeGraphic rectangle3DGraphic;

    public BarGraphic2D( Component component, String text, ModelViewTransform1D transform1D, double value, int x, int width, int y, int dx, int dy, Paint paint ) {
        super( component );
        this.transform1D = transform1D;
        this.value = value;
        this.x = x;
        this.y = y;
        this.width = width;

        rectangle3DGraphic = new PhetShapeGraphic( component, null, paint, new BasicStroke( 1 ), Color.black );
        label = new VerticalTextGraphic( component, new Font( "Lucida Sans", Font.BOLD, 20 ), text, Color.yellow );
        addGraphic( rectangle3DGraphic );

        addGraphic( label );
        updateBar();
    }

    private void updateBar() {
        int height = computeHeight();
        Rectangle rect = new Rectangle( x, y - height, width, height );
        label.setLocation( rect.x + 5, (int)( -5 + rect.getMaxY() ) );
        rectangle3DGraphic.setShape( rect );
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
