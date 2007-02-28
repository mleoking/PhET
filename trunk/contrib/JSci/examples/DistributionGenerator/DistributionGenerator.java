import java.awt.*;
import java.awt.event.*;
import JSci.awt.*;
import JSci.maths.statistics.*;

/**
* DistributionGenerator generates random numbers from a probability distribution.
* @author Mark Hale
* @version 1.1
*/
public final class DistributionGenerator extends Frame implements Runnable {
        private final double mean;
        private final double width;
        private final ProbabilityDistribution dist;
        private final HistogramModel model;

        public static void main(String arg[]) {
        // set distribution here
                DistributionGenerator app=new DistributionGenerator(new CauchyDistribution(), 0.0, 10.0);
//                DistributionGenerator app=new DistributionGenerator(new PoissonDistribution(50.0), 50.0, 100.0);
                Thread thr=new Thread(app);
                thr.setPriority(Thread.MIN_PRIORITY);
                thr.start();
        }
        /**
        * Constructs a distribution generator.
        * @param pd a probability distribution
        * @param m the mean of the sampling region
        * @param w the width of the sampling region
        */
        public DistributionGenerator(ProbabilityDistribution pd, double m, double w) {
                super("Distribution Generator");
                dist=pd;
                mean=m;
                width=w;
                model=new HistogramModel();
                ScatterGraph graph=new ScatterGraph(model);
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dispose();
                                System.exit(0);
                        }
                });
                add(graph);
                setSize(300,300);
                setVisible(true);
        }
        public void run() {
                for(int i=0;i<10000;i++)
                        model.add(random());
        }
        /**
        * Returns a random number from the distribution.
        * Uses the rejection method.
        */
        public double random() {
        /*
         * We use the uniform function f(x)=1 to be totally general.
         * For greater efficiency use a function f(x) which roughly
         * over-approximates the required distribution.
         */
                double x,y;
                do {
                /*
                 * random x coordinate:
                 * x is such that int(f(s),min,x,ds) = Math.random()*int(f(s),min,max,ds)
                 */
                // uniform comparison function
                        x=mean+width*(Math.random()-0.5);
                // Lorentzian comparison function
                        // x=mean+Math.tan(Math.PI*Math.random());
                // uncomment line below if using a discrete distribution
                        // x=Math.floor(x);
                /*
                 * random y coordinate:
                 * y = Math.random()*f(x)
                 */
                // uniform comparison function
                        y=Math.random();
                // Lorentzian comparison function
                        // y=Math.random()/(1.0+(x-mean)*(x-mean));
                } while(y>dist.probability(x));
                return x;
        }
        private class HistogramModel extends AbstractGraphModel implements Graph2DModel {
                private final int histogram[]=new int[101];
                private final double binwidth=width/(histogram.length-1);

                public HistogramModel() {}
                public float getXCoord(int i) {
                        return (float)(binwidth*(i-(histogram.length-1)/2.0)+mean);
                }
                public float getYCoord(int i) {
                        return histogram[i];
                }
                public int seriesLength() {
                        return histogram.length;
                }
                public void firstSeries() {}
                public boolean nextSeries() {
                        return false;
                }
                public void add(double x) {
                        int bin=(int)((x-mean)/binwidth)+(histogram.length-1)/2;
                        if(bin>=0 && bin<histogram.length) {
                                histogram[bin]++;
                                fireGraphSeriesUpdated(0);
                        }
                }
        }
}

