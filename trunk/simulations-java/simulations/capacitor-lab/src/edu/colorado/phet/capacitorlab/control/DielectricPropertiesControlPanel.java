/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Controls related to the capacitor's dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricPropertiesControlPanel extends PhetTitledPanel {
    
    private final Capacitor capacitor;
    private final DielectricMaterialControl materialControl;
    private final DielectricConstantControl constantControl;
    private final DielectricChargesControl chargesControl;

    public DielectricPropertiesControlPanel( CLModel model ) {
        super( CLStrings.TITLE_DIELECTRIC );
        
        this.capacitor = model.getCapacitor();
        capacitor.addCapacitorChangeListener( new CapacitorChangeAdapter() {
            @Override
            public void dielectricMaterialChanged() {
                updateControls();
            }
        });
        
        materialControl = new DielectricMaterialControl( model.getDielectricMaterials(), model.getCapacitor().getDielectricMaterial() );
        materialControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                capacitor.setDielectricMaterial( materialControl.getMaterial() );
            }
        });
        
        constantControl = new DielectricConstantControl( CLConstants.DIELECTRIC_CONSTANT_RANGE.getDefault() );
        constantControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( capacitor.getDielectricMaterial() instanceof CustomDielectricMaterial ) {
                    ( (CustomDielectricMaterial) capacitor.getDielectricMaterial() ).setDielectricConstant( constantControl.getValue() );
                }
            }
        } );
        
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
        updateControls();
        //XXX chargesControl?
    }
    
    private void updateControls() {
        DielectricMaterial material = capacitor.getDielectricMaterial();
        materialControl.setMaterial( material );
        constantControl.setValue( material.getDielectricConstant() );
        constantControl.setEnabled( material instanceof CustomDielectricMaterial );
    }
}
