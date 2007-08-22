/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.colorado.phet.opticaltweezers.view.DNAForceNode;
import edu.colorado.phet.opticaltweezers.view.FluidDragForceNode;
import edu.colorado.phet.opticaltweezers.view.TrapForceNode;

/**
 * ForcesControlPanel controls things related to force vectors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ForcesControlPanel extends JPanel implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // vector icon properties
    private static final int VECTOR_ICON_LENGTH = 25;
    private static final int VECTOR_ICON_TAIL_WIDTH = 3;
    private static final int VECTOR_ICON_HEAD_SIZE = 10;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;
    private Fluid _fluid;
    private TrapForceNode _trapForceNode;
    private FluidDragForceNode _dragForceNode;
    private DNAForceNode _dnaForceNode;
    
    private JCheckBox _trapForceCheckBox;
    private JCheckBox _dragForceCheckBox;
    private JCheckBox _brownianMotionCheckBox;
    private JCheckBox _dnaForceCheckBox;
    private JCheckBox _showValuesCheckBox;
    private JCheckBox _constantTrapForceCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param titleFont
     * @param controlFont
     * @param bead
     * @param fluid
     * @param trapForceNode
     * @param dragForceNode
     * @param dnaForceNode optional
     */
    public ForcesControlPanel( 
            Font titleFont, 
            Font controlFont, 
            Bead bead, 
            Fluid fluid,
            TrapForceNode trapForceNode, 
            FluidDragForceNode dragForceNode, 
            DNAForceNode dnaForceNode ) {
        super();
        
        _bead = bead;
        _bead.addObserver( this );
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _trapForceNode = trapForceNode;
        _dragForceNode = dragForceNode;
        _dnaForceNode = dnaForceNode;
        
        JLabel titleLabel = new JLabel( OTResources.getString( "label.forcesOnBead" ) );
        titleLabel.setFont( titleFont );
        
        // Trap force checkbox and icon
        JPanel trapForcePanel = null;
        {
            _trapForceCheckBox = new JCheckBox( OTResources.getString( "label.showTrapForce" ) );
            _trapForceCheckBox.setFont( controlFont );
            _trapForceCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleTrapForceCheckBox();
                }
            } );
            
            JLabel trapForceLabel = new JLabel( createVectorIcon( OTConstants.TRAP_FORCE_COLOR ) );
            trapForceLabel.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    setTrapForceSelected( !isTrapForceSelected() );
                }
            } );
            
            trapForcePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            trapForcePanel.add( _trapForceCheckBox );
            trapForcePanel.add( trapForceLabel );
        }
        
        // Drag force checkbox and icon
        JPanel dragForcePanel = null;
        {
            _dragForceCheckBox = new JCheckBox( OTResources.getString( "label.showDragForce" ) );
            _dragForceCheckBox.setFont( controlFont );
            _dragForceCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleFluidDragCheckBox();
                }
            } );
            
            JLabel dragForceLabel = new JLabel( createVectorIcon( OTConstants.FLUID_DRAG_FORCE_COLOR ) );
            dragForceLabel.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    setDragForceSelected( !isDragForceSelected() );
                }
            } );
            
            dragForcePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            dragForcePanel.add( _dragForceCheckBox );
            dragForcePanel.add( dragForceLabel );
        }
        
        // DNA force checkbox and icon
        JPanel dnaForcePanel = null;
        if ( _dnaForceNode != null ) {
            
            _dnaForceCheckBox = new JCheckBox( OTResources.getString( "label.showDNAForce" ) );
            _dnaForceCheckBox.setFont( controlFont );
            _dnaForceCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleDNAForceCheckBox();
                }
            } );
            
            JLabel dnaForceLabel = new JLabel( createVectorIcon( OTConstants.DNA_FORCE_COLOR ) );
            dnaForceLabel.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    setDNAForceSelected( !isDNAForceSelected() );
                }
            } );
            
            dnaForcePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            dnaForcePanel.add( _dnaForceCheckBox );
            dnaForcePanel.add( dnaForceLabel );
        }
        
        _brownianMotionCheckBox = new JCheckBox( OTResources.getString( "label.enableBrownianMotion" ) );
        _brownianMotionCheckBox.setFont( controlFont );
        _brownianMotionCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleBrownianMotionCheckBox();
            }
         });
        
        _showValuesCheckBox = new JCheckBox( OTResources.getString( "label.showForceValues" ) );
        _showValuesCheckBox.setFont( controlFont );
        _showValuesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleShowValuesCheckBox();
            }
        } );
        
        _constantTrapForceCheckBox = new JCheckBox( OTResources.getString( "label.constantTrapForce" ) );
        _constantTrapForceCheckBox.setFont( controlFont );
        _constantTrapForceCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleConstantTrapForceCheckBox();
            }
        } );
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row++, column );
        layout.addComponent( trapForcePanel, row++, column );
        layout.addComponent( dragForcePanel, row++, column );
        if ( dnaForcePanel != null ) {
            layout.addComponent( dnaForcePanel, row++, column );
        }
        layout.addComponent( _brownianMotionCheckBox, row++, column );
        layout.addComponent( _showValuesCheckBox, row++, column );
        layout.addComponent( _constantTrapForceCheckBox, row++, column );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Default state
        _trapForceCheckBox.setSelected( false );
        _dragForceCheckBox.setSelected( false );
        _dragForceCheckBox.setEnabled( _fluid.isEnabled() );
        _brownianMotionCheckBox.setSelected( _bead.isBrownianMotionEnabled() );
        _brownianMotionCheckBox.setEnabled( _fluid.isEnabled() );
        if ( _dnaForceCheckBox != null ) {
            _dnaForceCheckBox.setSelected( false );
        }
        _showValuesCheckBox.setSelected( false );
        _constantTrapForceCheckBox.setSelected( false );
    }
    
    public void cleanup() {
        _bead.deleteObserver( this );
        _fluid.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setTrapForceSelected( boolean b ) {
        _trapForceCheckBox.setSelected( b );
        handleTrapForceCheckBox();
    }
    
    public boolean isTrapForceSelected() {
        return _trapForceCheckBox.isSelected();
    }
    
    public void setDragForceSelected( boolean b ) {
        _dragForceCheckBox.setSelected( b );
        handleFluidDragCheckBox();
    }
    
    public boolean isDragForceSelected() {
        return _dragForceCheckBox.isSelected();
    }
   
    public void setBrownianMotionSelected( boolean b ) {
        _brownianMotionCheckBox.setSelected( b );
        handleBrownianMotionCheckBox();
    }
    
    public boolean isBrownianMotionSelected() {
        return _brownianMotionCheckBox.isSelected();
    }
    
    public void setDNAForceSelected( boolean b ) {
        if ( _dnaForceCheckBox != null ) {
            _dnaForceCheckBox.setSelected( b );
            handleDNAForceCheckBox();
        }
    }
    
    public boolean isDNAForceSelected() { 
        boolean selected = false;
        if ( _dnaForceCheckBox != null ) {
            selected = _dnaForceCheckBox.isSelected();
        }
        return selected;
    }
    
    public void setShowValuesSelected( boolean b ) {
        _showValuesCheckBox.setSelected( b );
        handleShowValuesCheckBox();
    }
    
    public boolean isShowValuesSelected() {
        return _showValuesCheckBox.isSelected();
    }
    
    public void setConstantTrapForceSelected( boolean b ) {
        _constantTrapForceCheckBox.setSelected( b );
        handleConstantTrapForceCheckBox();
    }
    
    public boolean isConstantTrapForceSelected() {
        return _constantTrapForceCheckBox.isSelected();
    }
    
    //----------------------------------------------------------------------------
    // Disable features that are not visible in for some control panels
    //----------------------------------------------------------------------------
    
    public void setBrownianMotionCheckBoxVisible( boolean visible ) {
        _brownianMotionCheckBox.setVisible( visible );
    }
    
    public void setShowValuesCheckBoxVisible( boolean visible ) {
        _showValuesCheckBox.setVisible( visible );
    }
    
    public void setConstantTrapForceCheckBoxVisible( boolean visible ) {
        _constantTrapForceCheckBox.setVisible( visible );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleTrapForceCheckBox() {
        final boolean selected = _trapForceCheckBox.isSelected();
        _trapForceNode.setVisible( selected );
    }
    
    private void handleFluidDragCheckBox() {
        final boolean selected = _dragForceCheckBox.isSelected();
        _dragForceNode.setVisible( selected );
    }
    
    private void handleBrownianMotionCheckBox() {
        final boolean selected = _brownianMotionCheckBox.isSelected();
        _bead.setBrownianMotionEnabled( selected );
    }
    
    private void handleDNAForceCheckBox() {
        final boolean selected = _dnaForceCheckBox.isSelected();
        _dnaForceNode.setVisible( selected );
    }
    
    private void handleShowValuesCheckBox() {
        final boolean visible = _showValuesCheckBox.isSelected();
        _trapForceNode.setValuesVisible( visible );
        _dragForceNode.setValuesVisible( visible );
        if ( _dnaForceNode != null ) {
            _dnaForceNode.setValuesVisible( visible );
        }
    }
    
    private void handleConstantTrapForceCheckBox() {
        //XXX
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _fluid ) {
            if ( arg == Fluid.PROPERTY_ENABLED ) {
                _brownianMotionCheckBox.setEnabled( _fluid.isEnabled() );
                _dragForceCheckBox.setEnabled( _fluid.isEnabled() );
                _dragForceNode.setVisible( _fluid.isEnabled() && _dragForceCheckBox.isSelected() );
            }
        }
        else if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_BROWNIAN_MOTION_ENABLED ) {
                _brownianMotionCheckBox.setSelected( _bead.isBrownianMotionEnabled() );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    private static Icon createVectorIcon( Color color ) {
        Vector2DNode vectorNode = new Vector2DNode( -VECTOR_ICON_LENGTH, 0, VECTOR_ICON_LENGTH, VECTOR_ICON_LENGTH );
        vectorNode.setArrowFillPaint( color );
        vectorNode.setTailWidth( VECTOR_ICON_TAIL_WIDTH );
        vectorNode.setHeadSize( VECTOR_ICON_HEAD_SIZE, VECTOR_ICON_HEAD_SIZE );
        Image image = vectorNode.toImage();
        return new ImageIcon( image );
    }
}
