package hhmodel.src.fodor.anthony.hhapplet.reid;

import hhmodel.src.fodor.anthony.hhapplet.Model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class TestFodor {
    public static void main(String[] args) {
        final Model model = new Model(null);

        XYSeriesCollection dataset = new XYSeriesCollection();
        final XYSeries series = new XYSeries("series1");
        dataset.addSeries(series);
        JFreeChart charte = ChartFactory.createScatterPlot("title", "time", "volts", dataset, PlotOrientation.VERTICAL, true, true, true);

        ChartFrame chartFrame = new ChartFrame("chart", charte);
        chartFrame.setSize(1024, 768);
        chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chartFrame.setVisible(true);

        JPanel controlPanel = new JPanel();
        JButton button = new JButton("Add volts");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.setV(model.getV() + 15);
            }
        });
        controlPanel.add(button);

        final Timer timer = new Timer(30,new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 10; i++) {
                    double time = model.getElapsedTime();
                    double v = model.getV();

                    series.add(time, v);
                    model.Advance();
                }
            }
        });
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timer.start();
            }
        });
        controlPanel.add(startButton);
        JFrame controlFrame = new JFrame();
        controlFrame.setContentPane(controlPanel);
        controlFrame.pack();
//        controlFrame.setLocation(chartFrame.getX(), chartFrame.getY() + chartFrame.getHeight());
        controlFrame.setLocation(0, 0);

        controlFrame.setVisible(true);
    }
}
