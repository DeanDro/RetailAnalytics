package interfaceFile;

import java.io.FileNotFoundException;
import java.util.*;

public class PricingAnalysis extends DataAnalysis{
	
	private HashMap<String, DataStorage> allItems;
	private HashMap<String, DataStorage> newItems;
	private Hashtable<String, DataStorage> allBrands;
	private PromoAnalysis promo;
	private static String thisPeriod = "current";
	private static String lastPeriod = "past";
	
	public PricingAnalysis(String fileName) throws FileNotFoundException {
		super(fileName);
		this.allItems = super.getAllItems();
		this.newItems = super.returnNewItems();
		this.allBrands = super.getAllBrands();
		this.promo = new PromoAnalysis(fileName);
	}
	
	public HashMap<String, DataStorage> getAllItems(){
		return allItems;
	}
	
	public HashMap<String, DataStorage> returnNewItems(){
		return newItems;
	}
	
	public Hashtable<String, DataStorage> getAllBrands(){
		return this.allBrands;
	}
	
	public PromoAnalysis getPromoData() {
		return this.promo;
	}
	
	//Method to get the average price per brand
	public HashMap<String, Pricing> getAvgPricesPerBrand(){
		HashMap<String, Pricing> priceInfo = new HashMap<String, Pricing>();
		Hashtable<String, DataStorage> brandsData = this.getAllBrands();
		for(String key: brandsData.keySet()) {
			Double[] data = new Double[8];
			data[0] = brandsData.get(key).getPrice(thisPeriod);
			data[1] = brandsData.get(key).getPrice(lastPeriod);
			data[2] = brandsData.get(key).getAnyPromoPrice(thisPeriod);
			data[3] = brandsData.get(key).getAnyPromoPrice(lastPeriod);
			data[4] = brandsData.get(key).getNoPromoPrice(thisPeriod);
			data[5] = brandsData.get(key).getNoPromoPrice(lastPeriod);
			Double [] items = this.numberOfItems(key);
			data[6] = items[0];
			data[7] = items[1];
			Pricing input = new Pricing(data);
			priceInfo.put(key, input);
		}
		
		return priceInfo;
	}
	
	//Helper method to get num of items and num of promo items per brand 
	private Double[] numberOfItems(String key) {
		Double [] data = new Double[2];
		double numItems = 0.0;
		double numPromo = 0.0;
		for(String item: this.allItems.keySet()) {
			if(this.allItems.get(item).getBrand().equals(key)) {
				if(this.allItems.get(item).getSales(thisPeriod)>0) {
					numItems+=1;
				}
				if(this.allItems.get(item).getAnyPromo(thisPeriod)>0) {
					numPromo+=1;
				}
			}
		}
		data[0]=numItems;
		data[1] = numPromo;
		
		return data;
	}
	
	//Helper method to find the average price for each brand in each segment
	public void findAverages(HashMap<String, Pricing> data) {
		for(String brand:data.keySet()) {
			double num = data.get(brand).getNumItems();
			double promoNum = data.get(brand).getNumPromoItems();
			data.get(brand).setAvgPrice(thisPeriod, data.get(brand).getAvgPrice(thisPeriod)/num);
			data.get(brand).setAvgPrice(lastPeriod, data.get(brand).getAvgPrice(lastPeriod)/num);
			data.get(brand).setNoPromoPrice(thisPeriod, data.get(brand).getNonPromoPrice(thisPeriod)/num);
			data.get(brand).setNoPromoPrice(lastPeriod, data.get(brand).getNonPromoPrice(lastPeriod)/num);
			if(promoNum>0) {
				data.get(brand).setPromoPrice(thisPeriod, data.get(brand).getPromoPrice(thisPeriod)/promoNum);
				data.get(brand).setPromoPrice(lastPeriod, data.get(brand).getPromoPrice(lastPeriod)/promoNum);
			} else {
				data.get(brand).setPromoPrice(thisPeriod, 0.01);
				data.get(brand).setAvgPrice(lastPeriod, 0.01);
			}
		}
	}
	
