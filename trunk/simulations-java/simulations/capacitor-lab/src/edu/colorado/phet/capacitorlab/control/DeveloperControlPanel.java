/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


/**
 * Developer controls for capacitor.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlPanel extends CLTitledControlPanel {
    
    private final CLModel model;
    private final LinearValueControl plateSizeControl, plateSeparationControl, dielectricOffsetControl;

    public DeveloperControlPanel( final CLModel model ) {
        super( "Developer", Color.RED );
        
        this.model = model;
        this.model.getCapacitor().addCapacitorChangeListener( new CapacitorChangeAdapter() {
            
            @Override
            public void plateSizeChanged() {
                plateSizeControl.setValue( model.getCapacitor().getPlateSize() );
            }
            
            @Override
            public void plateSeparationChanged() {
                plateSeparationControl.setValue( model.getCapacitor().getPlateSeparation() );
            }
            
            @Override
            public void dielectricOffsetChanged() {
                dielectricOffsetControl.setValue( model.getCapacitor().getDielectricOffset() );
            }
        });
        
        // plate size
        {
            double min = CLConstants.PLATE_SIZE_RANGE.getMin();
            double max = CLConstants.PLATE_SIZE_RANGE.getMax();
            String label = "Plate size:";
            String textFieldPattern = "##0.0";
            String units = "mm";
            plateSizeControl = new LinearValueControl( min, max, label, textFieldPattern, units );
            plateSizeControl.setValue( model.getCapacitor().getPlateSize() );
            plateSizeControl.setBorder( new EtchedBorder() );
            plateSizeControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.getCapacitor().setPlateSize( plateSizeControl.getValue() );
                }
            } );
        }
        
        // plate separation
        {
            double min = CLConstants.PLATE_SEPARATION_RANGE.getMin();
            double max = CLConstants.PLATE_SEPARATION_RANGE.getMax();
            String label = "Plate separation:";
            String textFieldPattern = "##0.0";
            String units = "mm";
            plateSeparationControl = new LinearValueControl( min, max, label, textFieldPattern, units );
            plateSeparationControl.setValue( model.getCapacitor().getPlateSeparation() );
            plateSeparationControl.setBorder( new EtchedBorder() );
            plateSeparationControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.getCapacitor().setPlateSeparation( plateSeparationControl.getValue() );
                }
            } );
        }
        
        // dielectric offset
        {
            double min = CLConstants.DIELECTRIC_OFFSET_RANGE.getMin();
            double max = CLConstants.DIELECTRIC_OFFSET_RANGE.getMax();
            String label = "Dielectric offset:";
            String textFieldPattern = "##0.0";
            String units = "mm";
            dielectricOffsetControl = new LinearValueControl( min, max, label, textFieldPattern, units );
            dielectricOffsetControl.setValue( model.getCapacitor().getDielectricOffset() );
            dielectricOffsetControl.setBorder( new EtchedBorder() );
            dielectricOffsetControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.getCapacitor().setDielectricOffset( dielectricOffsetControl.getValue() );
                }
            } );
        }
        
        // layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( plateSizeControl, row++, column );
        layout.addComponent( plateSeparationControl, row++, column );
        layout.addComponent( dielectricOffsetControl, row++, column );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
    }
}
