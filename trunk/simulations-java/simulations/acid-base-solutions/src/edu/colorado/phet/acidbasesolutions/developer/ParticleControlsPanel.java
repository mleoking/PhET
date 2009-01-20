/* Copyright 2008, University of Colorado */

package edu.colorado.phet.acidbasesolutions.developer;

import java.awt.Frame;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.view.ParticlesNode;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * This is a developer control panel that controls the "look" of the particle view (aka ratio view) in the beaker.  
 * These controls will not be available to the user, and are not localized.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ParticleControlsPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // number of particles at pH=7
    private static final DoubleRange NUM_PARTICLES_PH7_RANGE = new DoubleRange( 50, 200 );
    private static final double NUM_PARTICLES_PH7_DELTA = 1;
    private static final String NUM_PARTICLES_PH7_PATTERN = "##0";
    
    // number of particles at pH=15
    private static final DoubleRange NUM_PARTICLES_PH15_RANGE = new DoubleRange( 1000, 10000 );
    private static final double NUM_PARTICLES_PH15_DELTA = 1;
    private static final String NUM_PARTICLES_PH15_PATTERN = "####0";
    
    // minimum # particles of minority type
    private static final DoubleRange MIN_MINORITY_PARTICLES_RANGE = new DoubleRange( 1, 25 );
    private static final double MIN_MINORITY_PARTICLES_DELTA = 1;
    private static final String MIN_MINORITY_PARTICLES_PATTERN = "#0";

    // particle diameter
    private static final DoubleRange PARTICLE_DIAMETER_RANGE = new DoubleRange( 1, 25 );
    private static final double PARTICLE_DIAMETER_DELTA = 0.1;
    private static final String PARTICLE_DIAMETER_PATTERN = "#0.0";

    // particle transparency (alpha channel)
    private static final DoubleRange TRANSPARENCY_RANGE = new DoubleRange( 0, 255 );
    private static final double TRANSPARENCY_DELTA = 1;
    private static final String TRANSPARENCY_PATTERN = "##0";

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ParticlesNode _particlesNode;
    private final LinearValueControl _numberOfParticlesAtPH7Control, _numberOfParticlesAtPH15Control, _minMinorityParticlesControl;
    private final LinearValueControl _diameterControl;
    private final LinearValueControl _majorityTransparencyControl, _minorityTransparencyControl;
    private final ColorControl _h3oColorControl, _ohColorControl;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ParticleControlsPanel( Frame dialogOwner, ParticlesNode particlesNode ) {
        
        _particlesNode = particlesNode;

        // number of particles at pH=7
        _numberOfParticlesAtPH7Control = new LinearValueControl( NUM_PARTICLES_PH7_RANGE.getMin(), NUM_PARTICLES_PH7_RANGE.getMax(), "# particles at pH=7:", NUM_PARTICLES_PH7_PATTERN, "" );
        _numberOfParticlesAtPH7Control.setValue( particlesNode.getNumberOfParticlesAtPH7() );
        _numberOfParticlesAtPH7Control.setUpDownArrowDelta( NUM_PARTICLES_PH7_DELTA );
        _numberOfParticlesAtPH7Control.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _particlesNode.setNumberOfParticlesAtPH7( (int) _numberOfParticlesAtPH7Control.getValue() );
            }
        } );
        
        // number of particles at pH=15
        _numberOfParticlesAtPH15Control = new LinearValueControl( NUM_PARTICLES_PH15_RANGE.getMin(), NUM_PARTICLES_PH15_RANGE.getMax(), "# particles at pH=15:", NUM_PARTICLES_PH15_PATTERN, "" );
        _numberOfParticlesAtPH15Control.setValue( particlesNode.getNumberOfParticlesAtPH15() );
        _numberOfParticlesAtPH15Control.setUpDownArrowDelta( NUM_PARTICLES_PH15_DELTA );
        _numberOfParticlesAtPH15Control.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _particlesNode.setNumberOfParticlesAtPH15( (int) _numberOfParticlesAtPH15Control.getValue() );
            }
        } );
        
        // min minority particles
        _minMinorityParticlesControl = new LinearValueControl( MIN_MINORITY_PARTICLES_RANGE.getMin(), MIN_MINORITY_PARTICLES_RANGE.getMax(), "min # of minority particles:", MIN_MINORITY_PARTICLES_PATTERN, "" );
        _minMinorityParticlesControl.setValue( particlesNode.getMinMinorityParticles() );
        _minMinorityParticlesControl.setUpDownArrowDelta( MIN_MINORITY_PARTICLES_DELTA );
        _minMinorityParticlesControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _particlesNode.setMinMinorityParticles( (int) _minMinorityParticlesControl.getValue() );
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

        // majority transparency
        _majorityTransparencyControl = new LinearValueControl( TRANSPARENCY_RANGE.getMin(), TRANSPARENCY_RANGE.getMax(), "majority transparency:", TRANSPARENCY_PATTERN, "" );
        _majorityTransparencyControl.setValue( _particlesNode.getMajorityTransparency() );
        _majorityTransparencyControl.setUpDownArrowDelta( TRANSPARENCY_DELTA );
        _majorityTransparencyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _particlesNode.setMajorityTransparency( (int) _majorityTransparencyControl.getValue() );
            }
        } );
        Hashtable majorityTransparencyLabelTable = new Hashtable();
        majorityTransparencyLabelTable.put( new Double( _majorityTransparencyControl.getMinimum() ), new JLabel( "invisible" ) );
        majorityTransparencyLabelTable.put( new Double( _majorityTransparencyControl.getMaximum() ), new JLabel( "opaque" ) );
        _majorityTransparencyControl.setTickLabels( majorityTransparencyLabelTable );

        // minority transparency
        _minorityTransparencyControl = new LinearValueControl( TRANSPARENCY_RANGE.getMin(), TRANSPARENCY_RANGE.getMax(), "minority transparency:", TRANSPARENCY_PATTERN, "" );
        _minorityTransparencyControl.setValue( _particlesNode.getMinorityTransparency() );
        _minorityTransparencyControl.setUpDownArrowDelta( TRANSPARENCY_DELTA );
        _minorityTransparencyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _particlesNode.setMinorityTransparency( (int) _minorityTransparencyControl.getValue() );
            }
        } );
        Hashtable minorityTransparencyLabelTable = new Hashtable();
        minorityTransparencyLabelTable.put( new Double( _minorityTransparencyControl.getMinimum() ), new JLabel( "invisible" ) );
        minorityTransparencyLabelTable.put( new Double( _minorityTransparencyControl.getMaximum() ), new JLabel( "opaque" ) );
        _minorityTransparencyControl.setTickLabels( minorityTransparencyLabelTable );
        
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

        EasyGridBagLayout particlePanelLayout = new EasyGridBagLayout( this );
        this.setLayout( particlePanelLayout );
        int row = 0;
        int column = 0;
        particlePanelLayout.addComponent( _numberOfParticlesAtPH7Control, row++, column );
        particlePanelLayout.addComponent( _numberOfParticlesAtPH15Control, row++, column );
        particlePanelLayout.addComponent( _minMinorityParticlesControl, row++, column );
        particlePanelLayout.addComponent( _diameterControl, row++, column );
        particlePanelLayout.addComponent( _majorityTransparencyControl, row++, column );
        particlePanelLayout.addComponent( _minorityTransparencyControl, row++, column );
        particlePanelLayout.addComponent( _h3oColorControl, row++, column );
        particlePanelLayout.addComponent( _ohColorControl, row++, column );
    }
}
