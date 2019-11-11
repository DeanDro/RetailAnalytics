package interfaceFile;

import javax.swing.*;

import interfaceFile.PricingAnalysis.Pricing;
import interfaceFile.PromoAnalysis.BrandsData;
import java.awt.*;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;

public class TargetBrandInsightsInterface {
	
	private String targetedBrand;
	private TargetedBrandPerformance performanceData;
	private CreateChartsForTargetedBrand performanceCharts;
	
	
	public static void main(String []args) throws FileNotFoundException {
		TargetBrandInsightsInterface branding = new TargetBrandInsightsInterface("/Users/Konstantine/Desktop/Programs/Java/Java Projects/Testing data/multivitaminsOnlyMeijerData2.xls");
		System.out.println(branding+" successfully loaded");
	}
	
	public TargetBrandInsightsInterface(String filepath) throws FileNotFoundException {
		this.loadAllData(filepath);
		
		//Create frame
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);
		frame.setLocationRelativeTo(null);
		frame.setBackground(Color.WHITE);
		frame.setTitle("Targeted Brand Analysis");
		
		Container content = frame.getContentPane();
		content.setBackground(Color.WHITE);
		JPanel sumPanel = new JPanel();
		sumPanel.setBackground(Color.WHITE);
		sumPanel.setLayout(new BoxLayout(sumPanel, BoxLayout.Y_AXIS));
		
		//Top row 
		JPanel headerPanel = new JPanel();
		JButton mainMenu = new JButton("Main Menu");
		JLabel header = new JLabel(this.targetedBrand+" Analysis");
		headerPanel.add(mainMenu, BorderLayout.WEST);
		headerPanel.add(header, BorderLayout.CENTER);
		content.add(headerPanel, BorderLayout.NORTH);
		
		//Main page --- New Items segment
		JPanel newItemsAnalysis = new JPanel();
		newItemsAnalysis.setAlignmentX(Container.CENTER_ALIGNMENT);
		newItemsAnalysis.setBackground(Color.WHITE);
		
		//Data for new items segment
		JLabel newItems = new JLabel("New Items Analysis");
		newItems.setFont(headerFont);
		newItems.setBackground(Color.WHITE);
		
		//New items ranking and pie chart
		JPanel rankingChart = new JPanel();
		JPanel rankingChart2 = new JPanel();
		rankingChart.setBackground(Color.WHITE);
		JTable targetedItems = this.createNewItemsTable();
		CreateChartsForTargetedBrand chartData = this.getDataChartsTargetBrand();
		JPanel pieChartNewItemsShare = chartData.createNewItemsSharePieChart();
		rankingChart.setLayout(new BoxLayout(rankingChart, BoxLayout.Y_AXIS));
		rankingChart.add(newItems, BorderLayout.NORTH);
		rankingChart.add(targetedItems, BorderLayout.CENTER);
		JLabel withoutNewItems = this.targetBrandPerformanceWithOrNotNewItems();
		rankingChart.add(withoutNewItems);
		JTable comparison = this.getComparisonDataForNewItems();
		rankingChart.add(comparison, BorderLayout.AFTER_LAST_LINE);
		rankingChart2.add(pieChartNewItemsShare, BorderLayout.CENTER);
		
		//Add everything to the new items segment
		newItemsAnalysis.add(rankingChart, BorderLayout.CENTER);
		newItemsAnalysis.add(rankingChart2, BorderLayout.CENTER);
		sumPanel.add(newItemsAnalysis);
		
		//Create the promo analysis segment and add everything in the sumpanel
		JPanel promoAnalysis = this.promoAnalysisSegment();
		sumPanel.add(promoAnalysis, BorderLayout.CENTER);
		
		//Create the pricing analysis and add everything in the sumpanel
		JPanel pricingAnalysis = this.getPriceAnalysisTargetBrand();
		sumPanel.add(pricingAnalysis);
		
		JScrollPane scroll = new JScrollPane(sumPanel);
		JPanel addAll = new JPanel();
		addAll.setBackground(Color.DARK_GRAY);
		addAll.add(scroll);
		content.add(addAll);
		
