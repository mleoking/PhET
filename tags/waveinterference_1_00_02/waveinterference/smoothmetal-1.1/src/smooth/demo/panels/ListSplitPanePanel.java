package smooth.demo.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * List/SplitPane demonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class ListSplitPanePanel extends JPanel {

    private final JSplitPane splitPane = new JSplitPane();

    private final String[] listOneData = {
            "java.applet",
            "java.awt",
            "java.awt.color",
            "java.awt.datatransfer",
            "java.awt.dnd",
            "java.awt.event",
            "java.awt.font",
            "java.awt.geom",
            "java.awt.im",
            "java.awt.im.spi",
            "java.awt.image",
            "java.awt.image.renderable",
            "java.awt.print",
            "java.beans",
            "java.beans.beancontext",
            "java.io",
            "java.lang",
            "java.lang.ref",
            "java.lang.reflect",
            "java.math",
            "java.net",
            "java.nio",
            "java.nio.channels",
            "java.nio.channels.spi",
            "java.nio.charset",
            "java.nio.charset.spi",
            "java.rmi",
            "java.rmi.activation",
            "java.rmi.dgc",
            "java.rmi.registry",
            "java.rmi.server",
            "java.security",
            "java.security.acl",
            "java.security.cert",
            "java.security.interfaces",
            "java.security.spec",
            "java.sql",
            "java.text",
            "java.util",
            "java.util.jar",
            "java.util.logging",
            "java.util.prefs",
            "java.util.regex",
            "java.util.zip"
    };

    private final String[] listTwoData = {
            "javax.accessibility",
            "javax.crypto",
            "javax.crypto.interfaces",
            "javax.crypto.spec",
            "javax.imageio",
            "javax.imageio.event",
            "javax.imageio.metadata",
            "javax.imageio.plugins.jpeg",
            "javax.imageio.spi",
            "javax.imageio.stream",
            "javax.naming",
            "javax.naming.directory",
            "javax.naming.event",
            "javax.naming.ldap",
            "javax.naming.spi",
            "javax.net",
            "javax.net.ssl",
            "javax.print",
            "javax.print.attribute",
            "javax.print.attribute.standard",
            "javax.print.event",
            "javax.rmi",
            "javax.rmi.CORBA",
            "javax.security.auth",
            "javax.security.auth.callback",
            "javax.security.auth.kerberos",
            "javax.security.auth.login",
            "javax.security.auth.spi",
            "javax.security.auth.x500",
            "javax.security.cert",
            "javax.sound.midi",
            "javax.sound.midi.spi",
            "javax.sound.sampled",
            "javax.sound.sampled.spi",
            "javax.sql",
            "javax.swing",
            "javax.swing.border",
            "javax.swing.colorchooser",
            "javax.swing.event",
            "javax.swing.filechooser",
            "javax.swing.plaf",
            "javax.swing.plaf.basic",
            "javax.swing.plaf.metal",
            "javax.swing.plaf.multi",
            "javax.swing.table",
            "javax.swing.text",
            "javax.swing.text.html",
            "javax.swing.text.html.parser",
            "javax.swing.text.rtf",
            "javax.swing.tree",
            "javax.swing.undo",
            "javax.transaction",
            "javax.transaction.xa",
            "javax.xml.parsers",
            "javax.xml.transform",
            "javax.xml.transform.dom",
            "javax.xml.transform.sax",
            "javax.xml.transform.stream",
    };

    private final JList listOneList = new JList( listOneData );
    private final JScrollPane listOneScrollPane = new JScrollPane();
    private final JList listTwoList = new JList( listTwoData );
    private final JScrollPane listTwoScrollPane = new JScrollPane();

    public ListSplitPanePanel() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        setLayout( new BorderLayout() );
        setBorder( new EmptyBorder( 4, 4, 4, 4 ) );

        listOneScrollPane.setViewportView( listOneList );
        listTwoScrollPane.setViewportView( listTwoList );

        splitPane.setLeftComponent( listOneScrollPane );
        splitPane.setRightComponent( listTwoScrollPane );
        splitPane.setDividerLocation( 200 );

        add( splitPane, BorderLayout.CENTER );
    }

}
