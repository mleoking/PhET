// Copyright 2002-2011, University of Colorado
package org.reid.scenic.controller;

import java.awt.event.MouseEvent;

import org.reid.scenic.model.Model;

import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * @author Sam Reid
 */
public class MouseReleasedHandler implements Function2<Model, MouseEvent, Model> {
    public Model apply( Model model, MouseEvent mouseEvent ) {
        if ( model.button1.pressed ) {
            return model.button1.apply( model ).button1( model.button1.pressed( false ) );
        }
        else if ( model.button2.pressed ) {
            return model.button2.apply( model ).button2( model.button2.pressed( false ) );
        }
        else if ( model.button3.pressed ) {
            return model.person( model.button3.apply( model.person ) ).button3( model.button3.pressed( false ) );
        }
        else {
            return model;
        }
    }
}