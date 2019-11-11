package interfaceFile;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;

import org.jfree.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.ApplicationFrame;

//import com.sun.media.jfxmedia.logging.Logger;

import java.awt.*;
import javax.swing.*;

import interfaceFile.PromoAnalysis.BrandsData;

import java.util.*;

public class CreateChartsForTargetedBrand extends ApplicationFrame{
	
	private TargetedBrandPerformance performanceData;
	private DefaultPieDataset targetedBrandItemsShare;
	private String targetBrand;

	public CreateChartsForTargetedBrand(String filepath) throws FileNotFoundException {
		super(filepath);
		this.performanceData = new TargetedBrandPerformance(filepath);
		this.targetedBrandItemsShare = new DefaultPieDataset();
		this.targetBrand = performanceData.getTargetedBrandLink();
	}
	
	//Getter method of targeted brand performance class data
	public TargetedBrandPerformance getTargetedBrandPerformanceData() {
		return this.performanceData;
	}
	
	//Create database for targeted brand's new items share of total brands sales 
	public void createNewItemsShareDataset() throws FileNotFoundException {
		TargetedBrandPerformance targetBrandData = this.getTargetedBrandPerformanceData();
		targetBrandData.loadData();
		Hashtable<String, BrandsData> promoPerBrand = targetBrandData.getPromoPerBrandAndTotalSales("sales");
		HashMap<String, DataStorage> targetedBrandNewItems = targetBrandData.getItemsInTargetedBrand();
		double salesNewItems = 0.0;
		double totalBrandSales = promoPerBrand.get(this.targetBrand).getSales("current");
		for(String upc:targetedBrandNewItems.keySet()) {
			if(targetedBrandNewItems.get(upc).getBrand().equals(this.targetBrand)) {
				salesNewItems +=targetedBrandNewItems.get(upc).getSales("current");
			}
		}
		this.targetedBrandItemsShare.setValue("Dollar Share New Items", (salesNewItems/totalBrandSales));
		this.targetedBrandItemsShare.setValue("Remaining Items", ((totalBrandSales-salesNewItems)/totalBrandSales));
	}
	
	//Create pie chart with dollar sales share for new items versus old remaining items
	public JPanel createNewItemsSharePieChart() throws FileNotFoundException {
		this.createNewItemsShareDataset();
		JFreeChart chart = ChartFactory.createPieChart("$Share of new items", 
				this.targetedBrandItemsShare, 
				false, true, false);
		ChartPanel pieChart = new ChartPanel(chart);
		pieChart.setPreferredSize(new Dimension(400,250));
		return pieChart;
	}

}
