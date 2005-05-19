package phet.paint;

import javax.swing.*;
import java.awt.*;

public class PainterPanel extends JPanel {
    Painter p;

    public PainterPanel( Painter p ) {
        this.p = p;
    }

    public void setPainter( Painter p ) {
        this.p = p;
    }

    public void paintComponent( Graphics g ) {
        super.paintComponent( g );
        if( p != null ) {
            Graphics2D g2 = (Graphics2D)g;
            p.paint( g2 );
        }
    }
}
