import java.applet.*;
import java.awt.*;

/**
* Displays JSci version information.
* @author Mark Hale
*/
public final class VersionApplet extends Applet {
        private String versionStr;
        private final Font H3 = new Font("H3", Font.BOLD, 20);

        public String getAppletInfo() {
                return "JSci Applet: written by Mark Hale";
        }
        public void init() {
                JSci.Version ver = JSci.Version.getCurrent();
                versionStr = "Version "+ver.toString()+" ("+ver.platform+")";
        }
        public void paint(Graphics g) {
                g.setColor(Color.white);
                g.fillRect(0, 0, getSize().width, getSize().height);
                g.setFont(H3);
                final int xpos=(getSize().width-g.getFontMetrics().stringWidth(versionStr))/2;
                g.setColor(Color.black);
                g.drawString(versionStr, xpos, 20);
        }
}

