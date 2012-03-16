package edu.colorado.phet.unfuddletool.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.unfuddletool.Authentication;
import edu.colorado.phet.unfuddletool.gui.tabs.*;
import edu.colorado.phet.unfuddletool.util.Activity;

public class UnfuddleToolGUI extends JFrame {

    public UnfuddleToolGUI() {
        setTitle( "Unfuddle Tool" );
        setSize( 900, 700 );

        JTabbedPane tabber = new JTabbedPane();
        tabber.addTab( "Recent Tickets", new RecentTicketsTab() );
        tabber.addTab( "My Active Tickets", new MyActiveTicketsTab() );
        tabber.addTab( "All Active Tickets", new AllActiveTicketsTab() );
        tabber.addTab( "Ticket Search", new TicketSearchTab() );
        tabber.addTab( "Events", new EventsTab() );
        add( tabber );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        setJMenuBar( createMenu() );

        setVisible( true );
        repaint( 0, 0, 0, 5000, 5000 );

    }

    public JMenuBar createMenu() {
        JMenuBar bar = new JMenuBar();

        JMenu developmentMenu = new JMenu( "Development" );

        JMenuItem updateItem = new JMenuItem( "Update" );

        updateItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                Activity.requestRecentActivity( 3 );
            }
        } );

        developmentMenu.add( updateItem );

        bar.add( developmentMenu );

        return bar;
    }

    public static Color priorityColor( int priority ) {
        switch( priority ) {
            case 1:
                return new Color( 0xAA, 0xAA, 0xFF );
            case 2:
                return new Color( 0xDD, 0xDD, 0xFF );
            case 3:
                return new Color( 0xFF, 0xFF, 0xFF );
            case 4:
                return new Color( 0xFF, 0xDD, 0xDD );
            case 5:
                return new Color( 0xFF, 0xAA, 0xAA );
        }

        return new Color( 0xFF, 0xFF, 0xFF );
    }

    public static void main( String[] args ) {
        Authentication.auth = args[0];

        new UnfuddleToolGUI();
    }
}
