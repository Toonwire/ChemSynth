package view;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

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
//	-   *****                           *****      -
//	-   *****                           *****      -
//	-   *****                           *****      -
//	-                                              -
//	-            Seeking:  *****                   -
//	-											   -
//	-                                   SYNTHESIZE -
//	------------------------------------------------

	private JLabel titleLabel = new JLabel("Resources", JLabel.CENTER);
	private JLabel chemLabel = new JLabel("Chemicals");
	private JLabel seekLabel = new JLabel("Seeking");
	private JLabel amountLabel = new JLabel("Amounts");
	private JTextField a = new JTextField("enter formula");
	
	private JButton synthButton = new JButton("Synthesize");
	
	
	public ResourcePanel(Model model){
		
		
		
		this.add(titleLabel);
		
	}

}
