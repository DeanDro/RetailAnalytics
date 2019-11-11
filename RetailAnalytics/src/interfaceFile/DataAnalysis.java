package interfaceFile;

import java.util.*;
import java.io.*;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


//This is the abstract class based on which the analysis for the rest of the categories has been built
public abstract class DataAnalysis {
	
	private String filePath;
	private String targetBrand;
	private HashMap<String, DataStorage> allItems;
	private HashMap<String, DataStorage> newItems;
	private Hashtable<String, DataStorage> allBrands;//this was added last night to capture all brands
	private static String thisPeriod = "current";
	private static String lastPeriod = "past";
	
	public DataAnalysis(String filename) throws FileNotFoundException {
		this.filePath = filename;
		this.targetBrand = "";
		this.allItems = new HashMap<String, DataStorage>();
		this.newItems = new HashMap<String, DataStorage>();
		this.allBrands = new Hashtable<String, DataStorage>();
		this.loadAllItems();
		this.getNewItems();
	}
	
	public HashMap<String, DataStorage> getAllItems(){
		return allItems;
	}
	
	public HashMap<String, DataStorage> returnNewItems(){
		return newItems;
	}
	
	public void setFilePath(String filename) {
		this.filePath = filename;
	}
	
	public String returnFilepath() {
		String path = this.filePath;
		return path;
	}
	
	public void setTargetBrand(String brand) {
		this.targetBrand = brand;
	}
	
	public String getTargetBrand() {
		return this.targetBrand;
	}
	
	public Hashtable<String, DataStorage> getAllBrands(){
		return this.allBrands;
	}
	
	//Read file and load all items in the allItems hashMap
		public void loadAllItems() throws FileNotFoundException{
			try {
				String fileName = this.filePath;
				FileInputStream a = new FileInputStream(new File(fileName));
				
				//This reads a 97-2003 excel file. 
				HSSFWorkbook wb = new HSSFWorkbook(a);
				HSSFSheet sheet = (HSSFSheet) wb.getSheetAt(0);
				HSSFRow row;
				HSSFCell cell;
				
				int rows = sheet.getPhysicalNumberOfRows(); //this is the number of rows
				int cols = 0; //number of columns
				int tmp = 0;
				
				//The trick below ensures that you get the data properly even if they don't start at line 1
				for (int i=0; i<10||i<rows; i++) {
					row = sheet.getRow(i);
					if(row!=null) {
						tmp = sheet.getRow(i).getPhysicalNumberOfCells();
						if (tmp>cols) cols=tmp;
					}
				}
			
				//we want to keep the brand 
				String brand ="";
				for(int r=0; r<rows; r++) {
					row = sheet.getRow(r);
					if(row!=null) {
						String [] data = new String[23];
						for(int c=0; c<cols; c++) {
							cell = row.getCell(c); // here the code online had a (short) casting for c
							if(cell!=null) {
								String in = cell.toString();
								data[c] = in;
								if(in.matches("[^0-9]+")) {
									brand = in;
								} 
							}
						}
						
						//We only store upc because each item includes information about which brand they are
						if(data[0].matches("[0-9]+")) {
							DataStorage input = new DataStorage(data, brand);
							allItems.put(data[0], input);
						} else {
							DataStorage secondInput = new DataStorage(data, brand);
							allBrands.put(data[0], secondInput);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//This method will populate all new items hashMap
		public void getNewItems() {
			for(String key:allItems.keySet()) {
				double sales = allItems.get(key).getSales(thisPeriod);
				double salesYA = allItems.get(key).getSales(lastPeriod);
				if(sales>0 && salesYA==0) {
					DataStorage values = allItems.get(key);
					newItems.put(key, values);
				}
			}
			
		}
		
		public abstract void loadData() throws FileNotFoundException;
		
}
