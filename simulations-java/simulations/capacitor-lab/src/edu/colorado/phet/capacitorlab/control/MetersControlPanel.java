/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricCanvas;
import edu.colorado.phet.capacitorlab.view.ModelValuesDialog;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel for meter settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MetersControlPanel extends CLTitledControlPanel {
    
    private final Frame parentFrame;
    private final CLModel model;
    private final JCheckBox capacitanceCheckBox, plateChargeCheckBox, energyCheckBox, voltmeterCheckBox, fieldDetectorCheckBox, modelValuesCheckBox;
    
    private ModelValuesDialog modelValuesDialog;
    private Point modelValuesDialogLocation;

    public MetersControlPanel( Frame parentFrame, CLModel model, final DielectricCanvas canvas, boolean dev ) {
        super( CLStrings.TITLE_METERS );
        
        this.parentFrame = parentFrame;
        this.model = model;
        
        capacitanceCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_CAPACITANCE );
        
        plateChargeCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_PLATE_CHARGE );
        plateChargeCheckBox.setSelected( canvas.getPlateChargeMeterNode().isVisible() );
        plateChargeCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                canvas.getPlateChargeMeterNode().setVisible( plateChargeCheckBox.isSelected() );
            }
        });
        canvas.getPlateChargeMeterNode().addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                    plateChargeCheckBox.setSelected( canvas.getPlateChargeMeterNode().isVisible() );
                }
            }
        });
        
        energyCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_ENERGY );
        
        voltmeterCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_VOLTMETER );
        
        fieldDetectorCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_FIELD_DETECTOR );
        
        modelValuesCheckBox = new JCheckBox( "Model Values" );
        modelValuesCheckBox.setForeground( Color.RED );
        modelValuesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( modelValuesCheckBox.isSelected() ) {
                    openModelValuesDialog();
                }
                else {
                    closeModelValuesDialog();
                }
            }
        });
        
        // layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( capacitanceCheckBox, row++, column );
        layout.addComponent( plateChargeCheckBox, row++, column );
        layout.addComponent( energyCheckBox, row++, column );
        layout.addComponent( voltmeterCheckBox, row++, column );
        layout.addComponent( fieldDetectorCheckBox, row++, column );
        if ( dev ) {
            layout.addComponent( modelValuesCheckBox, row++, column );
        }
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
    }
    
    private void openModelValuesDialog() {
        
        closeModelValuesDialog();
        
        modelValuesDialog = new ModelValuesDialog( parentFrame, model );
        modelValuesDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            @Override
            public void windowClosing( WindowEvent e ) {
                closeModelValuesDialog();
            }

            // called by JDialog.dispose
            @Override
            public void windowClosed( WindowEvent e ) {
                modelValuesDialog = null;
                if ( modelValuesCheckBox.isSelected() ) {
                    modelValuesCheckBox.setSelected( false );
                }
            }
        } );
        
        if ( modelValuesDialogLocation == null ) {
            SwingUtils.centerDialogInParent( modelValuesDialog );
        }
        else {
            modelValuesDialog.setLocation( modelValuesDialogLocation );
        }
        
        modelValuesDialog.setVisible( true );
    }
    
    private void closeModelValuesDialog() {

        if ( modelValuesDialog != null ) {
            modelValuesDialogLocation = modelValuesDialog.getLocation();
            modelValuesDialog.dispose();
            modelValuesDialog = null;
        }
    }
}
