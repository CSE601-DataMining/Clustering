package edu.buffalo.cse.clustering;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @see http://stackoverflow.com/questions/7205742
 * @see http://stackoverflow.com/questions/7208657
 * @see http://stackoverflow.com/questions/7071057
 */
public class ScatterAdd extends JFrame {

    private static final int N = 8;
    private static final String title = "Clustering";
    private static final Random rand = new Random();
    private XYSeries added = new XYSeries("Added");
    
    
    double[][] data1 = {
    		{-0.69,	-0.96},
    		{-0.21,	0.19},
    		{-0.3,	-0.56},
    		{0.07,	0.26},
    		{-1.04,	0.13},
    };
    
    double[][] data2 = {
    		{-1.17,	0.09},
    		{-0.16,	0.35},
    		{-0.89,	0.77},
    		{-0.18,	0.14},
    		{-0.42,	-0.57},
    };
    
    private ArrayList<double[][]> dataList;

    public ScatterAdd(String s, ArrayList<double[][]> dataList) {
        super(s);
        this.dataList = dataList;
        final ChartPanel chartPanel = createDemoPanel();
        this.add(chartPanel, BorderLayout.CENTER);
        JPanel control = new JPanel();
        control.add(new JButton(new AbstractAction("Add") {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < N; i++) {
                    added.add(rand.nextGaussian(), rand.nextGaussian());
                }
            }
        }));
        this.add(control, BorderLayout.SOUTH);
    }

    private ChartPanel createDemoPanel() {
        JFreeChart jfreechart = ChartFactory.createScatterPlot(
            title, "X", "Y", createSampleData(),
            PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        XYItemRenderer renderer = xyPlot.getRenderer();
        //renderer.setSeriesPaint(0, Color.blue);
        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        domain.setVerticalTickLabels(true);
        return new ChartPanel(jfreechart);
    }

    private XYDataset createSampleData() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        int i = 0;
        for(double[][] data : dataList){
        	XYSeries series = new XYSeries("Cluster-" + i);
        	for(int j = 0; j < data.length; j++){
        		double x = data[j][0];
                double y = data[j][1];
                series.add(x, y);
        	}
        	xySeriesCollection.addSeries(series);
        	i++;
        }
//        XYSeries series1 = new XYSeries("Random1");
//        for (int i = 0; i < data1.length; i++) {
//            double x = data1[i][0];
//            double y = data1[i][1];
//            series1.add(x, y);
//        }
//        
//        XYSeries series2 = new XYSeries("Random2");
//        for (int i = 0; i < data2.length; i++) {
//            double x = data2[i][0];
//            double y = data2[i][1];
//            series2.add(x, y);
//        }
//        xySeriesCollection.addSeries(series1);
//        xySeriesCollection.addSeries(series2);
        //xySeriesCollection.addSeries(added);
        return xySeriesCollection;
    }

//    public static void main(String args[]) {
//    	
//        EventQueue.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//            	 double[][] data1 = {
//         	    		{-0.69,	-0.96},
//         	    		{-0.21,	0.19},
//         	    		{-0.3,	-0.56},
//         	    		{0.07,	0.26},
//         	    		{-1.04,	0.13},
//         	    };
//         	    
//         	    double[][] data2 = {
//         	    		{-1.17,	0.09},
//         	    		{-0.16,	0.35},
//         	    		{-0.89,	0.77},
//         	    		{-0.18,	0.14},
//         	    		{-0.42,	-0.57},
//         	    };
//         	    
//         	    ArrayList<double[][]> data = new ArrayList<double[][]>();
//         	    data.add(data1);
//         	    data.add(data2);
//                ScatterAdd demo = new ScatterAdd(title, data);
//                demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                demo.pack();
//                demo.setLocationRelativeTo(null);
//                demo.setVisible(true);
//            }
//        });
//    }
}