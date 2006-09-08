/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.module;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.control.AtomicModelSelector;
import edu.colorado.phet.hydrogenatom.control.HAClockControlPanel;
import edu.colorado.phet.hydrogenatom.control.ModeSwitch;
import edu.colorado.phet.hydrogenatom.energydiagrams.BohrEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.DeBroglieEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.SchrodingerEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.SolarSystemEnergyDiagram;
import edu.colorado.phet.hydrogenatom.enums.AtomicModel;
import edu.colorado.phet.hydrogenatom.model.HAClock;
import edu.colorado.phet.hydrogenatom.spectrometer.Spectrometer;
import edu.colorado.phet.hydrogenatom.view.*;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;


public class HAModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 750, 750 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetPCanvas _canvas;
    private PNode _rootNode;
    
    private ModeSwitch _modeSwitch;
    private AtomicModelSelector _atomicModelSelector;
    private GunNode _gunNode;
    private PText _notToScaleLabel;
    private JCheckBox _energyDiagramCheckBox;
    private PNode _energyDiagramCheckBoxNode;
    private JCheckBox _spectrometerCheckBox;
    private PNode _spectrometerCheckBoxNode;
    
    private MagicBoxBack _magicBoxBack;
    private MagicBoxFront _magicBoxFront;
    
    private ExperimentAtomNode _experimentAtomNode;
    private BilliardBallAtomNode _billiardBallAtomNode;
    private BohrAtomNode _bohrAtomNode;
    private DeBroglieAtomNode _deBroglieAtomNode;
    private PlumPuddingAtomNode _plumPuddingAtomNode;
    private SchrodingerAtomNode _schrodingerAtomNode;
    private SolarSystemAtomNode _solarSystemAtomNode;
    
    private BohrEnergyDiagram _bohrEnergyDiagram;
    private DeBroglieEnergyDiagram _deBroglieEnergyDiagram;
    private SchrodingerEnergyDiagram _schrodingerEnergyDiagram;
    private SolarSystemEnergyDiagram _solarSystemEnergyDiagram;
    
    private Spectrometer _spectrometer;
    
    private HAClockControlPanel _clockControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public HAModule() {
        super( SimStrings.get( "HAModule.title" ), new HAClock() );
        
        // hide the PhET logo
        setLogoPanel( null );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------
        
        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( CANVAS_RENDERING_SIZE );
            _canvas.setBackground( HAConstants.CANVAS_BACKGROUND );
            setSimulationPanel( _canvas );
        }
        
        // Root of our scene graph
        {
            _rootNode = new PNode();
            _canvas.addWorldChild( _rootNode );
        }
        
        // Mode switch (experiment/prediction)
        {
            _modeSwitch = new ModeSwitch( _canvas );
            _modeSwitch.setOffset( 30, 10 );
            _rootNode.addChild( _modeSwitch );
            _modeSwitch.addChangeListener( new ChangeListener() {
               public void stateChanged( ChangeEvent event ) {
                   handleModeChange();
               }
            });
        }
        
        // Atomic Model selector
        {
            _atomicModelSelector = new AtomicModelSelector( _canvas );
            _atomicModelSelector.setOffset( 300, 10 );
            _rootNode.addChild( _atomicModelSelector );
            
            _atomicModelSelector.addChangeListener( new ChangeListener() {
               public void stateChanged( ChangeEvent event ) {
                   handleAtomicModelChange();
               }
            });
        }
        
        // Gun
        {
            _gunNode = new GunNode( _canvas );
            _gunNode.setOffset( 30, 200 );
            _rootNode.addChild( _gunNode );
        }
        
        // "Not to scale" label
        {
            _notToScaleLabel = new PText( SimStrings.get( "label.notToScale" ) );
            _notToScaleLabel.setFont( new Font( HAConstants.FONT_NAME, Font.PLAIN, 14 ) );
            PBounds b = _modeSwitch.getFullBounds();
            double x = b.getX();
            double y = b.getY() + b.getHeight() + 15;
            _notToScaleLabel.setOffset( x, y );
            _rootNode.addChild( _notToScaleLabel );
        }
        
        // Energy Diagram checkbox
        {
            _energyDiagramCheckBox = new JCheckBox( SimStrings.get( "label.showEnergyDiagram" ) );
            _energyDiagramCheckBox.setOpaque( false );
            _energyDiagramCheckBox.setFont( HAConstants.CONTROL_FONT );
            _energyDiagramCheckBoxNode = new PSwing( _canvas, _energyDiagramCheckBox );
            _energyDiagramCheckBoxNode.setOffset( 850, 120 );
            _rootNode.addChild( _energyDiagramCheckBoxNode );
            
            _energyDiagramCheckBox.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    updateEnergyDiagram();
                }
            } );
        }
        
        // Energy diagrams
        {
            _bohrEnergyDiagram = new BohrEnergyDiagram();
            _deBroglieEnergyDiagram = new DeBroglieEnergyDiagram();
            _schrodingerEnergyDiagram = new SchrodingerEnergyDiagram();
            _solarSystemEnergyDiagram = new SolarSystemEnergyDiagram();
            
            PBounds b = _energyDiagramCheckBoxNode.getFullBounds();
            double x = b.getX() + b.getWidth() - _bohrEnergyDiagram.getFullBounds().getWidth();
            double y = b.getY() + b.getHeight() + 10;
            _bohrEnergyDiagram.setOffset( x, y );
            _deBroglieEnergyDiagram.setOffset( x, y );
            _schrodingerEnergyDiagram.setOffset( x, y );
            _solarSystemEnergyDiagram.setOffset( x, y );
            
            _rootNode.addChild( _bohrEnergyDiagram );
            _rootNode.addChild( _deBroglieEnergyDiagram );
            _rootNode.addChild( _schrodingerEnergyDiagram );
            _rootNode.addChild( _solarSystemEnergyDiagram );
        }
        
        // Spectrometer checkbox
        {
            _spectrometerCheckBox = new JCheckBox( SimStrings.get( "label.showSpectrometer" ) );
            _spectrometerCheckBox.setOpaque( false );
            _spectrometerCheckBox.setFont( HAConstants.CONTROL_FONT );
            _spectrometerCheckBoxNode = new PSwing( _canvas, _spectrometerCheckBox );
            _spectrometerCheckBoxNode.setOffset( 700, 710 );
            _rootNode.addChild( _spectrometerCheckBoxNode );
            
            _spectrometerCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    _spectrometer.setVisible( _spectrometerCheckBox.isSelected() );
                }
            } );
        }
        
        // Spectrometer
        {
            _spectrometer = new Spectrometer( _canvas, false /* isaSnapshot */ );
            _spectrometer.setOffset( 700, 525 );
            _rootNode.addChild( _spectrometer );
        }
        
        // Atom representations
        {
            _experimentAtomNode = new ExperimentAtomNode();
            _billiardBallAtomNode = new BilliardBallAtomNode();
            _bohrAtomNode = new BohrAtomNode();
            _deBroglieAtomNode = new DeBroglieAtomNode();
            _plumPuddingAtomNode = new PlumPuddingAtomNode();
            _schrodingerAtomNode = new SchrodingerAtomNode();
            _solarSystemAtomNode = new SolarSystemAtomNode();
            
            double x = 300;
            double y = 135;
            _experimentAtomNode.setOffset( x, y );
            _billiardBallAtomNode.setOffset( x, y );
            _bohrAtomNode.setOffset( x, y );
            _deBroglieAtomNode.setOffset( x, y );
            _plumPuddingAtomNode.setOffset( x, y );
            _schrodingerAtomNode.setOffset( x, y );
            _solarSystemAtomNode.setOffset( x, y );
            
//            _rootNode.addChild( _experimentAtomNode );
//            _rootNode.addChild( _billiardBallAtomNode );
//            _rootNode.addChild( _bohrAtomNode );
//            _rootNode.addChild( _deBroglieAtomNode );
//            _rootNode.addChild( _plumPuddingAtomNode );
//            _rootNode.addChild( _schrodingerAtomNode );
//            _rootNode.addChild( _solarSystemAtomNode );
        }
        
        // Magic Box
        {
           _magicBoxBack = new MagicBoxBack( 385, 385, 10 ); 
           _rootNode.addChild( _magicBoxBack );
           _magicBoxBack.setOffset( 300, 135 );
           
           _magicBoxFront = new MagicBoxFront( 385, 385 ); 
           _magicBoxFront.setWindowOpen( true );
           _rootNode.addChild( _magicBoxFront );
           _magicBoxFront.setOffset( 300, 135 );
        }
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Clock controls
        {
            _clockControlPanel = new HAClockControlPanel( (HAClock) getClock() );
            setClockControlPanel( _clockControlPanel );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        if ( hasHelp() ) {
            HelpPane helpPane = getDefaultHelpPane();
            
            //XXX add help items
        }
        
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
       
        _canvas.addComponentListener( new LayoutListener() );
        
        reset();
        layoutCanvas();

    }
    
    //----------------------------------------------------------------------------
    //
    //----------------------------------------------------------------------------
    
    private void reset() {
        _modeSwitch.setPredictionSelected( true );
        _atomicModelSelector.setSelection( AtomicModel.BILLIARD_BALL );
        _spectrometerCheckBox.setSelected( true );
        _energyDiagramCheckBox.setSelected( false );
    }
    
    private void layoutCanvas() {
        //XXX
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /*
     * Fixes the "play area" layout when the window size changes.
     */
    private class LayoutListener extends ComponentAdapter {
        public void componentResized( ComponentEvent event ) {
            if ( event.getSource() == _canvas ) {
                layoutCanvas();
            }
        }
    }
    
    private void handleModeChange() {
        _atomicModelSelector.setVisible( _modeSwitch.isPredictionSelected() );
        updateMagicBox();
        updateAtomicModel();
        updateEnergyDiagram();
    }
    
    private void handleAtomicModelChange() {
        updateAtomicModel();
        updateEnergyDiagram(); 
    }
    
    private void updateMagicBox() {
        _magicBoxFront.setWindowOpen( _modeSwitch.isPredictionSelected() );
    }
    
    private void updateAtomicModel() {
        
        AtomicModel atomicModel = _atomicModelSelector.getSelection();
        
        _experimentAtomNode.setVisible( false );
        _billiardBallAtomNode.setVisible( false );
        _bohrAtomNode.setVisible( false );
        _deBroglieAtomNode.setVisible( false );
        _plumPuddingAtomNode.setVisible( false );
        _schrodingerAtomNode.setVisible( false );
        _solarSystemAtomNode.setVisible( false );
        
        if ( _modeSwitch.isExperimentSelected() ) {
            _experimentAtomNode.setVisible( true );
        }
        else {
            if ( atomicModel == AtomicModel.BILLIARD_BALL ) {
                _billiardBallAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.BOHR ) {
                _bohrAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.DEBROGLIE ) {
                _deBroglieAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.PLUM_PUDDING ) {
                _plumPuddingAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.SCHRODINGER ) {
                _schrodingerAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.SOLAR_SYSTEM ) {
                _solarSystemAtomNode.setVisible( true );
            }
        }
    }
    
    private void updateEnergyDiagram() {
        
        AtomicModel atomicModel = _atomicModelSelector.getSelection();
        
        _energyDiagramCheckBoxNode.setVisible( false );
        _bohrEnergyDiagram.setVisible( false );
        _deBroglieEnergyDiagram.setVisible( false );
        _schrodingerEnergyDiagram.setVisible( false );
        _solarSystemEnergyDiagram.setVisible( false );
        
        if ( _modeSwitch.isPredictionSelected() ) {
            if ( atomicModel == AtomicModel.BOHR ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _bohrEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
            else if ( atomicModel == AtomicModel.DEBROGLIE ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _deBroglieEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
            else if ( atomicModel == AtomicModel.SCHRODINGER ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _schrodingerEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
            else if ( atomicModel == AtomicModel.SOLAR_SYSTEM ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _solarSystemEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
        }
    }
}
