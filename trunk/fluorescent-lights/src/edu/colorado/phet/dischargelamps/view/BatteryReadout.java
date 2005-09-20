/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.view;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.model.Battery;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * BatteryReadout
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BatteryReadout extends CompositePhetGraphic {
    private static Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 12 );
    private static Color VALUE_COLOR = Color.BLACK;

    private PhetTextGraphic batteryReadout;
    private PhetShapeGraphic background;
    private Point centerPoint;
    private int offset;

    public BatteryReadout( Component component, Battery battery, Point centerPoint, int offset ) {
        super( component );
        this.centerPoint = centerPoint;
        this.offset = offset;

        Rectangle2D backgroundRect = new Rectangle2D.Double( 0, 0, 40, 20 );
        background = new PhetShapeGraphic( component, backgroundRect, Color.lightGray, new BasicStroke( 1 ), Color.black );
        addGraphic( background );

        batteryReadout = new PhetTextGraphic( component, VALUE_FONT, "", VALUE_COLOR );
        addGraphic( batteryReadout );
        battery.addChangeListener( new Battery.ChangeListener() {
            public void voltageChanged( Battery.ChangeEvent event ) {
                double voltage = event.getVoltageSource().getVoltage();
                update( voltage );
            }
        } );
        update( battery.getVoltage() );
    }

    private void update( double voltage ) {
        DecimalFormat voltageFormat = new DecimalFormat( "#0.0" );
        Object[] args = {voltageFormat.format( Math.abs( voltage ) * DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR )};
        String text = MessageFormat.format( SimStrings.get( "BatteryGraphic.voltage" ), args );
        batteryReadout.setText( text );
        batteryReadout.setLocation( (int)centerPoint.getX() + (int)background.getWidth(), (int)centerPoint.getY() );
        background.setLocation( centerPoint );
        // Right justify in the bckground rectangle
        batteryReadout.setRegistrationPoint( batteryReadout.getWidth() + 5, VALUE_FONT.getSize() - 30 );
    }
}
