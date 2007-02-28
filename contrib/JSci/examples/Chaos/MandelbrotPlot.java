import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.MemoryImageSource;
import JSci.maths.Complex;
import JSci.maths.chaos.*;

/**
* Plot of the Mandelbrot set.
* @author Mark Hale
* @version 1.1
*/
public final class MandelbrotPlot extends Applet {
        private final int colorTable[]=new int[22];
        private final int range[]={1,10,20,30,40,50,60,70,80,90,100,200,300,400,500,600,700,800,900,1000};
        private final int N=2000;
        private Image mandelbrotImage;
        private Image imageBuffer;

        /**
        * Initialise the applet.
        */
        public void init() {
                // create colour palette
                for(int i=0;i<colorTable.length;i++)
                        colorTable[i]=grey(255-10*i);
                // draw Mandelbrot set to an image buffer
                mandelbrotImage=drawMandelbrot();
                // mouse listener
                addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent evt) {
                                if(imageBuffer==mandelbrotImage) {
                                        final int width=getSize().width;
                                        final int height=getSize().height;
                                        final Complex z=new Complex(evt.getX()*3.0/width-2.0,1.25-evt.getY()*2.5/height);
                                        imageBuffer=drawJulia(z);
                                        System.err.println(z.toString());
                                        showStatus("Click to return to Mandelbrot set");
                                } else {
                                        imageBuffer=mandelbrotImage;
                                        showStatus("Click to generate a Julia set");
                                }
                                repaint();
                        }
                });
        }
        public void start() {
                imageBuffer=mandelbrotImage;
                showStatus("Click to generate a Julia set");
        }
        public void paint(Graphics g) {
                g.drawImage(imageBuffer,0,0,this);
        }
        private Image drawMandelbrot() {
                final int width=getSize().width;
                final int height=getSize().height;
                final MandelbrotSet set=new MandelbrotSet();
                double x,y;
                int pixels[]=new int[width*height];
                int index=0;
                for(int j=0;j<height;j++) {
                        for(int i=0;i<width;i++) {
                                x=i*3.0/width-2.0;
                                y=1.25-j*2.5/height;
                                pixels[index++]=colorLookup(set.isMember(x,y,N));
                        }
                }
                return createImage(new MemoryImageSource(width,height,pixels,0,width));
        }
        private Image drawJulia(Complex z) {
                final int width=getSize().width;
                final int height=getSize().height;
                final JuliaSet set=new JuliaSet(z);
                double x,y;
                int pixels[]=new int[width*height];
                int index=0;
                for(int j=0;j<height;j++) {
                        for(int i=0;i<width;i++) {
                                x=i*4.0/width-2.0;
                                y=1.25-j*2.5/height;
                                pixels[index++]=colorLookup(set.isMember(x,y,N));
                        }
                }
                return createImage(new MemoryImageSource(width,height,pixels,0,width));
        }
        /**
        * Returns a RGBA value.
        */
        private int colorLookup(int n) {
                if(n==0) {
                        return 0xff000000;   // black
                } else {
                        for(int i=0;i<range.length-1;i++) {
                                if(n>=range[i] && n<range[i+1])
                                        return colorTable[i];
                        }
                        return 0xffffffff; // white
                }
        }
        /**
        * Returns a grey RGBA value.
        */
        private static int grey(int n) {
                return 0xff000000 | n<<16 | n<<8 | n;
        }
}

