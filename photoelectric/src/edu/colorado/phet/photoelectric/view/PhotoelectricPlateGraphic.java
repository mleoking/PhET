/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.quantum.view.PlateGraphic;

import java.awt.*;
import java.util.ArrayList;

/**
 * PhotoelectricPlateGraphic
 * <p>
 * A PlateGraphic that displays a number of "+" or "-" characters on it, the number being proportional
 * to the voltage between the plate and the other plate in the model 
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricPlateGraphic extends PlateGraphic {

    public static int POSITIVE = 0;
    public static int NEGATIVE = 1;
    private static String[] chars = new String[]{"+", "-"};
    private static Color[] colors = new Color[]{Color.red, Color.blue};

    private int polarity;
    private ArrayList chargeIndicatorGraphics = new ArrayList();

    public PhotoelectricPlateGraphic( Component component, double plateLength, PhotoelectricModel model,
                                      int polarity ) {
        super( component, plateLength );

        if( polarity != POSITIVE && polarity != NEGATIVE ) {
            throw new RuntimeException( "invalid polarity" );
        }
        this.polarity = polarity;
        model.addChangeListener( new PotentialChangeListener() );
    }



    //----------------------------------------------------------------
    // Inner classes 
    //----------------------------------------------------------------
    
    /**
     * Listens for changes in the voltage between the plates of the model, and paints "+" or "-"
     * characters on the plate to represent the plate's potential
     */
    class PotentialChangeListener extends PhotoelectricModel.ChangeListenerAdapter {

        public void voltageChanged( PhotoelectricModel.ChangeEvent event ) {
            String chargeIndicator;
            Color color = null;
            PhotoelectricModel model = event.getPhotoelectricModel();

            int idx = 0;
            if( model.getVoltage() > 0 ) {
                idx = polarity;
            }
            else if( model.getVoltage() < 0 ) {
                idx = ( polarity + 1 ) % chars.length;
            }
            chargeIndicator = chars[idx];
            color = colors[idx];

            // Clear any existing indicators
            for( int i = 0; i < chargeIndicatorGraphics.size(); i++ ) {
                PhetGraphic graphic = (PhetGraphic)chargeIndicatorGraphics.get( i );
                removeGraphic( graphic );
            }
            // Add new indicators to the list and display them
            chargeIndicatorGraphics.clear();
            int numChargeIndicators = (int)Math.abs( model.getVoltage() * 10 );
            double indicatorSpacing = (double)(getHeight() - 10) / ( numChargeIndicators + 1 );
            for( int i = 0; i < numChargeIndicators; i++ ) {
                PhetTextGraphic indicator = new PhetTextGraphic( getComponent(),
                                                                 DischargeLampsConfig.DEFAULT_CONTROL_FONT,
                                                                 chargeIndicator,
                                                                 color,
                                                                 getWidth() / 2 - DischargeLampsConfig.DEFAULT_CONTROL_FONT.getSize() / 2,
                                                                 (int)( indicatorSpacing * ( i + 1 ) ) );
                chargeIndicatorGraphics.add( indicator );
                addGraphic( indicator );
            }
            setBoundsDirty();
            repaint();

        }
    }
}
