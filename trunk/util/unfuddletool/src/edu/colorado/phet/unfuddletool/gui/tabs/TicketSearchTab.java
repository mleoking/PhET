package edu.colorado.phet.unfuddletool.gui.tabs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.unfuddletool.data.Ticket;
import edu.colorado.phet.unfuddletool.gui.TicketDisplayPane;
import edu.colorado.phet.unfuddletool.gui.TicketTable;
import edu.colorado.phet.unfuddletool.gui.TicketTableModel;
import edu.colorado.phet.unfuddletool.util.TicketLoader;
import edu.colorado.phet.unfuddletool.util.TicketSearch;

public class TicketSearchTab extends JSplitPane {

    private static TicketTableModel model;

    public TicketTable ticketTable;
    public TicketDisplayPane ticketTableDisplay;
    public TicketDisplayPane ticketTableHeader;

    public TicketSearchTab() {
        // set up the model
        model = new TicketTableModel();
        //TicketHandler.getTicketHandler().addTicketAddListener( model );

        ticketTable = new TicketTable( model );
        JScrollPane ticketTableScrollPane = new JScrollPane( ticketTable );
        ticketTable.setFillsViewportHeight( true );
        ticketTableScrollPane.setMinimumSize( new Dimension( 600, 0 ) );
        ticketTableDisplay = new TicketDisplayPane();
        ticketTableDisplay.setText( "Testing" );
        final JScrollPane tableAreaScrollPane = new JScrollPane( ticketTableDisplay );
        tableAreaScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        //tableAreaScrollPane.setMinimumSize( new Dimension( 0, 200 ) );
        ticketTableHeader = new TicketDisplayPane();
        ticketTableHeader.setText( "Ticket Header" );
        final JScrollPane tableHeaderScrollPane = new JScrollPane( ticketTableHeader );
        tableHeaderScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        //tableHeaderScrollPane.setMinimumSize( new Dimension( 0, 200 ) );
        final JSplitPane rightSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, tableHeaderScrollPane, tableAreaScrollPane );
        rightSplitPane.setOneTouchExpandable( true );
        ticketTable.ticketSelectionModel.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent event ) {
                if ( !event.getValueIsAdjusting() ) {
                    int[] indices = ticketTable.getSelectedRows();
                    if ( indices.length == 1 ) {
                        int index = ticketTable.convertRowIndexToModel( indices[0] );
                        Ticket ticket = model.getTicketAt( index );
                        ticketTableDisplay.setText( ticket.getHTMLComments() );
                        ticketTableHeader.setText( ticket.getHTMLHeader() );

                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                int sizeAvailable = rightSplitPane.getHeight();
                                System.out.println( "Size avail: " + sizeAvailable );

                                int sizeTop = (int) ticketTableHeader.getPreferredScrollableViewportSize().getHeight();
                                System.out.println( "Size top: " + sizeTop );

                                int sizeBottom = (int) ticketTableDisplay.getPreferredScrollableViewportSize().getHeight();
                                System.out.println( "Size bottom: " + sizeBottom );

                                int extraPadding = 5;

                                if ( sizeAvailable >= sizeTop + extraPadding * 2 + sizeBottom + rightSplitPane.getDividerSize() ) {
                                    // if we have room for everything
                                    rightSplitPane.setDividerLocation( sizeTop + extraPadding );
                                }
                                else {
                                    int minTop = 200;
                                    int minBottom = 200;
                                    int maxTop = sizeAvailable - minBottom - extraPadding - rightSplitPane.getDividerSize();

                                    if ( sizeBottom < minBottom ) {
                                        rightSplitPane.setDividerLocation( sizeAvailable - sizeBottom - extraPadding - rightSplitPane.getDividerSize() );
                                    }
                                    else if ( sizeTop < minTop ) {
                                        rightSplitPane.setDividerLocation( sizeTop + extraPadding );
                                    }
                                    else {
                                        rightSplitPane.setDividerLocation( minTop + extraPadding );
                                    }
                                }
                                ticketTableHeader.setSelectionStart( 0 );
                                ticketTableHeader.setSelectionEnd( 0 );
                            }
                        } );


                        /*
                        int sizeAvailable = rightSplitPane.getHeight();
                        System.out.println( "Size avail: " + sizeAvailable );

                        int sizeTop = (int) ticketTableHeader.getPreferredScrollableViewportSize().getHeight();
                        System.out.println( "Size top: " + sizeTop );

                        int sizeBottom = (int) ticketTableDisplay.getPreferredScrollableViewportSize().getHeight();
                        System.out.println( "Size bottom: " + sizeBottom );

                        int extraPadding = 5;

                        if( sizeAvailable >= sizeTop + extraPadding * 2 + sizeBottom + rightSplitPane.getDividerSize() ) {
                            // if we have room for everything
                            rightSplitPane.setDividerLocation( sizeTop + extraPadding );
                        } else {
                            int minTop = 200;
                            int minBottom = 200;
                            int maxTop = sizeAvailable - minBottom - extraPadding - rightSplitPane.getDividerSize();

                            if( sizeBottom < minBottom ) {
                                rightSplitPane.setDividerLocation( sizeAvailable - sizeBottom - extraPadding - rightSplitPane.getDividerSize() );
                            } else if( sizeTop < minTop ) {
                                rightSplitPane.setDividerLocation( sizeTop + extraPadding );
                            } else {
                                rightSplitPane.setDividerLocation( minTop + extraPadding );
                            }
                        }


                        ticketTableHeader.setSelectionStart( 0 );
                        ticketTableHeader.setSelectionEnd( 0 );
                        */

                        /*

                        tableHeaderScrollPane.validate();
                        tableAreaScrollPane.validate();

                        tableHeaderScrollPane.getVerticalScrollBar().setValue( 0 );
                        */

                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                //tableHeaderScrollPane.getVerticalScrollBar().setValue( 0 );
                                tableAreaScrollPane.getVerticalScrollBar().setValue( tableAreaScrollPane.getVerticalScrollBar().getMaximum() );
                            }
                        } );

                        //tableHeaderScrollPane.getViewport().setViewPosition( new Point( 0, 0 ) );

                        //int prefDisplayHeight = (int) ticketTableDisplay.getPreferredScrollableViewportSize().getHeight();
                        //tableAreaScrollPane.getViewport().setViewPosition( new Point( 0, (int) (prefDisplayHeight - tableAreaScrollPane.getViewport().getViewSize().getHeight()) ) );

                        //rightSplitPane.setDividerLocation( -1 );
                    }
                }
            }
        } );


        JButton searchButton = new JButton( "Search" );
        final JTextField searchField = new JTextField( "" );
        searchField.setEditable( true );

        JPanel topLeftPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        topLeftPanel.add( searchField, c );
        c.gridx = 1;
        c.weightx = 0.0;
        topLeftPanel.add( searchButton, c );

        JPanel leftPanel = new JPanel( new BorderLayout() );
        leftPanel.add( topLeftPanel, BorderLayout.NORTH );
        leftPanel.add( ticketTableScrollPane, BorderLayout.CENTER );

        setLeftComponent( leftPanel );
        setRightComponent( rightSplitPane );


        searchButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.clear();
                List<Integer> ticketList = TicketSearch.getTicketSearchIDs( searchField.getText() );
                System.out.println( "Report found " + ticketList.size() + " tickets" );
                //model.addTicketIDList( ticketList );
                new TicketLoader( ticketList, model ).start();
            }
        } );

    }
}