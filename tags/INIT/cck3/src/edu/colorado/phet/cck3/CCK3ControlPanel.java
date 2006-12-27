/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolver;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 1, 2004
 * Time: 11:03:06 AM
 * Copyright (c) Jun 1, 2004 by Sam Reid
 */
public class CCK3ControlPanel extends JPanel {
    private CCK3Module module;

    public CCK3ControlPanel( final CCK3Module module ) {
        this.module = module;
        JButton save = new JButton( "Save" );
        JButton load = new JButton( "Load" );
        JSeparator js = new JSeparator();
        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clear();
            }
        } );
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        add( save );
        add( load );
        add( js );
        add( clear );
        JSeparator j2 = new JSeparator();
        add( j2 );

        JButton solve = new JButton( "Solve" );
        solve.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.solve();
            }
        } );
        add( solve );
        JButton printKirkhoffsLaws = new JButton( "Get Kirkhoff's Laws" );
        printKirkhoffsLaws.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                printEm();
            }
        } );
        add( printKirkhoffsLaws );

        JRadioButton lifelike = new JRadioButton( "Lifelike", true );
        JRadioButton schematic = new JRadioButton( "Schematic", false );
        ButtonGroup bg = new ButtonGroup();
        bg.add( lifelike );
        bg.add( schematic );
        lifelike.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setLifelike( true );
            }
        } );
        schematic.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setLifelike( false );
            }
        } );
        JPanel visualizationPanel = new JPanel();
        visualizationPanel.setBorder( BorderFactory.createRaisedBevelBorder() );
        visualizationPanel.setLayout( new BoxLayout( visualizationPanel, BoxLayout.Y_AXIS ) );
        visualizationPanel.add( lifelike );
        visualizationPanel.add( schematic );

        add( visualizationPanel );
        final JCheckBox virtualAmmeter = new JCheckBox( "Virtual Ammeter", false );
        virtualAmmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVirtualAmmeterVisible( virtualAmmeter.isSelected() );
            }
        } );
        add( virtualAmmeter );

        final JCheckBox voltmeter = new JCheckBox( "Voltmeter", false );
        voltmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVoltmeterVisible( voltmeter.isSelected() );
                module.getApparatusPanel().repaint();
            }
        } );
        add( voltmeter );

        final JSpinner zoom = new JSpinner( new SpinnerNumberModel( 1, .1, 10, .1 ) );
        zoom.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Number value = (Number)zoom.getValue();
                double v = value.doubleValue();
                zoom( v );
            }
        } );
        add( zoom );
    }

    private void zoom( double scale ) {
        module.setZoom( scale );
    }

    private void printEm() {
        KirkhoffSolver ks = new KirkhoffSolver();
        Circuit circuit = module.getCircuit();
        KirkhoffSolver.MatrixTable mt = new KirkhoffSolver.MatrixTable( circuit );
        System.out.println( "mt = " + mt );

        KirkhoffSolver.Equation[] junctionEquations = ks.getJunctionEquations( circuit, mt );
        KirkhoffSolver.Equation[] loopEquations = ks.getLoopEquations( circuit, mt );
        KirkhoffSolver.Equation[] ohmsLaws = ks.getOhmsLaw( circuit, mt );

        String je = mt.describe( junctionEquations, "Junction Equations" );
        String le = mt.describe( loopEquations, "Loop Equations" );
        String oh = mt.describe( ohmsLaws, "Ohms Law Equations" );
        System.out.println( je );
        System.out.println( le );
        System.out.println( oh );

        JFrame readoutFrame = new JFrame();
        JTextArea jta = new JTextArea( je + "\n" + le + "\n" + oh + "\n" ) {
            protected void paintComponent( Graphics g ) {
                GraphicsUtil.setAntiAliasingOn( (Graphics2D)g );
                super.paintComponent( g );
            }
        };
        jta.setEditable( false );
        jta.setFont( new Font( "Lucida Sans", Font.BOLD, 16 ) );
        readoutFrame.setContentPane( jta );
        readoutFrame.pack();
        GraphicsUtil.centerWindowOnScreen( readoutFrame );
        readoutFrame.setVisible( true );
    }
}
