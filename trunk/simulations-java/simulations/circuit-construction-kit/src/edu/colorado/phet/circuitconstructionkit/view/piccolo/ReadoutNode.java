package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.*;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * User: Sam Reid
 * Date: Jun 2, 2004
 * Time: 3:17:14 PM
 */
public class ReadoutNode extends PhetPNode {
    private ShadowHTMLNode htmlNode;
    protected CCKModule module;
    protected Branch branch;
    private JComponent panel;
    protected DecimalFormat formatter;
    private PPath linePNode;//This is a dotted line attaching the text to the branch

    static Font font = new PhetFont(Font.BOLD, 16);

    public ReadoutNode(CCKModule module, Branch branch, JComponent panel, DecimalFormat formatter) {
        this.module = module;
        this.branch = branch;
        this.panel = panel;
        this.formatter = formatter;

        htmlNode = new ShadowHTMLNode("");
        htmlNode.setFont(font);
        Color foregroundColor = Color.black;
        Color backgroundColor = Color.yellow;
        htmlNode.setPaint(foregroundColor);
        htmlNode.setShadowColor(backgroundColor);
        htmlNode.setShadowOffset(1, 1);
        htmlNode.setScale(1.0 / 80.0);

        branch.addObserver(new SimpleObserver() {
            public void update() {
                recompute();
            }
        });
        addChild(htmlNode);
        setPickable(false);
        setChildrenPickable(false);

        linePNode = new PhetPPath(new BasicStroke(1.0f / 60.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f / 80.0f, new float[]{5.0f / 60.0f, 5.0f / 60.0f}, 0), Color.blue);
        addChild(linePNode);

        recompute();
    }

    public void recompute() {
        htmlNode.setHtml(toHTML(getText()));
        Shape shape = branch.getShape();
        Point2D pt = new Point2D.Double(shape.getBounds2D().getCenterX() - htmlNode.getFullBounds().getWidth() / 2,
                shape.getBounds2D().getY() - htmlNode.getFullBounds().getHeight());
        if (isVertical()) {
            pt = new Point2D.Double(shape.getBounds2D().getMaxX(), shape.getBounds2D().getCenterY() - htmlNode.getFullBounds().getHeight() / 2);
        }
        htmlNode.setOffset(pt);
        Point2D ctr = new Point2D.Double(shape.getBounds2D().getCenterX(), shape.getBounds2D().getCenterY());
        double distToCenter = pt.distance(ctr);
        linePNode.setVisible(distToCenter > 1.0);
        Point2D textSource = new Point2D.Double(htmlNode.getFullBounds().getCenterX(), htmlNode.getFullBounds().getMaxY());
        if (isVertical()) {
            textSource = new Point2D.Double(htmlNode.getFullBounds().getX(), htmlNode.getFullBounds().getCenterY());
        }
        linePNode.setPathTo(new Line2D.Double(textSource, ctr));
    }

    private boolean isVertical() {
        double angle = branch.getAngle();
        while (angle < 0) {
            angle += Math.PI * 2;
        }
        while (angle > Math.PI * 2) {
            angle -= Math.PI * 2;
        }
//        System.out.println( "angle = " + angle );

        boolean up = angle > Math.PI / 4 && angle < 3.0 / 4.0 * Math.PI;
        boolean down = angle > 5.0 / 4.0 * Math.PI && angle < 7.0 / 4.0 * Math.PI;
        return up || down;
    }

    private String toHTML(String[] text) {
        String html = "<html>";
        for (int i = 0; i < text.length; i++) {
            html += text[i];
            if (i < text.length - 1) {
                html += "<br>";
            }
        }
        return html + "</html>";
    }

    public DecimalFormat getFormatter() {
        return formatter;
    }

    protected String[] getText() {
        if (branch instanceof Battery) {
            return getBatteryText();
        } else if (branch instanceof Capacitor) {
            return getCapacitorText((Capacitor) branch);
        } else if (branch instanceof Inductor) {
            return getInductorText((Inductor) branch);
        }
        double r = branch.getResistance();
        if (branch instanceof Switch) {
            Switch swit = (Switch) branch;
            if (!swit.isClosed()) {
                r = Double.POSITIVE_INFINITY;
            }
        }
        String res = formatter.format(r);
        String cur = formatter.format(Math.abs(branch.getCurrent()));
        String vol = formatter.format(Math.abs(branch.getVoltageDrop()));
        res = abs(res);
        cur = abs(cur);
        vol = abs(vol);

        String text = res + " " + CCKResources.getString("ReadoutGraphic.Ohms");
        return new String[]{text};
    }

    private String[] getInductorText(Inductor inductor) {
        return new String[]{formatter.format(inductor.getInductance()) + " " + CCKResources.getString("ReadoutGraphic.Henries")};
    }

    private String[] getCapacitorText(Capacitor capacitor) {
        return new String[]{formatter.format(capacitor.getCapacitance()) + " " + CCKResources.getString("ReadoutGraphic.Farads")};
    }

    private String[] getBatteryText() {
        boolean internal = CCKModel.INTERNAL_RESISTANCE_ON;
        double volts = Math.abs(branch.getVoltageDrop());
        String vol = formatter.format(volts);
        String str = "" + vol + " " + CCKResources.getString("ReadoutGraphic.Volts");
        ArrayList text = new ArrayList();
        text.add(str);
        if (internal) {
            String s2 = formatter.format(branch.getResistance()) + " " + CCKResources.getString("ReadoutGraphic.Ohms");
            text.add(s2);
        }
        return (String[]) text.toArray(new String[0]);
    }

    protected String abs(String vol) {
        StringTokenizer st = new StringTokenizer(vol, ".0");
        if (st.hasMoreTokens() && st.nextToken().equals("-")) {
            return vol.substring(1);
        } else {
            return vol;
        }
    }

    public Branch getBranch() {
        return branch;
    }

    public String format(double amplitude) {
        return abs(formatter.format(amplitude));
    }

    public void update() {
        setVisible(branch.isEditing());
    }
}
