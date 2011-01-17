// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.view;

import java.awt.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;
import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1.Unit1;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolox.pswing.PSwing;

public class RandomnessSlider extends ModelSlider {
    private ParticleModel particleModel;

    public RandomnessSlider( final ParticleModel particleModel ) {
        super( "Randomness", "radians", 0, Math.PI * 2, particleModel.getRandomness(), new DecimalFormat( "0.00" ) );
        this.particleModel = particleModel;
        setModelTicks( new double[] { 0, Math.PI, Math.PI * 2 } );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                getParticleModel().setRandomness( getValue() );
            }
        } );
        getParticleModel().addListener( new ParticleModel.Adapter() {
            public void randomnessChanged() {
                setValue( getParticleModel().getRandomness() );
            }
        } );
    }

    private ParticleModel getParticleModel() {
        return particleModel;
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        SelfDrivenParticleModelApplication tutorialApplication = new SelfDrivenParticleModelApplication();
        BasicTutorialCanvas pane = new BasicTutorialCanvas( tutorialApplication, new Unit1( tutorialApplication ) );
        pane.setZoomEventHandler( new PZoomEventHandler() );
        pane.addChild( new PSwing( pane, new RandomnessSlider( new ParticleModel( 400, 400 ) ) ) );
        frame.setContentPane( pane );
        frame.pack();
        frame.setSize( 400, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width / 2 - frame.getWidth() / 2,
                           Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
        frame.show();

    }
}
