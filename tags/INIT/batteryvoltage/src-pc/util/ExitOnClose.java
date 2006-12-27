package util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jan 17, 2003
 * Time: 7:53:58 PM
 * To change this template use Options | File Templates.
 */
public class ExitOnClose extends WindowAdapter {
    public ExitOnClose() {
    }

    public void windowClosing( WindowEvent e ) {
        System.exit( 0 );
    }
}
