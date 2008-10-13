/*  */
package edu.colorado.phet.quantumwaveinterference.view.gun;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.ParticleUnits;
import edu.colorado.phet.quantumwaveinterference.model.Propagator;
import edu.colorado.phet.quantumwaveinterference.model.propagators.ModifiedRichardsonPropagator;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:48 PM
 */
public class DefaultGunParticle extends GunParticle {
    private JSlider velocitySlider;
    private VerticalLayoutPanel controlPanel;
    private ParticleUnits particleUnits;

    public DefaultGunParticle( AbstractGunNode gunNode, String label, String imageLocation, ParticleUnits particleUnits ) {
        super( gunNode, label, imageLocation );
        this.particleUnits = particleUnits;
        createControls();
    }

    public DefaultGunParticle( AbstractGunNode gunNode, String label, String imageLocation ) {
        super( gunNode, label, imageLocation );
        createControls();
    }

    private void createControls() {
        velocitySlider = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        TitledBorder titledBorder = new TitledBorder( new LineBorder( Color.white, 1, true ), QWIResources.getString( "gun.velocity" ), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new PhetFont( Font.BOLD, 12 ), Color.white ) {
            public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintBorder( c, g, x, y, width, height );
            }
        };

        controlPanel = new VerticalLayoutPanel();
        controlPanel.addFullWidth( velocitySlider );
        controlPanel.setBorder( titledBorder );
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout( new BorderLayout() );
        labelPanel.add( getLowLabel(), BorderLayout.WEST );
        labelPanel.add( getHighLabel(), BorderLayout.EAST );
        controlPanel.addFullWidth( labelPanel );
    }

    private Dictionary createLabels( JSlider velocitySlider ) {
        Dictionary labels = new Hashtable();
        DecimalFormat numberFormat = new DecimalFormat( "0.0" );
        labels.put( new Integer( velocitySlider.getMinimum() ), getLowLabel() );
        labels.put( new Integer( velocitySlider.getMaximum() ), getHighLabel() );
        return labels;
    }

    private JLabel getHighLabel() {
        JLabel label = new JLabel( particleUnits.getMaxVelocity().toPrettyString() );
        label.setForeground( Color.lightGray );
        return label;
    }

    private JLabel getLowLabel() {
        JLabel label = new JLabel( particleUnits.getMinVelocity().toPrettyString() );
        label.setForeground( Color.lightGray );
        return label;
    }

    public double getSliderFraction() {
        double val = velocitySlider.getValue();
        return val / 1000;
    }

    public void activate( AbstractGunNode gunNode ) {
        super.active = true;
        getSchrodingerModule().setUnits( particleUnits );
        getDiscreteModel().setPropagator( createPropagator() );
        gunNode.setGunControls( controlPanel );
    }

    public void deactivate( AbstractGunNode abstractGunNode ) {
        super.active = false;
        abstractGunNode.removeGunControls();
    }

    private Propagator createPropagator() {
        return new ModifiedRichardsonPropagator( getDT(), getDiscreteModel().getPotential(), getHBar(), getParticleMass() );
    }

    protected double getHBar() {
        return particleUnits.getHbar().getValue();
    }

    private double getDT() {
        return particleUnits.getDt().getValue();
    }

    public double getStartPy() {
        return -getVelocity() * getParticleMass() * 45.0 / getDiscreteModel().getGridHeight();
    }

    public double getVelocity() {
        return new Function.LinearFunction( 0, 1000, getMinVelocity(), getMaxVelocity() ).evaluate( velocitySlider.getValue() );
    }

    private double getMaxVelocity() {
        return particleUnits.getMaxVelocity().getValue();
    }

    private double getMinVelocity() {
        return particleUnits.getMinVelocity().getValue();
    }

    public void detachListener( ChangeHandler changeHandler ) {
        velocitySlider.removeChangeListener( changeHandler );
    }

    public void hookupListener( ChangeHandler changeHandler ) {
        velocitySlider.addChangeListener( changeHandler );
    }

    public Point getGunLocation() {
        Point p = super.getGunLocation();
        p.y -= AbstractGunNode.GUN_PARTICLE_OFFSET;
        return p;
    }

    public Map getModelParameters() {
        Map map = super.getModelParameters();
        map.put( "init_mass", "" + getParticleMass() );
        map.put( "init_vel", "" + getVelocity() );
        map.put( "init_momentum", "" + getStartPy() );
        map.put( "start_y", "" + getStartY() );
        map.put( "initial_dx_lattice", "" + getStartDxLattice() );
        return map;
    }

    private double getParticleMass() {
        return particleUnits.getMass().getValue();
    }

    public boolean isFiring() {
        return false;//firing is always a one-shot deal, so we're never in the middle of a shot.
    }

    public double getMinimumProbabilityForDetection() {
        return 0.0;
    }

    public boolean getTimeThresholdAllowed() {
        return true;
    }

    public int getTimeThresholdCount() {
        return 5;
    }

    public static DefaultGunParticle createElectron( AbstractGunNode gun ) {
        return new DefaultGunParticle( gun, QWIResources.getString( "particles.electrons" ), "quantum-wave-interference/images/electron-thumb.jpg", new ParticleUnits.ElectronUnits() );
    }

    public static DefaultGunParticle createHelium( AbstractGunNode gun ) {
        return new DefaultGunParticle( gun, QWIResources.getString( "particles.helium-atoms" ), "quantum-wave-interference/images/atom-thumb.jpg", new ParticleUnits.HeliumUnits() );
    }

    public static DefaultGunParticle createNeutron( AbstractGunNode gun ) {
        return new DefaultGunParticle( gun, QWIResources.getString( "particles.neutrons" ), "quantum-wave-interference/images/neutron-thumb.gif", new ParticleUnits.NeutronUnits() );
    }
}
