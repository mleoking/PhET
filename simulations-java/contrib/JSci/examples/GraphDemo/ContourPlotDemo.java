import java.awt.*;
import java.awt.event.*;
import JSci.awt.*;
import JSci.swing.*;

/**
* Sample program demonstrating use of the Swing/AWT contour plot component.
* @author Mark Hale
* @version 1.0
*/
public class ContourPlotDemo extends Frame {
        public static void main(String arg[]) {
                new ContourPlotDemo();
        }
        public ContourPlotDemo() {
                super("JSci Contour Plot Demo");
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dispose();
                                System.exit(0);
                        }
                });
                setSize(250,250);
                add(new ContourPlot(createContourData()));
                setVisible(true);
        }
        private static double[][] createContourData() {
                double data[][]=new double[50][50];
                double x,y;
                for(int i=0;i<data.length;i++) {
                        for(int j=0;j<data[0].length;j++) {
                                x=(i-data.length/2.0)*3.0/data.length;
                                y=(j-data[0].length/2.0)*3.0/data[0].length;
                                data[i][j]=Math.exp(-x*x-y*y);
                        }
                }
                return data;
        }
}

