/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.developer;

import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.phscale.PHScaleConstants;

/**
 * This is a developer control panel that controls the "look" of 
 * the particle view (aka ratio view) in the beaker.  
 * This panel is not localized.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ParticleControlPanel extends JPanel {

    private static final DoubleRange MAX_PARTICLES_RANGE = new DoubleRange( 1000, 10000, 5000 );
    private static final double MAX_PARTICLES_DELTA = 1;
    private static final String MAX_PARTICLES_PATTERN = "####0";

    private static final DoubleRange PARTICLE_DIAMETER_RANGE = new DoubleRange( 1, 25, 4 );
    private static final double PARTICLE_DIAMETER_DELTA = 0.1;
    private static final String PARTICLE_DIAMETER_PATTERN = "#0.0";

    private static final DoubleRange TRANSPARENCY_RANGE = new DoubleRange( 0, 255, 128 );
    private static final double TRANSPARENCY_DELTA = 1;
    private static final String TRANSPARENCY_PATTERN = "##0";

    private final ArrayList _listeners;
    private final LinearValueControl _maxParticlesControl, _diameterControl, _transparencyControl;
    private final ColorControl _h3oColorControl, _ohColorControl, _h2oColorControl;

    public ParticleControlPanel( Frame dialogOwner ) {
        
        _listeners = new ArrayList();

        // max particles
        _maxParticlesControl = new LinearValueControl( MAX_PARTICLES_RANGE.getMin(), MAX_PARTICLES_RANGE.getMax(), "max # particles:", MAX_PARTICLES_PATTERN, "" );
        _maxParticlesControl.setValue( MAX_PARTICLES_RANGE.getDefault() );
        _maxParticlesControl.setUpDownArrowDelta( MAX_PARTICLES_DELTA );
        _maxParticlesControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyMaxParticlesChanged();
            }
        } );

        // diameter
        _diameterControl = new LinearValueControl( PARTICLE_DIAMETER_RANGE.getMin(), PARTICLE_DIAMETER_RANGE.getMax(), "particle diameter:", PARTICLE_DIAMETER_PATTERN, "" );
        _diameterControl.setValue( PARTICLE_DIAMETER_RANGE.getDefault() );
        _diameterControl.setUpDownArrowDelta( PARTICLE_DIAMETER_DELTA );
        _diameterControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyParticleDiameterChanged();
            }
        } );

        // transparency
        _transparencyControl = new LinearValueControl( TRANSPARENCY_RANGE.getMin(), TRANSPARENCY_RANGE.getMax(), "particle transparency:", TRANSPARENCY_PATTERN, "" );
        _transparencyControl.setValue( TRANSPARENCY_RANGE.getDefault() );
        _transparencyControl.setUpDownArrowDelta( TRANSPARENCY_DELTA );
        _transparencyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyParticleTransparencyChanged();
            }
        } );
        Hashtable particleTransparencyLabelTable = new Hashtable();
        particleTransparencyLabelTable.put( new Double( _transparencyControl.getMinimum() ), new JLabel( "invisible" ) );
        particleTransparencyLabelTable.put( new Double( _transparencyControl.getMaximum() ), new JLabel( "opaque" ) );
        _transparencyControl.setTickLabels( particleTransparencyLabelTable );

        // H3O color
        _h3oColorControl = new ColorControl( dialogOwner, "H3O color:", PHScaleConstants.H3O_COLOR );
        _h3oColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyParticleColorChanged();
            }
        } );

        // OH color
        _ohColorControl = new ColorControl( dialogOwner, "OH color:", PHScaleConstants.OH_COLOR );
        _ohColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyParticleColorChanged();
            }
        } );

        // H2O color
        _h2oColorControl = new ColorControl( dialogOwner, "H2O color:", PHScaleConstants.H2O_COLOR );
        _h2oColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyParticleColorChanged();
            }
        } );

        setBorder( new TitledBorder( "particle controls" ) );
        EasyGridBagLayout particlePanelLayout = new EasyGridBagLayout( this );
        this.setLayout( particlePanelLayout );
        int row = 0;
        int column = 0;
        particlePanelLayout.addComponent( _maxParticlesControl, row++, column );
        particlePanelLayout.addComponent( _diameterControl, row++, column );
        particlePanelLayout.addComponent( _transparencyControl, row++, column );
        particlePanelLayout.addComponent( _h3oColorControl, row++, column );
        particlePanelLayout.addComponent( _ohColorControl, row++, column );
        particlePanelLayout.addComponent( _h2oColorControl, row++, column );
    }
    
    public void setMaxParticles( int maxParticles ) {
        _maxParticlesControl.setValue( maxParticles );
    }
    
    public double getMaxParticles() {
        return (int) _maxParticlesControl.getValue();
    }
    
    public void setDiameter( double diameter ) {
        _diameterControl.setValue( diameter );
    }
    
    public double getDiameter() {
        return _diameterControl.getValue();
    }

    public void setTransparency( int alpha ) {
        _transparencyControl.setValue( alpha );
    }
    
    public int getTransparency() {
        return (int) _transparencyControl.getValue();
    }
    
    public void setH3OColor( Color color ) {
        _h3oColorControl.setColor( color );
    }
    
    public Color getH3OColor() {
        return _h3oColorControl.getColor();
    }
    
    public void setOHColor( Color color ) {
        _ohColorControl.setColor( color );
    }
    
    public Color getOHColor() {
        return _ohColorControl.getColor();
    }
    
    public void setH2OColor( Color color ) {
        _h2oColorControl.setColor( color );
    }
    
    public Color getH2OColor() {
        return _h2oColorControl.getColor();
    }
    
    public interface ParticleFactoryControlPanelListener {
        public void maxParticlesChanged();
        public void particleDiameterChanged();
        public void particleTransparencyChanged();
        public void particleColorChanged();
    }

    public void addParticleFactoryControlPanelListener( ParticleFactoryControlPanelListener listener ) {
        _listeners.add( listener );
    }

    public void removeParticleFactoryControlPanelListener( ParticleFactoryControlPanelListener listener ) {
        _listeners.remove( listener );
    }

    private void notifyMaxParticlesChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ParticleFactoryControlPanelListener) i.next() ).maxParticlesChanged();
        }
    }

    private void notifyParticleDiameterChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ParticleFactoryControlPanelListener) i.next() ).particleDiameterChanged();
        }
    }

    private void notifyParticleTransparencyChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ParticleFactoryControlPanelListener) i.next() ).particleTransparencyChanged();
        }
    }

    private void notifyParticleColorChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ParticleFactoryControlPanelListener) i.next() ).particleColorChanged();
        }
    }
}
