/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent.view;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.flourescent.model.Spectrometer;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * SpectrometerGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpectrometerGraphic extends CompositePhetGraphic implements Spectrometer.ChangeListener {

    private String spectrometerImageFileName = "images/spectrometer-panel.png";

    private PhetImageGraphic backgroundPanel;
    private Point displayOrigin = new Point( 15, 115 );
    private int displayHeight = 100;
    private int displayWidth = 400;
    private int horizontalDisplayMargin = 10;


    public SpectrometerGraphic( Component component ) {
        super( component );

        backgroundPanel = new PhetImageGraphic( component, spectrometerImageFileName );
        addGraphic( backgroundPanel );

        setCursorHand();
        addTranslationListener( new DefaultTranslator( this ) );
    }

    class DefaultTranslator implements TranslationListener {
        private PhetGraphic graphic;

        public DefaultTranslator( PhetGraphic graphic ) {
            this.graphic = graphic;
        }

        public void translationOccurred( TranslationEvent translationEvent ) {
            int dx = translationEvent.getDx();
            int dy = translationEvent.getDy();
            graphic.translate( dx, dy );
        }
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void countChanged( Spectrometer.ChangeEvent event ) {
        // Determine the y location for the wavelength of the photon in the event
        double wavelength = event.getWavelength();
        double minWavelength = Photon.DEEP_RED;
        double maxWavelength = Photon.BLUE;
        int wavelengthLoc = (int)( ( wavelength - minWavelength )
                                   / ( maxWavelength - minWavelength ) * ( displayWidth - horizontalDisplayMargin * 2 )
                                 + horizontalDisplayMargin );
        double indicatorRadius = 1.5;
        int indicatorLoc = (int)( -( event.getPhotonCount() - 1 ) * indicatorRadius * 2 );

        // If we havent' filled the height of the display with indicators, add one
        if( -indicatorLoc <= displayHeight - indicatorRadius) {
            // Create a graphic for the photon
            Ellipse2D.Double shape = new Ellipse2D.Double( wavelengthLoc + displayOrigin.getX(),
                                                           indicatorLoc + displayOrigin.getY(),
                                                           indicatorRadius * 2, indicatorRadius * 2 );
            PhetShapeGraphic psg = new PhetShapeGraphic( getComponent(),
                                                         shape,
                                                         new VisibleColor( wavelength ) );
            psg.setRegistrationPoint( 0, 3 );
            addGraphic( psg );
            setBoundsDirty();
            repaint();
        }
    }
}
