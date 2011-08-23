// Copyright 2002-2011, University of Colorado
package org.reid.scenic.controller;

import java.awt.event.MouseEvent;

import org.reid.scenic.model.Model;

import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * @author Sam Reid
 */
public class MousePressHandler implements Function2<Model, MouseEvent, Model> {
    public Model apply( Model model, MouseEvent mouseEvent ) {
        return model.
                button1( model.button1.hover ? model.button1.pressed( true ) : model.button1 ).
                button2( model.button2.hover ? model.button2.pressed( true ) : model.button2 );
    }
}