import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import JSci.awt.*;
import JSci.swing.*;

/**
* Sample program demonstrating use of the Swing/AWT graph components.
* @author Mark Hale
* @version 1.1
*/
public class GraphDemo extends Frame {
        private DefaultCategoryGraph2DModel categoryModel;
        private DefaultGraph2DModel valueModel;

        public static void main(String arg[]) {
                new GraphDemo();
        }
        public GraphDemo() {
                super("JSci Graph Demo");
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dispose();
                                System.exit(0);
                        }
                });
                setSize(700,600);
                final Font titleFont=new Font("Default",Font.BOLD,14);
                Label title;
        // category graphs
                categoryModel = createCategoryData();
                // bar graph
                JBarGraph barGraph=new JBarGraph(categoryModel);
                final Panel barGraphPanel=new Panel(new JGraphLayout());
                title=new Label("Bar graph",Label.CENTER);
                title.setFont(titleFont);
                barGraphPanel.add(title, JGraphLayout.TITLE);
                barGraphPanel.add(barGraph, JGraphLayout.GRAPH);
                barGraphPanel.add(new Label("y-axis",Label.RIGHT), JGraphLayout.Y_AXIS);
                barGraphPanel.add(new Label("Category axis",Label.CENTER), JGraphLayout.X_AXIS);
                // pie chart
                JPieChart pieChart=new JPieChart(categoryModel);
                final Panel pieChartPanel=new Panel(new GraphLayout());
                title=new Label("Pie chart",Label.CENTER);
                title.setFont(titleFont);
                pieChartPanel.add(title, GraphLayout.TITLE);
                pieChartPanel.add(pieChart, GraphLayout.GRAPH);
        // value graphs
                valueModel=createValueData();
                // line graph
                JLineGraph lineGraph=new JLineGraph(valueModel);
                lineGraph.setGridLines(true);
                lineGraph.setMarker(new Graph2D.DataMarker.Circle(5));
                final Panel lineGraphPanel=new Panel(new JGraphLayout());
                title=new Label("Line graph",Label.CENTER);
                title.setFont(titleFont);
                lineGraphPanel.add(title, JGraphLayout.TITLE);
                lineGraphPanel.add(lineGraph, JGraphLayout.GRAPH);
                Choice choice=new Choice();
                choice.add("Temp.");
                choice.add("Rain fall");
                choice.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent evt) {
                                if(evt.getStateChange()==ItemEvent.SELECTED) {
                                        if(evt.getItem().toString().equals("Temp.")) {
                                                valueModel.setSeriesVisible(0,true);
                                                valueModel.setSeriesVisible(1,false);
                                        } else if(evt.getItem().toString().equals("Rain fall")) {
                                                valueModel.setSeriesVisible(0,false);
                                                valueModel.setSeriesVisible(1,true);
                                        }
                                }
                        }
                });
                lineGraphPanel.add(choice, JGraphLayout.Y_AXIS);
                lineGraphPanel.add(new Label("x-axis",Label.CENTER), JGraphLayout.X_AXIS);

                // data series tables
                final Box tablePanel = new Box(BoxLayout.Y_AXIS);
                tablePanel.add(new JLabel("Data series 0", JLabel.CENTER));
                tablePanel.add(new JTable(valueModel.getSeries(0)));
                tablePanel.add(new JLabel("Data series 1", JLabel.CENTER));
                tablePanel.add(new JTable(valueModel.getSeries(1)));
        // layout
                GridBagLayout gb=new GridBagLayout();
                GridBagConstraints gbc=new GridBagConstraints();
                setLayout(gb);
                gbc.weightx=0.5;gbc.weighty=0.5;
                gbc.fill=GridBagConstraints.BOTH;

                gbc.gridx=0;gbc.gridy=0;
                gb.setConstraints(barGraphPanel, gbc);
                add(barGraphPanel);
                gbc.gridx=1;gbc.gridy=0;
                gb.setConstraints(pieChartPanel, gbc);
                add(pieChartPanel);

                gbc.fill=GridBagConstraints.HORIZONTAL;
                gbc.gridx=0;gbc.gridy=1;
                gbc.gridwidth=GridBagConstraints.REMAINDER;
                Button updateButton=new Button("Update");
                updateButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                float newData[]={3.4f,5.6f,6.2f,3.9f,1.8f};
                                categoryModel.changeSeries(0,newData);
                        }
                });
                gb.setConstraints(updateButton, gbc);
                add(updateButton);

                gbc.fill=GridBagConstraints.BOTH;
                gbc.gridx=0;gbc.gridy=2;
                gbc.gridwidth=1;
                gb.setConstraints(lineGraphPanel, gbc);
                add(lineGraphPanel);
                gbc.gridx=1;gbc.gridy=2;
                gbc.gridwidth=GridBagConstraints.REMAINDER;
                gb.setConstraints(tablePanel, gbc);
                add(tablePanel);
                setVisible(true);
        }
        private static DefaultCategoryGraph2DModel createCategoryData() {
                String labels[]={"Alpha1","Beta2","Gamma3","Delta4","Epsilon5"};
                float values1[]={2.4f,7.3f,3.2f,0.5f,2.2f};
                float values2[]={0.9f,3.4f,2.1f,6.5f,8.2f};
                DefaultCategoryGraph2DModel model=new DefaultCategoryGraph2DModel();
                model.setCategories(labels);
                model.addSeries(values1);
                model.addSeries(values2);
                return model;
        }
        private static DefaultGraph2DModel createValueData() {
                float values1[]={3.0f,2.8f,3.5f,3.6f,3.1f,2.6f};
                float values2[]={7.8f,4.1f,0.9f,0.2f,1.3f,2.5f};
                DefaultGraph2DModel model=new DefaultGraph2DModel();
                model.setXAxis(0.0f,5.0f,values1.length);
                model.addSeries(values1);
                model.addSeries(values2);
                model.setSeriesVisible(1,false);
                return model;
        }
}

