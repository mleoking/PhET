/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;
import edu.colorado.phet.selfdrivenparticlemodel.view.InteractionRadiusControl;
import edu.colorado.phet.selfdrivenparticlemodel.view.NumberSliderPanel;
import edu.colorado.phet.selfdrivenparticlemodel.view.RandomnessSlider;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 12:46:07 AM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */

public class FullFeatureBaseClass extends Page {
    protected PSwing checkBoxGraphic;
    protected PSwing randomnessGraphic;
    protected PSwing particleCountGraphic;
    protected PSwing radiusControlGraphic;
    private NumberSliderPanel numberSliderPanel;

    public FullFeatureBaseClass( final BasicTutorialCanvas basicPage ) {
        super( basicPage );

        final JCheckBox showHalos = new JCheckBox( "Show Range", basicPage.isHalosVisible() );
        showHalos.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                basicPage.setHalosVisible( showHalos.isSelected() );
            }
        } );
        checkBoxGraphic = new PSwing( basicPage, showHalos );
        RandomnessSlider randomnessSlider = new RandomnessSlider( getParticleModel() );
        randomnessGraphic = new PSwing( basicPage, randomnessSlider );
        numberSliderPanel = new NumberSliderPanel( basicPage, 0, 50, 1, new int[]{0, 10, 20, 30, 40, 50} );
        particleCountGraphic = new PSwing( basicPage, numberSliderPanel );
        InteractionRadiusControl interactionRadiusControl = new InteractionRadiusControl( getParticleModel() );
        radiusControlGraphic = new PSwing( basicPage, interactionRadiusControl );
    }

    public NumberSliderPanel getNumberSliderPanel() {
        return numberSliderPanel;
    }

    public void init() {
        super.init();

        checkBoxGraphic.setOffset( getBasePage().getPreviousButton().getFullBounds().getX(), getBasePage().getPreviousButton().getFullBounds().getMaxY() );
        randomnessGraphic.setOffset( checkBoxGraphic.getFullBounds().getX(), checkBoxGraphic.getFullBounds().getMaxY() + getDy() );
        particleCountGraphic.setOffset( randomnessGraphic.getFullBounds().getX(), randomnessGraphic.getFullBounds().getMaxY() + getDy() );
        radiusControlGraphic.setOffset( particleCountGraphic.getFullBounds().getX(), particleCountGraphic.getFullBounds().getMaxY() + getDy() );
        addChild( checkBoxGraphic );
        addChild( randomnessGraphic );
        addChild( particleCountGraphic );
        addChild( radiusControlGraphic );
    }

    public void teardown() {
        super.teardown();
        removeChild( checkBoxGraphic );
        removeChild( randomnessGraphic );
        removeChild( particleCountGraphic );
        removeChild( radiusControlGraphic );
    }

    public PSwing getCheckBoxGraphic() {
        return checkBoxGraphic;
    }

    public PSwing getRandomnessGraphic() {
        return randomnessGraphic;
    }

    public PSwing getParticleCountGraphic() {
        return particleCountGraphic;
    }

    public PSwing getRadiusControlGraphic() {
        return radiusControlGraphic;
    }

}
