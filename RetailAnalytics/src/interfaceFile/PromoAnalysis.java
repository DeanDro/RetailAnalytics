package interfaceFile;

import java.io.FileNotFoundException;
import java.util.*;

public class PromoAnalysis extends DataAnalysis{
	
	private Hashtable<String,Double> salesByPromo;
	private HashMap<String, DataStorage> allItems;
	private static String thisPeriod = "current";
	private static String lastPeriod = "past";

	public PromoAnalysis(String filename) throws FileNotFoundException {
		super(filename);
		this.salesByPromo = new Hashtable<String, Double>();
		this.allItems = super.getAllItems();
		this.loadData();
	}
	
	//Getter methods
	public Hashtable<String, Double> getSalesByPromo(){
		return this.salesByPromo;
	}
	
	public HashMap<String, DataStorage> getAllItems(){
		return this.allItems;
	}
	
	//Setter methods
	public void setSalesByPromo(String key, Double value) {
		this.salesByPromo.put(key, value);
	}
	
	//Get figures for each segment. Goal is to break down sales by segment
	public void getPromoShare() {
		HashMap<String, DataStorage> items = this.getAllItems();
		for(String key:items.keySet()) {
			Hashtable<String, Double> sales = new Hashtable<String, Double>();
			sales.put("Total Sales",items.get(key).getSales(thisPeriod));
			sales.put("Total Sales YA", items.get(key).getSales(lastPeriod));
			sales.put("Any Promo Sales", items.get(key).getAnyPromo(thisPeriod));
			sales.put("Any Promo Sales YA", items.get(key).getAnyPromo(lastPeriod));
			sales.put("Feature", items.get(key).getFeat(thisPeriod));
			sales.put("Feature YA", items.get(key).getFeat(lastPeriod));
			sales.put("Display", items.get(key).getDisplay(thisPeriod));
			sales.put("Display YA", items.get(key).getDisplay(lastPeriod));
			sales.put("Quality", items.get(key).getQual(thisPeriod));
			sales.put("Quality YA", items.get(key).getQual(lastPeriod));
			sales.put("Price Disc.", items.get(key).getPriceDisc(thisPeriod));
			sales.put("Price Disc.YA", items.get(key).getPriceDisc(lastPeriod));
			this.allocateValues(sales);
		}
	}
	
	//helper method to add values in the hash table
	private void allocateValues(Hashtable<String, Double> data) {
		for(String key:data.keySet()) {
			if(this.salesByPromo.containsKey(key)) {
				double input = this.salesByPromo.get(key)+ data.get(key);
				this.setSalesByPromo(key, input);
			} else {
				this.setSalesByPromo(key, data.get(key));
			}
		}
	}
	
	//Method to get the percentage of change for each promotional vehicle 
	public Hashtable<String, Double> promoPercChange(){
		Hashtable<String, Double> percChange = new Hashtable<String, Double>();
		percChange.put("Sales %Chg.", (this.getSalesByPromo().get("Total Sales")/this.getSalesByPromo().get("Total Sales YA"))-1);
		percChange.put("Feat %Chg.", (this.getSalesByPromo().get("Feature")/this.getSalesByPromo().get("Feature YA"))-1);
		percChange.put("Display %Chg.", (this.getSalesByPromo().get("Display")/this.getSalesByPromo().get("Display YA"))-1);
		percChange.put("Quality %Chg.", (this.getSalesByPromo().get("Quality")/this.getSalesByPromo().get("Quality YA"))-1);
		percChange.put("Price Disc. %Chg.", (this.getSalesByPromo().get("Price Disc.")/this.getSalesByPromo().get("Price Disc.YA"))-1);
		return percChange;
	}
	
	//Method to get promo change and sales change by brand per promo type
	public Hashtable<String, BrandsData> getPromoPerBrand(){
		Hashtable<String, BrandsData> brandsAnalysis = new Hashtable<String, BrandsData>();
		for(String key:this.allItems.keySet()) {
			String brand = this.allItems.get(key).getBrand();
			BrandsData addition = this.convertDataStorageToBrandsData(brand, this.allItems.get(key));
			if(brandsAnalysis.containsKey(brand)) {
				BrandsData existing = brandsAnalysis.get(brand);
				BrandsData updated = this.allocateDataStorage(existing, addition);
				brandsAnalysis.put(brand, updated);
			} else {
				brandsAnalysis.put(brand, addition);
			}
		}
		
		return brandsAnalysis;
	}
	
	//Create ranking on dollar sales per brand 
	public ArrayList<String> getRankedBrands(String type){
		ArrayList<String> rank = new ArrayList<String>();
		Hashtable<String, BrandsData> brands = this.getPromoPerBrand();
		ArrayList<BrandsData> temp = new ArrayList<BrandsData>(brands.values());
		Collections.sort(temp, new Comparator<BrandsData>() {
			@Override
			public int compare(BrandsData b1, BrandsData b2) {
				if(type.equals("Price Disc.")) {
					double value = b2.getPriceDisc(thisPeriod) - b1.getPriceDisc(thisPeriod);
					return (int) Math.round(value);
				}else if (type.equals("Feature")) {
					double value = b2.getFeat(thisPeriod) - b1.getFeat(thisPeriod);
					return (int) Math.round(value);
				}else if (type.equals("Display")) {
					double value = b2.getDisplay(thisPeriod) -b1.getDisplay(thisPeriod);
					return (int) Math.round(value);
				} else if (type.equals("Quality")) {
					double value = b2.getQual(thisPeriod) - b1.getQual(thisPeriod);
					return (int) Math.round(value);
				} else if (type.equals("sales")) {
					double value = b2.getSales(thisPeriod) - b1.getSales(thisPeriod);
					return (int) Math.round(value);
				} else {
					return 1;
				}
			}
		});
		
		for (int i=0; i<temp.size(); i++) {
			rank.add(temp.get(i).getBrand());
		}
		
		return rank;
	}
	
