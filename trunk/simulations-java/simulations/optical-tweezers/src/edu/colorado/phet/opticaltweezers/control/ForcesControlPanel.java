/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.event.DragNotificationHandler.DragNotificationAdapter;
import edu.colorado.phet.common.piccolophet.event.DragNotificationHandler.DragNotificationEvent;
import edu.colorado.phet.common.piccolophet.event.DragNotificationHandler.DragNotificationListener;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.LaserPositionController;
import edu.colorado.phet.opticaltweezers.view.*;

/**
 * ForcesControlPanel controls things related to force vectors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ForcesControlPanel extends JPanel implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;
    private Fluid _fluid;
    private Laser _laser;
    private LaserPositionController _laserPositionController;
    private TrapForceNode _trapForceNode;
    private FluidDragForceNode _dragForceNode;
    private DNAForceNode _dnaForceNode;
    
    private JCheckBox _trapForceCheckBox;
    private JCheckBox _dragForceCheckBox;
    private JCheckBox _brownianMotionCheckBox;
    private JCheckBox _dnaForceCheckBox;
    private JCheckBox _showValuesCheckBox;
    private JCheckBox _keepTrapForceConstantCheckBox;
    
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
     * @param laser 
     * @param laserPositionController
     * @param trapForceNode
     * @param dragForceNode
     * @param dnaForceNode optional
     */
    public ForcesControlPanel( 
            Font titleFont, 
            Font controlFont, 
            Bead bead, 
            Fluid fluid,
            Laser laser,
            LaserPositionController laserPositionController,
            TrapForceNode trapForceNode, 
            FluidDragForceNode dragForceNode, 
            DNAForceNode dnaForceNode,
            BeadNode beadNode,
            LaserNode laserNode ) {
        super();
        
        _bead = bead;
        _bead.addObserver( this );
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _laser = laser;
        _laser.addObserver( this );
        
        if ( laserPositionController != null ) {
            _laserPositionController = laserPositionController;
            laserPositionController.addObserver( this );
        }
        
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
            
            JLabel trapForceLabel = new JLabel( TrapForceNode.createIcon() );
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
            
            JLabel dragForceLabel = new JLabel( FluidDragForceNode.createIcon() );
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
            
            JLabel dnaForceLabel = new JLabel( DNAForceNode.createIcon() );
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
        
        _keepTrapForceConstantCheckBox = new JCheckBox( OTResources.getString( "label.keepTrapForceConstant" ) );
        _keepTrapForceConstantCheckBox.setFont( controlFont );
        _keepTrapForceConstantCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleKeepTrapForceConstantCheckBox();
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
        if ( dnaForcePanel != null ) {
            layout.addComponent( dnaForcePanel, row++, column );
        }
        layout.addComponent( dragForcePanel, row++, column );
        layout.addComponent( _brownianMotionCheckBox, row++, column );
        layout.addComponent( _showValuesCheckBox, row++, column );
        layout.addComponent( _keepTrapForceConstantCheckBox, row++, column );
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
        _keepTrapForceConstantCheckBox.setSelected( false );
        
        // Disable constant trap force when laser or bead is dragged
        DragNotificationListener constantTrapForceDisabler = new DragNotificationAdapter() {
            public void dragBegin( DragNotificationEvent event ) {
                if ( isKeepTrapForceConstantSelected() ) {
                    setKeepTrapForceConstantSelected( false );
                }
            }
        };
        beadNode.addDragNotificationListener( constantTrapForceDisabler );
        laserNode.addDragNotificationListener( constantTrapForceDisabler );
    }
    
    public void cleanup() {
        _bead.deleteObserver( this );
        _fluid.deleteObserver( this );
        _laser.deleteObserver( this );
        if ( _laserPositionController != null ) {
            _laserPositionController.deleteObserver( this );
        }
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
    
    public void setKeepTrapForceConstantSelected( boolean b ) {
        _keepTrapForceConstantCheckBox.setSelected( b );
        handleKeepTrapForceConstantCheckBox();
    }
    
    public boolean isKeepTrapForceConstantSelected() {
        return _keepTrapForceConstantCheckBox.isSelected();
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
    
    public void setKeepTrapForceConstantCheckBoxVisible( boolean visible ) {
        _keepTrapForceConstantCheckBox.setVisible( visible );
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
        _bead.deleteObserver( this );
        _bead.setBrownianMotionEnabled( selected );
        _bead.addObserver( this );
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
    
    private void handleKeepTrapForceConstantCheckBox() {
        if ( _laserPositionController != null ) {
            
            boolean selected = _keepTrapForceConstantCheckBox.isSelected();
            
            if ( !selected ) {
                // turn off laser position control
                _laserPositionController.deleteObserver( this );
                _laserPositionController.setEnabled( false );
                _laserPositionController.addObserver( this );
            }
            else {
                Point2D beadPosition = _bead.getPositionReference();
                double beadLeftEdgeX = beadPosition.getX() - ( _bead.getDiameter() / 2 );
                double beadRightEdgeX = beadPosition.getX() + ( _bead.getDiameter() / 2 );
                
                if ( _laser.contains( beadLeftEdgeX, beadPosition.getY() ) || _laser.contains( beadRightEdgeX, beadPosition.getY() ) ) {
                    // if either edge of the bead is inside the trap, go ahead and enable laser positon control
                    _laserPositionController.deleteObserver( this );
                    _laserPositionController.setEnabled( true );
                    _laserPositionController.addObserver( this );
                }
                else {
                    // if the bead is outside the trap, tell the user why this feature can't be turned on
                    _keepTrapForceConstantCheckBox.setSelected( false );
                    Component parent = PiccoloPhetApplication.instance().getPhetFrame();
                    String message = OTResources.getString( "message.keepTrapForceConstantInfoDialog" );
                    String title = OTResources.getString( "title.keepTrapForceConstantInfoDialog" );
                    JOptionPane.showMessageDialog( parent, message, title, JOptionPane.INFORMATION_MESSAGE );
                }
            }
        }
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
        else if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POWER || arg == Laser.PROPERTY_RUNNING ) {
                if ( isKeepTrapForceConstantSelected() ) {
                    setKeepTrapForceConstantSelected( false );
                }
            }
        }
        else if ( o == _laserPositionController ) {
            if ( arg == LaserPositionController.PROPERTY_ENABLED ) {
                _keepTrapForceConstantCheckBox.setSelected( _laserPositionController.isEnabled() );
            }
        }
    }
}
