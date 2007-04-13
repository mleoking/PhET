import java.awt.*;
import java.awt.event.*;
import JSci.awt.*;
import JSci.maths.*;
import JSci.maths.matrices.*;
import JSci.maths.vectors.*;

/**
* Wavefunction numerically solves the time-independent
* Schr&ouml;dinger equation.
* @author Mark Hale
* @version 1.2
*/
public final class Wavefunction extends Frame implements Runnable {
        private final Runnable animator=this;
        private final int N=200;
        private final LineGraph graph;
        private final GraphModel model=new GraphModel();
        private final Label kLabel=new Label("Harmonic coupling");
        private final TextField kField=new TextField("0.0",5);
        private final Label hLabel=new Label("Anharmonic coupling");
        private final TextField hField=new TextField("0.0",5);
        private final Button probButton=new Button("Probability");
        private final Button animButton=new Button("Evolve");
        private final Label statusLabel=new Label("-",Label.CENTER);
        private final Button incButton=new Button("+");
        private final Button decButton=new Button("-");
        private volatile Thread animateThread=null;
        /**
        * Eigenstates.
        */
        private final AbstractDoubleVector eigenstates[]=new AbstractDoubleVector[N];
        /**
        * Eigenvalues.
        */
        private double eigenvalues[];
        /**
        * Harmonic coupling.
        */
        private double k=0.0;
        /**
        * Anharmonic coupling.
        */
        private double h=0.0;
        /**
        * Kinetic energy operator.
        */
        private final DoubleTridiagonalMatrix T=new DoubleTridiagonalMatrix(N);
        /**
        * Potential energy operator.
        */
        private DoubleDiagonalMatrix V;
        /**
        * Runs an instance of Wavefunction.
        */
        public static void main(String arg[]) {
                new Wavefunction();
        }
        private static void setDefaultSize(Component c, int defaultWidth, int defaultHeight) {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                final int width = (defaultWidth < screenSize.width) ? defaultWidth : screenSize.width;
                final int height = (defaultHeight < screenSize.height) ? defaultHeight : screenSize.height;
                c.setSize(width, height);
        }
        /**
        * Constructs Wavefunction.
        */
        public Wavefunction() {
                super("Wavefunction");
                constructKineticEnergyOperator();
                calculateHamiltonian();
                graph=new LineGraph(model);
                graph.setYExtrema(-0.5f,0.5f);
// register listeners
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dispose();
                                System.exit(0);
                        }
                });
                incButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                model.incrementLevel();
                                updateStatusLabel();
                        }
                });
                decButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                model.decrementLevel();
                                updateStatusLabel();
                        }
                });
                kField.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                k=Double.valueOf(kField.getText()).doubleValue();
                                calculateHamiltonian();
                                updateStatusLabel();
                        }
                });
                hField.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                h=Double.valueOf(hField.getText()).doubleValue();
                                calculateHamiltonian();
                                updateStatusLabel();
                        }
                });
                probButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                if(model.isShowingProbability()) {
                                        model.showAmplitude();
                                        graph.setYExtrema(-0.5f,0.5f);
                                        probButton.setLabel("Probability");
                                } else {
                                        model.showProbability();
                                        graph.setYExtrema(0.0f,0.25f);
                                        probButton.setLabel("Amplitude");
                                }
                        }
                });
                animButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                if(animateThread==null) {
                                        animateThread=new Thread(animator);
                                        animateThread.start();
                                        animButton.setLabel("Freeze");
                                } else {
                                        animateThread=null;
                                        animButton.setLabel("Evolve");
                                }
                        }
                });
