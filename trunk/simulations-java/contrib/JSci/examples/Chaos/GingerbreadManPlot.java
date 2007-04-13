import java.applet.*;
import java.awt.*;
import JSci.awt.*;
import JSci.maths.chaos.*;

/**
* Plot of the Gingerbread Man.
* @author Mark Hale
* @version 1.1
*/
public final class GingerbreadManPlot extends Applet {
        private GingerbreadManMap cm;
        private ScatterGraph graph;
        private final int N=10000;
        public void init() {
                cm=new GingerbreadManMap();
                float xData[]=new float[N];
                float yData[]=new float[N];
                double x[]=cm.X_CHAOS;
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

