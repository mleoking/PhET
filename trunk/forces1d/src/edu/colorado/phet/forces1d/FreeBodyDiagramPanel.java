/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.forces1d.view.Force1DPanel;
import edu.colorado.phet.forces1d.view.FreeBodyDiagram;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 16, 2005
 * Time: 8:11:43 PM
 * Copyright (c) Jan 16, 2005 by Sam Reid
 */

public class FreeBodyDiagramPanel {

    private FreeBodyDiagram freeBodyDiagram;
    private ApparatusPanel2 apparatusPanel;
    private JCheckBox checkBox;

    public FreeBodyDiagramPanel( final Force1DModule module ) {
        apparatusPanel = new ApparatusPanel2( module.getModel(), module.getClock() );
        apparatusPanel.addGraphicsSetup( new BasicGraphicsSetup() );
        int fbdWidth = 180;
//        int fbdWidth = 120;
        apparatusPanel.setPreferredSize( new Dimension( fbdWidth, fbdWidth ) );
        Force1DPanel forcePanel = module.getForcePanel();
        freeBodyDiagram = forcePanel.getFreeBodyDiagram();
        freeBodyDiagram.setComponent( apparatusPanel );
        apparatusPanel.addGraphic( freeBodyDiagram );

        int fbdInset = 2;
        freeBodyDiagram.setBounds( fbdInset, fbdInset, fbdWidth - 2 * fbdInset, fbdWidth - 2 * fbdInset );

        checkBox = new JCheckBox( "Free Body Diagram", true );
        checkBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                boolean showFBD = checkBox.isSelected();
                apparatusPanel.setVisible( showFBD );
            }
        } );
//        return apparatusPanel;
    }

    public FreeBodyDiagram getFreeBodyDiagram() {
        return freeBodyDiagram;
    }

    public Component getCheckBox() {
        return checkBox;
    }

    public ApparatusPanel2 getApparatusPanel() {
        return apparatusPanel;
    }

    public void updateGraphics() {
        freeBodyDiagram.updateAll();
        apparatusPanel.repaint( 0, 0, apparatusPanel.getWidth(), apparatusPanel.getHeight() );
        apparatusPanel.paint();
    }
}
