/*
 * Panel.java
 *
 * Created on Четвртак, 2004, Мај 13, 22.19
 */

package com.birosoft.liquid.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author  mikeai
 */
public class Panel extends javax.swing.JPanel {
    Image image;
    static Color buttonBg = new Color(215, 231, 249);
    static Color bg = new Color(246, 245, 244);
    
    
    /** Creates a new instance of Panel */
    public Panel(Image i) {
        image = i;
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(image, 10, 100, null);
        //drawIt(g, 10, 10, 48, 48, bg, bg);
    }
    
    private void drawIt(Graphics g, int x, int y, int w, int h, Color c, Color bg) {
        //g.setColor(Color.white);
        //g.fillRect(0, 0, this.getWidth(), this.getHeight());
        int x2 = x+w-1;
        int y2 = y+h-1;
        // outer dark rect
        g.setColor(Colors.dark(c,  115));
        g.drawLine(x+2, y, x2-2, y); // t
        g.drawLine(x, y+2, x, y2-2); // l
        g.drawLine(x+1, y+1, x+1, y+1); // tl
        g.setColor(Colors.dark(c, 150));
        g.drawLine(x+2, y2, x2-2, y2); // b
        g.drawLine(x2, y+2, x2, y2-2); // r
        g.drawLine(x2-1, y2-1, x2-1, y2-1); // br
        g.setColor(Colors.dark(c, 132));
        g.drawLine(x2-1, y+1, x2-1, y+1); // tr
        g.drawLine(x+1, y2-1, x+1, y2-1); // bl
        
        // inner top light lines
        g.setColor(Colors.light(c, 105));
        g.drawLine(x+2, y+1, x2-2, y+1);
        g.drawLine(x+1, y+2, x2-1, y+2);
        g.drawLine(x+1, y+3, x+2, y+3);
        g.drawLine(x2-2, y+3, x2-1, y+3);
        g.drawLine(x+1, y+4, x+1, y+4);
        g.drawLine(x2-1, y+4, x2-1, y+4);
        
        // inner bottom light lines
        g.setColor(Colors.light(c, 110));
        g.drawLine(x+2, y2-1, x2-2, y2-1);
        g.drawLine(x+1, y2-2, x2-1, y2-2);
        g.drawLine(x+1, y2-3, x+2, y2-3);
        g.drawLine(x2-2, y2-3, x2-1, y2-3);
        g.drawLine(x+1, y2-4, x+1, y2-4);
        g.drawLine(x2-1, y2-4, x2-1, y2-4);
        
        // inner left mid lines
        g.setColor(c);
        g.drawLine(x+1, y+5, x+1, y2-5);
        g.drawLine(x+2, y+4, x+2, y2-4);
        
        // inner right mid lines
        g.drawLine(x2-1, y+5, x2-1, y2-5);
        g.drawLine(x2-2, y+4, x2-2, y2-4);
        
        Graphics2D g2 = (Graphics2D)g;
        BufferedImage img = Colors.getClearFill();
        TexturePaint tp = new TexturePaint(img, new Rectangle2D.Float(0.0f,0.0f,img.getWidth(),img.getHeight()));
        Paint p=g2.getPaint();
        g2.setPaint(tp);
        g2.fillRect(x+2, y+2, w-4, h-4);
        g2.setPaint(p);
        int red, green, blue;
        Color btnColor = Colors.dark(c, 130);
        red = (btnColor.getRed() >> 1) + (bg.getRed() >> 1);
        green = (btnColor.getGreen() >> 1) + (bg.getGreen() >> 1);
        blue = (btnColor.getBlue() >> 1) + (bg.getBlue() >> 1);
        btnColor = new Color(red, green, blue);
        
        g.setColor(btnColor);
        g.drawLine(x+1, y, x+1, y);
        g.drawLine(x, y+1, x, y+1);
        g.drawLine(x+1, y2, x+1, y2);
        g.drawLine(x, y2-1, x, y2-1);
        
        g.drawLine(x2-1, y, x2-1, y);
        g.drawLine(x2, y+1, x2, y+1);
        g.drawLine(x2-1, y2, x2-1, y2);
        g.drawLine(x2, y2-1, x2, y2-1);
        
    }
    
}
