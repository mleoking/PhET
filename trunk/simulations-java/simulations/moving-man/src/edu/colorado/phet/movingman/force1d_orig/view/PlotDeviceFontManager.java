package edu.colorado.phet.movingman.force1d_orig.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

/**
 * User: Sam Reid
 * Date: Nov 8, 2004
 * Time: 3:20:49 PM
 */
public class PlotDeviceFontManager {
    private static MMFontSet fontSet;

    public static MMFontSet getFontSet() {
        return fontSet;
    }

    public static class MMFontSet {
        protected Font axisFont;
        protected Font titleFont;
        protected Font readoutFont;
        protected Font wiggleMeFont;
        protected Font controlButtonFont;
        protected Font chartButtonFont;
        protected Font textBoxFont;
        protected Font timeLabelFont;
        protected Font timeFont;
        protected Font walkwayFont;
        protected Font verticalTitleFont;

        public Font getAxisFont() {
            return axisFont;
        }

        public Font getTitleFont() {
            return titleFont;
        }

        public Font getReadoutFont() {
            return readoutFont;
        }

        public Font getWiggleMeFont() {
            return wiggleMeFont;
        }

        public Font getControlButtonFont() {
            return controlButtonFont;
        }

        public Font getTextBoxFont() {
            return textBoxFont;
        }

        public Font getChartButtonFont() {
            return chartButtonFont;
        }

        public Font getTimeLabelFont() {
            return timeLabelFont;
        }

        public Font getTimeFont() {
            return timeFont;
        }

        public Font getWalkwayFont() {
            return walkwayFont;
        }

        public Font getVerticalTitleFont() {
            return verticalTitleFont;
        }
    }

    static class Large extends MMFontSet {
        public Large() {
            axisFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 14 );
            titleFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 12 );
            readoutFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 22 );
            wiggleMeFont = new Font( "Sans Serif", Font.BOLD, 16 );
            controlButtonFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.PLAIN, 14 );
            textBoxFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.PLAIN, 11 );
            chartButtonFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 14 );
            timeLabelFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 14 );
            timeFont = new Font( PhetDefaultFont.LUCIDA_SANS, 0, 36 );
            walkwayFont = new Font( "dialog", 0, 20 );
            verticalTitleFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 16 );
        }
    }

    static class Medium extends MMFontSet {
        public Medium() {
            axisFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 14 );
            titleFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 10 );
            readoutFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 16 );
            wiggleMeFont = new Font( "Sans Serif", Font.BOLD, 16 );
            controlButtonFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.PLAIN, 10 );
            textBoxFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.PLAIN, 10 );
            chartButtonFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 10 );
            timeLabelFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 12 );
            timeFont = new Font( PhetDefaultFont.LUCIDA_SANS, 0, 36 );
            walkwayFont = new Font( "dialog", 0, 20 );
            verticalTitleFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 12 );
        }
    }

    static class Small extends Medium {

    }

    static {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if ( d.width > 1024 ) {
            fontSet = new Large();
            System.out.println( "MM: Chose font for width> 1280" );
        }
        else if ( d.width <= 800 ) {
            fontSet = new Small();
            System.out.println( "MM: Chose font for <=800" );
        }
        else {
            fontSet = new Medium();
            System.out.println( "MM: Chose font for width between between 800 and 1280" );
        }
    }
}