		frame.setVisible(true);
		
	}
	
	//Helper method to help initialize file source, performance data and all new items in the targeted brand
	public void loadAllData(String filepath) throws FileNotFoundException {
		this.performanceData = new TargetedBrandPerformance(filepath);
		this.targetedBrand = this.getPerformanceData().getTargetedBrandLink();
		this.performanceData.getTargetBrandItems();
		this.performanceCharts = new CreateChartsForTargetedBrand(filepath);
	}
	
	//Method to create a table with all the data for the new items in the targeted brand
	public JTable createNewItemsTable() {
		HashMap<String, DataStorage> newItems = this.performanceData.getItemsInTargetedBrand();
		int numNewItems = newItems.size();
		JTable table = new JTable(numNewItems+1, 3);
		table.setPreferredSize(new Dimension(400,numNewItems*25));
		table.setValueAt("UPC", 0, 0);
		table.setValueAt("Dollar Sales", 0, 1);
		table.setValueAt("Brands Share", 0, 2);
		double targetBrandTotalSales = this.getTotalSalesForTargetBrand();
		int j=1;
		for(String key:newItems.keySet()) {
			table.setValueAt(key+"\t", j, 0);
			table.setValueAt(f1.format(newItems.get(key).getSales("current"))+"\t", j, 1);
			table.setValueAt(f2.format(newItems.get(key).getSales("current")/targetBrandTotalSales)+"\t", j, 2);
			j++;
		}
		return table;
	}
	
	//Helper method to get total sales for targeted brand in order to calculate share
	public Double getTotalSalesForTargetBrand() {
		TargetedBrandPerformance data = this.getPerformanceData();
		Hashtable<String, BrandsData> salesByBrandByType = data.getPromoPerBrandAndTotalSales("sales");
		String target = this.getTargetedBrand();
		return salesByBrandByType.get(target).getSales("current");
	}
	
	//Get the data for the createChartsForTargetedBrand class
	public CreateChartsForTargetedBrand getDataChartsTargetBrand() {
		return this.performanceCharts;
	}
	
	//Get TargetedBrandPerformance data 
	public TargetedBrandPerformance getPerformanceData() {
		return this.performanceData;
	}
	
	//Get targeted brand
	public String getTargetedBrand() {
		return this.targetedBrand;
	}
	
	//Create label with dollar sales with and without new items
	public JLabel targetBrandPerformanceWithOrNotNewItems() {
		Hashtable<String, ArrayList<Double>> figures = this.getPerformanceData().getSalesPerformanceWithoutNewItems();
		for(String key:figures.keySet()) {
			if(key.equals(this.targetedBrand)) {
				double salesWithout = figures.get(key).get(0) - figures.get(key).get(2);
				JLabel label = new JLabel("Dollar sales without new items were "+f1.format(salesWithout)+" at "+f2.format((salesWithout-figures.get(key).get(1))/figures.get(key).get(1)));
				return label;
			}
		}
		return null;
	}
	
	//Method to create table for comparing target brand versus the 2 biggest competitors
	public JTable getComparisonDataForNewItems() {
		ArrayList<String> brandsList = this.getPerformanceData().getPromoAnalysisData().getRankedBrands("sales");//this will give us the top3 brands
		HashMap<String, DataStorage> items = this.getPerformanceData().getAllItems();
		HashMap<String, DataStorage> output = new HashMap<String, DataStorage>();
		Hashtable<String, DataStorage> outputNewItems = new Hashtable<String, DataStorage>();
		JTable table = new JTable(6,4);
		int column = 1;
		for(String key:items.keySet()) {
			String checkedBrand = items.get(key).getBrand();
			//We do that to look only in the top 3 brands or 2 if the targeted brand is among the top 3
			if(brandsList.get(0).equals(checkedBrand) || brandsList.get(1).equals(checkedBrand) || checkedBrand.equals(this.targetedBrand)) {
				if(output.containsKey(items.get(key).getBrand())) {
					DataStorage result = output.get(items.get(key).getBrand());
					DataStorage combined = result.addTwoDataStorage(items.get(key));
					output.put(combined.getBrand(), combined);
					//Here if it is a new item we added in a different hashtable
					if(items.get(key).getProductType()==1 && outputNewItems.get(items.get(key).getBrand())!=null) {
						DataStorage resultNewItems = outputNewItems.get(items.get(key).getBrand());
						DataStorage newOutput = resultNewItems.addTwoDataStorage(items.get(key));
						outputNewItems.put(newOutput.getBrand(), newOutput);
					} else if (items.get(key).getProductType()==1 && outputNewItems.get(items.get(key).getBrand())==null) {
						outputNewItems.put(items.get(key).getBrand(), items.get(key));
					}
				}else {
					output.put(items.get(key).getBrand(), items.get(key));
					if(items.get(key).getProductType()==1) {
						outputNewItems.put(items.get(key).getBrand(), items.get(key));
					}
				}
			}
		}
		for(String brand:output.keySet()) {
			double salesWoutNew = output.get(brand).getSales("current")-outputNewItems.get(brand).getSales("current");
			double performanceWoutNewItems = (salesWoutNew-output.get(brand).getSales("past"))/output.get(brand).getSales("past");
			table.setValueAt(brandsList.indexOf(brand)+1, 1, column);
			table.setValueAt(f1.format(outputNewItems.get(brand).getSales("current")), 2, column);
			table.setValueAt(f2.format(outputNewItems.get(brand).getSales("current")/output.get(brand).getSales("current")), 3, column);
			table.setValueAt(outputNewItems.get(brand).getProductType(), 4, column);
			table.setValueAt(f2.format(performanceWoutNewItems), 5, column);
			column++;
		}
		String [] measures = {"New Items Ranking", "Dollas", "%Share", "Num. Items", "Perf. w/out new items"};
		for(int i=0; i<measures.length; i++) {
			table.setValueAt(measures[i], i+1, 0);
		}
		
		return table;
	}
	
	//Promotional analysis method returns final panel to be placed for the promo segment
	public JPanel promoAnalysisSegment() {
		JPanel promoSegment = new JPanel();
		JTable targetBrandPromo = this.returnTargetBrandPromo(this.targetedBrand);
		JTable competitorsPromo = this.returnTargetBrandPromo("competition");
		JLabel segmentTitle = new JLabel("Promotional Analysis for "+this.targetedBrand);
		Font f = new Font(Font.SANS_SERIF, Font.BOLD, 15);
		segmentTitle.setFont(f);
		promoSegment.setLayout(new BoxLayout(promoSegment, BoxLayout.Y_AXIS));
		JPanel tablesPanel = new JPanel();
		tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.X_AXIS));
		tablesPanel.add(targetBrandPromo, BorderLayout.CENTER);
		tablesPanel.add(competitorsPromo, BorderLayout.CENTER);
		tablesPanel.setBackground(Color.WHITE);
		promoSegment.setBackground(Color.WHITE);
		promoSegment.add(segmentTitle, BorderLayout.NORTH);
		promoSegment.add(tablesPanel);
		
		return promoSegment;
	}
	
	//Create table with promo insights for either the targeted brand or the top 2 remaining brands
	public JTable returnTargetBrandPromo(String scope) {
		int x=6;
		int y=0;
		if(scope.equals(this.targetedBrand)) {
			y=2;
		} else {
			y=3;
		}
		JTable table = new JTable(x,y);
		HashMap<String, DataStorage> itemsList = this.getAllItemsForTopBrands();
		if(scope.equals(this.targetedBrand)) {
			double totalSales = itemsList.get(scope).getSales("current");
			table.setValueAt(this.targetedBrand, 0, 1);
			table.setValueAt(f2.format(itemsList.get(scope).getAnyPromo("current")/totalSales), 1, 1);
			table.setValueAt(f2.format(itemsList.get(scope).getDisplay("current")/totalSales), 2, 1);
			table.setValueAt(f2.format(itemsList.get(scope).getFeat("current")/totalSales), 3, 1);
			table.setValueAt(f2.format(itemsList.get(scope).getPriceDisc("current")/totalSales), 4, 1);
			table.setValueAt(f2.format(itemsList.get(scope).getQual("current")/totalSales), 5, 1);
		} else {
			int column = 1;
			for(String key:itemsList.keySet()) {
				if(!key.equals(this.targetedBrand)) {
					double totalSales = itemsList.get(key).getSales("current");
					table.setValueAt(key, 0, column);
					table.setValueAt(f2.format(itemsList.get(key).getAnyPromo("current")/totalSales), 1, column);
					table.setValueAt(f2.format(itemsList.get(key).getDisplay("current")/totalSales), 2, column);
					table.setValueAt(f2.format(itemsList.get(key).getFeat("current")/totalSales), 3, column);
					table.setValueAt(f2.format(itemsList.get(key).getPriceDisc("current")/totalSales), 4, column);
					table.setValueAt(f2.format(itemsList.get(key).getQual("current")/totalSales), 5, column);
					column++;
				}
			}
		}
		String [] categories = {"Brand", "Any Promo", "Display", "Feature", "Price Disc.", "Quality"};
		for(int i=0; i<categories.length; i++) {
			table.setValueAt(categories[i], i, 0);
		}
		
		return table;
	}
	
	//Helper method to find out which are the top 3 selling brands in a category if the targeted brand is included 
	//and return a hashmap with all the items that are included in these 3 brands both new and old items
	public HashMap<String, DataStorage> getAllItemsForTopBrands(){
		HashMap<String, DataStorage> result = new HashMap<String, DataStorage>();
		HashMap<String, DataStorage> allItemsSelection = this.getPerformanceData().getAllItems();
		ArrayList<String> listTopBrands = this.getPerformanceData().getPromoAnalysisData().getRankedBrands("sales");
		String [] topBrands = new String[3];
		int j=0;
		for(int i=0; i<listTopBrands.size() && i<2; i++) {
			if(!listTopBrands.get(i).equals(this.targetedBrand)) {
				topBrands[j] = listTopBrands.get(i);
				j++;
			}
		}
		//Here we will go through each item
		for(String key:allItemsSelection.keySet()) {
			String brand = allItemsSelection.get(key).getBrand();
			if(brand.equals(topBrands[0]) || brand.equals(topBrands[1]) || brand.equals(this.targetedBrand)) {
				if(result.containsKey(brand)) {
					DataStorage step1 = result.get(brand);
					DataStorage step2 = step1.addTwoDataStorage(allItemsSelection.get(key));
					result.put(brand, step2);
				} else {
					result.put(brand, allItemsSelection.get(key));
				}
			}
		}
		
		return result;
	}
	
	//Method to take DataStorage for price analysis for the targeted brand and for the top 2 competitive brands and category
	public JTable pricingTargetCompCategory(){
		JTable table = new JTable(4,4);
		Hashtable<String, Pricing> targetPrice = this.getPerformanceData().getPricingDataForTarget();
		int i=2;
		for(String key: targetPrice.keySet()) {
			if(key.equals(this.targetedBrand)) {
				table.setValueAt(key, 0, 1);
				table.setValueAt(f3.format(targetPrice.get(key).getAvgPrice("current")), 1, 1);
				table.setValueAt(f3.format(targetPrice.get(key).getPromoPrice("current")), 2, 1);
				table.setValueAt(f3.format(targetPrice.get(key).getNonPromoPrice("current")), 3, 1);
			} else if(!key.equals(this.targetedBrand) && i<4) {
				table.setValueAt(key, 0, i);
				table.setValueAt(f3.format(targetPrice.get(key).getAvgPrice("current")), 1, i);
				table.setValueAt(f3.format(targetPrice.get(key).getPromoPrice("current")), 2, i);
				table.setValueAt(f3.format(targetPrice.get(key).getNonPromoPrice("current")), 3, i);
				i++;
			} 
		}
		String [] titles = {"Brand", "Avg. Price", "Promo Price", "No Promo Price"};
		for(int j=0; j<titles.length; j++) {
			table.setValueAt(titles[j], j, 0);
		}
		
		return table;
	}

	//Create a panel that will include the price analysis table 
	public JPanel getPriceAnalysisTargetBrand() {
		JTable table = this.pricingTargetCompCategory();
		JPanel panel = new JPanel();
		JLabel pricingTitle = new JLabel("Pricing Analysis");
		pricingTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(new Dimension(300,150));
		panel.setBackground(Color.WHITE);
		panel.add(pricingTitle, BorderLayout.NORTH);
		panel.add(table);
		return panel;
	}
	
	//Format sales and share in the table
	DecimalFormat f1 = new DecimalFormat("$##,###,###");
	DecimalFormat f2 = new DecimalFormat("##.##%");
	DecimalFormat f3 = new DecimalFormat("$##.##");
	
	//Font for segment headers
	private static Font headerFont = new Font(Font.SERIF, Font.BOLD, 20);

}
