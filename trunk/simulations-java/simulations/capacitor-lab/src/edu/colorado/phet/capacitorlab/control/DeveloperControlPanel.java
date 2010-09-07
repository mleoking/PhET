/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;

import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.util.GridPanel;
import edu.colorado.phet.capacitorlab.util.GridPanel.Anchor;
import edu.colorado.phet.capacitorlab.view.ModelValuesDialog;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;


/**
 * Developer controls for capacitor.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlPanel extends PhetTitledPanel {
    
    private final Frame parentFrame;
    private final CLModel model;
    private final JCheckBox modelValuesCheckBox;
    
    private ModelValuesDialog modelValuesDialog;
    private Point modelValuesDialogLocation;

    public DeveloperControlPanel( Frame parentFrame, final CLModel model ) {
        super( "Developer" );
        setTitleColor( Color.RED );
        
        this.parentFrame = parentFrame;
        
        this.model = model;
        this.model.getCapacitor().addCapacitorChangeListener( new CapacitorChangeAdapter() {
            //XXX override whatever is needed
        });
        
        // Model Values dialog
        {
            modelValuesCheckBox = new JCheckBox( "Model Values" );
            modelValuesCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( modelValuesCheckBox.isSelected() ) {
                        openModelValuesDialog();
                    }
                    else {
                        closeModelValuesDialog();
                    }
                }
            } );
        }
        
        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( modelValuesCheckBox );
        
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
