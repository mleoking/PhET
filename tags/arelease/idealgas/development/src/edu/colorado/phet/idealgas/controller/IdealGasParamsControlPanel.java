/*
 * Class: IdealGasParamsControlPanel
 * Package: edu.colorado.phet.idealgas.controller
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.idealgas.physics.HeavySpecies;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 */
public class IdealGasParamsControlPanel extends JPanel {

    /**
     * Create a panel for specifying free and fixed variables
     */
    public IdealGasParamsControlPanel() {

        JPanel parameterPanel = new JPanel( new BorderLayout() );

        JPanel paramLabels = new JPanel( new GridLayout( 4, 1 ));
        paramLabels.add( new Label( "" ));
        paramLabels.add( new Label( "Free variable" ));
        paramLabels.add( new Label( "Control variable" ));
        paramLabels.add( new Label( "Fixed" ));
        parameterPanel.add( paramLabels, BorderLayout.WEST );

        JPanel selectionPanel = new JPanel( new GridLayout( 4, 4 ));
        selectionPanel.add( new Label( "P" ));
        selectionPanel.add( new Label( "N" ));
        selectionPanel.add( new Label( "V" ));
        selectionPanel.add( new Label( "T" ));
        JRadioButton pFreeRB = new JRadioButton();
        JRadioButton nFreeRB = new JRadioButton();
        JRadioButton vFreeRB = new JRadioButton();
        JRadioButton tFreeRB = new JRadioButton();
        ButtonGroup freeGroup = new ButtonGroup();
        freeGroup.add( pFreeRB );
        freeGroup.add( nFreeRB );
        freeGroup.add( vFreeRB );
        freeGroup.add( tFreeRB );

        parameterPanel.add( selectionPanel, BorderLayout.CENTER );
        parameterPanel.setPreferredSize( new Dimension( 200, 200 ));
    }

    public static void main( String[] args ) {

        IdealGasParamsControlPanel panel = new IdealGasParamsControlPanel();
        JFrame testFrame = new JFrame();
        testFrame.getContentPane().add( panel );
        testFrame.setSize( 30, 300 );
        testFrame.pack();
        testFrame.show();
    }
}
