/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.developer;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.phscale.view.ParticlesNode;

/**
 * This is a developer control panel that controls the "look" of 
 * the particle view (aka ratio view) in the beaker.  
 * This panel is not localized.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ParticlesControlPanel extends JPanel {

    private static final DoubleRange MAX_PARTICLES_RANGE = new DoubleRange( 1000, 10000, 5000 );
    private static final double MAX_PARTICLES_DELTA = 1;
    private static final String MAX_PARTICLES_PATTERN = "####0";

    private static final DoubleRange PARTICLE_DIAMETER_RANGE = new DoubleRange( 1, 25, 4 );
    private static final double PARTICLE_DIAMETER_DELTA = 0.1;
    private static final String PARTICLE_DIAMETER_PATTERN = "#0.0";

    private static final DoubleRange TRANSPARENCY_RANGE = new DoubleRange( 0, 255, 128 );
    private static final double TRANSPARENCY_DELTA = 1;
    private static final String TRANSPARENCY_PATTERN = "##0";

    private final ParticlesNode _particlesNode;
    private final LinearValueControl _maxParticlesControl, _diameterControl, _transparencyControl;
    private final ColorControl _h3oColorControl, _ohColorControl;

    public ParticlesControlPanel( Frame dialogOwner, ParticlesNode particlesNode ) {
        
        _particlesNode = particlesNode;

        // max particles
        _maxParticlesControl = new LinearValueControl( MAX_PARTICLES_RANGE.getMin(), MAX_PARTICLES_RANGE.getMax(), "max # particles:", MAX_PARTICLES_PATTERN, "" );
        _maxParticlesControl.setValue( particlesNode.getMaxParticles() );
        _maxParticlesControl.setUpDownArrowDelta( MAX_PARTICLES_DELTA );
        _maxParticlesControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _particlesNode.setMaxParticles( (int) _maxParticlesControl.getValue() );
            }
        } );

        // diameter
        _diameterControl = new LinearValueControl( PARTICLE_DIAMETER_RANGE.getMin(), PARTICLE_DIAMETER_RANGE.getMax(), "particle diameter:", PARTICLE_DIAMETER_PATTERN, "" );
        _diameterControl.setValue( _particlesNode.getParticleDiameter() );
        _diameterControl.setUpDownArrowDelta( PARTICLE_DIAMETER_DELTA );
        _diameterControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _particlesNode.setParticleDiameter( _diameterControl.getValue() );
            }
        } );

        // transparency
        _transparencyControl = new LinearValueControl( TRANSPARENCY_RANGE.getMin(), TRANSPARENCY_RANGE.getMax(), "particle transparency:", TRANSPARENCY_PATTERN, "" );
        _transparencyControl.setValue( _particlesNode.getParticleTransparency() );
        _transparencyControl.setUpDownArrowDelta( TRANSPARENCY_DELTA );
        _transparencyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _particlesNode.setParticleTransparency( (int) _transparencyControl.getValue() );
            }
        } );
        Hashtable particleTransparencyLabelTable = new Hashtable();
        particleTransparencyLabelTable.put( new Double( _transparencyControl.getMinimum() ), new JLabel( "invisible" ) );
        particleTransparencyLabelTable.put( new Double( _transparencyControl.getMaximum() ), new JLabel( "opaque" ) );
        _transparencyControl.setTickLabels( particleTransparencyLabelTable );

        // H3O color
        _h3oColorControl = new ColorControl( dialogOwner, "H3O color:", _particlesNode.getH3OColor() );
        _h3oColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _particlesNode.setH3OColor( _h3oColorControl.getColor() );
            }
        } );

        // OH color
        _ohColorControl = new ColorControl( dialogOwner, "OH color:", _particlesNode.getOHColor() );
        _ohColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _particlesNode.setOHColor( _ohColorControl.getColor() );
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
    }
}
