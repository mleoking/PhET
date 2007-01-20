/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.control;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.quantum.model.ElectronSource;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.TitledBorder;

/**
 * ElectronProductionControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectronProductionControl extends JPanel {
    private DischargeLampModel model;
    private double currentDisplayFactor;

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    public static class ProductionType {
        private ProductionType() {
        }
    }

    public static final ProductionType CONTINUOUS = new ProductionType();
    public static final ProductionType SINGLE_SHOT = new ProductionType();

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private CurrentSlider heaterControlSlider;

    public ElectronProductionControl( DischargeLampModel model, double maxCurrent, final double currentDisplayFactor ) {
        this.model = model;
        this.currentDisplayFactor = currentDisplayFactor;
        setBorder( new TitledBorder( SimStrings.get( "Controls.ElectronProduction" ) ) );
        heaterControlSlider = new CurrentSlider( maxCurrent );
        add( heaterControlSlider );
        heaterControlSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                ElectronProductionControl.this.model.setCurrent( heaterControlSlider.getValue(), 1 / currentDisplayFactor );
            }
        } );
        heaterControlSlider.setValue( 10 );
    }

    public void setMaxCurrent( double maxCurrent ) {
        heaterControlSlider.setMaxCurrent( maxCurrent );
    }

    public void setHeaterValue( double value ) {
        heaterControlSlider.setValue( value );
    }

    public void setProductionType( ElectronProductionControl.ProductionType type ) {
        if( type == CONTINUOUS ) {
            heaterControlSlider.setVisible( true );
            model.getLeftHandPlate().setCurrent( heaterControlSlider.getValue() / currentDisplayFactor );
            model.setElectronProductionMode( ElectronSource.CONTINUOUS_MODE );
        }
        if( type == SINGLE_SHOT ) {
            heaterControlSlider.setVisible( false );
            model.getLeftHandPlate().setCurrent( 0 );
//            singleShotBtn.setVisible( true );
            model.setElectronProductionMode( ElectronSource.SINGLE_SHOT_MODE );            
        }
    }
}
