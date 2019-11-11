package interfaceFile;

import javax.swing.*;

import interfaceFile.SalesAnalysis.brandStore;
import interfaceFile.SalesAnalysis.upcStore;

import java.awt.*;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;

public class NewItemsFrame {
	
	private SalesAnalysis salesData;
	private ArrayList<String> upcRankKeys;
	private ArrayList<String> brandRankKeys;
	private CreateCharts charts;
	private String filepath;
	
	public NewItemsFrame(String fileName) throws FileNotFoundException {
		this.filepath = fileName;
		this.salesData = new SalesAnalysis(filepath);
		this.upcRankKeys = new ArrayList<String>();
		this.brandRankKeys = new ArrayList<String>();
		this.charts = new CreateCharts(filepath);
		this.createFrame();
		
	}
	
	public SalesAnalysis getData(){
		return salesData;
	}
	
	public CreateCharts getChartData() {
		return this.charts;
	}
	
	public void createFrame() throws FileNotFoundException {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000,600);
		frame.setBackground(Color.WHITE);
		frame.setLocationRelativeTo(null);
		
		Container content = frame.getContentPane();
		
		JPanel logo = new JPanel();
		JLabel title = new JLabel("New Items Analysis");
		JButton mainPage = new JButton("Main Page");
		Font f = new Font(Font.SANS_SERIF, Font.BOLD, 20);
		title.setFont(f);
		title.setForeground(Color.BLACK);
		logo.setBackground(Color.WHITE);
		logo.add(mainPage, BorderLayout.EAST);
		logo.add(title, BorderLayout.CENTER);
		
		GridLayout grid = new GridLayout(1,3);
		JPanel dataPict = new JPanel();
		dataPict.setBackground(Color.WHITE);
		dataPict.setForeground(Color.BLACK);
		dataPict.setLayout(grid);
		
		//UPC and Brand ranking
		HashMap<String, String> brands = this.getTop5Brands();
		HashMap<String, String> upcs = this.getTop5UPC();
		JPanel brandsPanel = this.allocateRankWithSales(brands, brandRankKeys, "Brand Ranking New Items");
		JPanel upcPanel = this.allocateRankWithSales(upcs, upcRankKeys, "UPC Ranking New Items");
		dataPict.add(brandsPanel, 0);
		dataPict.add(upcPanel, 1);
		
		//Promotions per type
		JPanel promoTypes = this.getPromotionalTypes();
		dataPict.add(promoTypes, 2);
		
		//Get pie and bar chart data
		JPanel pieChart = this.getPieChart();
		JPanel barChart = this.getBarChart();
		JPanel chartsContent = new JPanel();
		chartsContent.setLayout(new BoxLayout(chartsContent, BoxLayout.X_AXIS));
		chartsContent.setSize(300,300);
		chartsContent.add(pieChart, BorderLayout.LINE_START);
		chartsContent.add(barChart, BorderLayout.CENTER);
		
		content.add(logo, BorderLayout.BEFORE_FIRST_LINE);
		content.add(chartsContent, BorderLayout.CENTER);
		content.add(dataPict, BorderLayout.AFTER_LAST_LINE);
		
		frame.setVisible(true);
	}
	
	//Method to pull the top 5 brands
	public HashMap<String, String> getTop5Brands() {
		HashMap<String, String> values = new HashMap<String, String>();
		SalesAnalysis data = this.getData();
		data.getBrandRanking();
		HashMap<String, brandStore> info = data.getBrandsMap();
		brandRankKeys = data.getRankingforBrands();
		String [] brands = new String[5];
		for(int i=0; i<5; i++) {
			String key = brandRankKeys.get(i);
			brands[i] = key;
		}
		
		for(int j=0; j<5; j++) {
			String sales="";
			if(brands[j].equals(null)) {
				sales = "0.0";
			} else {
				sales = df3.format(info.get(brands[j]).getTotalSales()/2);
			}
			values.put(brands[j], sales);
		}
		return values;
	}
	
	//Method to pull the top 5 upc
	public HashMap<String, String> getTop5UPC() {
		HashMap<String, String> values = new HashMap<String, String>();
		SalesAnalysis data = this.getData();
		data.getUPCRanking();
		HashMap<String, upcStore> info = data.getUPCMap();
		upcRankKeys = data.getRankingforUPC();
		String [] keys = new String[5];
		for(int i=0; i<5; i++) {
			String key = upcRankKeys.get(i);
			keys[i] = key;
		}
		for(int k=0; k<5; k++) {
			String sales="";
			if(keys[k].equals(null)) {
				sales = "$0.0";
			} else {
				sales = df3.format(info.get(keys[k]).getUPCSales());
			}
			values.put(keys[k], sales);
		}
		
		return values;
	}
	
	public JPanel allocateRankWithSales(HashMap<String, String> data, ArrayList<String> keys, String logo) {
		JPanel result = new JPanel();
		JLabel title = new JLabel(logo);
		
		Font font2 = new Font(Font.SANS_SERIF, Font.BOLD, 15);
		title.setFont(font2);
		result.add(title, BorderLayout.BEFORE_FIRST_LINE);
		
		//Add the labels
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		result.setBackground(Color.WHITE);
		Font f = new Font(Font.SANS_SERIF, Font.BOLD, 10);
		for(int i=0; i<5; i++) {
			String k = keys.get(i);
			JLabel label = new JLabel(k+"\t"+data.get(k));
			label.setFont(f);
			label.setForeground(Color.BLACK);
			result.add(label, i+1);
		}
		
		return result;
	}
	
	//Take data for promo analysis for new items
	public JPanel getPromotionalTypes(){
		JPanel panel = new JPanel();
		JLabel title = new JLabel("<html>New items promo sales as<br/> percentage of total sales</html>");
		
		SalesAnalysis data = this.getData();
		HashMap<String, Double> info = data.getPromoPerType();
		Font font3 = new Font(Font.SANS_SERIF, Font.BOLD, 15);
		title.setFont(font3);
		title.setBackground(Color.WHITE);
		panel.add(title, BorderLayout.BEFORE_FIRST_LINE);
		
		//Add the label
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.WHITE);
		Font f4 = new Font(Font.SANS_SERIF, Font.BOLD, 10);
		for(String key:info.keySet()) {
			JLabel input = new JLabel(key+": "+df4.format(info.get(key)));
			input.setFont(f4);
			panel.add(input);
		}
		
		return panel;
	}
	
	private static DecimalFormat df3 = new DecimalFormat(": $###,###.##");
	private static DecimalFormat df4 = new DecimalFormat("##.##%");
	
	//Get the pie chart
	public JPanel getPieChart() {
		CreateCharts data = this.getChartData();
		return data.createPieChart();
	}
	
	public JPanel getBarChart() {
		CreateCharts data = this.getChartData();
		return data.createBarChart();
	}
}
