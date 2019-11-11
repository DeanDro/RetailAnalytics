package interfaceFile;

import java.io.*;
import java.text.DecimalFormat;

import org.apache.poi.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.Package;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.hssf.*;
import org.apache.poi.xssf.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.*;
import org.apache.xmlbeans.*;

import java.util.*;
import javax.swing.*;

public class SalesAnalysis extends DataAnalysis{
	
	private HashMap<String, DataStorage> allItems;
	private HashMap<String, DataStorage> newItems;
	private static String thisPeriod = "current";
	private static String lastPeriod = "last";
	private ArrayList<String> allKeys;
	private ArrayList<String> allBrands;
	private HashMap<String, Double> promoTypes;
	private HashMap<String, brandStore> brandRanking;
	private ArrayList<String> brandsRank;
	private HashMap<String, upcStore> upcRanking;
	private ArrayList<String> rankedUPCKeys;
	
	public SalesAnalysis(String filepath) throws FileNotFoundException {
		super(filepath);
		this.allItems = this.getAllItems();
		this.newItems = this.returnNewItems();
		this.promoTypes = new HashMap<String, Double>();
		this.brandsRank = new ArrayList<String>();
		this.upcRanking = new HashMap<String, upcStore>();
		this.rankedUPCKeys = new ArrayList<String>();
		this.brandRanking = new HashMap<String, brandStore>();
		this.allBrands = new ArrayList<String>();
		this.allKeys = new ArrayList<String>();
		this.loadData();
	}
	
	//It gives only the brands that have new items
	public ArrayList<String> getRankingforBrands(){
		return this.brandsRank;
	}
	
	public ArrayList<String> getRankingforUPC(){
		return this.rankedUPCKeys;
	}
	
	public HashMap<String, Double> getPromoPerType(){
		return this.promoTypes;
	}
	
	public HashMap<String, brandStore> getBrandsMap(){
		return this.brandRanking;
	}
	
	public HashMap<String, upcStore> getUPCMap(){
		return this.upcRanking;
	}
	
	//Format numbers to only have two decimal points
	private static DecimalFormat df2 = new DecimalFormat(".##");
	
	//helper method to populate allKeys
	private void populateAllKeys() {
		ArrayList<String> collectKeys = new ArrayList<String>();
		ArrayList<String> collectBrands = new ArrayList<String>();
		for(String key:allItems.keySet()) {
			if(allItems.get(key).getUPC()!=null) {
				collectKeys.add(allItems.get(key).getUPC());
				String brand = allItems.get(key).getBrand();
				if(!collectBrands.contains(brand)) {
					collectBrands.add(brand);
				}
			}
		}
		this.allKeys = collectKeys;
		this.allBrands = collectBrands;
	}
	
	//Get upc level ranking
	public void getUPCRanking(){
		
		for(String key: allItems.keySet()) {
			if(allItems.get(key).getSales(thisPeriod)>0 && allItems.get(key).getSales(lastPeriod)==0) {
				upcStore input = new upcStore(key, allItems.get(key).getSales(thisPeriod));
				upcRanking.put(key, input);
			}
		}
		
		List<upcStore> rank = new ArrayList<upcStore>(upcRanking.values());
		Collections.sort(rank, new Comparator<upcStore>() {
			public int compare(upcStore upc1, upcStore upc2) {
				double value = (upc2.getUPCSales() - upc1.getUPCSales());
				return (int) Math.round(value);
			}
		});
		
		for(upcStore upc: rank) {
			rankedUPCKeys.add(upc.getUPC());
		}
	}
	
	//This will give us a brand ranking for new items
	public void getBrandRanking(){
		
		for(String key: allItems.keySet()) {
			String brand = allItems.get(key).getBrand();
			if(allItems.get(key).getSales(thisPeriod)>0.0 && allItems.get(key).getSales(lastPeriod)==0.0) {
				if(brandRanking.containsKey(brand)) {
					brandStore target = brandRanking.get(brand);
					double sales = target.getTotalSales();
					sales = sales+allItems.get(key).getSales(thisPeriod);
					brandRanking.put(brand, new brandStore(brand,sales));
				} else {
					double sales = allItems.get(key).getSales(thisPeriod);
					brandRanking.put(brand, new brandStore(brand, sales));
				}
			}
		}
		
		//Not sorted yet
		List<brandStore> nameList = new ArrayList<brandStore>(brandRanking.values());
		Collections.sort(nameList, new Comparator<brandStore>() {
			public int compare(brandStore b1, brandStore b2) {
				double value =(b2.getTotalSales() - b1.getTotalSales());
				return (int) Math.round(value);
			}
		});
		
		for(brandStore br:nameList) {
			brandsRank.add(br.brandName);
		}
		
		//Testing content
		/*for(int i=0; i<nameList.size(); i++) {
			System.out.println(i+" "+nameList.get(i).brandName+"\t"+nameList.get(i).totalSales);
		}*/
	
	}
	
	public double getPercPromoSales() {
		double promoSales = 0.0;
		double totalSales = 0.0;
		
		for(String key:newItems.keySet()) {
			promoSales+=newItems.get(key).getAnyPromo(thisPeriod);
			totalSales+=newItems.get(key).getSales(thisPeriod);
		}
		
		return Math.round((promoSales/totalSales)*100)/100;
	}
	
