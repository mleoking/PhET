/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.timeseries.TimeSeriesModel;
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

public class TimeGraphic extends PNode implements ModelElement {
    private TimeSeriesModel timeModel;
    private DecimalFormat format = new DecimalFormat( "0.00" );
    public PText phetTextGraphic;

    public TimeGraphic( TimeSeriesModel clock ) {
        this.timeModel = clock;
//        Font font = new Font( "Lucida Sans", Font.BOLD, 22 );
        phetTextGraphic = new PText( "" );
        phetTextGraphic.setFont( RampFontSet.getFontSet().getTimeReadoutFont() );
        addChild( phetTextGraphic );
        stepInTime( 0.0 );
        //setIgnoreMouse( true );
    }

    public void stepInTime( double dt ) {
        double time = timeModel.getTime();
        String text = MessageFormat.format( TheRampStrings.getString( "0.seconds" ), new Object[]{format.format( time )} );
        phetTextGraphic.setText( text );
    }
}
