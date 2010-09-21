package edu.colorado.phet.eatingandexercise.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.eatingandexercise.model.Human;

/**
 * Created by: Sam
 * Jun 27, 2008 at 10:13:56 AM
 */
public class AliveCheckBox extends JCheckBox {
    public AliveCheckBox( final Human human ) {
        super( "Alive", human.isAlive() );
        human.addListener( new Human.Adapter() {
            public void aliveChanged() {
                setSelected( human.isAlive() );
            }
        } );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                human.setAlive( isSelected() );
            }
        } );
    }
}
