// Copyright 2002-2011, University of Colorado
package org.reid.scenic.controller;

import java.awt.event.MouseEvent;

import org.reid.scenic.model.Model;
import org.reid.scenic.view.View;

import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * @author Sam Reid
 */
public class MouseMovedHandler implements Function2<Model, MouseEvent, Model> {
    public Model apply( Model model, MouseEvent mouseEvent ) {
        return model.
                button1( model.button1.hover( new View( model ).button1Contains( mouseEvent.getX(), mouseEvent.getY() ) ) ).
                button2( model.button2.hover( new View( model ).button2Contains( mouseEvent.getX(), mouseEvent.getY() ) ) ).
                button3( model.button3.hover( new View( model ).button3Contains( mouseEvent.getX(), mouseEvent.getY() ) ) );
    }
}