package edu.colorado.phet.unfuddletool.gui;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.unfuddletool.data.Ticket;

public class TicketDisplayPane extends JSplitPane {
    public HTMLDisplayPane ticketTableDisplay;
    public HTMLDisplayPane ticketTableHeader;

    public TicketDisplayPane() {
        ticketTableDisplay = new HTMLDisplayPane();
        ticketTableDisplay.setText( "Please select a ticket to display here" );
        final JScrollPane tableAreaScrollPane = new JScrollPane( ticketTableDisplay );
        tableAreaScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );

        ticketTableHeader = new HTMLDisplayPane();
        ticketTableHeader.setText( "Please select a ticket to display here" );
        final JScrollPane tableHeaderScrollPane = new JScrollPane( ticketTableHeader );
        tableHeaderScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );

        setLeftComponent( tableHeaderScrollPane );
        setRightComponent( tableAreaScrollPane );
        this.setOrientation( JSplitPane.VERTICAL_SPLIT );
        this.setOneTouchExpandable( true );
    }

    public void showTicket( Ticket ticket ) {

        String htmlComments = ticket.getHTMLComments();
        String htmlHeader = ticket.getHTMLHeader();

        ticketTableDisplay.setText( htmlComments );
        ticketTableHeader.setText( htmlHeader );

        // size everything
        int sizeAvailable = this.getHeight();
        int sizeTop = (int) ticketTableHeader.getPreferredScrollableViewportSize().getHeight();
        int sizeBottom = (int) ticketTableDisplay.getPreferredScrollableViewportSize().getHeight();
        this.setDividerLocation( getTicketDividerLocation( sizeAvailable, sizeTop, sizeBottom, this.getDividerSize() ) );

        // size it again (for if scrollbars were added after sizing)
        sizeAvailable = this.getHeight();
        sizeTop = (int) ticketTableHeader.getPreferredScrollableViewportSize().getHeight();
        sizeBottom = (int) ticketTableDisplay.getPreferredScrollableViewportSize().getHeight();
        this.setDividerLocation( getTicketDividerLocation( sizeAvailable, sizeTop, sizeBottom, this.getDividerSize() ) );

        // make sure the header is set to the top
        ticketTableHeader.setSelectionStart( 0 );
        ticketTableHeader.setSelectionEnd( 0 );

        ticketTableDisplay.scrollRectToVisible( new Rectangle( 0, ticketTableDisplay.getBounds( null ).height, 1, 1 ) );

        //ticketTableDisplay.setSelectionStart( htmlComments.length() );
        //ticketTableDisplay.setSelectionEnd( htmlComments.length() );

        //tableAreaScrollPane.getVerticalScrollBar().setValue( tableAreaScrollPane.getVerticalScrollBar().getMaximum() );

        //ticketTableDisplay.revalidate();
        //ticketTableHeader.revalidate();

        // try:
        // revalidate()
        // repaint() // immediately maybe?
        // revalidate?

        /* WAS WORKING WITH FLICKER
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {

                System.out.println( "during runnable status: " );
                TicketSearchTab.this.printStatus();

                int sizeAvailable = rightSplitPane.getHeight();
                int sizeTop = (int) ticketTableHeader.getPreferredScrollableViewportSize().getHeight();
                int sizeBottom = (int) ticketTableDisplay.getPreferredScrollableViewportSize().getHeight();
                rightSplitPane.setDividerLocation( getTicketDividerLocation( sizeAvailable, sizeTop, sizeBottom, rightSplitPane.getDividerSize() ) );

                ticketTableHeader.setSelectionStart( 0 );
                ticketTableHeader.setSelectionEnd( 0 );
            }
        } );
        */

        /* UNSURE IF THIS IS HELPFUL?

        tableHeaderScrollPane.validate();
        tableAreaScrollPane.validate();

        tableHeaderScrollPane.getVerticalScrollBar().setValue( 0 );
        */

        /* WAS WORKING WITH FLICKER
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                //tableHeaderScrollPane.getVerticalScrollBar().setValue( 0 );
                tableAreaScrollPane.getVerticalScrollBar().setValue( tableAreaScrollPane.getVerticalScrollBar().getMaximum() );
            }
        } );
        */

        //tableHeaderScrollPane.getViewport().setViewPosition( new Point( 0, 0 ) );

        //int prefDisplayHeight = (int) ticketTableDisplay.getPreferredScrollableViewportSize().getHeight();
        //tableAreaScrollPane.getViewport().setViewPosition( new Point( 0, (int) (prefDisplayHeight - tableAreaScrollPane.getViewport().getViewSize().getHeight()) ) );

        // REALLY OLD solution
        //rightSplitPane.setDividerLocation( -1 );
    }

    private static int getTicketDividerLocation( int sizeAvailable, int sizeTop, int sizeBottom, int dividerSize ) {
        int extraPadding = 25;

        int ret;

        if ( sizeAvailable >= sizeTop + extraPadding * 2 + sizeBottom + dividerSize ) {
            // if we have room for everything
            ret = sizeTop + extraPadding;
        }
        else {
            int minTop = 400;
            int minBottom = 200;
            int maxTop = sizeAvailable - minBottom - extraPadding - dividerSize;

            if ( sizeBottom < minBottom ) {
                ret = sizeAvailable - sizeBottom - extraPadding - dividerSize;
            }
            else if ( sizeTop < minTop ) {
                ret = sizeTop + extraPadding;
            }
            else {
                ret = minTop + extraPadding;
            }
        }

        return ret;

    }
}
