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
import edu.colorado.phet.hydrogenatom.control.GunControlPanel;
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
    private GunControlPanel _gunControlPanel;
    private JCheckBox _energyDiagramCheckBox;
    private Spectrometer _spectrometer;
    private JCheckBox _spectrometerCheckBox;
    private SpectrometerListener _spectrometerListener;
    
    public HAController(
            HAModule module,
            ModeSwitch modeSwitch,
            AtomicModelSelector atomicModelSelector,
            GunNode gunNode,
            GunControlPanel gunControlPanel,
            JCheckBox energyDiagramCheckBox,
            Spectrometer spectrometer,
            JCheckBox spectrometerCheckBox )
    {
        _module = module;
        _modeSwitch = modeSwitch;
        _atomicModelSelector = atomicModelSelector;
        _gunNode = gunNode;
        _gunControlPanel = gunControlPanel;
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
        
        _gunNode.getGunOnOffControl().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleGunOnOffChange();
            }
        } );
        
        _gunControlPanel.getGunTypeControl().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleGunTypeChange();
            }
        } );
        
        _gunControlPanel.getLightTypeControl().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleLightTypeChange();
            }
        } );
        
        _gunControlPanel.getLightIntensityControl().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleLightIntensityChange();
            }
        } );
        
        _gunControlPanel.getWavelengthControl().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleLightWavelengthChange();
            }
        } );
        
        _gunControlPanel.getAlphaParticlesIntensityControl().addChangeListener( new ChangeListener() {
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
        _module.updateAtomicModel();
        _module.updateEnergyDiagram();
    }
    
    private void handleAtomicModelChange() {
        _module.updateAtomicModel();
        _module.updateEnergyDiagram();
    }
    
    private void handleGunOnOffChange() {
        _module.updateBeam();
    }
    
    private void handleGunTypeChange() {
        _module.updateBeam();
    }
    
    private void handleLightTypeChange() {
        _module.updateBeam();
    }
    
    private void handleLightIntensityChange() {
        _module.updateBeam();
    }
    
    private void handleLightWavelengthChange() {
        _module.updateBeam();
    }
    
    private void handleAlphaParticleIntensityChange() {
        _module.updateBeam();
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
