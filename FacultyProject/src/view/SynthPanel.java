package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import model.Model;
import view.components.Connection;
import view.components.ConnectionPanel;
import view.components.Vertex;
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
	private ConnectionPanel connectionPanel;
	
	private Map<Integer, List<Vertex>> vertexMap = new HashMap<>();
	private String recursiveChem = null;
	private List<Connection> connections = new ArrayList<>();
	
	private int x = 20, y = 20;
	
	public SynthPanel(Model model){
		this.model = model;
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		this.setLayout(null);
		this.setBackground(Color.LIGHT_GRAY);
		
		connectionPanel = new ConnectionPanel();
		connectionPanel.setBounds(0,100, SIZE, SIZE-200);
		connectionPanel.setBackground(Color.WHITE);
		connectionPanel.setLayout(null);
		connectionPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
		titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
		titleLabel.setBounds(SIZE/2-70, 40, 200, 50);
		
		backButton.setBounds(SIZE-200, SIZE-100, 100, 60);
		
		this.add(titleLabel);
		this.add(backButton);
		this.add(connectionPanel);
		
	}


	public void runAnimation() {
		/*
		 * Create a vertex for each chemical in the reaction being handled
		 */
		
	}


	public void registerListeners(SynthController controller) {
		backButton.addActionListener(controller);
	}


	public void addReactionToPath(int reactionID, String recursiveOnFormula, String reaction) {
		this.recursiveChem = recursiveOnFormula;

		/*
		 *  create new vertices based on each chemical found in the reaction (parameters)
		 *  get the split regex from whereever we did it before
		 */
		List<Vertex> vertexList = new ArrayList<Vertex>();
		for (String formula : splitReaction(reaction)) {
			Vertex vertex = new Vertex(reactionID, formula);
			vertex.setBounds(x,y,100,35);
			connectionPanel.add(vertex);
			vertexList.add(vertex);
			System.out.println("Created " + vertex);
			if (!vertexMap.isEmpty()) {
				for (Integer id : vertexMap.keySet()) {
					for (Vertex v : vertexMap.get(id)) {
						if (v.getFormula().equals(vertex.getFormula())) {
							boolean primaryLink = (v.getFormula().equals(recursiveChem)) ? true : false;
							connections.add(v.formLink(vertex, primaryLink));
							System.out.println("linked vertices " + v + " and " + vertex);
							if (primaryLink) System.out.println("PRIMARY");
						}
					}
				}
			}
			
			this.x += 120;
			
		}
		
		this.x = 20;
		this.y += 80;

		vertexMap.put(reactionID, vertexList);
		connectionPanel.setConnections(connections);
		
	}


	private List<String> splitReaction(String reaction) {
		
		Pattern p = Pattern.compile("\\w+(\\(\\w+\\)\\w)*");
		Matcher m = p.matcher(reaction);
		
		List<String> formulas = new ArrayList<>();
		while(m.find()) {
			formulas.add(m.group().trim());
			
		}
		
		return formulas;
	}
}
