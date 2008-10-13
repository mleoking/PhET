/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.controls.ClearButton;
import edu.colorado.phet.quantumwaveinterference.controls.RulerPanel;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.view.gun.AbstractGunNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 10:54:25 PM
 */

public class DGControlPanel extends ControlPanel {
    private DGModule dgModule;
    private boolean addExtraControls = false;

    public DGControlPanel( DGModule dgModule ) {
        this.dgModule = dgModule;
        addRulerPanel();
        addProtractorPanel();
        addControl( new ClearButton( dgModule.getSchrodingerPanel() ) );
        final JCheckBox plotCheckBox = new JCheckBox( QWIResources.getString( "plot" ) );
        plotCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                DGControlPanel.this.setPlotVisible( plotCheckBox.isSelected() );
            }
        } );
        addControl( plotCheckBox );
        addControlFullWidth( new AtomLatticeControlPanel( dgModule.getDGModel() ) );
        if( addExtraControls ) {
            addExtraControls( dgModule );
        }
        dgModule.getPlotFrame().addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                plotCheckBox.setSelected( false );
            }
        } );
    }

    private void addExtraControls( DGModule dgModule ) {
        AbstractGunNode gun = dgModule.getSchrodingerPanel().getSchrodingerScreenNode().getGunGraphic();
        if( gun instanceof DGGun ) {
            DGGun dgGun = (DGGun)gun;
            final DGParticle particle = dgGun.getDgParticle();
            final ModelSlider covariance = new ModelSlider( QWIResources.getString( "covariance" ), "", 0, 0.3, particle.getCovariance() );
            covariance.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    particle.setCovariance( covariance.getValue() );
                }
            } );
            addControl( covariance );

            final ModelSlider y0 = new ModelSlider( QWIResources.getString( "particle.y0" ), "", 0, 1.0, particle.getStartYFraction() );
            y0.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    particle.setStartYFraction( y0.getValue() );
                }
            } );
            addControl( y0 );
        }
        addControlFullWidth( new AtomShapeControlPanel( dgModule ) );
        addControlFullWidth( new DGReaderControlPanel( dgModule ) );
    }

    private void setPlotVisible( boolean selected ) {
        dgModule.getPlotFrame().setVisible( selected );
    }

    private QWIModel getDiscreteModel() {
        return dgModule.getQWIModel();
    }

    private void addRulerPanel() {
        try {
            RulerPanel rulerPanel = new RulerPanel( dgModule.getSchrodingerPanel() );
            addControl( rulerPanel );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void addProtractorPanel() {
        final JCheckBox protractor = new JCheckBox( QWIResources.getString( "protractor" ), false );
        protractor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dgModule.setProtractorVisible( protractor.isSelected() );
            }
        } );
        addControl( protractor );
    }
}
