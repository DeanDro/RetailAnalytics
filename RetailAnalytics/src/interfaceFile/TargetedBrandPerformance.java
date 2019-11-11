package interfaceFile;

import java.io.FileNotFoundException;
import java.util.*;

import interfaceFile.PricingAnalysis.Pricing;
import interfaceFile.PromoAnalysis.BrandsData;

public class TargetedBrandPerformance extends DataAnalysis{
	
	private DataAnalysis data;
	private HashMap<String, DataStorage> allItems;
	private HashMap<String, DataStorage> newItems;
	private HashMap<String, DataStorage> targetNewItems;
	private String targetedBrand;
	private PricingAnalysis pricingData;
	private PromoAnalysis promoData;
	
	public TargetedBrandPerformance(String filepath) throws FileNotFoundException {
		super(filepath);
		this.allItems = super.getAllItems();
		this.newItems = super.returnNewItems();
		//this.targetedBrand = super.getTargetBrand();
		this.pricingData = new PricingAnalysis(filepath);
		this.promoData = new PromoAnalysis(filepath);
		this.targetedBrand = "CENTRUM";
		this.targetNewItems = new HashMap<String,DataStorage>();
		super.loadAllItems();
		this.loadData();
	}
	
	/*
	 * ----------------NEW ITEMS SEGMENT--------------
	 */
	
	//Get all new items for the target brand and capture sales, share of brand
	public void getTargetBrandItems(){
		HashMap<String, DataStorage> newSkus = super.returnNewItems();
		for(String key: newSkus.keySet()) {
			if(newSkus.get(key).getBrand().contains(this.targetedBrand)) {
				targetNewItems.put(key, newSkus.get(key));
			}
		}
	}
	
	//Get targeted brand 
	public String getTargetedBrandLink() {
		return this.targetedBrand;
	}
	
	//Get the hashMap with all the new items in the targeted brand
	public HashMap<String, DataStorage> getItemsInTargetedBrand(){
		return this.targetNewItems;
	}
	
	//Get comparison of targeted brand with competitive brands with new items. Check only from new items perspective
	public HashMap<String, NewSku> getComparisonWithCompetitiveNewItems(){
		HashMap<String, NewSku> brandingNewSkus = new HashMap<String, NewSku>();
		HashMap<String, DataStorage> newSkus = super.returnNewItems();
		for(String key: newSkus.keySet()) {
			String brand = newSkus.get(key).getBrand();
			DataStorage data = newSkus.get(key);
			if(brandingNewSkus.containsKey(brand)) {
				this.allocateSkuDataExistingBrands(brandingNewSkus, data);
			} else {
				this.allocateSkuDataNewBrands(brandingNewSkus, data);
			}
		}
		return brandingNewSkus;
	}
	
	//Helper method for the brands that are already included in the new skus branding map
	private void allocateSkuDataExistingBrands(HashMap<String,NewSku> items, DataStorage data) {
		Double [] collection = new Double[9];
		collection[0] = data.getSales("current");
		collection[1] = data.getAnyPromo("current");
		collection[2] = data.getQual("current");
		collection[3] = data.getFeat("current");
		collection[4] = data.getDisplay("current");
		collection[5] = data.getPriceDisc("current");
		collection[6] = data.getPrice("current");
		collection[7] = data.getNoPromoPrice("current");
		collection[8] = data.getAnyPromoPrice("current");
		String brand = data.getBrand();
		String [] measures = {"sales", "promo", "quality", "feat", "display", "priceDisc", "avgPrice", "noPromoPrice", "promoPrice"};
		NewSku input = items.get(brand);
		for(int i=0; i<measures.length; i++) {
			double add = input.getMeasure(measures[i])+collection[i];
			input.setMeasure(measures[i], add);
			items.put(brand, input);
		}
	}
	
	//Helper method for brands which are new to the map
	private void allocateSkuDataNewBrands(HashMap<String, NewSku> items, DataStorage data) {
		Double [] collection = new Double[9];
		collection[0] = data.getSales("current");
		collection[1] = data.getAnyPromo("current");
		collection[2] = data.getQual("current");
		collection[3] = data.getFeat("current");
		collection[4] = data.getDisplay("current");
		collection[5] = data.getPriceDisc("current");
		collection[6] = data.getPrice("current");
		collection[7] = data.getNoPromoPrice("current");
		collection[8] = data.getAnyPromoPrice("current");
		String brand = data.getBrand();
		NewSku addition = new NewSku(brand, collection);
		items.put(brand, addition);
	}

	@Override
	public void loadData() throws FileNotFoundException {
		// TODO Auto-generated method stub
		this.getTargetBrandItems();
	}
	
	//Get promo analysis data
	public PromoAnalysis getPromoAnalysisData() {
		return this.promoData;
	}
	
	//Get data for data analysis
	public DataAnalysis getDataAnalyisis() {
		return this.data;
	}
	
