/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Nov 17, 2003
 * Time: 7:53:29 PM
 * Copyright (c) Nov 17, 2003 by Sam Reid
 */
public class CCKMenu extends JMenu {
    private DeveloperPanel developerPanel;

    public CCKMenu( final CCK2Module module ) {
        super( "Options" );
        JMenuItem backgroundColor = new JMenuItem( "Background Color" );
        backgroundColor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JColorChooser jcc = new JColorChooser( module.getApparatusPanel().getBackground() );
                jcc.getSelectionModel().addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        Color color = jcc.getColor();
                        module.getApparatusPanel().setBackground( color );
                    }
                } );
//                Color c=JColorChooser.showDialog(getApparatusPanel(),"Color",getApparatusPanel().getBackground());
                ActionListener ok = new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        Color color = jcc.getColor();
                        module.getApparatusPanel().setBackground( color );
                    }
                };
                ActionListener cancel = new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        //noop for now.
                    }
                };
                JDialog d = JColorChooser.createDialog( module.getApparatusPanel(), "Background Color", false, jcc, ok, cancel );
                d.setVisible( true );
//                System.out.println("c = " + c);
//                getApparatusPanel().setBackground(c);
            }
        } );
        add( backgroundColor );

        JMenuItem setBlurry = new JMenuItem( "Blurry Mode" );
        setBlurry.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Color c = module.getApparatusPanel().getBackground();
                Color alpha = new Color( c.getRed(), c.getGreen(), c.getBlue(), 60 );
                module.getApparatusPanel().setBackground( alpha );
            }
        } );
        add( setBlurry );

        JMenuItem setWireColor = new JMenuItem( "Wire Color" );
        setWireColor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JColorChooser jcc = new JColorChooser( module.getApparatusPanel().getBackground() );
                jcc.getSelectionModel().addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        Color color = jcc.getColor();
                        module.setLifelikeWireColor( color );
                    }
                } );
//                Color c=JColorChooser.showDialog(getApparatusPanel(),"Color",getApparatusPanel().getBackground());
                ActionListener ok = new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        Color color = jcc.getColor();
                        module.setLifelikeWireColor( color );
                    }
                };
                ActionListener cancel = new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        //noop for now.
                    }
                };
                JDialog d = JColorChooser.createDialog( module.getApparatusPanel(), "Background Color", false, jcc, ok, cancel );
                d.setVisible( true );
//                System.out.println("c = " + c);
//                getApparatusPanel().setBackground(c);
            }
        } );
        add( setWireColor );
        developerPanel = new DeveloperPanel( module );
        JMenuItem showDevPanel = new JMenuItem( "Show Developer Panel" );
        showDevPanel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                developerPanel.show();
            }
        } );
        addSeparator();
        add( showDevPanel );
    }
}
