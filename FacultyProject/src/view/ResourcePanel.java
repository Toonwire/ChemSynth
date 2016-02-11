package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import controller.ResourceController;
import model.Model;

public class ResourcePanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
//	------------------------------------------------
//	-                   Resources                  -
//	-                                              -
//	- Chemicals			             Amounts       -
//	-   *****                           ***** +-   -
//	-   *****                           ***** +-   -
//	-   *****                           ***** +-   -
//	-                                              -
//	-            Seeking:  *****                   -
//	-											   -
//	-                                   SYNTHESIZE -
//	------------------------------------------------
	private final int SIZE = 800;
	private final String chemTextPlaceholder = "Enter chemical resource";
	private final String seekingTextPlaceholder = "Enter desired chemical";
	
	private JLabel titleLabel = new JLabel("Resources");
	private JLabel chemLabel = new JLabel("Chemicals");
	private JLabel seekLabel = new JLabel("Seeking");
	private JLabel amountLabel = new JLabel("Amounts");
	private LinkedList<JTextField> chemList = new LinkedList<JTextField>();
	
	private JButton synthButton = new JButton("Synthesize");
	
	private JTextField seekingTextField = new JTextField();
	
	
	public ResourcePanel(Model model){
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		this.setLayout(null);
		
		titleLabel.setBounds(SIZE/2-70, 40, 200, 50);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
		
		chemLabel.setBounds(40, SIZE/2-150, 200, 50);
		chemLabel.setFont(new Font("Arial", Font.BOLD, 20));
		
		amountLabel.setBounds(SIZE-200, SIZE/2-150, 200, 50);
		amountLabel.setFont(new Font("Arial", Font.BOLD, 20));
		
		seekLabel.setBounds(SIZE/2-150, SIZE-200, 100, 50);
		seekLabel.setFont(new Font("Arial", Font.BOLD, 20));
		
		synthButton.setBounds(SIZE-250, SIZE-100, 200, 50);
		synthButton.setFont(new Font("Arial", Font.BOLD, 18));
		
		seekingTextField.setBounds(SIZE/2-50, SIZE-200, 130, 50);
		seekingTextField.setFont(new Font("Plain", Font.ITALIC, 18));
		
		this.add(titleLabel);
		this.add(chemLabel);
		this.add(amountLabel);
		this.add(seekLabel);
		this.add(synthButton);
		
		this.add(seekingTextField);
		this.addChemTextField();
		
	}
	
	public void registerListeners(ResourceController controller) {
		getCurrentChemTextField().addFocusListener(controller);
		synthButton.addActionListener(controller);
	}
	
	public JTextField getCurrentChemTextField(){
		return chemList.getLast();
	}
	
	public void addChemTextField(){
		
		if (chemList.size() < 5){
			JTextField chemTextField = new JTextField(chemTextPlaceholder);
			chemList.add(chemTextField);
			
			int n = chemList.size();
			chemTextField.setFont(new Font("Plain", Font.ITALIC, 14));
			chemTextField.setBounds(60, SIZE/2-150+50*n, 200, 30);
			this.add(chemTextField);
		}
		
	}

	public String getChemPlaceholderText() {
		return chemTextPlaceholder;
	}

	public void update() {
		
	}

	public LinkedList<JTextField> getChemList() {
		return chemList;
	}

	public JTextField getPreviousChemTextField() {
		return chemList.get(chemList.size()-2);
	}
	
	public void addLimitErrorLabel() {
		JLabel limitErrorLabel = new JLabel("Limit reached");
		limitErrorLabel.setFont(new Font("Arial", Font.ITALIC, 16));
		limitErrorLabel.setForeground(Color.RED);
		limitErrorLabel.setBounds(60, SIZE/2-150+50*6, 200, 30);
		this.add(limitErrorLabel);
	}

}
