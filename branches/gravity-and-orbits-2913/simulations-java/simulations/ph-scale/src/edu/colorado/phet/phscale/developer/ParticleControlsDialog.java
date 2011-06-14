// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.developer;

import java.awt.Frame;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.phscale.PHScaleApplication;
import edu.colorado.phet.phscale.view.beaker.ParticlesNode;

/**
 * ParticleControlsDialog is contains "developer only" controls for particles.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ParticleControlsDialog extends PaintImmediateDialog {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PHScaleApplication _app;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ParticleControlsDialog( Frame owner, PHScaleApplication app ) {
        super( owner, "Particle Controls" );
        setResizable( false );
        setModal( false );

        _app = app;

        JPanel inputPanel = createInputPanel( owner );

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private JPanel createInputPanel( Frame owner ) {

        ParticlesNode particlesNode = _app.getModule().dev_getParticlesNode();
        ParticleControlsPanel particlesPanel = new ParticleControlsPanel( owner, particlesNode );

        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( particlesPanel, row++, column );

        return panel;
    }
}
