import java.awt.*;
import java.awt.event.*;
import JSci.awt.*;
import JSci.maths.wavelet.*;
import JSci.maths.wavelet.cdf2_4.*;

/**
* Launch the following code on an image having odd dimensions
* and you'll have a visual example.
* @author Daniel Lemire
*/
public final class WaveletImageTransform extends Frame {
        public static void main(String[] arg) {
                if(arg.length==0) {
                        System.err.println("Please specify an image with odd dimensions.");
                        return;
                }
                new WaveletImageTransform(arg[0]);
        }
        public WaveletImageTransform(String filename) {
                super("Fast Wavelet Transform");
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dispose();
                                System.exit(0);
                        }
                });

                System.err.println("Opening \""+filename+"\"");
                PixelArray pa1=new PixelArray(filename);
                ImageCanvas ic1=new ImageCanvas(pa1.rebuildImage());
                System.err.println("Please wait... This could take a few minutes...");

                PixelArray[][] paa=pa1.greenFWT(new CDF2_4());

                ImageCanvas ic0_0=new ImageCanvas(paa[0][0].rebuildImage());

                ImageCanvas ic1_0=new ImageCanvas(paa[1][0].rebuildImage());
                ImageCanvas ic0_1=new ImageCanvas(paa[0][1].rebuildImage());
                ImageCanvas ic1_1=new ImageCanvas(paa[1][1].rebuildImage());

                setLayout(new GridLayout(2,0,2,2));
                add(ic1);
                Panel p2=new Panel();
                p2.setLayout(new GridLayout(0,2,1,1));
                p2.add(ic0_0);
                p2.add(ic1_0);
                p2.add(ic0_1);
                p2.add(ic1_1);
                add(p2);
                setBounds(0,0,400,400);
                show();
        }
}

