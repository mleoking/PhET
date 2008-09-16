package edu.colorado.phet.efield.electron.electricField;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.efield.EFieldResources;

public class DiscreteFieldSlider implements ActionListener, ChangeListener {
    //JButton b;
    ElectricFieldPainter efp;
    JFrame sliderFrame;
    JSlider s;
    Component paintMe;

    public DiscreteFieldSlider( ElectricFieldPainter efp, Component paintMe ) {
        this.paintMe = paintMe;
        //b=new JButton( EFieldResources.getString( "DiscreteFieldSlider.ChangeElectricFieldButton" ) );
        //b.addActionListener(this);
        sliderFrame = new JFrame( EFieldResources.getString( "DiscreteFieldSlider.ElectricFieldDiscretenessTitle" ) );
        s = new JSlider( 1, 30, 10 );
        s.setMajorTickSpacing( 1 );
        s.setPaintTicks( true );
        s.setPaintLabels( true );
        s.setSnapToTicks( true );
        sliderFrame.setContentPane( s );
        sliderFrame.pack();
        sliderFrame.setSize( sliderFrame.getWidth() * 3, sliderFrame.getHeight() );
        s.addChangeListener( this );
        this.efp = efp;
    }
//      public JButton getButton()
//      {
//  	return b;

    //      }

    public void actionPerformed( ActionEvent ae ) {
        sliderFrame.setVisible( true );
    }

    public void stateChanged( ChangeEvent ce ) {
        int nx = s.getValue();
        efp.setNX( nx );
        efp.setNY( nx );
        paintMe.repaint();
    }
}
