// Copyright 2002-2011, University of Colorado
package org.reid.scenic.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.reid.scenic.model.ButtonModel;

/**
 * @author Sam Reid
 */
public class ButtonView {
    private ButtonModel buttonModel;

    public ButtonView( ButtonModel buttonModel ) {
        this.buttonModel = buttonModel;
    }

    public void paint( Graphics2D graphics2D ) {
        graphics2D.setFont( buttonModel.font );
        graphics2D.setStroke( new BasicStroke( 1 ) );
        graphics2D.setPaint( buttonModel.pressed ? Color.red : Color.yellow );
        final RoundRectangle2D.Double outerRoundRect = getOuterRoundRect();
        graphics2D.fill( outerRoundRect );

        graphics2D.setPaint( Color.black );
        graphics2D.drawString( buttonModel.text, (float) buttonModel.x, (float) buttonModel.y );
    }

    class MyFontRenderContext extends FontRenderContext {
    }

    private RoundRectangle2D.Double getOuterRoundRect() {
        double sizeIncrease = 5;
        FontRenderContext fontRenderContext = new MyFontRenderContext();
        Rectangle2D bounds = buttonModel.font.getStringBounds( buttonModel.text, fontRenderContext );
        return new RoundRectangle2D.Double( buttonModel.x - sizeIncrease, buttonModel.y - bounds.getHeight() - sizeIncrease, bounds.getWidth() + sizeIncrease * 2, bounds.getHeight() + sizeIncrease * 2, 10, 10 );
    }

    public boolean contains( int x, int y ) {
        return getOuterRoundRect().contains( x, y );
    }
}