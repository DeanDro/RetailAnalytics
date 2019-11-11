package interfaceFile;

import javax.swing.*;

import interfaceFile.PricingAnalysis.Pricing;

import java.awt.*;
import java.io.FileNotFoundException;

import java.util.*;
public class PricingInterface{
	
	private CreateCharts charts;
	
	public PricingInterface(String filePath) throws FileNotFoundException {
		this.charts = new CreateCharts(filePath);
		this.createPricingFrame();
	}
	
	
	public void createPricingFrame() throws FileNotFoundException {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 600);
		frame.setTitle("Pricing Analysis");
		frame.setLocationRelativeTo(null);
		frame.setBackground(Color.WHITE);
		frame.setForeground(Color.BLACK);
		
		Container content = frame.getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		
		JPanel logo = new JPanel();
		JLabel title = new JLabel("Pricing Analysis");
		JButton mainPage = new JButton("Return Main Page");
		Font f1 = new Font(Font.SANS_SERIF, Font.BOLD, 10);
		title.setFont(f1);
		logo.add(mainPage, BorderLayout.EAST);
		logo.add(title, BorderLayout.CENTER);
		
		JPanel graphContent = new JPanel();
		graphContent.setLayout(new BoxLayout(graphContent, BoxLayout.X_AXIS));
		graphContent.setSize(800,300);
		graphContent.setBackground(Color.BLACK);
		JPanel categoryChart = charts.createPricingBarChart();
		graphContent.add(categoryChart);
		JPanel categoryChgChart = charts.createPriceChgChart();
		graphContent.add(categoryChgChart);
		
		//Add Price comparison table
		JPanel brandRanking = charts.getPricingComparisonTable();
		
		content.add(logo, BorderLayout.NORTH);
		content.add(graphContent, BorderLayout.CENTER);
		content.add(brandRanking, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}
	
	public static void main(String [] args) throws FileNotFoundException {
		PricingInterface test = new PricingInterface("/Users/Konstantine/Desktop/Programs/Java/Java Projects/Testing data/multivitaminsOnlyMeijerData2.xls");
	}
}
