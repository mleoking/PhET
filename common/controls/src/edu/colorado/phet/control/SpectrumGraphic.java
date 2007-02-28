/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.control;

import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.VisibleColor;

import java.awt.*;

/**
 * SpectrumGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpectrumGraphic extends CompositePhetGraphic {
    public static final Font DEFAULT_CONTROL_FONT = new Font( "Lucida sans", Font.BOLD, 10 );
//    private static Color invisibleColor = new Color( 0, 0, 0 );
    private static Color invisibleColor = new Color( 64, 64, 64 );


    private CompositePhetGraphic uvGraphic;
    private PhetGraphic visibleGraphic;
    private CompositePhetGraphic irGraphic;

    public SpectrumGraphic( Component component, double minWavelength, double maxWavelength ) {
        super( component );

        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        visibleGraphic = new PhetImageGraphic( component, Constants.SPECTRUM_IMAGE );
        double visibleBandwidth = VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH;
        double uvBandwidth = VisibleColor.MIN_WAVELENGTH - minWavelength;
        double irBandwith = maxWavelength - VisibleColor.MAX_WAVELENGTH;
        int uvGraphicWidth = (int)( ( uvBandwidth / visibleBandwidth ) * visibleGraphic.getWidth() );
        int irGraphicWidth = (int)( ( irBandwith / visibleBandwidth ) * visibleGraphic.getWidth() );
        Font font = new Font( DEFAULT_CONTROL_FONT.getName(),
                              DEFAULT_CONTROL_FONT.getStyle(),
                              DEFAULT_CONTROL_FONT.getSize() + 4 );

        if( uvGraphicWidth > 0 ) {
            PhetShapeGraphic uvGraphicBackground = new PhetShapeGraphic( component,
                                                                         new Rectangle( uvGraphicWidth,
                                                                                        visibleGraphic.getHeight() ),
                                                                         invisibleColor );
            PhetGraphic uvGraphicLabel = new PhetTextGraphic( component, font,
                                                              "UV", Color.white );
            uvGraphicLabel.setLocation( uvGraphicWidth / 2 - 10, 10 );
            uvGraphic = new CompositePhetGraphic( component );
            uvGraphic.addGraphic( uvGraphicBackground );
            uvGraphic.addGraphic( uvGraphicLabel );
            addGraphic( uvGraphic );
        }

        visibleGraphic.setLocation( uvGraphicWidth, 0 );
        addGraphic( visibleGraphic );

        if( irGraphicWidth > 0 ) {
            PhetShapeGraphic irGraphicBackground = new PhetShapeGraphic( component,
                                                                         new Rectangle( irGraphicWidth,
                                                                                        visibleGraphic.getHeight() ),
                                                                         invisibleColor );
            PhetGraphic irGraphicLabel = new PhetTextGraphic( component, font,
                                                              "IR", Color.white );
            irGraphicLabel.setLocation( irGraphicWidth / 2 - 5, 10 );
            irGraphic = new CompositePhetGraphic( component );
            irGraphic.addGraphic( irGraphicBackground );
            irGraphic.addGraphic( irGraphicLabel );
            irGraphic.setLocation( uvGraphicWidth + visibleGraphic.getWidth(), 0 );
            addGraphic( irGraphic );
        }
    }
}
