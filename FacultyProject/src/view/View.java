package view;

import java.awt.CardLayout;
import javax.swing.JFrame;
import model.Model;

public class View extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Model model;
	private ResourcePanel resourcePanel;
	private SynthPanel synthPanel;
	
	public View(Model model){		
		super("Snake");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.model = model;
		this.resourcePanel = new ResourcePanel(model);
		this.synthPanel = new SynthPanel(model);
		
		
		
		this.setResizable(false);
		this.setLayout(new CardLayout());

		this.add(resourcePanel);

		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);

	}

	public ResourcePanel getResourcePanel() {
		return resourcePanel;
	}

	public SynthPanel getSynthPanel() {
		return synthPanel;
	}

}