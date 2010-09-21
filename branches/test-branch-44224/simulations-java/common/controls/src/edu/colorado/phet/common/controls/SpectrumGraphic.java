/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.controls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;

/**
 * SpectrumGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpectrumGraphic extends CompositePhetGraphic {
    //    private static Color invisibleColor = new Color( 0, 0, 0 );
    private static Color invisibleColor = new Color( 64, 64, 64 );


    private CompositePhetGraphic uvGraphic;
    private PhetGraphic visibleGraphic;
    private CompositePhetGraphic irGraphic;

    public SpectrumGraphic( Component component, double minWavelength, double maxWavelength ) {
        super( component );

        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        visibleGraphic = new PhetImageGraphic( component, SpectrumSliderWithSquareCursor.SPECTRUM_IMAGE );
        double visibleBandwidth = VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH;
        double uvBandwidth = VisibleColor.MIN_WAVELENGTH - minWavelength;
        double irBandwith = maxWavelength - VisibleColor.MAX_WAVELENGTH;
        int uvGraphicWidth = (int) ( ( uvBandwidth / visibleBandwidth ) * visibleGraphic.getWidth() );
        int irGraphicWidth = (int) ( ( irBandwith / visibleBandwidth ) * visibleGraphic.getWidth() );
        Font font = new PhetFont( Font.BOLD, 14 );

        if ( uvGraphicWidth > 0 ) {
            PhetShapeGraphic uvGraphicBackground = new PhetShapeGraphic( component,
                                                                         new Rectangle( uvGraphicWidth,
                                                                                        visibleGraphic.getHeight() ),
                                                                         invisibleColor );
            PhetGraphic uvGraphicLabel = new PhetTextGraphic( component, font,
                                                              PhetCommonResources.getString( "wavelength.uv" ), Color.white );
            uvGraphicLabel.setLocation( uvGraphicWidth / 2 - 10, 10 );
            uvGraphic = new CompositePhetGraphic( component );
            uvGraphic.addGraphic( uvGraphicBackground );
            uvGraphic.addGraphic( uvGraphicLabel );
            addGraphic( uvGraphic );
        }

        visibleGraphic.setLocation( uvGraphicWidth, 0 );
        addGraphic( visibleGraphic );

        if ( irGraphicWidth > 0 ) {
            PhetShapeGraphic irGraphicBackground = new PhetShapeGraphic( component,
                                                                         new Rectangle( irGraphicWidth,
                                                                                        visibleGraphic.getHeight() ),
                                                                         invisibleColor );
            PhetGraphic irGraphicLabel = new PhetTextGraphic( component, font,
                                                              PhetCommonResources.getString( "wavelength.ir" ), Color.white );
            irGraphicLabel.setLocation( irGraphicWidth / 2 - 5, 10 );
            irGraphic = new CompositePhetGraphic( component );
            irGraphic.addGraphic( irGraphicBackground );
            irGraphic.addGraphic( irGraphicLabel );
            irGraphic.setLocation( uvGraphicWidth + visibleGraphic.getWidth(), 0 );
            addGraphic( irGraphic );
        }
    }
}
