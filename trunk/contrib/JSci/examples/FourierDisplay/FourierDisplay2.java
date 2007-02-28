import java.awt.*;
import java.awt.event.*;
import JSci.awt.*;
import JSci.maths.*;
import JSci.tests.FourierTest;

/**
* Sample program demonstrating use of FourierMath and LineGraph classes.
* @author Mark Hale
* @version 1.0
*/
public final class FourierDisplay2 extends Frame {
        private final int N=128;
        private List fns=new List(4);
        private DefaultGraph2DModel signalModel=new DefaultGraph2DModel();
        private DefaultGraph2DModel transformModel=new DefaultGraph2DModel();
        private double signal[];

        public static void main(String arg[]) {
                new FourierDisplay2();
        }
        private static void setDefaultSize(Component c, int defaultWidth, int defaultHeight) {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                final int width = (defaultWidth < screenSize.width) ? defaultWidth : screenSize.width;
                final int height = (defaultHeight < screenSize.height) ? defaultHeight : screenSize.height;
                c.setSize(width, height);
        }
        public FourierDisplay2() {
                super("Fourier Display 2");
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dispose();
                                System.exit(0);
                        }
                });
                float xAxis[]=new float[N];
                for(int i=0;i<N;i++)
                        xAxis[i]=i-N/2;
                signalModel.setXAxis(xAxis);
                signalModel.addSeries(xAxis);
                transformModel.setXAxis(xAxis);
                transformModel.addSeries(xAxis);
                transformModel.addSeries(xAxis);
                fns.add("Gaussian");
                fns.add("Top hat");
                fns.add("Constant");
                fns.add("Square");
                fns.add("Triangle");
                fns.add("Sine");
                fns.select(5);
                fns.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent evt) {
                                switch(fns.getSelectedIndex()) {
                                        case 0 : signal = FourierTest.gaussian(N, 1.0, 5.0);
                                                break;
                                        case 1 : signal = FourierTest.topHat(N,1.0);
                                                break;
                                        case 2 : signal = FourierTest.constant(N,1.0);
                                                break;
                                        case 3 : signal = FourierTest.square(N,1.0);
                                                break;
                                        case 4 : signal = FourierTest.triangle(N,1.0);
                                                break;
                                        case 5 : signal = FourierTest.sine(N,1.0,16);
                                                break;
                                }
                                displaySignal();
                                displayTransform();
                        }
                });
                LineGraph signalGraph=new LineGraph(signalModel);
                signalGraph.setColor(0,Color.red);
                LineGraph transformGraph=new LineGraph(transformModel);
                transformGraph.setColor(0,Color.red);
                Panel graphs=new Panel();
                graphs.setLayout(new GridLayout(1,2));
                graphs.add(signalGraph);
                graphs.add(transformGraph);
                add(graphs,"Center");
                add(fns,"South");
                signal = FourierTest.sine(N, 1.0, 16);
                displaySignal();
                displayTransform();
                setDefaultSize(this, 600, 400);
                setVisible(true);
        }
        private void displaySignal() {
                signalModel.changeSeries(0,signal);
        }
        private void displayTransform() {
                Complex result[]=FourierMath.sort(FourierMath.transform(FourierMath.sort(signal)));
                float realpart[]=new float[N];
                float imagpart[]=new float[N];
                for(int i=0;i<N;i++) {
                        realpart[i]=(float)result[i].real();
                        imagpart[i]=(float)result[i].imag();
                }
                transformModel.changeSeries(0,realpart);
                transformModel.changeSeries(1,imagpart);
        }
}
