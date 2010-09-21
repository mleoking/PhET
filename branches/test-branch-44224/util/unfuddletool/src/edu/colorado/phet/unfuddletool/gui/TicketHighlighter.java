package edu.colorado.phet.unfuddletool.gui;


import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;

import edu.colorado.phet.unfuddletool.data.Ticket;

public class TicketHighlighter implements Highlighter {

    private List<ChangeListener> listeners;

    public TicketHighlighter() {
        listeners = new LinkedList<ChangeListener>();
    }

    public Component highlight( Component component, ComponentAdapter adapter ) {
        if ( adapter.getValue() instanceof Ticket ) {
            Ticket ticket = (Ticket) adapter.getValue();

            switch( ticket.getPriority() ) {
                case 1:
                    component.setBackground( new Color( 0xAA, 0xAA, 0xFF ) );
                    break;
                case 2:
                    component.setBackground( new Color( 0xDD, 0xDD, 0xFF ) );
                    break;
                case 3:
                    component.setBackground( new Color( 0xFF, 0xFF, 0xFF ) );
                    break;
                case 4:
                    component.setBackground( new Color( 0xFF, 0xDD, 0xDD ) );
                    break;
                case 5:
                    component.setBackground( new Color( 0xFF, 0xAA, 0xAA ) );
                    break;
                default:
                    System.out.println( "WARNING: unknown priority!" );
            }

            if ( ticket.getStatus().equals( "closed" ) ) {
                component.setForeground( new Color( 0x33, 0x33, 0x33 ) );
            }
            else if ( ticket.getStatus().equals( "resolved" ) ) {
                component.setForeground( new Color( 0x00, 0x88, 0x00 ) );
            }
            else {
                component.setForeground( new Color( 0x00, 0x00, 0x00 ) );
                Font f = component.getFont();
                component.setFont( f.deriveFont( f.getStyle() ^ Font.BOLD ) );
            }
        }

        return component;
    }

    public void addChangeListener( ChangeListener changeListener ) {
        listeners.add( changeListener );
    }

    public void removeChangeListener( ChangeListener changeListener ) {
        listeners.remove( changeListener );
    }

    public ChangeListener[] getChangeListeners() {
        return (ChangeListener[]) listeners.toArray();
    }
}
