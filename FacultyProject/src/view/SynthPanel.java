package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Model;
import controller.SynthController;

public class SynthPanel extends JPanel {
	// Do something fancy in here, animation perhaps

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int SIZE = 800;
	private final Model model;
	
	private JLabel titleLabel = new JLabel("Synthesizer");
	private JButton backButton = new JButton("Back");
	private JPanel reactionsPanel = new JPanel();
	
	public SynthPanel(Model model){
		this.model = model;
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		this.setLayout(new BorderLayout());
		this.setBackground(Color.LIGHT_GRAY);
		
		
		titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
		
		
		this.add(backButton, BorderLayout.PAGE_END);
		this.add(titleLabel, BorderLayout.PAGE_START);
		
		
	}


	public void runAnimation() {
		reactionsPanel.setLayout(new GridLayout(model.getUsedIDs().size(), 1));
		for (int i = model.getUsedIDs().size()-1; i >= 0; i--) {
			String reaction = model.getDatabase().getReactionsFromID().get(model.getUsedIDs().get(i));		
			/*
			 * split each reaciton into verticies
			 */
			

			String[] reacProd = reaction.trim().split("->");
			Pattern p = Pattern.compile("\\w+(\\(\\w+\\)\\w)*");
			Matcher m = p.matcher(reacProd[0]);

			while(m.find()) {
//				Vertex
			}
		}
		
	}
		
	
	public void registerListeners(SynthController controller) {
		backButton.addActionListener(controller);
	}
}
