package edu.colorado.phet.balloons;

import java.awt.*;

import javax.swing.*;

public class TestFrameSize {

    public void start() {
        JFrame eight = new JFrame( "800x600" );
        eight.setSize( 800, 600 );
        eight.setVisible( true );

        System.out.println( "eight.getSize( = " + eight.getSize() );

        JDialog modal = new JDialog( eight, "Dialog, modal=false", false );
        modal.setSize( 800, 600 );
        System.out.println( "modal.getSize( = " + modal.getSize() );
        modal.setVisible( true );

        JDialog nonModal = new JDialog( eight, "Dialog, modal=true", false );
        System.out.println( "nonModal.getSize( = " + nonModal.getSize() );
        nonModal.setSize( 800, 600 );
        nonModal.setVisible( true );

        JFrame max = new JFrame( "Max" );
// this.setMaximizedBounds(env.getMaximumWindowBounds());
        max.setExtendedState( max.getExtendedState() | Frame.MAXIMIZED_BOTH );

//        max.setState( Frame.MAXIMIZED_BOTH );
        max.setMaximizedBounds( GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds() );

        max.setVisible( true );
        System.out.println( "max.getSize() = " + max.getSize() );


    }
}
