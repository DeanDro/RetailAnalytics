package interfaceFile;

//This class will be used to store the data for every item in the Linked List

public class DataStorage {
	
	private String brand; 
	private String upc;
	private double dollarSales;
	private double dollarSalesYA;
	private double acv;
	private double acvYA;
	private double anyPromo;
	private double anyPromoYA;
	private double display;
	private double displayYA;
	private double feat;
	private double featYA;
	private double featAndDisp;
	private double featAndDispYA;
	private double qual;
	private double qualYA;
	private double priceDisc;
	private double priceDiscYA;
	private double avgPrice;
	private double avgPriceYA;
	private double anyPromoPrice;
	private double anyPromoPriceYA;
	private double noPromoPrice;
	private double noPromoPriceYA;
	private double productType;
	
	public DataStorage(String [] data, String brand) {
		try { 
			this.brand = brand;
			this.upc = this.brandOrUPC(data[0]);
			this.dollarSales = this.convertToDouble(data[1]);
			this.dollarSalesYA = this.convertToDouble(data[2]);
			this.acv = this.convertToDouble(data[3]);
			this.acvYA = this.convertToDouble(data[4]);
			this.anyPromo = this.convertToDouble(data[5]);
			this.anyPromoYA = this.convertToDouble(data[6]);
			this.display = this.convertToDouble(data[7]);
			this.displayYA = this.convertToDouble(data[8]);
			this.feat = this.convertToDouble(data[9]);
			this.featYA = this.convertToDouble(data[10]);
			this.featAndDisp = this.convertToDouble(data[11]);
			this.featAndDispYA = this.convertToDouble(data[12]);
			this.qual = this.convertToDouble(data[13]);
			this.qualYA = this.convertToDouble(data[14]);
			this.priceDisc = this.convertToDouble(data[15]);
			this.priceDiscYA = this.convertToDouble(data[16]);
			this.avgPrice = this.convertToDouble(data[17]);
			this.avgPriceYA = this.convertToDouble(data[18]);
			this.anyPromoPrice = this.convertToDouble(data[19]);
			this.anyPromoPriceYA = this.convertToDouble(data[20]);
			this.noPromoPrice = this.convertToDouble(data[21]);
			this.noPromoPriceYA = this.convertToDouble(data[22]);
			if(this.dollarSales>0 && this.dollarSalesYA==0) {
				this.productType = 1.0;
			} else {
				this.productType = 0.0;
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}
	
	private String brandOrUPC(String in) {
		if(in.matches("[0-9]+")) {
			return in;
		} 
		return null;
	}
	
	private double convertToDouble(String input) {
		if(input.equals("")) {
			return 0.0;
		}
		return Double.parseDouble(input);
	}
	
	public double getSales(String input) {
		if (input.equals("current")) {
			return this.dollarSales;
		} else {
			return this.dollarSalesYA;
		}
	}
	
	public double getACV(String input) {
		if(input.equals("current")) {
			return this.acv;
		} else {
			return this.acvYA;
		}
	}
	
	public double getAnyPromo(String input) {
		if(input.equals("current")) {
			return this.anyPromo;
		}
		return this.anyPromoYA;
	}
	
	public double getDisplay(String input) {
		if(input.equals("current")) {
			return this.display;
		} else {
			return this.displayYA;
		}
	}
	
	public double getFeat(String input) {
		if(input.equals("current")) {
			return this.feat;
		} else {
			return this.featYA;
		}
	}
	
	public double getFandD(String input) {
		if (input.equals("current")) {
			return this.featAndDisp;
		} else {
			return this.featAndDispYA;
		}
	}
	
	public double getQual(String input) {
		if(input.equals("current")) {
			return this.qual;
		} else {
			return this.qualYA;
		}
	}
	
	public double getPriceDisc(String input) {
		if(input.equals("current")) {
			return this.priceDisc;
		} else {
			return this.priceDiscYA;
		}
	}
	
	public double getPrice(String input) {
		if(input.equals("current")) {
			return this.avgPrice;
		} else {
			return this.avgPriceYA;
		}
	}
	
	public String getUPC() {
		return this.upc;
	}
	
	public String getBrand() {
		return this.brand;
	}
	
	public double getAnyPromoPrice(String input) {
		if(input.equals("current")) {
			return this.anyPromoPrice;
		} else {
			return this.anyPromoPriceYA;
		}
	}
	
	public double getNoPromoPrice(String input) {
		if(input.equals("current")) {
			return this.noPromoPrice;
		} else {
			return this.noPromoPriceYA;
		}
	}
	
	public double getProductType() {
		return this.productType;
	}
	
	public void setProductType(double x) {
		this.productType = x;
	}
	
	//Setter methods
	public void setDataStorageMetrics(double value, String metric) {
		switch (metric) {
		case ("sales"): this.dollarSales=value;
		case ("salesYA"): this.dollarSalesYA=value;
		case ("ACV"): this.acv = value;
		case ("ACV YA"): this.acvYA=value;
		case ("anyPromo"): this.anyPromo = value;
		case ("anyPromoYA"): this.anyPromoYA = value;
		case ("display"): this.display = value;
		case ("displayYA"): this.displayYA = value;
		case ("feat"): this.feat = value;
		case ("featYA"): this.featYA = value;
		case ("FandD"): this.featAndDisp = value;
		case ("FandDYA"): this.featAndDispYA = value;
		case ("quality"): this.qual = value;
		case ("qualityYA"): this.qualYA = value;
		case ("priceDisc"): this.priceDisc = value;
		case ("priceDiscYA"): this.priceDiscYA = value;
		case ("price"): this.avgPrice = value;
		}
	}
	
	//helper method to add two dataStorage classes
	public DataStorage addTwoDataStorage(DataStorage d2) {
		String [] data = new String[23];
		if(this.getBrand().equals(d2.getBrand())) {
			data[0]= "Combination";
			data[1] = String.valueOf(this.getSales("current") +d2.getSales("current"));
			data[2] = String.valueOf(this.getSales("past")+d2.getSales("past"));
			data[3] = String.valueOf((this.getACV("current")+d2.getACV("current"))/2);
			data[4] = String.valueOf((this.getACV("past")+d2.getACV("past"))/2);
			data[5] = String.valueOf((this.getAnyPromo("current")+d2.getAnyPromo("current")));
			data[6] = String.valueOf(this.getAnyPromo("past")+d2.getAnyPromo("past"));
			data[7] = String.valueOf(this.getDisplay("current")+d2.getDisplay("current"));
			data[8] = String.valueOf(this.getDisplay("past")+d2.getDisplay("past"));
			data[9] = String.valueOf(this.getFeat("current")+d2.getFeat("current"));
			data[10] = String.valueOf(this.getFeat("past") +d2.getFeat("past"));
			data[11] = String.valueOf(this.getFandD("current")+d2.getFandD("current"));
			data[12] = String.valueOf(this.getFandD("past")+d2.getFandD("past"));
			data[13] = String.valueOf(this.getQual("current") +d2.getQual("current"));
			data[14] = String.valueOf(this.getQual("past")+d2.getQual("past"));
			data[15] = String.valueOf(this.getPriceDisc("current")+d2.getPriceDisc("current"));
			data[16] = String.valueOf(this.getPriceDisc("past")+d2.getPriceDisc("past"));
			data[17] = String.valueOf((this.getPrice("current")+d2.getPrice("current"))/2);
			data[18] = String.valueOf((this.getPrice("past")+d2.getPrice("past"))/2);
			data[19] = String.valueOf((this.getAnyPromoPrice("current")+d2.getAnyPromoPrice("current"))/2);
			data[20] = String.valueOf((this.getAnyPromoPrice("past")+d2.getAnyPromoPrice("past"))/2);
			data[21] = String.valueOf((this.getNoPromoPrice("current")+d2.getNoPromoPrice("current"))/2);
			data[22] = String.valueOf((this.getNoPromoPrice("past")+d2.getNoPromoPrice("past"))/2);
			String brand = d2.getBrand();
			DataStorage result = new DataStorage(data, brand);
			result.setProductType((this.getProductType()+d2.getProductType()));
			return result;
		}
		return null;
	}

}
