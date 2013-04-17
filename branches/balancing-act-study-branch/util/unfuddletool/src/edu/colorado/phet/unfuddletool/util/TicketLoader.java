package edu.colorado.phet.unfuddletool.util;

import java.util.List;

import edu.colorado.phet.unfuddletool.gui.TicketTableModel;

public class TicketLoader extends Thread {

    private List<Integer> idList;
    private TicketTableModel model;

    public TicketLoader( List<Integer> idList, TicketTableModel model ) {
        this.idList = idList;
        this.model = model;
    }

    public void run() {
        model.addTicketIDList( idList );
    }
}
