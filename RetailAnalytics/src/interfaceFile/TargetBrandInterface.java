package interfaceFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.*;

public class TargetBrandInterface extends DataAnalysis{
	
	private String targetBrand;
	private HashMap<String, DataStorage> allItmes;
	private DataAnalysis data;
	
	
	public TargetBrandInterface(String filepath) throws FileNotFoundException{
		super(filepath);
		this.allItmes = super.getAllItems();
		this.loadListOfBrands();
	}
	
	public void setTargetBrand(String brand) {
		this.targetBrand = brand;
		super.setTargetBrand(brand);
	}
	
	public String getTarget() {
		return this.targetBrand;
	}
	
	public DataAnalysis getData() {
		return this.data;
	}
	
	public void loadListOfBrands() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000,800);
		frame.setLocationRelativeTo(null);
		frame.setBackground(Color.WHITE);
		
		Container content = frame.getContentPane();
		
		//Create total list
		JPanel mainPage = new JPanel();
		JLabel title = new JLabel("List of Brands");
		JPanel input = this.listOfBrands();
		mainPage.setBackground(Color.WHITE);
		mainPage.setLayout(new BoxLayout(mainPage, BoxLayout.Y_AXIS));
		
		mainPage.add(title);
		mainPage.add(input);
		JScrollPane scroll = new JScrollPane(mainPage);
		
		Font f = new Font(Font.SERIF, Font.BOLD,25);
		title.setFont(f);
		
		content.add(scroll, BorderLayout.CENTER);
		
		frame.setVisible(true);
		
	}
	
	public HashMap<String, DataStorage> getAllItems(){
		return this.allItmes;
	}
	
	public JPanel listOfBrands() {
		JPanel listBrands = new JPanel();
		listBrands.setLayout(new BoxLayout(listBrands, BoxLayout.Y_AXIS));
		ArrayList<String> brands = new ArrayList<String>();
		HashMap<String, DataStorage> items = this.getAllItems();
		for(String key:items.keySet()) {
			String target = items.get(key).getBrand();
			if(!brands.contains(target)) {
				brands.add(target);
				JRadioButton selection = new JRadioButton(target);
				selection.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						String choice = e.getActionCommand();
						setTargetBrand(choice);
					}
				});
				listBrands.add(selection);
				selection.setForeground(Color.BLACK);
			}
		}
		listBrands.setBackground(Color.WHITE);
		super.setTargetBrand(this.getTarget());
		
		return listBrands;
	}

	@Override
	public void loadData() throws FileNotFoundException {
		// TODO Auto-generated method stub
		
	}

}
