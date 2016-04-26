package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Model;
import view.components.Vertex;
import controller.SynthController;

public class SynthPanel extends JPanel {
	// Do something fancy in here, animation perhaps

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int SIZE = 800;
	private final int VERTEX_SIZE = 40;
	private final Model model;
	
	private JLabel titleLabel = new JLabel("Synthesizer");
	private JButton backButton = new JButton("Back");
	private JPanel reactionsPanel = new JPanel();
	private Map<Integer, List<Vertex>> vertexMap = new HashMap<>();
	
	private Vertex latestVertex = null;
	private String recursiveChem = null;
	private int lastReactionID = -1;
	
	
	public SynthPanel(Model model){
		this.model = model;
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		this.setLayout(null);
		this.setBackground(Color.LIGHT_GRAY);
		
		
		titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
		titleLabel.setBounds(SIZE/2-70, 40, 200, 50);
		
		backButton.setBounds(SIZE-200, SIZE-100, 100, 60);
		
		this.add(titleLabel);
		this.add(backButton);
		
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

		System.out.println("from the id of " + reactionID);
		System.out.println("recursive on " + recursiveOnFormula);
		System.out.println("added to path " + reaction);
		
		/*
		 *  create new vertices based on each chemical found in the reaction (parameters)
		 *  get the split regex from whereever we did it before
		 */
		List<Vertex> vertexList = new ArrayList<Vertex>();
		for (String formula : splitReaction(reaction)) {
			Vertex vertex = new Vertex(reactionID, formula);
			vertexList.add(vertex);
			System.out.println("Created " + vertex);
			if (!vertexMap.isEmpty()) {
				for (Vertex v : vertexMap.get(lastReactionID)) {
					if (v.getFormula().equals(vertex.getFormula())) {
						boolean primaryLink = (v.getFormula().equals(recursiveChem)) ? true : false;
						v.addLink(vertex, primaryLink);
						System.out.println("linked vertices " + v + " and " + vertex);
						if (primaryLink) System.out.println("PRIMARY");
					}
				}
			}
		}

		vertexMap.put(reactionID, vertexList);
		this.lastReactionID = reactionID;
		
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