	//Method to calculate dollar sales and percent change excluding new items 
	public Hashtable<String, ArrayList<Double>> getSalesPerformanceWithoutNewItems(){
		Hashtable<String, ArrayList<Double>> data = new Hashtable<String, ArrayList<Double>>();
		Hashtable<String, Pricing> testedBrands = this.getPricingDataForTarget();
		HashMap<String, DataStorage> items = this.getAllItems();
		for(String key: testedBrands.keySet()) {
			ArrayList<Double> getPerformance = new ArrayList<Double>();
			double valueNewItems = 0.0;
			double valueAllItems = 0.0;
			double valueAILast = 0.0;
			double numNewItems = 0.0;
			for(String upc:items.keySet()) {
				if(items.get(upc).getBrand().equals(this.targetedBrand)) {
					if(items.get(upc).getSales("current")>0 && items.get(upc).getSales("past")==0) {
						valueNewItems +=items.get(upc).getSales("current");
						numNewItems+=1.0;
					} 
					valueAllItems += items.get(upc).getSales("current");
					valueAILast += items.get(upc).getSales("past");
				} 
			}
			getPerformance.add(valueAllItems);
			getPerformance.add(valueAILast);
			getPerformance.add(valueNewItems);
			getPerformance.add(numNewItems);
			data.put(key, getPerformance);
		}
		return data;
	}
	
	//Private class for comparing targeted brand vs other brands on new items
	class NewSku{
		private String brand;
		private double sales;
		private double anyPromo;
		private double qualityPromo;
		private double featPromo;
		private double displayPromo;
		private double priceDisc;
		private double avgPrice;
		private double noPromoPrice;
		private double promoPrice;
		
		public NewSku(String brand, Double[] collection) {
			this.brand = brand;
			this.sales = collection[0];
			this.anyPromo = collection[1];
			this.qualityPromo = collection[2];
			this.featPromo = collection[3];
			this.displayPromo= collection[4];
			this.priceDisc = collection[5];
			this.avgPrice = collection[6];
			this.noPromoPrice = collection[7];
			this.promoPrice = collection[8];
		}
		
		//Getter methods
		public String getBrand() {
			return this.brand;
		}
		
		public Double getMeasure(String type) {
			if(type.equals("sales")) {
				return this.sales;
			} else if (type.equals("promo")) {
				return this.anyPromo;
			} else if (type.equals("quality")) {
				return this.qualityPromo;
			} else if (type.equals("feat")) {
				return this.featPromo;
			} else if (type.equals("display")) {
				return this.displayPromo;
			} else if (type.equals("priceDisc")) {
				return this.priceDisc;
			} else if (type.equals("avgPrice")) {
				return this.avgPrice;
			} else if (type.equals("noPromoPrice")) {
				return this.noPromoPrice;
			} else if (type.equals("promoPrice")) {
				return this.promoPrice;
			} else {
				return 1.0;
			}
		}
		
		//Setter methods
		public void setBrand(String brand) {
			this.brand = brand;
		}
		
		public void setMeasure(String type, double measure) {
			if(type.equals("sales")) {
				this.sales = measure;
			} else if (type.equals("promo")) {
				this.anyPromo = measure;
			} else if (type.equals("quality")) {
				this.qualityPromo=measure;
			} else if (type.equals("feat")) {
				this.featPromo = measure;
			} else if (type.equals("display")) {
				this.displayPromo = measure;
			} else if (type.equals("priceDisc")) {
				this.priceDisc = measure;
			} else if (type.equals("avgPrice")) {
				this.avgPrice = measure;
			} else if (type.equals("noPromoPrice")) {
				this.noPromoPrice=measure;
			} else if (type.equals("promoPrice")) {
				this.promoPrice = measure;
			} 
		}
	}
	
	/*
	 * -------------------PROMO ANALYSIS FOR TARGETED BRAND----------------------------
	 */
	
	//Method to calculate average promotional figures for each brand with new items
	public Hashtable<String,PromoData> getAveragePromoDataBrands(){
		HashMap<String, DataStorage> allBrands = super.getAllItems();
		Hashtable<String, PromoData> averagePromoFigures = new Hashtable<String, PromoData>();
		for(String key: allBrands.keySet()) {
			String brand = allBrands.get(key).getBrand();
			DataStorage data = allBrands.get(key);
			if(averagePromoFigures.containsKey(brand)) {
				this.allocateValuesHashtable(averagePromoFigures, data, brand);
			} else {
				this.allocateNewValuesHashtable(averagePromoFigures, data, brand);
			}
		}
		return averagePromoFigures;
	}
	
