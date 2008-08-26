package edu.colorado.phet.forces1d.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

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
            axisFont = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 14 );
            titleFont = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 12 );
            readoutFont = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 22 );
            wiggleMeFont = new Font( "Sans Serif", Font.BOLD, 16 );
            controlButtonFont = new Font( PhetFont.getDefaultFontName(), Font.PLAIN, 14 );
            textBoxFont = new Font( PhetFont.getDefaultFontName(), Font.PLAIN, 11 );
            chartButtonFont = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 14 );
            timeLabelFont = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 14 );
            timeFont = new Font( PhetFont.getDefaultFontName(), 0, 36 );
            walkwayFont = new Font( "dialog", 0, 20 );
            verticalTitleFont = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 16 );
        }
    }

    static class Medium extends MMFontSet {
        public Medium() {
            axisFont = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 14 );
            titleFont = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 10 );
            readoutFont = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 16 );
            wiggleMeFont = new Font( "Sans Serif", Font.BOLD, 16 );
            controlButtonFont = new Font( PhetFont.getDefaultFontName(), Font.PLAIN, 10 );
            textBoxFont = new Font( PhetFont.getDefaultFontName(), Font.PLAIN, 10 );
            chartButtonFont = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 10 );
            timeLabelFont = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 12 );
            timeFont = new Font( PhetFont.getDefaultFontName(), 0, 36 );
            walkwayFont = new Font( "dialog", 0, 20 );
            verticalTitleFont = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 12 );
        }
    }

    static class Small extends Medium {

    }

    static {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if ( d.width > 1024 ) {
            fontSet = new Large();
        }
        else if ( d.width <= 800 ) {
            fontSet = new Small();
        }
        else {
            fontSet = new Medium();
        }
    }
}
