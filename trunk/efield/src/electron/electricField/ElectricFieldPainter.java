package electron.electricField;

import electron.gui.Painter;
import electron.gui.vectorChooser.VectorPainter;
import phys2d.DoublePoint;

import java.awt.*;
import java.util.Vector;

public class ElectricFieldPainter implements Painter {
    Vector sources;
    Rectangle r;
    int nX;
    int nY;
    VectorPainter vp;

    public ElectricFieldPainter(int x, int y, int width, int height, int nX, int nY, VectorPainter vp) {
        this.vp = vp;
        sources = new Vector();
        this.r = new Rectangle(x, y, width, height);
        this.nX = nX;
        this.nY = nY;
    }

    public void setNX(int nX) {
        this.nX = nX;
    }

    public void setNY(int ny) {
        this.nY = ny;
    }

    public void addSource(ElectricFieldSource efs) {
        sources.add(efs);
    }

    private DoublePoint fieldAt(double x, double y) {
        DoublePoint field = new DoublePoint();
        for (int i = 0; i < sources.size(); i++) {
            ElectricFieldSource efs = (ElectricFieldSource) sources.get(i);
            DoublePoint term = efs.getField(x, y);
            field = field.add(term);
        }
        return field;
    }

    public void paint(Graphics2D g) {
        double dx = r.width / ((double) nX);
        double dy = r.height / ((double) nY);
        double x = r.x;
        double y = r.y;
        for (int i = 0; i < nX; i++) {
            for (int j = 0; j < nY; j++) {
                DoublePoint f = fieldAt(x, y);
                vp.paint(g, (int) x, (int) y, (int) f.getX(), (int) f.getY());
                y += dy;
            }
            x += dx;
            y = r.y;
        }
    }
}