	//Method to get the average prices in the category
	public HashMap<String, Double> categoryPricing(){
		HashMap<String, Double> category = new HashMap<String,Double>();
		HashMap<String, Pricing> brandsData = this.getAvgPricesPerBrand();
		double avgPrice=0, avgPriceYA=0, promo=0, promoYA=0, noPromo=0, noPromoYA=0;
		int i=0;
		for(String brand: brandsData.keySet()) {
			avgPrice = brandsData.get(brand).getAvgPrice(thisPeriod)+avgPrice;
			avgPriceYA = brandsData.get(brand).getAvgPrice(lastPeriod)+avgPriceYA;
			promo = brandsData.get(brand).getPromoPrice(thisPeriod)+promo;
			promoYA = brandsData.get(brand).getPromoPrice(lastPeriod)+promoYA;
			noPromo = brandsData.get(brand).getNonPromoPrice(thisPeriod)+noPromo;
			noPromoYA = brandsData.get(brand).getNonPromoPrice(lastPeriod)+noPromoYA;
			i++;
		}
		category.put("Avg Price", avgPrice);
		category.put("Avg PriceYA", avgPriceYA);
		category.put("Any Promo", promo);
		category.put("Any PromoYA", promoYA);
		category.put("No Promo", noPromo);
		category.put("No PromoYA", noPromoYA);
		
		for(String key:category.keySet()) {
			if(category.get(key)>0) {
				category.put(key, category.get(key)/i);
			}
		}
		
		return category;
	}
	
	//Method to get brand level data from the abstract class
	public Hashtable<String, DataStorage> getBrandLevelDataFromAbstract(){
		return super.getAllBrands();
	}
	
	//Method to get data for total category
	public DataStorage categoryData() {
		Hashtable<String, DataStorage> brandsData = this.getBrandLevelDataFromAbstract();
		ArrayList<String> brandsList = this.getPromoData().getRankedBrands("sales");
		String [] data = new String[23];
		for(int j=0; j<23; j++) {
			data[j]="0";
		}
		for(int i=0; i<brandsList.size(); i++) {
			String brand = brandsList.get(i);
			data[0]=String.valueOf(Double.parseDouble(data[0])+1);//Here we will capture the total number of brands 
			data[1]=String.valueOf(Double.parseDouble(data[1])+brandsData.get(brand).getSales(thisPeriod));
			data[2] = String.valueOf(Double.parseDouble(data[2])+brandsData.get(brand).getSales(lastPeriod));
			data[3] = "100";
			data[4] = "100";
			data[5] = String.valueOf(Double.parseDouble(data[5])+brandsData.get(brand).getAnyPromo(thisPeriod));
			data[6] = String.valueOf(Double.parseDouble(data[6])+brandsData.get(brand).getAnyPromo(lastPeriod));
			data[7] = String.valueOf(Double.parseDouble(data[7])+brandsData.get(brand).getDisplay(thisPeriod));
			data[8] = String.valueOf(Double.parseDouble(data[8])+brandsData.get(brand).getDisplay(lastPeriod));
			data[9] = String.valueOf(Double.parseDouble(data[9])+brandsData.get(brand).getFeat(thisPeriod));
			data[10] = String.valueOf(Double.parseDouble(data[10])+brandsData.get(brand).getFeat(lastPeriod));
			data[11] = String.valueOf(Double.parseDouble(data[11])+brandsData.get(brand).getFandD(thisPeriod));
			data[12] = String.valueOf(Double.parseDouble(data[12])+brandsData.get(brand).getFandD(lastPeriod));
			data[13] = String.valueOf(Double.parseDouble(data[13])+brandsData.get(brand).getQual(thisPeriod));
			data[14] = String.valueOf(Double.parseDouble(data[14])+brandsData.get(brand).getQual(lastPeriod));
			data[15] = String.valueOf(Double.parseDouble(data[15])+brandsData.get(brand).getPriceDisc(thisPeriod));
			data[16] = String.valueOf(Double.parseDouble(data[16])+brandsData.get(brand).getPriceDisc(lastPeriod));
			data[17] = String.valueOf(Double.parseDouble(data[17])+brandsData.get(brand).getPrice(thisPeriod));
			data[18] = String.valueOf(Double.parseDouble(data[18])+brandsData.get(brand).getPrice(lastPeriod));
			data[19] = String.valueOf(Double.parseDouble(data[19])+brandsData.get(brand).getAnyPromoPrice(thisPeriod));
			data[20] = String.valueOf(Double.parseDouble(data[20])+brandsData.get(brand).getAnyPromoPrice(lastPeriod));
			data[21] = String.valueOf(Double.parseDouble(data[22])+brandsData.get(brand).getNoPromoPrice(thisPeriod));
			data[22] = String.valueOf(Double.parseDouble(data[22])+brandsData.get(brand).getNoPromoPrice(lastPeriod));
		}
		for(int k=0; k<data.length; k++) {
			data[k]=String.valueOf(Double.parseDouble(data[k])/Double.parseDouble(data[0]));
		}
		DataStorage category = new DataStorage(data, "Total Category");
		return category;
	}
	
	
	@Override
	public void loadData() throws FileNotFoundException {
		// TODO Auto-generated method stub
		this.getAvgPricesPerBrand();
		
		
	}
	