	//helper method to add BrandsData data to the existing brands analysis
	private BrandsData allocateDataStorage(BrandsData b1, BrandsData b2) {
		Double [] data = new Double[12];
		data[0] = b1.getSales(thisPeriod)+b2.getSales(thisPeriod);
		data[1] = b1.getSales(lastPeriod)+b2.getSales(lastPeriod);
		data[2] = b1.getAnyPromo(thisPeriod)+b2.getAnyPromo(thisPeriod);
		data[3] = b1.getAnyPromo(lastPeriod)+b2.getAnyPromo(lastPeriod);
		data[4] = b1.getPriceDisc(thisPeriod)+b2.getPriceDisc(thisPeriod);
		data[5] = b1.getPriceDisc(lastPeriod)+b2.getPriceDisc(lastPeriod);
		data[6] = b1.getFeat(thisPeriod)+b2.getFeat(thisPeriod);
		data[7] = b1.getFeat(lastPeriod)+b2.getFeat(lastPeriod);
		data[8] = b1.getDisplay(thisPeriod)+b2.getDisplay(thisPeriod);
		data[9] = b1.getDisplay(lastPeriod)+b2.getDisplay(lastPeriod);
		data[10] = b1.getQual(thisPeriod)+b2.getQual(thisPeriod);
		data[11] = b1.getQual(lastPeriod)+b2.getQual(lastPeriod);
		String brand = b2.getBrand();
		BrandsData input = new BrandsData(brand, data);
		return input;
	}
	
	//Second helper method to convert DataStorage data to BrandsData
	private BrandsData convertDataStorageToBrandsData(String brand, DataStorage o1) {
		Double [] data = new Double[12];
		data[0] = o1.getSales(thisPeriod);
		data[1] = o1.getSales(lastPeriod);
		data[2] = o1.getAnyPromo(thisPeriod);
		data[3] = o1.getAnyPromo(lastPeriod);
		data[4] = o1.getPriceDisc(thisPeriod);
		data[5] = o1.getPriceDisc(lastPeriod);
		data[6] = o1.getFeat(thisPeriod);
		data[7] = o1.getFeat(lastPeriod);
		data[8] = o1.getDisplay(thisPeriod);
		data[9] = o1.getDisplay(lastPeriod);
		data[10] = o1.getQual(thisPeriod);
		data[11] = o1.getQual(lastPeriod);
		
		BrandsData input = new BrandsData(brand, data);
		return input;
	}

	@Override
	public void loadData() throws FileNotFoundException {
		// TODO Auto-generated method stub
		this.getPromoShare();
	}
	
	class BrandsData{
		private String brand;
		private double sales;
		private double salesYA;
		private double anyPromo;
		private double anyPromoYA;
		private double priceDisc;
		private double priceDiscYA;
		private double feat;
		private double featYA;
		private double display;
		private double displayYA;
		private double qual;
		private double qualYA;
		
		public BrandsData(String brand, Double [] data) {
			this.sales = data[0];
			this.salesYA = data[1];
			this.anyPromo = data[2];
			this.anyPromoYA = data[3];
			this.priceDisc = data[4];
			this.priceDiscYA = data[5];
			this.feat = data[6];
			this.featYA = data[7];
			this.display = data[8];
			this.displayYA = data[9];
			this.qual = data[10];
			this.qualYA = data[11];
			this.brand = brand;
		}
		
		//Getter methods
		public String getBrand() {
			return this.brand;
		}
		
		public Double getSales(String time) {
			if(time.equals("current")) {
				return this.sales;
			} else {
				return this.salesYA;
			}
		}
		
		public Double getAnyPromo(String time) {
			if(time.equals("current")) {
				return this.anyPromo;
			} else {
				return this.anyPromoYA;
			}
		}
		
		public Double getPriceDisc(String time) {
			if(time.equals("current")) {
				return this.priceDisc;
			} else {
				return this.priceDiscYA;
			}
		}
		
		public Double getFeat(String time) {
			if(time.equals("current")) {
				return this.feat;
			} else {
				return this.featYA;
			}
		}
		
		public Double getDisplay(String time) {
			if(time.equals("current")) {
				return this.display;
			} else {
				return this.displayYA;
			}
		}
		
		public Double getQual(String time) {
			if(time.equals("current")) {
				return this.qual;
			} else {
				return this.qualYA;
			}
		}
	}
	
	public static void main(String []args) throws FileNotFoundException {
		PromoAnalysis promo = new PromoAnalysis("/Users/Konstantine/Desktop/Programs/Java/Java Projects/Testing data/multivitaminsOnlyMeijerData2.xls");
		promo.loadData();
		ArrayList<String> ranking = promo.getRankedBrands("Quality");
		System.out.println(ranking);
	}

}