	//helper method to allocate data for the average promoted values for each brand
	private void allocateValuesHashtable(Hashtable<String, PromoData> table, DataStorage data, String key) {
		Double [] values = new Double[8];
		values[0] = table.get(key).getMeasurement("numItems") + 1;
		if(data.getAnyPromo("current")>0) {
			values[1] = table.get(key).getMeasurement("numPromoItems") + 1;
		} else {
			values[1] = table.get(key).getMeasurement("numPromoItems");
		}
		values[2] = table.get(key).getMeasurement("sales")+data.getSales("current");
		values[3] = table.get(key).getMeasurement("anyPromo") +data.getAnyPromo("current");
		values[4] = table.get(key).getMeasurement("display") + data.getDisplay("current");
		values[5] = table.get(key).getMeasurement("feat") + data.getFeat("current");
		values[6] = table.get(key).getMeasurement("quality") + data.getQual("current");
		values[7] = table.get(key).getMeasurement("priceDisc") + data.getPriceDisc("current");
		PromoData addition = new PromoData(values, data.getBrand());
		table.put(data.getBrand(), addition);
	}
	
	//Helper method to add PromoData for the first time in the hashtable
	private void allocateNewValuesHashtable(Hashtable<String, PromoData> table, DataStorage data, String key) {
		Double [] values = new Double[8];
		values[0] = 1.0;
		if(data.getAnyPromo("current")>0) {
			values[1] = 1.0;
		} else {
			values[1] = 0.0;
		}
		values[2] = data.getSales("current");
		values[3] = data.getAnyPromo("current");
		values[4] = data.getDisplay("current");
		values[5] = data.getFeat("current");
		values[6] = data.getQual("current");
		values[7] = data.getPriceDisc("current");
		PromoData addition = new PromoData(values, data.getBrand());
		table.put(key, addition);
	}
	
	//Method to get brand ranking for promotional type and total sales
	public Hashtable<String, BrandsData> getPromoPerBrandAndTotalSales(String type){
		return this.promoData.getPromoPerBrand();
	}
	
	
	//Private class to store data for promotional analysis
	class PromoData{
		private String brand;
		private double numItems;
		private double numPromoItems;
		private double sales;
		private double anyPromo;
		private double display;
		private double feat;
		private double qual;
		private double priceDisc;
		
		public PromoData(Double [] values, String brand) {
			this.brand = brand;
			this.numItems = values[0];
			this.numPromoItems = values[1];
			this.sales = values[2];
			this.anyPromo = values[3];
			this.display = values[4];
			this.feat = values[5];
			this.qual = values[6];
			this.priceDisc = values[7];
		}
		
		
		//Getter methods
		public String getBrand() {
			return this.brand;
		} 
		
		public Double getMeasurement(String type) {
			switch (type) {
			case "sales": return this.sales;
			case "anyPromo": return this.anyPromo;
			case "display":	return this.display;
			case "feat": return this.feat;
			case "quality": return this.qual;
			case "priceDisc": return this.priceDisc;
			case "numItems": return this.numItems;
			case "numPromoItems": return this.numPromoItems;
			default: return 1.0;
			}
		}
		
		//Setter methods
		public void setBrand(String brand) {
			this.brand = brand;
		}
		
		public void setMeasurement(Double value, String target) {
			switch (target) {
			case "numItems": this.numItems =value;
			case "numPromoItems": this.numPromoItems=value;
			case "sales": this.sales = value;
			case "anyPromo": this.anyPromo = value;
			case "display": this.display = value;
			case "feat": this.feat = value;
			case "quality": this.qual = value;
			case "priceDisc": this.priceDisc = value;
			}
		}
	}
	
	/*
	 * -------------------PRICING ANALYSIS FOR TARGETED BRAND-------------------------
	 */
	
	//Getter on pricing data
	public PricingAnalysis getPricingData() {
		return this.pricingData;
	}
	
	//Method to get data for target brand and top 2 competitive brands
	public Hashtable<String, Pricing> getPricingDataForTarget(){
		Hashtable<String, Pricing> output = new Hashtable<String, Pricing>();
		HashMap<String, Pricing> data = this.pricingData.getAvgPricesPerBrand();
		ArrayList<String> rankedBrands = this.promoData.getRankedBrands("sales");
		for(String key: data.keySet()) {
			if(key.equals(this.targetedBrand)) {
				output.put(key, data.get(key));
			} else if(key.equals(rankedBrands.get(1)) || key.equals(rankedBrands.get(0))) {
				output.put(key, data.get(key));
			}
		}
		
		return output;
	}
	
	//Method to get pricing data for category
	public HashMap<String, Double> getCategoryPricing(){
		return this.getPricingData().categoryPricing();
	}
	
	public static void main(String []args) throws FileNotFoundException {
		TargetedBrandPerformance target = new TargetedBrandPerformance("/Users/Konstantine/Desktop/Programs/Java/Java Projects/Testing data/multivitaminsOnlyMeijerData2.xls");
		//HashMap<String, Double> input = target.getCategoryPricing();
		Hashtable<String, Pricing> input = target.getPricingDataForTarget();
		for(String key: input.keySet()) {
			System.out.println(key+" "+input.get(key).getAvgPrice("current"));
		}
	}

}
