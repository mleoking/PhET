/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Controls related to the capacitor's dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricPropertiesControlPanel extends CLTitledControlPanel {
    
    private final DielectricMaterialControl materialControl;
    private final DielectricConstantControl constantControl;
    private final DielectricChargesControl chargesControl;

    public DielectricPropertiesControlPanel() {
        super( CLStrings.TITLE_DIELECTRIC );
        
        materialControl = new DielectricMaterialControl();
        materialControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateConstantControl();
            }
        });
        
        constantControl = new DielectricConstantControl( CLConstants.DIELECTRIC_CONSTANT_RANGE.getDefault() );
        constantControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateMaterialControl();
            }
        });
        
        chargesControl = new DielectricChargesControl();
        
        // layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        int row = 0;
        int column = 0;
        layout.addComponent( materialControl, row++, column );
        layout.addComponent( constantControl, row++, column );
        layout.addComponent( chargesControl, row++, column );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // default state
        updateConstantControl();
    }
    
    private void updateMaterialControl() {
        DielectricMaterial material = materialControl.getMaterial();
        if ( material instanceof CustomDielectricMaterial ) {
            ( (CustomDielectricMaterial) material ).setDielectricConstant( constantControl.getValue() );
        }
    }
    
    private void updateConstantControl() {
        constantControl.setEnabled( materialControl.isCustomMaterial() );
        constantControl.setValue( materialControl.getMaterial().getDielectricConstant() );
    }
}
