/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.view.ControlPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jan 11, 2005
 * Time: 8:15:29 PM
 * Copyright (c) Jan 11, 2005 by Sam Reid
 */

public class SimpleControlPanel extends ControlPanel {
    private FreeBodyDiagramPanel freeBodyDiagramPanel;

    public SimpleControlPanel( final SimpleForceModule simpleForceModule ) {
        super( simpleForceModule );
        final JCheckBox frictionCheckBox = new JCheckBox( "Friction", true );
        frictionCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                simpleForceModule.setFrictionEnabled( frictionCheckBox.isSelected() );
            }
        } );

        freeBodyDiagramPanel = new FreeBodyDiagramPanel( simpleForceModule );
        add( freeBodyDiagramPanel.getCheckBox() );
        add( freeBodyDiagramPanel.getApparatusPanel() );
        add( frictionCheckBox );
        super.setHelpPanelEnabled( true );
    }

    public void updateGraphics() {
        freeBodyDiagramPanel.updateGraphics();
    }
}
