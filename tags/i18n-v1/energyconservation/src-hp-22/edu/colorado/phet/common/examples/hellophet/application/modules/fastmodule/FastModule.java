/**
 * Class: MessageModule
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: Jun 9, 2003
 */
package edu.colorado.phet.common.examples.hellophet.application.modules.fastmodule;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.examples.hellophet.model.HelloPhetModel;
import edu.colorado.phet.common.examples.hellophet.model.Message;
import edu.colorado.phet.common.examples.hellophet.model.MessageData;
import edu.colorado.phet.common.examples.hellophet.view.MessageView;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.help.HelpItem;

import javax.swing.*;
import java.awt.*;

public class FastModule extends Module {

    public FastModule() {
        super( "I'm the fast module." );
        double messageSpeed = 5.0;
        HelloPhetModel model = new HelloPhetModel();
        super.setModel( model );
        ApparatusPanel apparatus = new ApparatusPanel();
        apparatus.add( new HelpItem( "This is the fast module.!", 500, 100 ) );
        super.setApparatusPanel( apparatus );
//        HelloPhetSimpleControlPanel cp = new HelloPhetSimpleControlPanel( model );
        FastModuleControlPanel cp = new FastModuleControlPanel( this );
        super.setControlPanel( cp );
        JTextArea monitor = new JTextArea( "This is the MonitorPanel." );
        monitor.setBackground( Color.blue );
        monitor.setFont( new Font( "dialog", 0, 34 ) );
        JPanel monitorContainer = new JPanel();
        monitorContainer.add( monitor );
        super.setMonitorPanel( monitorContainer );
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                getApparatusPanel().repaint();
            }
        } );
    }

    public void activate( PhetApplication app ) {
    }

    public void deactivate( PhetApplication app ) {
    }

    public void addMessage() {
        double speed = 2;
        Message m = new Message( new MessageData( 100, 100 ), speed );
        MessageView mv = new MessageView( getModel(), m, 100 );
        getModel().addModelElement( m );
        getApparatusPanel().addGraphic( mv, 5 );
    }
}
