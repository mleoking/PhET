/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


/**
 * Developer controls for capacitor.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlPanel extends CLTitledControlPanel {
    
    private final LinearValueControl sizeControl, separationControl;

    public DeveloperControlPanel() {
        super( "Developer", Color.RED );
        
        // size
        {
            double min = CLConstants.PLATE_SIZE_RANGE.getMin();
            double max = CLConstants.PLATE_SIZE_RANGE.getMax();
            String label = "Plate size:";
            String textFieldPattern = "##0";
            String units = "mm";
            sizeControl = new LinearValueControl( min, max, label, textFieldPattern, units );
            sizeControl.setBorder( new EtchedBorder() );
        }
        
        // separation
        {
            double min = CLConstants.PLATE_SEPARATION_RANGE.getMin();
            double max = CLConstants.PLATE_SEPARATION_RANGE.getMax();
            String label = "Plate separation:";
            String textFieldPattern = "##0";
            String units = "mm";
            separationControl = new LinearValueControl( min, max, label, textFieldPattern, units );
            separationControl.setBorder( new EtchedBorder() );
        }
        
        // layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( sizeControl, row++, column );
        layout.addComponent( separationControl, row++, column );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // default state
//        sizeControl.setValue( CapacitorNode.getPlateWidth() ); //XXX
//        separationControl.setValue( capacitor.getSeparation() ); //XXX
    }
    
}