// construct gui
                Panel levelPanel=new Panel();
                levelPanel.setLayout(new GridLayout(2,1));
                levelPanel.add(incButton);
                levelPanel.add(decButton);
                Panel optPanel=new Panel();
                optPanel.add(kLabel);
                optPanel.add(kField);
                optPanel.add(hLabel);
                optPanel.add(hField);
                optPanel.add(probButton);
                optPanel.add(animButton);
                add(statusLabel,"North");
                add(graph,"Center");
                add(levelPanel,"East");
                add(optPanel,"South");
                setDefaultSize(this, 650, 450);
                updateStatusLabel();
                setVisible(true);
        }
        public void run() {
                while(animateThread==Thread.currentThread()) {
                        EventQueue.invokeLater(new Runnable() {
                                public void run() {model.evolve();}
                        });
                        try {
                                Thread.sleep(100);
                        } catch(InterruptedException e) {}
                }
        }
        /**
        * Updates the status label.
        */
        private void updateStatusLabel() {
                final int level=model.getLevel();
                statusLabel.setText("Energy ["+level+"] = "+(float)eigenvalues[level]);
        }

        /**
        * A custom graph model class.
        * This provides a graph interface onto the eigenstates
        * and eigenvalues.
        * There are various methods to alter the data view.
        */
        private final class GraphModel extends AbstractGraphModel implements Graph2DModel {
                /**
                * Time.
                */
                private double t=0.0;
                /**
                * Energy level.
                */
                private int level=0;
                /**
                * Show probability or amplitude.
                */
                private boolean showProb=false;
                /**
                * Series.
                */
                private final static int SERIES_WAVEFUNCTION=0;
                private final static int SERIES_POTENTIAL=1;
                private int series=SERIES_WAVEFUNCTION;

                public GraphModel() {}
                public float getXCoord(int i) {
                        return i*2.0f/(N-1)-1.0f;
                }
                public float getYCoord(int i) {
                        if(series==SERIES_WAVEFUNCTION) {
                                final double amp=eigenstates[level].getComponent(i);
                                if(showProb)
                                        return (float)(amp*amp);
                                else
                                        return (float)(amp*Math.cos(eigenvalues[level]*t));
                        } else if(series==SERIES_POTENTIAL)
                                return (float)(V.getElement(i,i)-eigenvalues[level]);
                        else
                                return 0.0f;
                }
                public void resetTime() {
                        t=0.0;
                        fireGraphSeriesUpdated(SERIES_WAVEFUNCTION);
                }
                public void evolve() {
                        t+=2.0;
                        fireGraphSeriesUpdated(SERIES_WAVEFUNCTION);
                }
                public void incrementLevel() {
                        if(level<N-1)
                                level++;
                        fireGraphSeriesUpdated(series);
                }
                public void decrementLevel() {
                        if(level>0)
                                level--;
                        fireGraphSeriesUpdated(series);
                }
                public int getLevel() {
                        return level;
                }
                public void showAmplitude() {
                        showProb=false;
                        fireGraphSeriesUpdated(SERIES_WAVEFUNCTION);
                }
                public void showProbability() {
                        showProb=true;
                        fireGraphSeriesUpdated(SERIES_WAVEFUNCTION);
                }
                public boolean isShowingAmplitude() {
                        return !showProb;
                }
                public boolean isShowingProbability() {
                        return showProb;
                }
                public int seriesLength() {
                        return N;
                }
                public void firstSeries() {
                        series=SERIES_WAVEFUNCTION;
                }
                public boolean nextSeries() {
                        series++;
                        if(series>1)
                                return false;
                        else
                                return true;
                }
        }

// Solve the Schrodinger equation

        /**
        * Constructs the kinetic energy operator.
        */
        private void constructKineticEnergyOperator() {
                T.setElement(0,0,2.0);
                T.setElement(0,1,-1.0);
                int i=1;
                for(;i<N-1;i++) {
                        T.setElement(i,i-1,-1.0);
                        T.setElement(i,i,2.0);
                        T.setElement(i,i+1,-1.0);
                }
                T.setElement(i,i-1,-1.0);
                T.setElement(i,i,2.0);
        }
        /**
        * Constructs the Hamiltonian
        * and solves the Schr&ouml;dinger equation.
        */
        private void calculateHamiltonian() {
                // potential energy operator
                final double pot[]=new double[N];
                for(int i=0;i<N;i++)
                        pot[i]=potential(i);
                V=new DoubleDiagonalMatrix(pot);
                // Hamiltonian
                final AbstractDoubleSquareMatrix H=T.add(V);
                // solve
                try {
                        eigenvalues=LinearMath.eigenSolveSymmetric(H,eigenstates);
                } catch(MaximumIterationsExceededException e) {
                        System.err.println(e.getMessage());
                        System.exit(-1);
                }
                sortStates();
                model.resetTime();
        }
        /**
        * Potential energy function.
        */
        private double potential(int i) {
                final double x=model.getXCoord(i);
                return k*x*x+h*x*x*x*x;
        }
        /**
        * Sorts the eigenstates and eigenvalues.
        * (Bidirectional bubble sort.)
        */
        private void sortStates() {
                int i;
                int limit=eigenstates.length;
                int st=-1;
                AbstractDoubleVector vec;
                double val;
                while(st<limit) {
                        boolean flipped=false;
                        st++;
                        limit--;
                        for(i=st;i<limit;i++) {
                                if(eigenvalues[i]>eigenvalues[i+1]) {
                                        vec=eigenstates[i];
                                        val=eigenvalues[i];
                                        eigenstates[i]=eigenstates[i+1];
                                        eigenvalues[i]=eigenvalues[i+1];
                                        eigenstates[i+1]=vec;
                                        eigenvalues[i+1]=val;
                                        flipped=true;
                                }
                        }
                        if(!flipped)
                                return;
                        for(i=limit;--i>=st;) {
                                if(eigenvalues[i]>eigenvalues[i+1]) {
                                        vec=eigenstates[i];
                                        val=eigenvalues[i];
                                        eigenstates[i]=eigenstates[i+1];
                                        eigenvalues[i]=eigenvalues[i+1];
                                        eigenstates[i+1]=vec;
                                        eigenvalues[i+1]=val;
                                        flipped=true;
                                }
                        }
                        if(!flipped)
                                return;
                }
        }
}

