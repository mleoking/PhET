// Copyright 2002-2011, University of Colorado
package org.reid.scenic.view;

import java.awt.Color;
import java.awt.Graphics2D;

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
        graphics2D.setFont( buttonModel.getFont() );
        graphics2D.setPaint( Color.black );
        graphics2D.drawString( buttonModel.getText(), 50, 50 );
    }
}
