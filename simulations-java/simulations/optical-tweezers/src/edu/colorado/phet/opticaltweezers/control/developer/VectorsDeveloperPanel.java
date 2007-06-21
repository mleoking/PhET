/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.developer;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.view.AbstractForceNode;
import edu.colorado.phet.opticaltweezers.view.LaserElectricFieldNode;
import edu.colorado.phet.opticaltweezers.view.LaserNode;

/**
 * VectorsControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VectorsDeveloperPanel extends JPanel {
    
    private List _forceVectorNodes; // list of AbstractForceNode
    private LaserNode _laserNode;
    
    private JCheckBox _showValuesCheckBox;
    private JCheckBox _showComponentsCheckBox;
    
    public VectorsDeveloperPanel( Font titleFont, Font controlFont, List forceVectorNodes, LaserNode laserNode ) {
        super();
        
        _forceVectorNodes = new ArrayList( forceVectorNodes );
        _laserNode = laserNode;
        
        TitledBorder border = new TitledBorder( "Vectors" );
        border.setTitleFont( titleFont );
        this.setBorder( border );
        
        _showValuesCheckBox = new JCheckBox( "Show values" );
        _showValuesCheckBox.setFont( controlFont );
        _showValuesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleShowValuesCheckBox();
            }
        } );
        
        _showComponentsCheckBox = new JCheckBox( "Show XY components" );
        _showComponentsCheckBox.setFont( controlFont );
        _showComponentsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleShowComponentsCheckBox();
            }
        } );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( new Insets( 0, 0, 0, 0 ) );
        this.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _showValuesCheckBox, row++, column );
        layout.addComponent( _showComponentsCheckBox, row++, column );
    }
    
    public void setValuesVisible( boolean visible ) {
        _showValuesCheckBox.setSelected( visible );
        handleShowValuesCheckBox();
    }
    
    public void setComponentsVisible( boolean visible ) {
        _showComponentsCheckBox.setSelected( visible );
        handleShowComponentsCheckBox();
    }
    
    private void handleShowValuesCheckBox() {
        final boolean visible = _showValuesCheckBox.isSelected();
        Iterator i = _forceVectorNodes.iterator();
        while ( i.hasNext() ) {
            AbstractForceNode forceVectorNode = (AbstractForceNode) i.next();
            forceVectorNode.setValuesVisible( visible );
        }
        _laserNode.setElectricFieldValuesVisible( visible );
    }
    
    private void handleShowComponentsCheckBox() {
        final boolean visible = _showComponentsCheckBox.isSelected();
        Iterator i = _forceVectorNodes.iterator();
        while ( i.hasNext() ) {
            AbstractForceNode forceVectorNode = (AbstractForceNode) i.next();
            forceVectorNode.setComponentVectorsVisible( visible );
        }
    }

}
