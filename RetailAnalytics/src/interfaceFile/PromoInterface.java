package interfaceFile;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import interfaceFile.PromoAnalysis.BrandsData;

public class PromoInterface {
	
	private CreateCharts chart;
	private PromoAnalysis promo;
	
	public CreateCharts getChart() {
		return this.chart;
	}
	
	public PromoAnalysis getPromoAnalysis() {
		return this.promo;
	}
	
	public PromoInterface(String filepath) throws FileNotFoundException {
		this.chart = new CreateCharts(filepath);
		this.promo = new PromoAnalysis(filepath);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 700);
		frame.setTitle("Promotional Analysis");
		frame.setLocationRelativeTo(null);
		frame.setBackground(Color.WHITE);
		
		Container content = frame.getContentPane();
		
		//Top part with logo and buttons
		JPanel topSegment = new JPanel();
		topSegment.setBackground(Color.WHITE);
		JLabel label = new JLabel("Promotional Analysis");
		JButton homeButton = new JButton("Main Page");
		homeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				try {
					StartingPage newFrame = new StartingPage(null);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		Font f1 = new Font(Font.SANS_SERIF, Font.BOLD, 15);
		label.setFont(f1);
		topSegment.add(homeButton, BorderLayout.BEFORE_LINE_BEGINS);
		topSegment.add(label, BorderLayout.CENTER);
		
		JPanel mainPagePanel = new JPanel();
		mainPagePanel.setLayout(new BoxLayout(mainPagePanel, BoxLayout.Y_AXIS));
		
		//Add graph segment
		JPanel chartsPanel = new JPanel();
		chartsPanel.setBackground(Color.WHITE);
		JPanel piePromoSegmChart = chart.createPromoPieChartPerType();
		JPanel barPercChangePerType = chart.createPromotionalChangePerTypeBarChart();
		chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.X_AXIS));
		chartsPanel.add(piePromoSegmChart);
		chartsPanel.add(barPercChangePerType);
		mainPagePanel.add(chartsPanel);
		
		//Add brand ranking segment 
		JPanel promos = new JPanel();
		JPanel brandsSegment1 = new JPanel();
		brandsSegment1.setBackground(Color.WHITE);
		brandsSegment1.setLayout(new BoxLayout(brandsSegment1, BoxLayout.X_AXIS));
		JPanel quality = this.brandsPerPromotionalTypeTable("Quality");
		JPanel feature = this.brandsPerPromotionalTypeTable("Feature");
		JPanel disc = this.brandsPerPromotionalTypeTable("Price Disc.");
		JPanel display = this.brandsPerPromotionalTypeTable("Display");
		brandsSegment1.add(quality);
		brandsSegment1.add(feature);
		
		JPanel brandsSegment2 = new JPanel();
		brandsSegment2.setBackground(Color.WHITE);
		brandsSegment2.setLayout(new BoxLayout(brandsSegment2, BoxLayout.X_AXIS));
		brandsSegment2.add(disc);
		brandsSegment2.add(display);
		
		promos.setLayout(new BoxLayout(promos, BoxLayout.Y_AXIS));
		promos.setBackground(Color.WHITE);
		promos.add(brandsSegment1);
		promos.add(brandsSegment2);
		JScrollPane scroll = new JScrollPane(promos);
		mainPagePanel.add(scroll);
		
		content.add(topSegment, BorderLayout.BEFORE_FIRST_LINE);
		content.add(mainPagePanel, BorderLayout.CENTER);
		
		
		frame.setVisible(true);
	}
	
	//Create a table with percentage of change for each promo type ranked by brand
	public JPanel brandsPerPromotionalTypeTable(String typeOfPromoSupport) {
		JPanel input = new JPanel();
		input.setBackground(Color.WHITE);
		input.setLayout(new BoxLayout(input, BoxLayout.Y_AXIS));
		JLabel title = new JLabel(typeOfPromoSupport);
		input.add(title);
		PromoAnalysis promotional = this.getPromoAnalysis();
		Hashtable<String, BrandsData> brands = promotional.getPromoPerBrand();
		ArrayList<String> ranking = promotional.getRankedBrands(typeOfPromoSupport);
		Font f = new Font(Font.SERIF, Font.ROMAN_BASELINE, 13);
		Object [] [] data = new Object[12][4];
		String [] colNames = {"Brand", "Dollars %Chg. YA", "Level", "Promo %Chg. YA"};
		for(int j=0; j<4; j++) {
			data[0][j] = colNames[j];
		}
		for(int i=1; i<11; i++) {
			String name = ranking.get(i);
			data[i][1] = f2.format(((brands.get(name).getSales("current")/brands.get(name).getSales("past"))-1));
			data[i][2] = f2.format(this.getPromotionForAType(typeOfPromoSupport, brands, name));
			data[i][3] = f2.format(this.getLevelOfPromoTypePerBrand(typeOfPromoSupport, brands, name));
			data[i][0] = name;
		}
		
		JTable context = new JTable(data,colNames);
		input.add(context);
		
		return input;
	}
	
	//Helper method to get the level of promotion for a particular type 
	private Double getPromotionForAType(String type, Hashtable<String, BrandsData> data, String key) {
		if(type.equals("Quality")) {
			return (data.get(key).getQual("current")/data.get(key).getQual("past"))-1;
		} else if (type.equals("Feature")) {
			return (data.get(key).getFeat("current")/data.get(key).getQual("past"))-1;
		} else if (type.equals("Display")) {
			return (data.get(key).getDisplay("current")/data.get(key).getDisplay("past"))-1;
		} else if (type.equals("Price Disc.")) {
			return (data.get(key).getPriceDisc("current")/data.get(key).getPriceDisc("past"))-1;
		} else {
			return 1.0;
		}
	}
	
	//Helper method to get level of promotional type for a brand
	private double getLevelOfPromoTypePerBrand(String type, Hashtable<String, BrandsData> data, String key) {
		if(type.equals("Quality")) {
			return data.get(key).getQual("current")/data.get(key).getSales("current");
		} else if (type.equals("Feature")) {
			return data.get(key).getFeat("current")/data.get(key).getSales("current");
		} else if (type.equals("Display")) {
			return data.get(key).getDisplay("current")/data.get(key).getSales("current");
		} else if (type.equals("Price Disc.")) {
			return data.get(key).getPriceDisc("current")/data.get(key).getSales("current");
		} else {
			return 1.0;
		}
	}
	
	private static DecimalFormat f2 = new DecimalFormat("##.##%");
	
	public static void main(String[] args) throws FileNotFoundException {
		PromoInterface promo = new PromoInterface("/Users/Konstantine/Desktop/Programs/Java/Java Projects/Testing data/multivitaminsOnlyMeijerData2.xls");
		
	}

}
