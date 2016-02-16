package view;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Model;

public class SynthPanel extends JPanel {
	// Do something fancy in here, animation perhaps

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int SIZE = 800;
	
	private JLabel titleLabel = new JLabel("Synthesizer");

	public SynthPanel(Model model){
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		this.setLayout(null);
		
		titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
		titleLabel.setBounds(SIZE/2-70, 40, 200, 50);
		
		this.add(titleLabel);
		
	}

	public void update() {
		
	}
}
