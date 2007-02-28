import java.awt.*;
import java.awt.event.*;
import JSci.awt.*;
import JSci.maths.*;
import JSci.maths.polynomials.RealPolynomial;

/**
* Sample program demonstrating use of LinearMath.leastSquaresFit method
* and the LineTrace graph class.
* @author Mark Hale
* @version 1.0
*/
public final class CurveFitter extends Frame {
        private Label fnLabel = new Label("P(x) = ?",Label.CENTER);
        private LineTrace graph = new LineTrace(-10.0f,10.0f,-10.0f,10.0f);
        private TextField polyDegreeField = new TextField("4");
        private Button fitButton = new Button("Fit");
        private Button clearButton = new Button("Clear");

        public static void main(String arg[]) {
                new CurveFitter();
        }
        public CurveFitter() {
                super("Curve Fitter");
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dispose();
                                System.exit(0);
                        }
                });
                fitButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                fitCurve();
                        }
                });
                clearButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                graph.clear();
                        }
                });
                add(fnLabel,"North");
                add(graph,"Center");
                Panel buttonPanel=new Panel();
                buttonPanel.add(new Label("Polynomial degree:"));
                buttonPanel.add(polyDegreeField);
                buttonPanel.add(fitButton);
                buttonPanel.add(clearButton);
                add(buttonPanel,"South");
                setSize(500,400);
                setVisible(true);
        }
        private void fitCurve() {
                Graph2DModel model=graph.getModel();
                model.firstSeries();
                double data[][]=new double[2][model.seriesLength()];
                for(int i=0;i<data[0].length;i++) {
                        data[0][i]=model.getXCoord(i);
                        data[1][i]=model.getYCoord(i);
                }
                int degree = Integer.parseInt(polyDegreeField.getText());
                RealPolynomial poly = LinearMath.leastSquaresFit(degree, data);
                fnLabel.setText(poly.toString());
        }
}