	//Get percentage of each promo type for the new items
	public void getPromoTypes(){
		
		double anyPromoSales=0.0;
		double featSales = 0.0;
		double displaySales = 0.0;
		double priceDiscSales = 0.0;
		double featAndDisp = 0.0;
		double qual = 0.0;
		double totalSales = 0.0;
		
		for(String key: allItems.keySet()) {
			if((allItems.get(key).getSales(lastPeriod)==0)){
				anyPromoSales += allItems.get(key).getAnyPromo(thisPeriod);
				featSales+=allItems.get(key).getFeat(thisPeriod);
				displaySales+=allItems.get(key).getDisplay(thisPeriod);
				priceDiscSales+=allItems.get(key).getPriceDisc(thisPeriod);
				featAndDisp+=allItems.get(key).getFandD(thisPeriod);
				qual+=allItems.get(key).getQual(thisPeriod);
				totalSales+=allItems.get(key).getSales(thisPeriod);
			}
		}
		
		promoTypes.put("Any Promo", Double.parseDouble(df2.format(anyPromoSales/totalSales)));
		promoTypes.put("Feat Sales", Double.parseDouble(df2.format(featSales/totalSales)));
		promoTypes.put("Display Sales", Double.parseDouble(df2.format(displaySales/totalSales)));
		promoTypes.put("Price Discount", Double.parseDouble(df2.format(priceDiscSales/totalSales)));
		promoTypes.put("Feat & Display", Double.parseDouble(df2.format(featAndDisp/totalSales)));
		promoTypes.put("Quality Merchandise", Double.parseDouble(df2.format(qual/totalSales)));
		
	}
	
	//TESTING ACCURACY METHOD 
	public void testingMethod() {
		
		HashMap<String, Double> test = this.newItemsVScategory();
		System.out.println(test);
		HashMap<String, Double> test2 = this.convertToPercentage(test);
		System.out.println(test2);
	}
	
	//Load method
	@Override
	public void loadData() throws FileNotFoundException {
		this.loadAllItems();
		this.populateAllKeys();
		this.getBrandRanking();
		this.getUPCRanking();
		this.getPromoTypes();
		this.getPercPromoSales();
		this.getNewItems();
		System.out.println("Data load completed");
	}
	
	//Method to take comparison for new items versus total category
	public HashMap<String, Double> newItemsVScategory(){
		HashMap<String, Double> newItemsComparison = new HashMap<String, Double>();
		
		for(String key:allItems.keySet()) {
			double sales = allItems.get(key).getSales(thisPeriod);
			double salesYA = allItems.get(key).getSales(lastPeriod);
			double anyPromo = allItems.get(key).getAnyPromo(thisPeriod);
			double priceDisc = allItems.get(key).getPriceDisc(thisPeriod);
			double feat = allItems.get(key).getFeat(thisPeriod);
			double qual = allItems.get(key).getQual(thisPeriod);
			double display = allItems.get(key).getDisplay(thisPeriod);
			double featAndDisplay = allItems.get(key).getFandD(thisPeriod);
			
			this.putDataInMap(newItemsComparison, "Total Sales", sales, salesYA, sales);
			this.putDataInMap(newItemsComparison, "Any Promo", sales, salesYA, anyPromo);
			this.putDataInMap(newItemsComparison, "Price Disc.", sales, salesYA, priceDisc);
			this.putDataInMap(newItemsComparison, "Feat", sales, salesYA, feat);
			this.putDataInMap(newItemsComparison, "Quality", sales, salesYA, qual);
			this.putDataInMap(newItemsComparison, "Display", sales, salesYA, display);
			this.putDataInMap(newItemsComparison, "Feature & Display", sales, salesYA, featAndDisplay);
		}
		
		return newItemsComparison;
	}
	
	//Helper method to convert comparison data for new items to percentage 
	public HashMap<String, Double> convertToPercentage(HashMap<String, Double> data){
		HashMap<String, Double> dataConverted = new HashMap<String, Double>();
		for(String key: data.keySet()) {
			if(key.matches("New Items")) {
				dataConverted.put(key, data.get(key)/data.get("New Items Total Sales"));
			} else {
				dataConverted.put(key, data.get(key)/data.get("Total Sales"));
			}
		}
		return dataConverted;
	}
	
	//Helper method to allocate data in a hashmap and distinguish between new and old items
	public void putDataInMap(HashMap<String, Double> database, String key, double sales, double salesYA, double value) {
		if(salesYA==0) {
			if(database.containsKey("New Items "+key)) {
				double newKeyValue = database.get("New Items "+key)+value;
				database.put("New Items "+key, newKeyValue);
			} else {
				String check = String.valueOf(value);
				if(!check.equals(null)) {
					database.put("New Items "+key, value);
				}
				
			}
		} 
		if(database.containsKey(key)) {
			double total = database.get(key)+value;
			database.put(key, total);
		} else {
			database.put(key, value);
		}
	}
	
	//Create internal class to store values for brand ranking
	class brandStore{
		private String brandName;
		private double totalSales;
		
		public brandStore(String name, Double sales) {
			this.brandName = name;
			this.totalSales = sales;
		}
		
		public String getName() {
			return brandName;
		}
		
		public Double getTotalSales() {
			return totalSales;
		}
	}
	
	class upcStore{
		private String upc;
		private double upcSales;
		
		public upcStore(String upc, double sales) {
			this.upc = upc;
			this.upcSales = sales;
		}
		
		public String getUPC() {
			return this.upc;
		}
		
		public Double getUPCSales() {
			return this.upcSales;
		}
	}
}

