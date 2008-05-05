package edu.colorado.phet.fitness.module.fitness;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.fitness.model.Human;

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
                JOptionPane.showMessageDialog( parentComponent, "Current BMI: " + new DecimalFormat("0.0").format( human.getBMI() )+ "\n\n" +
                                                                "BMI table for adults (20 and over)\n\n" +
                                                                "BMI             \tWeight Status\n" +
                                                                "0-18.5          \tUnderweight\n" +
                                                                "18.5 – 24.9     \tNormal\n" +
                                                                "25.0 – 29.9     \tOverweight\n" +
                                                                "30.0+           \tObese" );
            }
        } );
    }
}
