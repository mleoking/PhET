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

    private static final DoubleRange NEUTRAL_PARTICLES_RANGE = new DoubleRange( 50, 200, 100 );
    private static final double NEUTRAL_PARTICLES_DELTA = 1;
    private static final String NEUTRAL_PARTICLES_PATTERN = "####0";
    
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
    private final LinearValueControl _neutralParticlesControl, _maxParticlesControl, _diameterControl;
    private final LinearValueControl _majorityTransparencyControl, _minorityTransparencyControl;
    private final ColorControl _h3oColorControl, _ohColorControl;

    public ParticlesControlPanel( Frame dialogOwner, ParticlesNode particlesNode ) {
        
        _particlesNode = particlesNode;

        // neutral particles
        _neutralParticlesControl = new LinearValueControl( NEUTRAL_PARTICLES_RANGE.getMin(), NEUTRAL_PARTICLES_RANGE.getMax(), "# particles at pH=7:", NEUTRAL_PARTICLES_PATTERN, "" );
        _neutralParticlesControl.setValue( particlesNode.getNumberOfParticlesAtPH7() );
        _neutralParticlesControl.setUpDownArrowDelta( NEUTRAL_PARTICLES_DELTA );
        _neutralParticlesControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _particlesNode.setNumberOfParticlesAtPH7( (int) _neutralParticlesControl.getValue() );
            }
        } );
        
        // max particles
        _maxParticlesControl = new LinearValueControl( MAX_PARTICLES_RANGE.getMin(), MAX_PARTICLES_RANGE.getMax(), "# particles at pH=15:", MAX_PARTICLES_PATTERN, "" );
        _maxParticlesControl.setValue( particlesNode.getNumberOfParticlesAtPH15() );
        _maxParticlesControl.setUpDownArrowDelta( MAX_PARTICLES_DELTA );
        _maxParticlesControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _particlesNode.setNumberOfParticlesAtPH15( (int) _maxParticlesControl.getValue() );
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

        setBorder( new TitledBorder( "particle controls" ) );
        EasyGridBagLayout particlePanelLayout = new EasyGridBagLayout( this );
        this.setLayout( particlePanelLayout );
        int row = 0;
        int column = 0;
        particlePanelLayout.addComponent( _neutralParticlesControl, row++, column );
        particlePanelLayout.addComponent( _maxParticlesControl, row++, column );
        particlePanelLayout.addComponent( _diameterControl, row++, column );
        particlePanelLayout.addComponent( _majorityTransparencyControl, row++, column );
        particlePanelLayout.addComponent( _minorityTransparencyControl, row++, column );
        particlePanelLayout.addComponent( _h3oColorControl, row++, column );
        particlePanelLayout.addComponent( _ohColorControl, row++, column );
    }
}
