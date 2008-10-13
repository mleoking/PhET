/*  */
package edu.colorado.phet.quantumwaveinterference.view.gun;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.controls.IntensitySlider;
import edu.colorado.phet.quantumwaveinterference.phetcommon.ImagePComboBox;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.colorado.phet.quantumwaveinterference.view.colormaps.ColorData;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.BlueGunDetails;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 */

public class IntensityGunNode extends AbstractGunNode implements OnOffItem {
    protected OnOffCheckBox alwaysOnCheckBox;
    protected IntensitySlider intensitySlider;
    private boolean on = false;
    private IntensityBeam[] beams;
    private IntensityBeam currentBeam;
    private Photon photon;
    private static final double MAX_INTENSITY_READOUT = 40;
    private GunControlPanel gunControlPanel;
    private PSwing onPswing;
    private PText onOffTextNode = new PText( QWIResources.getString( "gun.off" ) );

    protected IntensitySlider getIntensitySlider() {
        return intensitySlider;
    }

    protected JCheckBox getAlwaysOnCheckBox() {
        return alwaysOnCheckBox;
    }

    public IntensityGunNode( final QWIPanel QWIPanel ) {
        super( QWIPanel );
        onOffTextNode.setFont( new PhetFont( 12, true ) );
        alwaysOnCheckBox = new OnOffCheckBox( this );
//        intensitySlider = new ModelSlider( "Intensity ( particles/second )", "", 0, MAX_INTENSITY_READOUT, MAX_INTENSITY_READOUT, new DecimalFormat( "0.000" ) );
//        intensitySlider.setModelTicks( new double[]{0, 10, 20, 30, 40} );
        addChild( onOffTextNode );
        intensitySlider = new IntensitySlider( Color.white, IntensitySlider.HORIZONTAL, new Dimension( 140, 30 ) );
        intensitySlider.setValue( 100 );

        intensitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateIntensity();
            }
        } );


        updateIntensity();
        QWIPanel.getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                stepBeam();
            }
        } );

        gunControlPanel = createGunControlPanel();

        setupObject( beams[0] );

        alwaysOnCheckBox.setBackground( BlueGunDetails.gunBackgroundColor );
        onPswing = new PSwing( alwaysOnCheckBox );
        onPswing.addInputEventListener( new CursorHandler() );
        setOnGunControl( onPswing );

        updateSliderColor();
    }

    protected JComponent getGunControls() {
        return gunControlPanel.getGunControls();
    }

    protected void setOnOffTextVisible( boolean vis ) {
        onOffTextNode.setVisible( vis );
    }

    public void reset() {
//        super.reset();
        currentBeam.reset();
    }

    protected GunControlPanel createGunControlPanel() {
        GunControlPanel gunControlPanel = new GunControlPanel( getSchrodingerPanel() );
        gunControlPanel.add( intensitySlider );
        return gunControlPanel;
    }

    protected void layoutChildren() {
        super.layoutChildren();
        if( onOffTextNode != null ) {
            onOffTextNode.setOffset( getOnGunGraphic().getFullBounds().getCenterX() - onOffTextNode.getFullBounds().getWidth() / 2, getOnGunGraphic().getFullBounds().getMaxY() );
        }
    }

//    protected double getControlOffsetX() {
//        return getGunImageGraphic().getFullBounds().getWidth() - 40;
//    }

    protected Point getGunLocation() {
        if( currentBeam != null ) {
            return currentBeam.getGunLocation();
        }
        else {
            return new Point();
        }
    }

    private void stepBeam() {
        currentBeam.stepBeam();
    }

    protected ImagePComboBox initComboBox() {
        photon = new Photon( this, QWIResources.getString( "particles.photons" ), "quantum-wave-interference/images/photon-thumb.jpg" );
        IntensityBeam[] mybeams = new IntensityBeam[]{
                new PhotonBeam( this, photon ),
                new ParticleBeam( DefaultGunParticle.createElectron( this ) ),
                new ParticleBeam( DefaultGunParticle.createNeutron( this ) ),
                new ParticleBeam( DefaultGunParticle.createHelium( this ) ),
        };
        setBeams( mybeams );
        final ImagePComboBox imageComboBox = new ImagePComboBox( beams );
//        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setupObject( beams[index] );
            }
        } );
        photon.addMomentumChangeListerner( new MomentumChangeListener() {
            public void momentumChanged( double val ) {
                updateSliderColor();
            }
        } );
//        updateSliderColor();
        return imageComboBox;
    }

    protected void setGunControls( JComponent gunControl ) {
        gunControlPanel.setGunControls( gunControl );
    }

    public GunControlPanel getGunControlPanel() {
        return gunControlPanel;
    }

    public boolean isPhotonMode() {
        return currentBeam instanceof PhotonBeam;
    }

    protected void setBeams( IntensityBeam[] mybeams ) {
        this.beams = mybeams;
    }

    public void setupObject( IntensityBeam beam ) {
        if( beam != currentBeam ) {
            getDiscreteModel().clearWavefunction();
            if( currentBeam != null ) {
                currentBeam.deactivate( this );
            }
            beam.activate( this );
            currentBeam = beam;
            currentBeam.setHighIntensityModeOn( on );
//            System.out.println( "alwaysOnCheckBox.isSelected() = " + alwaysOnCheckBox.isSelected() );
        }
        getSchrodingerModule().beamTypeChanged();
        updateGunLocation();
        updateSliderColor();
    }

    private void updateIntensity() {
        double intensity = new Function.LinearFunction( 0, MAX_INTENSITY_READOUT, 0, 0.5 ).evaluate( intensitySlider.getValue() );

//        System.out.println( "slidervalue=" + intensitySlider.getValue() + ", intensity = " + intensity );
        for( int i = 0; i < beams.length; i++ ) {
            beams[i].setIntensity( intensity );
        }
    }

    public void setOn( boolean on ) {
        this.on = on;
        if( currentBeam != null ) {
            currentBeam.setHighIntensityModeOn( on );
        }
        onOffTextNode.setText( on ? QWIResources.getString( "gun.on" ) : QWIResources.getString( "gun.off" ) );
        onOffTextNode.setTextPaint( on ? Color.red : Color.black );
    }

    public boolean isOn() {
        return on;
    }

    public ColorData getRootColor() {
        return photon == null ? null : photon.getRootColor();
    }

    private void updateSliderColor() {
        intensitySlider.setColor( currentBeam instanceof PhotonBeam && getRootColor() != null ? getRootColor().toColor( 1.0 ) : Color.white );
    }

//    public boolean isPhoton() {
//        return currentBeam instanceof PhotonBeam;
//    }

//    public double getVelocityRealUnits() {
//        if( getGunControls() instanceof SRRWavelengthSliderComponent ) {
//            SRRWavelengthSliderComponent srrwsc = (SRRWavelengthSliderComponent)getGunControls();
//            return srrwsc.getVelocityRealUnits();
//        }
//        else {
//            throw new RuntimeException( "No velocity");
//        }
//    }
}
