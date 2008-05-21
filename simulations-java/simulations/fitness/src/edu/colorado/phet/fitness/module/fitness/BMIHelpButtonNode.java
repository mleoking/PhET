package edu.colorado.phet.fitness.module.fitness;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.FitnessResources;

/**
 * Created by: Sam
 * May 4, 2008 at 11:16:09 PM
 */
public class BMIHelpButtonNode extends GradientButtonNode {
    private Human human;

    public BMIHelpButtonNode( final Component parentComponent, final Human human ) {
        super( "?", 14, Color.red );
        this.human = human;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String currentBMI = FitnessResources.getString( "current.bmi" );
                String line1 = FitnessResources.getString( "bmi.table.for.adults.20.and.over.n.n" );
                String line2 = FitnessResources.getString( "bmi.tweight.status.n" );
                String line3 = FitnessResources.getString( "0.18.5.tunderweight.n" );
                String line4 = FitnessResources.getString( "18.5.24.9.tnormal.n" );
                String line5 = FitnessResources.getString( "25.0.29.9.toverweight.n" );
                String line6 = FitnessResources.getString( "30.0.tobese" );
                JOptionPane.showMessageDialog( parentComponent, currentBMI + new DecimalFormat( "0.0" ).format( human.getBMI() ) + "\n\n" +
                                                                line1 +
                                                                line2 +
                                                                line3 +
                                                                line4 +
                                                                line5 +
                                                                line6 );
            }
        } );
    }
}
