/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.theramp.model.RampModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: May 30, 2005
 * Time: 6:45:21 PM
 * Copyright (c) May 30, 2005 by Sam Reid
 */

public class SpeedReadoutGraphic extends PNode implements ModelElement {
    private DecimalFormat format = new DecimalFormat( "0.00" );
    public PText phetTextGraphic;
    private RampModel rampModel;

    public SpeedReadoutGraphic( Component component, RampModel rampModel ) {
        super();
        this.rampModel = rampModel;
        Font font = new Font( "Lucida Sans", Font.BOLD, 28 );
//        phetTextGraphic = new PhetTextGraphic( component, font, "", Color.black );
        phetTextGraphic = new PText( "" );
        phetTextGraphic.setFont( font );
        addChild( phetTextGraphic );
        //setIgnoreMouse( true );
    }

    public void stepInTime( double dt ) {
        double value = rampModel.getBlock().getVelocity();
        String text = format.format( value ) + " meters/second";
        phetTextGraphic.setText( text );
    }
}
