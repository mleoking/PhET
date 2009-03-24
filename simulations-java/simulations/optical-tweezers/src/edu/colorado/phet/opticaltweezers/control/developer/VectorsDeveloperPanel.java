/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.developer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.view.DNAForceNode;
import edu.colorado.phet.opticaltweezers.view.ElectricFieldNode;
import edu.colorado.phet.opticaltweezers.view.FluidDragForceNode;
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
    private ElectricFieldNode _electricFieldNode;

    private ColorControl _trapForceColorChip;
    private ColorControl _fluidDragForceColorChip;
    private ColorControl _dnaForceColorChip;
    private ColorControl _electricFieldColorChip;
    private JCheckBox _showComponentsCheckBox;

    public VectorsDeveloperPanel( Font titleFont, Font controlFont,
            TrapForceNode trapForceNode, FluidDragForceNode fluidDragForceNode, DNAForceNode dnaForceNode, ElectricFieldNode electricFieldNode ) {
        super();

        _trapForceNode = trapForceNode;
        _fluidDragForceNode = fluidDragForceNode;
        _dnaForceNode = dnaForceNode;
        _electricFieldNode = electricFieldNode;

        TitledBorder border = new TitledBorder( "Vectors" );
        border.setTitleFont( titleFont );
        this.setBorder( border );

        Frame parentFrame = PhetApplication.instance().getPhetFrame();

        Paint trapForcePaint = _trapForceNode.getArrowFillPaint();
        Color trapForceColor = ( trapForcePaint instanceof Color ) ? ( (Color)trapForcePaint ) : Color.BLACK;
        _trapForceColorChip = new ColorControl( parentFrame, "Trap force color:", trapForceColor );
        _trapForceColorChip.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                _trapForceNode.setArrowFillPaint( _trapForceColorChip.getColor() );
            }
        });

        Paint fluidDragForcePaint = _fluidDragForceNode.getArrowFillPaint();
        Color fluidDragForceColor = ( fluidDragForcePaint instanceof Color ) ? ( (Color)fluidDragForcePaint ) : Color.BLACK;
        _fluidDragForceColorChip = new ColorControl( parentFrame, "Fluid drag force color:", fluidDragForceColor );
        _fluidDragForceColorChip.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                _fluidDragForceNode.setArrowFillPaint( _fluidDragForceColorChip.getColor() );
            }
        } );

        if ( _dnaForceNode != null ) {
            Paint dnaForcePaint = _dnaForceNode.getArrowFillPaint();
            Color dnaForceColor = ( dnaForcePaint instanceof Color ) ? ( (Color) dnaForcePaint ) : Color.BLACK;
            _dnaForceColorChip = new ColorControl( parentFrame, "DNA force color:", dnaForceColor );
            _dnaForceColorChip.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    _dnaForceNode.setArrowFillPaint( _dnaForceColorChip.getColor() );
                }
            } );
        }

        if ( _electricFieldNode != null ) {
            _electricFieldColorChip = new ColorControl( parentFrame, "E-field color:", _electricFieldNode.getVectorColor() );
            _electricFieldColorChip.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    _electricFieldNode.setVectorColor( _electricFieldColorChip.getColor() );
                }
            } );
        }

        _showComponentsCheckBox = new JCheckBox( "Show XY components" );
        _showComponentsCheckBox.setFont( controlFont );
        _showComponentsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleShowComponentsCheckBox();
            }
        } );

        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( OTConstants.SUB_PANEL_INSETS );
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
        layout.addComponent( _showComponentsCheckBox, row++, column );
    }

    public void setComponentsVisible( boolean visible ) {
        _showComponentsCheckBox.setSelected( visible );
        handleShowComponentsCheckBox();
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
