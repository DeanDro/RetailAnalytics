package interfaceFile;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.ApplicationFrame;

import interfaceFile.PricingAnalysis.Pricing;
import interfaceFile.SalesAnalysis.brandStore;

import java.awt.*;
import javax.swing.*;

public class CreateCharts extends ApplicationFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SalesAnalysis data;
	private PricingAnalysis pricingData;
	private PromoAnalysis promoData;
	private DefaultPieDataset dataset;
	private DefaultCategoryDataset barDataset;
	private DefaultCategoryDataset pricingBarDatabase;
	private DefaultCategoryDataset categoryPriceChange;
	private DefaultPieDataset promoSegmentationPie;
	private DefaultCategoryDataset promoPerTypeChangeDatabase;
	private HashMap<String, Pricing> brandsPricing;
	private ArrayList<brandComparison> rank;
	
	public CreateCharts(String filePath) throws FileNotFoundException {
		super(filePath);
		this.data = new SalesAnalysis(filePath);
		this.dataset = new DefaultPieDataset();
		this.barDataset = new DefaultCategoryDataset();
		this.pricingData = new PricingAnalysis(filePath);
		this.pricingBarDatabase = new DefaultCategoryDataset();
		this.categoryPriceChange = new DefaultCategoryDataset();
		this.promoSegmentationPie = new DefaultPieDataset();
		this.promoPerTypeChangeDatabase = new DefaultCategoryDataset();
		this.brandsPricing = pricingData.getAvgPricesPerBrand();
		this.promoData = new PromoAnalysis(filePath);
		this.rank = new ArrayList<brandComparison>();
		setContentPane(createPieChart());
	}
	
	public ArrayList<brandComparison> getRank(){
		return this.rank;
	}
	
	public SalesAnalysis getData() {
		return this.data;
	}
	
	public PricingAnalysis getPricingData() {
		return this.pricingData;
	}
	
	public HashMap<String, Pricing> getPricingPerBrand(){
		return brandsPricing;
	}
	
	public PromoAnalysis getPromoData() {
		return this.promoData;
	}
	
	//Method to populate values in the database for the pie chart
	public void getPieDataset() {
		SalesAnalysis data = this.getData();
		HashMap<String, brandStore> brands = data.getBrandsMap();
		ArrayList<String> rank = data.getRankingforBrands();
		double allOther = 0.0;
		for(int i=0; i<rank.size(); i++) {
			if(i<5) {
				double figure = brands.get(rank.get(i)).getTotalSales();
				dataset.setValue(rank.get(i), figure);
			} else {
				allOther = allOther+brands.get(rank.get(i)).getTotalSales();
			}
		}
		dataset.setValue("All Other", allOther);
	}
	
	//create promotional pie chart
	public void promotionalPieChart() {
		PromoAnalysis promo = this.getPromoData();
		promo.getPromoShare();
		Hashtable<String, Double> promoPerType = promo.getSalesByPromo();
		this.promoSegmentationPie.setValue("No Promo Sales", (promoPerType.get("Total Sales")-promoPerType.get("Any Promo Sales"))/promoPerType.get("Total Sales"));
		this.promoSegmentationPie.setValue("Feature", promoPerType.get("Feature")/promoPerType.get("Total Sales"));
		this.promoSegmentationPie.setValue("Display", promoPerType.get("Display")/promoPerType.get("Total Sales"));
		this.promoSegmentationPie.setValue("Price Disc.", promoPerType.get("Price Disc.")/promoPerType.get("Total Sales"));
		this.promoSegmentationPie.setValue("Quality", promoPerType.get("Quality")/promoPerType.get("Total Sales"));
	}
	
	//Method to populate values in the database for the bar chart
	public void getBarDataset() {
		SalesAnalysis data = this.getData();
		HashMap<String, Double> comparisonValues = data.newItemsVScategory();
		HashMap<String, Double> percentageValues = data.convertToPercentage(comparisonValues);
		String [] categories = {"Any Promo", "Price Disc.", "Feat", "Display", "Feature & Display", "Quality"};
		for(int i=0; i<categories.length; i++) {
			barDataset.addValue(percentageValues.get("New Items "+categories[i]), "New Items", categories[i]);
			barDataset.addValue(percentageValues.get(categories[i]), "Total Category", categories[i]);
		}
	}
	
	//Create database for pricing analysis
	public void getCategoryPricingBarDatabase() {
		PricingAnalysis prices = this.getPricingData();
		Hashtable<String, DataStorage> brands = prices.getAllBrands();
		double avgPrice=0.0, avgPriceYA=0.0, promoPrice=0.0, promoPriceYA=0.0, noPromo=0.0, noPromoYA=0.0;
		int count1=0, count2=0, count3=0, count4=0, count5=0, count6=0;
		for(String key:brands.keySet()) {
			if(brands.get(key).getSales("current")>0) {
				avgPrice+=brands.get(key).getPrice("current");
				noPromo +=brands.get(key).getNoPromoPrice("current");
				count1++;
				count5++;
			}
			if(brands.get(key).getSales("past")>0) {
				avgPriceYA+=brands.get(key).getPrice("past");
				noPromoYA+=brands.get(key).getNoPromoPrice("past");
				count2++;
				count6++;
			}
			if(brands.get(key).getAnyPromo("current")>0) {
				promoPrice+=brands.get(key).getAnyPromoPrice("current");
				count3++;
			}
			if(brands.get(key).getAnyPromoPrice("past")>0) {
				promoPriceYA+= brands.get(key).getAnyPromoPrice("past");
				count4++;
			}
		}
		pricingBarDatabase.addValue(avgPrice/count1, "Avg. Price", "Avg. Price");
		pricingBarDatabase.addValue(avgPriceYA/count2, "Avg.PriceYA", "Avg.PriceYA");
		pricingBarDatabase.addValue(promoPrice/count3, "Any Promo Price", "Any Promo Price");
		pricingBarDatabase.addValue(promoPriceYA/count4, "Any Promo Price YA", "Any Promo Price YA");
		pricingBarDatabase.addValue(noPromo/count5, "No Promo Price", "No Promo Price");
		pricingBarDatabase.addValue(promoPriceYA/count6, "No Promo Price YA", "No Promo Price YA");
		categoryPriceChange.addValue((avgPrice-avgPriceYA)/avgPriceYA, "Avg.Price %Chg.", "Avg.Price %Chg.");
		categoryPriceChange.addValue((promoPrice-promoPriceYA)/promoPriceYA, "Any Promo %Chg.", "Any Promo %Chg.");
		categoryPriceChange.addValue((noPromo-noPromoYA)/noPromoYA, "No Promo %Chg.", "No Promo %Chg.");
	}
	
	//Create database on promotional change from last year for each promotional type also including non promo and promo sales
	public void getPromotionalChangePerTypeDatabase() {
		PromoAnalysis promo = this.getPromoData();
		Hashtable<String, Double> promoFigures = promo.promoPercChange();
		for(String key: promoFigures.keySet()) {
			this.promoPerTypeChangeDatabase.addValue(promoFigures.get(key), key, key);
		}
		
	}
	
	//Create the category price change bar chart
	public JPanel createPriceChgChart() {
		this.getCategoryPricingBarDatabase();
		JFreeChart chart = ChartFactory.createBarChart("Category Prices %Chg. YA", 
				"Price Segment", 
				"Price %Chg.YA", 
				this.categoryPriceChange,
				PlotOrientation.VERTICAL,
				true,
				true,
				false);
		ChartPanel barChart = new ChartPanel(chart);
		barChart.setSize(250,250);
		return barChart;
	}
	
	//Create the pricing bar chart
	public JPanel createPricingBarChart() {
		this.getCategoryPricingBarDatabase();
		JFreeChart chart = ChartFactory.createBarChart("Category pricing", 
				"Price Segment", 
				"Price", 
				this.pricingBarDatabase, 
				PlotOrientation.VERTICAL,
				true, 
				true, false
				);
		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis xAxis = plot.getDomainAxis();
		xAxis.setMaximumCategoryLabelLines(2);
		ChartPanel barChart = new ChartPanel(chart);
		barChart.setSize(250,250);
		return barChart;
	}
	
	//Method to create the bar chart
	public JPanel createBarChart() {
		this.getBarDataset();
		JFreeChart chart = ChartFactory.createBarChart("% of total sales for new items vs category", 
				"category", 
				"% of total dollar sales", 
				this.barDataset, 
				PlotOrientation.VERTICAL, 
				true, 
				true, 
				false);
		ChartPanel barChart = new ChartPanel(chart);
		barChart.setSize(250,250);
		return barChart;
	}
	
	//Method to create bar chart with promotional figures %change versus year ago
	public JPanel createPromotionalChangePerTypeBarChart() {
		this.getPromotionalChangePerTypeDatabase();
		JFreeChart chart = ChartFactory.createBarChart("%Change vs YA Promo Type", 
				"Promotional Type", 
				"%Chg. vs YA", 
				this.promoPerTypeChangeDatabase, 
				PlotOrientation.VERTICAL, 
				true, 
				true, 
				false);
		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis xAxis = plot.getDomainAxis();
		xAxis.setMaximumCategoryLabelLines(2);
		ChartPanel barChart = new ChartPanel(chart);
		barChart.setSize(250,250);
		return barChart;
	}
	
	//Method to create the pie chart
	public JPanel createPieChart() {
		this.getPieDataset();
		JFreeChart chart = ChartFactory.createPieChart("New Items Dollar Sales Per Brand", 
				this.dataset,
				false, //this includes the legend at the bottom with all the figures
				true,
				false);
		ChartPanel pieChart = new ChartPanel(chart);
		pieChart.setSize(50, 50);
		pieChart.setBackground(Color.BLACK);
		return pieChart;
	}
	
	//Create promo per type pie chart
	public JPanel createPromoPieChartPerType() {
		this.promotionalPieChart();
		JFreeChart chart = ChartFactory.createPieChart("Promotional Segmentation",
				this.promoSegmentationPie,
				false,
				true,
				false);
		ChartPanel pieChart = new ChartPanel(chart);
		pieChart.setSize(50,50);
		return pieChart;
	}
	
	private static DecimalFormat f1 = new DecimalFormat("##.##%");
	private static DecimalFormat f2 = new DecimalFormat("$##.##");
	
	
	//helper method to get the rank for all brands
	/*
	 * I CREATED A PRIVATE CLASS TO COMPLETE THE SORT 
	 */
	public ArrayList<brandComparison> getRankForAllBrands() {
		HashMap<String, DataStorage> items = data.getAllItems();
		HashMap<String, brandComparison> testing = new HashMap<String, brandComparison>();
		for(String key:items.keySet()) {
			String brand = items.get(key).getBrand();
			double dollars = items.get(key).getSales("current");
			if(testing.containsKey(brand)) {
				dollars = dollars+testing.get(brand).getSales();
				brandComparison addition = new brandComparison(brand, dollars);
				testing.put(brand, addition);
			} else {
				brandComparison addition2 = new brandComparison(brand, dollars);
				testing.put(brand, addition2);
			}
		}
		
		ArrayList<brandComparison> newList = new ArrayList<brandComparison>(testing.values());
		Collections.sort(newList, new Comparator<brandComparison>() {
			public int compare(brandComparison o1, brandComparison o2) {
				double value = o2.getSales() - o1.getSales();
				return (int) Math.round(value);
			}
		});
		
		return newList;
	}
	
	class brandComparison{
		private String brand;
		private double sales;
		
		public brandComparison(String brandName, double dollarSales) {
			this.brand = brandName;
			this.sales = dollarSales;
		}
		
		public String getBrand() {
			return this.brand;
		}
		
		public Double getSales() {
			return this.sales;
		}
		
		public void setSales(double dollars) {
			this.sales = dollars;
		}
		
		public void setBrand(String name) {
			this.brand = name;
		}
	}
	/*
	 * HERE WE HAVE THE UPDATED DATA FOR THE PRICING INTERFACE CLASS
	 */
	
	public JPanel getPricingComparisonTable() {
		Hashtable<String,DataStorage> pricingBrands = pricingData.getAllBrands();
		ArrayList<String> rankBrands = promoData.getRankedBrands("sales");
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		int numRows = pricingBrands.size();
		JTable table = new JTable(numRows, 7);
		String [] columnTitles = {"Brand", "Avg.Price", "Avg.Price %Chg.", "Promo Price", "Promo Price %Chg.", "No Promo Price", "No Promo Price %Chg."};
		for(int i=0; i<columnTitles.length; i++) {
			table.setValueAt(columnTitles[i], 0, i);
		}
		int row=1;
		for(int j=0; j<rankBrands.size(); j++) {
			String key = rankBrands.get(j);
			double avgPriceChange = pricingBrands.get(key).getPrice("current")-pricingBrands.get(key).getPrice("past");
			double promoPriceChange = pricingBrands.get(key).getAnyPromoPrice("current")-pricingBrands.get(key).getAnyPromoPrice("past");
			double noPromoPriceChange = pricingBrands.get(key).getNoPromoPrice("current")-pricingBrands.get(key).getNoPromoPrice("past");
			table.setValueAt(key, row, 0);
			table.setValueAt(f2.format(pricingBrands.get(key).getPrice("current")), row, 1);
			table.setValueAt(f1.format(avgPriceChange/pricingBrands.get(key).getPrice("past")), row, 2);
			table.setValueAt(f2.format(pricingBrands.get(key).getAnyPromoPrice("current")), row, 3);
			table.setValueAt(f1.format(promoPriceChange/pricingBrands.get(key).getAnyPromoPrice("past")), row, 4);
			table.setValueAt(f2.format(pricingBrands.get(key).getNoPromoPrice("current")), row, 5);
			table.setValueAt(f1.format(noPromoPriceChange/pricingBrands.get(key).getNoPromoPrice("past")), row, 6);
			row++;
		}
		
		JLabel pricingTitle = new JLabel("Price Review Per Brand - Ranked By Total Sales");
		pricingTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		JScrollPane scroll = new JScrollPane(table);
		panel.add(pricingTitle, BorderLayout.CENTER);
		panel.add(scroll);
		return panel;
	}
	
}
