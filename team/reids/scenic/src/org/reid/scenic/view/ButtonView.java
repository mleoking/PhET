// Copyright 2002-2011, University of Colorado
package org.reid.scenic.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
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
        Rectangle2D bounds = graphics2D.getFont().getStringBounds( buttonModel.text, graphics2D.getFontRenderContext() );
        graphics2D.setStroke( new BasicStroke( 1 ) );
        graphics2D.setPaint( Color.yellow );
        double sizeIncrease = 5;
        graphics2D.fill( new RoundRectangle2D.Double( buttonModel.x - sizeIncrease, buttonModel.y - bounds.getHeight() - sizeIncrease, bounds.getWidth() + sizeIncrease * 2, bounds.getHeight() + sizeIncrease * 2, 10, 10 ) );

        graphics2D.setPaint( Color.black );
        graphics2D.drawString( buttonModel.text, (float) buttonModel.x, (float) buttonModel.y );
    }
}