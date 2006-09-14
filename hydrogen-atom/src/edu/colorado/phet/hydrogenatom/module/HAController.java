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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.hydrogenatom.control.AtomicModelSelector;
import edu.colorado.phet.hydrogenatom.control.ModeSwitch;
import edu.colorado.phet.hydrogenatom.spectrometer.Spectrometer;
import edu.colorado.phet.hydrogenatom.spectrometer.SpectrometerListener;
import edu.colorado.phet.hydrogenatom.spectrometer.SpectrometerListener.SpectrometerAdapter;
import edu.colorado.phet.hydrogenatom.view.GunNode;


public class HAController {

    private HAModule _module;
    private ModeSwitch _modeSwitch;
    private AtomicModelSelector _atomicModelSelector;
    private GunNode _gunNode;
    private JCheckBox _energyDiagramCheckBox;
    private Spectrometer _spectrometer;
    private JCheckBox _spectrometerCheckBox;
    private SpectrometerListener _spectrometerListener;
    
    public HAController(
            HAModule module,
            ModeSwitch modeSwitch,
            AtomicModelSelector atomicModelSelector,
            GunNode gunNode,
            JCheckBox energyDiagramCheckBox,
            Spectrometer spectrometer,
            JCheckBox spectrometerCheckBox )
    {
        _module = module;
        _modeSwitch = modeSwitch;
        _atomicModelSelector = atomicModelSelector;
        _gunNode = gunNode;
        _energyDiagramCheckBox = energyDiagramCheckBox;
        _spectrometer = spectrometer;
        _spectrometerCheckBox = spectrometerCheckBox;
        
        initListeners();
    }
    
    private void initListeners() {
        
        _module.getSimulationPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent event ) {
                handleCanvasResize();
            }
        } );
        
        _modeSwitch.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleModeChange();
            }
         });
        
        _atomicModelSelector.addChangeListener( new ChangeListener() {
           public void stateChanged( ChangeEvent event ) {
               handleAtomicModelChange();
           }
        });
        
        _gunNode.getGunControlPanel().getGunTypeControl().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleGunTypeChange();
            }
        } );
        
        _gunNode.getGunControlPanel().getLightTypeControl().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleLightTypeChange();
            }
        } );
        
        _gunNode.getGunControlPanel().getLightIntensityControl().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleLightIntensityChange();
            }
        } );
        
        _gunNode.getGunControlPanel().getWavelengthControl().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleLightWavelengthChange();
            }
        } );
        
        _gunNode.getGunControlPanel().getAlphaParticlesIntensityControl().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleAlphaParticleIntensityChange();
            }
        } );
        
        _energyDiagramCheckBox.addChangeListener( new ChangeListener() { 
            public void stateChanged( ChangeEvent event ) {
                handleEnergyDiagramSelection();
            }
        } );
        
        _spectrometerListener = new SpectrometerAdapter() {
            public void close( SpectrometerEvent event ) {
                handleSpectrometerClose( (Spectrometer)event.getSource() );
            }
            public void snapshot( SpectrometerEvent event ) {
                handleSpectrometerSnapshot();
            }
        };
        _spectrometer.addSpectrometerListener( _spectrometerListener );
         
        _spectrometerCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleSpectrometerSelection();
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public void addSpectrometerListener( Spectrometer spectrometer ) {
        spectrometer.addSpectrometerListener( _spectrometerListener );
    }
    
    public void removeSpectrometerListener( Spectrometer spectrometer ) {
        spectrometer.removeSpectrometerListener( _spectrometerListener );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleCanvasResize() {
        _module.updateCanvasLayout();
    }
    
    private void handleModeChange() {
        _module.updateAtomicModelSelector();
        _module.updateBlackBox();
        _module.updateAtomicModel();
        _module.updateEnergyDiagram();
    }
    
    private void handleAtomicModelChange() {
        _module.updateAtomicModel();
        _module.updateEnergyDiagram();
    }
    
    private void handleGunTypeChange() {
        //XXX
    }
    
    private void handleLightTypeChange() {
        //XXX
    }
    
    private void handleLightIntensityChange() {
        //XXX
    }
    
    private void handleLightWavelengthChange() {
        //XXX
    }
    
    private void handleAlphaParticleIntensityChange() {
        //XXX
    }
    
    private void handleEnergyDiagramSelection() {
        _module.updateEnergyDiagram();
    }
    
    private void handleSpectrometerSelection() {
        _module.updateSpectrometer();
    }
    
    private void handleSpectrometerSnapshot() {
        _module.createSpectrometerSnapshot();
    }
    
    private void handleSpectrometerClose( Spectrometer spectrometer ) {
        if ( spectrometer != _spectrometer ) {
            removeSpectrometerListener( spectrometer );
        }
        _module.deleteSpectrometerSnapshot( spectrometer );
    }
}
