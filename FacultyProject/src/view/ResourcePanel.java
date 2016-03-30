package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
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
//	- Chemicals									   -
//	-   *****                                      -
//	-   *****                                      -
//	-   *****                                      -
//	-	*****									   -
//	-	*****									   -
//	-											   -
//	-                                              -
//	-            Desiring:  *****                  -
//	-											   -
//	-                                   SYNTHESIZE -
//	------------------------------------------------
	private final int SIZE = 800;
	private final String chemTextPlaceholder = " Enter chemical resource";
	private final String desiredTextPlaceholder = " Enter desired chemical";
	
	private JLabel titleLabel = new JLabel("Resources");
	private JLabel chemLabel = new JLabel("Chemicals");
	private JLabel desireLabel = new JLabel("Desiring");
	private JLabel errorLabel = new JLabel("");
	private JLabel readyLabel = new JLabel("");
	
	private JButton synthButton = new JButton("Synthesize");

	private JTextField desireTextField = new JTextField(desiredTextPlaceholder);
	
	
	private LinkedList<JTextField> chemList = new LinkedList<JTextField>();
	private HashMap<JTextField,String> resourceMap = new HashMap<JTextField,String>();
	
	public ResourcePanel(Model model){
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		this.setLayout(null);
		
		titleLabel.setBounds(SIZE/2-70, 40, 200, 50);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
		
		chemLabel.setBounds(40, SIZE/2-150, 200, 50);
		chemLabel.setFont(new Font("Arial", Font.BOLD, 20));
		
//		amountLabel.setBounds(SIZE-200, SIZE/2-150, 200, 50);
//		amountLabel.setFont(new Font("Arial", Font.BOLD, 20));
		
		desireLabel.setBounds(SIZE/2-200, SIZE-200, 100, 50);
		desireLabel.setFont(new Font("Arial", Font.BOLD, 20));
		
		synthButton.setBounds(SIZE-250, SIZE-100, 200, 50);
		synthButton.setFont(new Font("Arial", Font.BOLD, 18));
		synthButton.setEnabled(false);
		
		desireTextField.setBounds(SIZE/2-100, SIZE-200, 200, 50);
		desireTextField.setFont(new Font("Plain", Font.ITALIC, 16));
		desireTextField.setName("DesireTextField");

		errorLabel.setFont(new Font("Roman", Font.ITALIC, 15));
		errorLabel.setBounds(60, SIZE/2-150+50*6, 300, 30);
		errorLabel.setForeground(Color.RED);
		
		readyLabel.setFont(new Font("Roman", Font.ITALIC, 18));
		readyLabel.setBounds(SIZE-200, SIZE-150, 200, 50);
		
//		// Dark theme
//		chemLabel.setForeground(Color.WHITE);
//		titleLabel.setForeground(Color.WHITE);
//		desireLabel.setForeground(Color.WHITE);
//		synthButton.setForeground(Color.WHITE);
//		this.setBackground(Color.BLACK);
		
		this.add(titleLabel);
		this.add(chemLabel);
		this.add(desireLabel);
		this.add(synthButton);
		this.add(errorLabel);
		this.add(readyLabel);
		this.add(desireTextField);
		this.addChemTextField();
				
	}

	public void registerListeners(ResourceController controller) {
		getCurrentChemTextField().addFocusListener(controller);
		desireTextField.addFocusListener(controller);
		desireTextField.addKeyListener(controller);
		synthButton.addActionListener(controller);
		this.addKeyListener(controller);
	}
	
	public JTextField getCurrentChemTextField(){
		return chemList.getLast();
	}
	
	public void addChemTextField(){
		 
		JTextField chemTextField = new JTextField(chemTextPlaceholder);
		chemTextField.setName("ChemTextField");
		resourceMap.put(chemTextField, "");
		resourceMap.put(desireTextField, "");
		//always pair one chemical with an amount
		chemList.add(chemTextField);
		
		chemTextField.setFont(new Font("Plain", Font.ITALIC, 14));
		chemTextField.setBounds(60, SIZE/2-150+50*chemList.size(), 200, 30);
		this.add(chemTextField);
				
	}

	public String getChemPlaceholderText() {
		return this.chemTextPlaceholder;
	}

	public LinkedList<JTextField> getChemList() {
		return this.chemList;
	}

	public JTextField getPreviousChemTextField() {
		return this.chemList.get(chemList.size()-2);
	}
	
	public JTextField getDesiredTextField() {
		return this.desireTextField;
	}

	public String getDesiredPlaceholderText() {
		return this.desiredTextPlaceholder;
	}

	public JLabel getErrorLabel() {
		return this.errorLabel;
	}

	public HashMap<JTextField,String> getResourceMap() {
		return resourceMap;
	}

	public JLabel getReadyLabel() {
		return readyLabel;
	}

	public JButton getSynthButton() {
		return synthButton;
		
	}
	
	public ArrayList<String> getResourceList(){
		ArrayList<String> resourceList = new ArrayList<String>();
		
		for (JTextField tf : chemList){
			resourceList.add(tf.getText());
		}
		
		return resourceList;
	}
	
	@Override
	public String getName() {
		return "ResourcePanel";
	}

}
