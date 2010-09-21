package edu.colorado.phet.common.motion.tests;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.motion.MotionResources;

/**
 * Author: Sam Reid
 * Aug 6, 2007, 2:41:56 PM
 */
public class ColorArrows {

    /**
     * Takes a template button (based on blue color) and makes it match the specified color.
     *
     * @param image
     * @param color
     * @return
     */
    public static BufferedImage filter(BufferedImage image, Color color) {
        LookupOp lookupOp = new LookupOp(new Table(color), null);
        return lookupOp.filter(image, null);
    }

    /**
     * Creates an arrow image with the specified color.  See the template for details.
     *
     * @param color
     * @return
     */
    public static BufferedImage createArrow(Color color) {
        try {
            return filter(MotionResources.loadBufferedImage("arrow-template.png"), color);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static class Table extends LookupTable {
        private int[] replacement;
        private int[] template = new int[]{79, 140, 232, 255};

        public Table(Color color) {
            super(0, 4);
            this.replacement = new int[]{color.getRed(), color.getGreen(), color.getBlue(), 255};
        }

        public int[] lookupPixel(int[] src, int[] dst) {
            System.arraycopy(getReplacement(src), 0, dst, 0, dst.length);
            return dst;
        }

        private int[] getReplacement(int[] src) {
            int[] diff = diffRGB(src, template);
            int diffSum = diff[0] + diff[1] + diff[2];
//            System.out.println( "diffSum = " + diffSum );
//            int[] ints = new int[]{replacement[0] + diff[0], replacement[1] + diff[1], replacement[2] + diff[2], src[3]};
            int[] ints = new int[]{replacement[0] + diffSum, replacement[1] + diffSum, replacement[2] + diffSum, src[3]};
//            System.out.println( "ints = " + ints + " = " + ints[0] + ", " + ints[1] + ", " + ints[2] );
            for (int i = 0; i < ints.length; i++) {
                ints[i] = Math.min(ints[i], 255);
                ints[i] = Math.max(ints[i], 0);
            }
            return ints;
        }

        private int[] diffRGB(int[] src, int[] template) {
            int[] diff = new int[3];
            for (int i = 0; i < diff.length; i++) {
                diff[i] = src[i] - template[i];
            }
            return diff;
        }

        private double distRGB(int[] a, int[] b) {
            double sum = 0.0;
            for (int i = 0; i < a.length; i++) {
                int term = a[0] - b[0];
                sum += term * term;
            }
            return Math.sqrt(sum);
        }

    }

    public static void main(String[] args) throws IOException {
        BufferedImage image = MotionResources.loadBufferedImage("blue-arrow.png");
        JFrame frame = new JFrame("Test Duotone Image Op");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.getContentPane().add(new JLabel(new ImageIcon(createArrow(Color.green))));
        frame.getContentPane().add(new JLabel(new ImageIcon(createArrow(Color.red))));
        frame.getContentPane().add(new JLabel(new ImageIcon(createArrow(Color.blue))));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.show();
    }
}
