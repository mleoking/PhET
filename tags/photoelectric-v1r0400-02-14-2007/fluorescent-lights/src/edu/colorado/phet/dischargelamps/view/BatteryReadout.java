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

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.dischargelamps.model.Battery;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;

/**
 * BatteryReadout
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BatteryReadout extends GraphicLayerSet {
    private Point centerPoint;
    private JTextField readout;
    private PhetGraphic readoutGraphic;
    // Used to avoid problems when JOptionPane is shown indicating invalid input
    private boolean focusLossTemporary;

    public BatteryReadout( final Component component, final Battery battery, Point centerPoint, int offset ) {
        super( component );
        this.centerPoint = centerPoint;

        readout = new JTextField( 5 );
        readout.setHorizontalAlignment( JTextField.HORIZONTAL );
        readout.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setBatteryVoltage( battery, component );
            }
        } );
        readout.addFocusListener( new FocusAdapter() {
            public void focusLost( FocusEvent e ) {
                if( !focusLossTemporary ) {
                    setBatteryVoltage( battery, component );
                }
            }
        } );
        readoutGraphic = PhetJComponent.newInstance( component, readout );
        addGraphic( readoutGraphic, 1E9 );

        battery.addChangeListener( new Battery.ChangeListener() {
            public void voltageChanged( Battery.ChangeEvent event ) {
                double voltage = event.getVoltageSource().getVoltage();
                update( voltage );
            }
        } );
        update( battery.getVoltage() );
    }

    private void setBatteryVoltage( Battery battery, Component component ) {
        double voltage = 0;
        try {
            String text = readout.getText().toUpperCase();
            int vLoc = text.indexOf( 'V' );
            text = vLoc >= 0 ? readout.getText().substring( 0, vLoc ) : text;
            voltage = Double.parseDouble( text );
            double maxVoltage = DischargeLampModel.MAX_VOLTAGE;
            if( voltage >= -maxVoltage && voltage <= maxVoltage ) {
                battery.setVoltage( voltage );
            }
            else {
                focusLossTemporary = true;
                JOptionPane.showMessageDialog( SwingUtilities.getRoot( component ),
                                               "Voltage must be between -" + Double.toString( maxVoltage ) +
                                               "V and " + Double.toString( maxVoltage ) + "V" );
                focusLossTemporary = false;
            }
        }
        catch( NumberFormatException e1 ) {
            JOptionPane.showMessageDialog( SwingUtilities.getRoot( component ),
                                           "Voltage must be numeric, or a number followed by \"V\"" );
            updateText( battery.getVoltage() );
        }
    }

    private void update( double voltage ) {
        updateText( voltage );
        readoutGraphic.setLocation( (int)centerPoint.getX(), (int)centerPoint.getY() );
        readoutGraphic.setBoundsDirty();
        readoutGraphic.repaint();
    }

    private void updateText( double voltage ) {
        DecimalFormat voltageFormat = DischargeLampsConfig.VOLTAGE_FORMAT;
        Object[] args = {voltageFormat.format( voltage )};
        String text = voltageFormat.format( voltage );
//        String text = MessageFormat.format( SimStrings.get( "BatteryGraphic.voltage" ), args );
        readout.setText( text );
    }
}
