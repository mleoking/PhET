/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.developer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.control.ColorControl;
import edu.colorado.phet.opticaltweezers.view.DNAForceNode;
import edu.colorado.phet.opticaltweezers.view.FluidDragForceNode;
import edu.colorado.phet.opticaltweezers.view.LaserElectricFieldNode;
import edu.colorado.phet.opticaltweezers.view.TrapForceNode;

/**
 * VectorsDeveloperPanel contains developer controls related to the display of vectors.
 * This panel is for developers only, and it is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VectorsDeveloperPanel extends JPanel {
    
    private TrapForceNode _trapForceNode;
    private FluidDragForceNode _fluidDragForceNode;
    private DNAForceNode _dnaForceNode;
    private LaserElectricFieldNode _electricFieldNode;

    private ColorControl _trapForceColorChip;
    private ColorControl _fluidDragForceColorChip;
    private ColorControl _dnaForceColorChip;
    private ColorControl _electricFieldColorChip;
    private JCheckBox _showValuesCheckBox;
    private JCheckBox _showComponentsCheckBox;
    
    public VectorsDeveloperPanel( Font titleFont, Font controlFont, 
            TrapForceNode trapForceNode, FluidDragForceNode fluidDragForceNode, DNAForceNode dnaForceNode, LaserElectricFieldNode electricFieldNode ) {
        super();
        
        _trapForceNode = trapForceNode;
        _fluidDragForceNode = fluidDragForceNode;
        _dnaForceNode = dnaForceNode;
        _electricFieldNode = electricFieldNode;
        
        TitledBorder border = new TitledBorder( "Vectors" );
        border.setTitleFont( titleFont );
        this.setBorder( border );
        
        Paint trapForcePaint = _trapForceNode.getArrowFillPaint();
        Color trapForceColor = ( trapForcePaint instanceof Color ) ? ( (Color)trapForcePaint ) : Color.BLACK;
        _trapForceColorChip = new ColorControl( "Trap force color:", trapForceColor ) {
            protected void setColor( Color color ) {
                super.setColor( color );
                _trapForceNode.setArrowFillPaint( color );
            }
        };
        
        Paint fluidDragForcePaint = _fluidDragForceNode.getArrowFillPaint();
        Color fluidDragForceColor = ( fluidDragForcePaint instanceof Color ) ? ( (Color)fluidDragForcePaint ) : Color.BLACK;
        _fluidDragForceColorChip = new ColorControl( "Fluid drag force color:", fluidDragForceColor ) {
            protected void setColor( Color color ) {
                super.setColor( color );
                _fluidDragForceNode.setArrowFillPaint( color );
            }
        };
        
        if ( _dnaForceNode != null ) {
            Paint dnaForcePaint = _dnaForceNode.getArrowFillPaint();
            Color dnaForceColor = ( dnaForcePaint instanceof Color ) ? ( (Color) dnaForcePaint ) : Color.BLACK;
            _dnaForceColorChip = new ColorControl( "DNA force color:", dnaForceColor ) {
                protected void setColor( Color color ) {
                    super.setColor( color );
                    _dnaForceNode.setArrowFillPaint( color );
                }
            };
        }
        
        if ( _electricFieldNode != null ) {
            _electricFieldColorChip = new ColorControl( "E-field color:", _electricFieldNode.getVectorColor() ) {
                protected void setColor( Color color ) {
                    super.setColor( color );
                    _electricFieldNode.setVectorColor( color );
                }
            };
        }
        
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
        layout.addComponent( _trapForceColorChip, row++, column );
        layout.addComponent( _fluidDragForceColorChip, row++, column );
        if ( _dnaForceColorChip != null ) {
            layout.addComponent( _dnaForceColorChip, row++, column );
        }
        if ( _electricFieldColorChip != null ) {
            layout.addComponent( _electricFieldColorChip, row++, column );
        }
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
        _trapForceNode.setValuesVisible( visible );
        _fluidDragForceNode.setValuesVisible( visible );
        if ( _dnaForceNode != null ) {
            _dnaForceNode.setValuesVisible( visible );
        }
        if ( _electricFieldNode != null ) {
            _electricFieldNode.setValuesVisible( visible );
        }
    }
    
    private void handleShowComponentsCheckBox() {
        final boolean visible = _showComponentsCheckBox.isSelected();
        _trapForceNode.setComponentVectorsVisible( visible );
        _fluidDragForceNode.setComponentVectorsVisible( visible );
        if ( _dnaForceNode != null ) {
            _dnaForceNode.setComponentVectorsVisible( visible );
        }
        // don't show component for E-field, it only has an x component
    }
}