	class Pricing{
		
		private double avgPrice;
		private double avgPriceYA;
		private double promoPrice;
		private double promoPriceYA;
		private double noPromoPrice;
		private double noPromoPriceYA;
		private double numItems;
		private double promoItems;
		
		public Pricing(Double [] data) {
			this.avgPrice = data[0];
			this.avgPriceYA = data[1];
			this.promoPrice = data[2];
			this.promoPriceYA = data[3];
			this.noPromoPrice = data[4];
			this.noPromoPriceYA = data[5];
			this.numItems = data[6];
			this.promoItems = data[7];
		}
		
		//setter methods
		public void setAvgPrice(String period, double newPrice) {
			if(period.equals("current")) {
				this.avgPrice = newPrice;
			} else {
				this.avgPriceYA = newPrice;
			}
		}
		
		public void setPromoPrice(String period, double newPrice) {
			if(period.equals("current")) {
				this.promoPrice = newPrice;
			} else {
				this.promoPriceYA = newPrice;
			}
		}
		
		public void setNoPromoPrice(String period, double newPrice) {
			if(period.equals("current")) {
				this.noPromoPrice = newPrice;
			} else {
				this.noPromoPriceYA = newPrice;
			}
		}
		
		//getter methods
		public Double getAvgPrice(String period) {
			if(period.equals("current")) {
				return this.avgPrice;
			} else {
				return this.avgPriceYA;
			}
		}
		
		public Double getPromoPrice(String period) {
			if(period.equals("current")) {
				return this.promoPrice;
			} else {
				return this.promoPriceYA;
			}
		}
		
		public Double getNonPromoPrice(String period) {
			if(period.equals("current")) {
				return this.noPromoPrice;
			} else {
				return this.noPromoPriceYA;
			}
		}
		
		public double getNumItems() {
			return this.numItems;
		}
		
		public double getNumPromoItems() {
			return this.promoItems;
		}
	}
	
	/*public static void main(String [] args) throws FileNotFoundException {
		PricingAnalysis test = new PricingAnalysis("/Users/Konstantine/Desktop/Programs/Java/Java Projects/Testing data/multivitaminsOnlyMeijerData2.xls");
		HashMap<String, Pricing> brands = test.getAvgPricesPerBrand();
		for(String key: brands.keySet()) {
			System.out.println(key+" "+brands.get(key).getAvgPrice(thisPeriod));
		}
	}*/

}
