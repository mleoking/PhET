/**
 * Class: MessageModule
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: Jun 9, 2003
 */
package edu.colorado.phet.common.examples.hellophet.application.modules.messagemodule;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.examples.hellophet.model.HelloPhetModel;
import edu.colorado.phet.common.examples.hellophet.model.Message;
import edu.colorado.phet.common.examples.hellophet.model.MessageData;
import edu.colorado.phet.common.examples.hellophet.view.MessageView;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.components.menu.PhetFileMenu;
import edu.colorado.phet.common.view.help.HelpItem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public class MessageModule extends Module {
    // The file menu we want used when we are active
    private PhetFileMenu fileMenu;

    public MessageModule() {
        super( "Messages Flyin' Around" );
        HelloPhetModel model = new HelloPhetModel();
        super.setModel( model );
        ApparatusPanel apparatus = new ApparatusPanel();
        apparatus.add( new HelpItem( "Help!", 100, 100 ) );
        super.setApparatusPanel( apparatus );
        HelloPhetSimpleControlPanel cp = new HelloPhetSimpleControlPanel( this );
        super.setControlPanel( cp );
        MessageMonitorPanel monitor = new MessageMonitorPanel( this );
        super.setMonitorPanel( monitor );
        MessageModuleResetHandler resetter = new MessageModuleResetHandler( this );
        super.setResetter( resetter );

        fileMenu = createFileMenu();

    }

    public void activate( PhetApplication app ) {
        app.getApplicationView().setFileMenu( fileMenu );
    }

    public void deactivate( PhetApplication app ) {
        app.getApplicationView().removeFileMenu( fileMenu );
    }

    Hashtable t = new Hashtable();

    public void addNewMessage( int speed, int y ) {
        final Message m = new Message( new MessageData( 100, y ), speed );
        final MessageView mv = new MessageView( getModel(), m, 100 );
        getModel().execute( new Command() {
            public void doIt() {
                getModel().addModelElement( m );
                getApparatusPanel().addGraphic( mv, 5 );
            }
        } );
        t.put( m, mv );
    }

    class RemoveMessageCommand implements Command {

        public void doIt() {
            if( getModel().numModelElements() == 0 ) {
                return;
            }
            ModelElement last = getModel().modelElementAt( getModel().numModelElements() - 1 );
            if( !( last instanceof Message ) ) {
                return;
            }
            MessageView viewElement = (MessageView)t.get( last );
            remove( last, viewElement );
        }
    }

    public void removeLastMessage() {
        getModel().execute( new RemoveMessageCommand() );
    }

    public void reset() {
        System.out.println( "Resetted!" );
    }

    private PhetFileMenu createFileMenu() {
        JMenuItem item1 = new JMenuItem( "foo" );
        item1.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( null, "Hi there!" );
            }
        } );
        JSeparator separator = new JSeparator();

        JComponent[] menuStuff = new JComponent[]{
            item1,
            separator};
        PhetFileMenu menu = new PhetFileMenu( menuStuff );
        return menu;
    }
}