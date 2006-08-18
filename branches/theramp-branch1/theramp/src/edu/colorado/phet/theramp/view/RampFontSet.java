/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.theramp.common.LucidaSansFont;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 19, 2005
 * Time: 9:43:58 AM
 * Copyright (c) Aug 19, 2005 by Sam Reid
 */

public class RampFontSet {
    private Font normalButtonFont;
    private Font barGraphTitleFont;
    private Font timeReadoutFont;
    private Font speedReadoutFont;
    private Font forceArrowLabelFont;
    private Font barFont;

    public static RampFontSet getFontSet() {
        //use toolkit.getDimension to return an appropriate font set.
        return new RampFontSet();
    }

    public RampFontSet() {
        normalButtonFont = new LucidaSansFont( 10 );
        barGraphTitleFont = new LucidaSansFont( 12, true );
        timeReadoutFont = new LucidaSansFont( 14, true );
        speedReadoutFont = timeReadoutFont;
        forceArrowLabelFont = new LucidaSansFont( 12, true );
        barFont = new LucidaSansFont( 12, true );
    }

    public Font getNormalButtonFont() {
        return normalButtonFont;
    }

    public Font getBarGraphTitleFont() {
        return barGraphTitleFont;
    }

    public Font getTimeReadoutFont() {
        return timeReadoutFont;
    }

    public Font getSpeedReadoutFont() {
        return speedReadoutFont;
    }

    public Font getForceArrowLabelFont() {
        return forceArrowLabelFont;
    }

    public Font getBarFont() {
        return barFont;
    }
}
