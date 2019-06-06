package jfreechart;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.File;

/**
 * Created by wuzh on 2019/5/24.
 * Describe：测试JFreeChart生成线性图
 */
public class JFreeChartDemo {
    public static void main(String[] args) throws Exception {
        CategoryDataset dataSet = getDataSet();
        JFreeChart chart = ChartFactory.createLineChart("总人口(亿人)", "", "", dataSet, PlotOrientation.VERTICAL, true, false, false);
        processChart(chart);
        ChartUtils.setLegendEmptyBorder(chart);
        CategoryPlot plot = chart.getCategoryPlot();
        ChartUtils.setLineRender(plot,true);
        plot.setBackgroundPaint(ChartColor.white);
        plot.setRangeGridlinePaint(ChartColor.gray);
        ValueAxis axis = plot.getRangeAxis();
        axis.setRangeWithMargins(13.50,14.00);
        ChartUtilities.saveChartAsPNG(new File("D:\\line.png"),chart,800,500);
    }

    /**
     * 获取一个演示用的组合数据集对象
     *
     * @return
     */
    private static CategoryDataset getDataSet() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(13.6782,  "","2014年");
        dataset.addValue(13.7462, "","2015年");
        dataset.addValue(13.8271, "", "2016年");
        dataset.addValue(13.9008, "", "2017年");
        dataset.addValue(13.9538, "", "2018年");
        return dataset;
    }
    /**
     * 解决图表汉字显示问题
     *
     * @param chart
     */
    private static void processChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        ValueAxis rAxis = plot.getRangeAxis();
        chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        TextTitle textTitle = chart.getTitle();
        textTitle.setFont(new Font("宋体", Font.PLAIN, 20));
        domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
        domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        rAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));
        rAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
        // renderer.setItemLabelGenerator(new LabelGenerator(0.0));
        // renderer.setItemLabelFont(new Font("宋体", Font.PLAIN, 12));
        // renderer.setItemLabelsVisible(true);
    }
}
