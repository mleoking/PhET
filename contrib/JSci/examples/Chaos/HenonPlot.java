import java.applet.*;
import java.awt.*;
import JSci.awt.*;
import JSci.maths.chaos.*;

/**
* Plot of the Henon map.
* @author Mark Hale
* @version 1.1
*/
public final class HenonPlot extends Applet {
        private HenonMap cm;
        private ScatterGraph graph;
        private final int N=10000;
        public void init() {
                cm=new HenonMap(cm.A_CHAOS,cm.B_CHAOS);
                float xData[]=new float[N];
                float yData[]=new float[N];
                double x[]={0.0,0.0};
                for(int i=0;i<N;i++) {
                        xData[i]=(float)x[0];
                        yData[i]=(float)x[1];
                        x=cm.map(x);
                }
                DefaultGraph2DModel model=new DefaultGraph2DModel();
                model.setXAxis(xData);
                model.addSeries(yData);
                graph=new ScatterGraph(model);
                graph.setNumbering(false);
                setLayout(new BorderLayout());
                add(graph,"Center");
        }
}

