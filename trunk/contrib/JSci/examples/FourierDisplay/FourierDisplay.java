import java.awt.*;
import java.awt.event.*;
import JSci.awt.*;
import JSci.maths.*;
import JSci.tests.FourierTest;

/**
* Sample program demonstrating use of FourierMath and LineGraph classes.
* @author Mark Hale
* @version 1.3
*/
public final class FourierDisplay extends Frame {
        private final int N=128;
        private List fns=new List(4);
        private Checkbox inverse=new Checkbox("inverse");
        private DefaultGraph2DModel model=new DefaultGraph2DModel();
        private double signal[];
        private boolean doInverse=false;

        public static void main(String arg[]) {
                new FourierDisplay();
        }
        private static void setDefaultSize(Component c, int defaultWidth, int defaultHeight) {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                final int width = (defaultWidth < screenSize.width) ? defaultWidth : screenSize.width;
                final int height = (defaultHeight < screenSize.height) ? defaultHeight : screenSize.height;
                c.setSize(width, height);
        }
        public FourierDisplay() {
                super("Fourier Display");
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dispose();
                                System.exit(0);
                        }
                });
                float xAxis[]=new float[N];
                for(int i=0;i<N;i++)
                        xAxis[i]=i-N/2;
                model.setXAxis(xAxis);
                model.addSeries(xAxis);
                model.addSeries(xAxis);
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
                                        case 0 : signal = FourierTest.gaussian(N,1.0,5.0);
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
                                displayTransform();
                        }
                });
                inverse.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent evt) {
                                doInverse=!doInverse;
                                displayTransform();
                        }
                });
                LineGraph graph=new LineGraph(model);
                graph.setColor(0,Color.red);
                Panel cntrl=new Panel();
                cntrl.add(fns);
                cntrl.add(inverse);
                add(graph,"Center");
                add(cntrl,"South");
                signal = FourierTest.sine(N, 1.0, 16);
                displayTransform();
                setDefaultSize(this, 400, 400);
                setVisible(true);
        }
        private void displayTransform() {
                Complex result[];
                if(doInverse)
                        result=FourierMath.sort(FourierMath.inverseTransform(FourierMath.sort(signal)));
                else
                        result=FourierMath.sort(FourierMath.transform(FourierMath.sort(signal)));
                float realpart[]=new float[N];
                float imagpart[]=new float[N];
                for(int i=0;i<N;i++) {
                        realpart[i]=(float)result[i].real();
                        imagpart[i]=(float)result[i].imag();
                }
                model.changeSeries(0,realpart);
                model.changeSeries(1,imagpart);
        }
}
