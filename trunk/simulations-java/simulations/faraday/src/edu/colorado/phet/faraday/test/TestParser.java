package edu.colorado.phet.faraday.test;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * @author Sam Reid
 */
public class TestParser {
    public static void main(String[] args) throws IOException {
        double[] allocatedArray = new double[1000000 * 4];
        System.out.println("Allocated array of length " + allocatedArray.length);
        long startTime = System.currentTimeMillis();
        FileReader fileReader = new FileReader("C:/Users/Sam/Desktop/lines.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
//        ArrayList<String> lines = new ArrayList<String>();
        String line = bufferedReader.readLine();
        int index = 0;
        while (line != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(line, "\t");
            while (stringTokenizer.hasMoreTokens()) {
                String token = stringTokenizer.nextToken();
                allocatedArray[index] = Double.parseDouble(token);
                index++;
            }
//            lines.add(line);
            line = bufferedReader.readLine();

        }
//        System.out.println("lines.size() = " + lines.size());
        final double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
        System.out.println("elapsed time (sec) = " + elapsed);
//        System.out.println("time per point = " + elapsed / lines.size());
    }

    public static class Generator {
        public static void main(String[] args) throws IOException {
            FileWriter fileWriter = new FileWriter("C:/Users/Sam/Desktop/lines.txt");
            Random random = new Random();
            System.out.println("x\ty\tbx\tby");
            for (int i = 0; i < 1000000; i++) {
                String line = nextDouble(random) + "\t" + nextDouble(random) + "\t" + nextDouble(random) + "\t" + nextDouble(random);
//            System.out.println(line);
                fileWriter.append(line + "\n");
            }
            fileWriter.close();
        }
    }

    static DecimalFormat format = new DecimalFormat("1.00E00");

    private static String nextDouble(Random random) {
        return format.format(random.nextDouble());
    }
}
