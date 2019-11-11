package interfaceFile;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class StartingPage {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		StartingPage start = new StartingPage(null);
	}
	
	private String[] buttonsContent = {"New Items", "Pricing Gap", "Promotional Effectiveness", "Overall Report"};
	private String fileDirectory;
	private DataAnalysis data;
	
	public void setFileDirectory(String path) {
		this.fileDirectory = path;
	}
	
	public DataAnalysis getData() {
		return this.data;
	}
	
	public String getFileDirectory() {
		return this.fileDirectory;
	}
	
	public StartingPage(String path) throws IOException {
		//Create frame and setup path. Originally we setup path to null because we don't have imported data but after we 
		// import data, we set the filepath as the file path and pass this path through all classes
		this.fileDirectory = path;
		JFrame frame = new JFrame();
		frame.setSize(600,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setTitle("Retail Analytics");
		
		Container content = frame.getContentPane();
		content.setBackground(Color.WHITE);
		
		//Create content 
		//Starting with title
		JPanel name = new JPanel();
		JLabel label = new JLabel("Retail Analytics");
		Font LabelFont = new Font(Font.SANS_SERIF, Font.BOLD, 30);
		label.setFont(LabelFont);
		name.setBackground(Color.WHITE);
		name.add(label);
		
		//Create main selection buttons
		JPanel mainComp = new JPanel();
		GridLayout grid = new GridLayout(2,2);
		JPanel butPanel = new JPanel();
		butPanel.setSize(400,400);
		butPanel.setBackground(Color.WHITE);
		Font f = new Font(Font.SERIF, Font.BOLD, 20);
		butPanel.setLayout(grid);
		for(int i=0; i<4; i++) {
			JButton but = new JButton(buttonsContent[i]);
			but.setFont(f);
			but.setPreferredSize(new Dimension(100,100));
			this.connectWithAction(but, frame);
			butPanel.add(but, i);
		}
		mainComp.add(butPanel, BorderLayout.CENTER);	
		
		//get logo picture
		JPanel part2 = new JPanel();
		part2.setBackground(Color.WHITE);
		JLabel picture = this.getLogoImage("/Users/Konstantine/Desktop/Programs/Java/Java Projects/Testing data/analytics3.png");
		part2.add(picture, BorderLayout.CENTER);
		mainComp.add(part2, BorderLayout.AFTER_LAST_LINE);
		mainComp.setLayout(new BoxLayout(mainComp, BoxLayout.Y_AXIS));
		mainComp.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//Create bottom segment buttons
		JPanel inputPanel = new JPanel();
		Color darkGreen = new Color(0, 196, 88);
		inputPanel.setBackground(darkGreen);
		inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		Font f2 = new Font(Font.SERIF, Font.ITALIC, 15);
		String [] buttonsContent = {"Input Data", "Select Target Brand", "Unknown"};
		for(int i=0; i<buttonsContent.length; i++) {
			JButton botSegBut = new JButton(buttonsContent[i]);
			botSegBut.setFont(f2);
			this.bottomButtons(botSegBut);
			inputPanel.add(botSegBut, BorderLayout.CENTER);
		}
		
		//Add panels to content and make frame visible
		content.add(name, BorderLayout.BEFORE_FIRST_LINE);
		content.add(mainComp, BorderLayout.CENTER);
		content.add(inputPanel, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}
	
	private JLabel getLogoImage(String file) throws IOException {
		JLabel picture = new JLabel();
		picture.setBackground(Color.WHITE);
		try {
			BufferedImage br = ImageIO.read(new File(file));
			ImageIcon icon = new ImageIcon(br);
			int x = icon.getIconWidth();
			int y = icon.getIconHeight();
			int type = BufferedImage.TYPE_INT_RGB;
			BufferedImage dist = new BufferedImage(x, y,type);
			Graphics g = dist.getGraphics();
			g.drawImage(icon.getImage(), 0, 0, x, y, picture);
			g.dispose();
			ImageIcon test = new ImageIcon(dist);
			picture.setIcon(test);
		} catch (Exception e) {
			e.getMessage();
		}
		
		picture.setSize(100,100);
		return picture;
	}
	
	public void connectWithAction(JButton button, JFrame frame) {
		String context = button.getText();
		String file = this.getFileDirectory();
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					frame.dispose();
					if(context.equals("New Items")) {
						NewItemsFrame newItems = new NewItemsFrame(file);
					} else if(context.equals("Pricing Gap")) {
						PricingInterface pricing = new PricingInterface(file);
					} else if (context.equals("Promotional Effectiveness")) {
						PromoInterface promo = new PromoInterface(file);
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	
	public void bottomButtons(JButton button) {
		String context = button.getText();
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(context.equals("Input Data")) {
					final JFileChooser fc = new JFileChooser();
					int returnVal = fc.showOpenDialog(button);
					if(returnVal==JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						String path = file.getAbsolutePath();
						try {
							//we restart the up with the new link in file 
							StartingPage test = new StartingPage(path);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						System.out.println("File load was canceled");
					}
				}
			}
		});
	}

}
