/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: May 30, 2005
 * Time: 6:45:21 PM
 * Copyright (c) May 30, 2005 by Sam Reid
 */

public class SpeedReadoutGraphic extends PNode implements ModelElement {
    private DecimalFormat format = new DecimalFormat( "0.00" );
    private PText phetTextGraphic;
    private RampPhysicalModel rampPhysicalModel;

    public SpeedReadoutGraphic( RampPhysicalModel rampPhysicalModel ) {
        this.rampPhysicalModel = rampPhysicalModel;
//        Font font = new Font( "Lucida Sans", Font.BOLD, 22 );
        phetTextGraphic = new PText( "" );
        phetTextGraphic.setFont( RampFontSet.getFontSet().getSpeedReadoutFont() );
        addChild( phetTextGraphic );
    }

    public void stepInTime( double dt ) {
        double value = rampPhysicalModel.getBlock().getVelocity();
        String text = MessageFormat.format( TheRampStrings.getString( "0.m.s" ), new Object[]{format.format( value )} );
        phetTextGraphic.setText( text );
    }
}
