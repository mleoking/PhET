package edu.colorado.phet.fitness.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.fitness.model.Human;

/**
 * Created by: Sam
 * Apr 23, 2008 at 8:02:01 AM
 */
public class NamePanel extends JPanel {
    public NamePanel( Human human ) {

        JPanel namePanel = new JPanel();
        namePanel.add( new JLabel( "Name: " ) );
        JTextField name = new JTextField( human.getName() );
        name.setColumns( 10 );
        namePanel.add( name );
        add( namePanel );
    }
}
