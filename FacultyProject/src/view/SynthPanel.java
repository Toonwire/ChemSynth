package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		Map<String, Integer> splitMap = splitReaction(reaction);
		Vertex recursiveVertex = null;
		Vertex destVertex = null;
		
		for (String formula : splitMap.keySet()) {
			Vertex vertex = new Vertex(reactionID, formula, splitMap.get(formula));
			vertex.setBounds(x,y,100,35);
			connectionPanel.add(vertex);
			vertexList.add(vertex);
			if (!vertexMap.isEmpty()) {
				for (Integer id : vertexMap.keySet()) {
					for (Vertex v : vertexMap.get(id)) {
						if (v.getFormula().equals(vertex.getFormula())) {
							boolean recursiveLink = (v.getFormula().equals(recursiveChem)) ? true : false;
							if (recursiveLink) { /* remove to get all links */
								connections.add(v.formLink(vertex, recursiveLink));
								recursiveVertex = v;
								destVertex = vertex;
//								factor = vertex.getCoef()
							}
						}
					}
				}
			}
			
			this.x += 120;
			
		}
		
		this.x = 20;
		this.y += 80;
		
		updateCoefs(recursiveVertex, destVertex, vertexList);
		vertexMap.put(reactionID, vertexList);
		connectionPanel.setConnections(connections);
		
	}

	private void updateCoefs(Vertex recursiveVertex, Vertex destVertex, List<Vertex> vertexList) {
		if (recursiveVertex != null && Math.abs(recursiveVertex.getCoef()) != Math.abs(destVertex.getCoef())) {
			if (Math.abs(recursiveVertex.getCoef()) > Math.abs(destVertex.getCoef())) {
				for (Vertex v : vertexList) {
					v.setCoef(Math.abs(recursiveVertex.getCoef()*v.getCoef()/gcd(v.getCoef(), recursiveVertex.getCoef())));
					System.out.println("coef of " + v.getFormula() + " : "  + v.getCoef());
				}
			} 
			else if (Math.abs(recursiveVertex.getCoef()) < Math.abs(destVertex.getCoef())) {
				for (List<Vertex> list : vertexMap.values()) {
					for (Vertex v : list) {
						v.setCoef(Math.abs(recursiveVertex.getCoef()*destVertex.getCoef()/gcd(destVertex.getCoef(), recursiveVertex.getCoef())));
					}
				}
			}
		}
		
	}


	private int gcd(int a, int b){
		return (b == 0) ? a : gcd(b, a%b);
	}


	private Map<String, Integer> splitReaction(String reaction) {
		Map<String, Integer> map = new LinkedHashMap<>();
		
		String[] splitReaction = reaction.trim().split("->");
		Pattern p = Pattern.compile("\\w+(\\(\\w+\\)\\w)*");
		Matcher m = p.matcher(splitReaction[0]);
		
		while(m.find()) {
			String formula = m.group();
			int coef;
			try {
				coef = Integer.parseInt(formula.split("\\D")[0]);
				formula = formula.split("\\d", 2)[1];
			} catch (Exception e) {
				coef = 1;
			}
			map.put(formula, -coef);
			
		}
		
		Matcher m2 = p.matcher(splitReaction[1]);
		
		while(m2.find()) {
			String formula = m2.group();
			int coef;
			try {
				coef = Integer.parseInt(formula.split("\\D")[0]);
				formula = formula.split("\\d", 2)[1];
			} catch (Exception e) {
				coef = 1;
			}
			map.put(formula, coef);
		}
		
		return map;
	}


	
}
