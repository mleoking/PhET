/**
 * Class: OnScreenHelpItemOld
 * Package: edu.colorado.phet.graphics.graphics
 * User: Ron LeMaster
 * Date: Feb 4, 2003
 * Time: 10:13:05 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.common.view.help;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Class for on-screen help. An icon appears on the screen, and if the mouse is left
 * for a moment on the icon, text is displayed.
 * <p>
 * Line breaks can be specified in the text with the standard "\n" escape character,
 * or by using the same HTML tags that are used for Swing tools tips.
 */
public class HelpItem extends JLabel {

    private Color color;

    public HelpItem(String text, int x, int y) {
        this(text, new Point2D.Float(x, y), Color.BLACK);
    }

    public HelpItem(String text, int x, int y, Color color) {
        this(text, new Point2D.Float(x, y), color);
    }

    public HelpItem(String text, Point2D.Float location) {
        this(text, location, Color.BLACK);
    }

    public HelpItem(String text, Point2D.Float location, Color color) {
        this.setIcon(new ImageIcon(s_helpItemIcon));

        String displayString = text;
        if (text.indexOf("\n") != -1) {
            displayString = xformText(text);
        }
        setToolTipText(displayString);
        setLocation(new Point((int) location.getX(), (int) location.getY()));
        setColor(color);
        this.setBounds((int) location.getX(), (int) location.getY(),
                s_helpItemIcon.getWidth(this), s_helpItemIcon.getHeight(this));
    }

//    public void setLocation( int x, int y ) {
//        this.setBounds((int) location.getX(), (int) location.getY(),
//                s_helpItemIcon.getWidth(this), s_helpItemIcon.getHeight(this));
//    }
    /**
     *
     * @param inString
     * @return
     */
    private String xformText(String inString) {
        StringBuffer outString = new StringBuffer("<html>");
        int lastIdx = 0;
        for (int nextIdx = inString.indexOf("\n", lastIdx);
             nextIdx != -1;
             nextIdx = inString.indexOf("\n", lastIdx)) {
            outString.append(inString.substring(lastIdx, nextIdx));
            if (nextIdx < inString.length()) {
                outString.append("<br>");
            }
            lastIdx = nextIdx + 1;
        }
        outString.append(inString.substring(lastIdx, inString.length()));
        outString.append("</html>");
        return outString.toString();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    //
    // Static fields and methods
    //
    public static final String IMAGE_DIRECTORY = "images/";
    public static final String HELP_ITEM_ICON_IMAGE_FILE = IMAGE_DIRECTORY + "help-item-icon.gif";

    private static Image s_helpItemIcon;
    private static ImageLoader s_loader;

    static {
        s_loader = new ImageLoader();
        s_helpItemIcon = s_loader.loadImage(HELP_ITEM_ICON_IMAGE_FILE);
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }
}
