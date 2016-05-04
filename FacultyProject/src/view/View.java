package view;

import java.awt.CardLayout;
import java.awt.KeyboardFocusManager;

import javax.swing.JFrame;
import javax.swing.JTextField;

import model.Model;

public class View extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ResourcePanel resourcePanel;
	private SynthPanel synthPanel;
	
	public View(Model model){		
		super("ChemSynth");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.resourcePanel = new ResourcePanel(model);
		this.synthPanel = new SynthPanel(model);
		
		this.setResizable(false);
		this.setLayout(new CardLayout());

		this.add(resourcePanel);

		this.pack();
		this.setLocationByPlatform(true);

		// initial focus component
		resourcePanel.getDesiredTextField().requestFocusInWindow();
		
		this.setVisible(true);

	}

	public ResourcePanel getResourcePanel() {
		return resourcePanel;
	}

	public SynthPanel getSynthPanel() {
		return synthPanel;
	}
	
	public JTextField getFocus() {
		return (JTextField) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
	}
	
}
