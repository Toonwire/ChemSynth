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
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import controller.SynthController;
import model.Model;
import view.components.Connection;
import view.components.ConnectionPanel;
import view.components.Vertex;

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
	private JLabel netLabel = new JLabel("Net Reaction");
	private JPanel netPanel = new JPanel();
	
	private Map<Integer, List<Vertex>> vertexMap = new HashMap<>();
	private String recursiveChem = null;
	private List<Connection> connections = new ArrayList<>();
//	private JScrollPane scrollPane = new JScrollPane();
	private Color connectionHighlightColor = Color.BLUE;
	
	private int x = 20, y = 20;
	
	public SynthPanel(Model model){
		this.model = model;
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		this.setLayout(null);
		this.setBackground(Color.LIGHT_GRAY);
		
		connectionPanel = new ConnectionPanel();
		connectionPanel.setBounds(0,100, SIZE, SIZE-200);
//		connectionPanel.setSize(new Dimension(SIZE, 3000));
		connectionPanel.setBackground(Color.WHITE);
		connectionPanel.setLayout(null);
		connectionPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
		netLabel.setFont(new Font("Arial", Font.BOLD, 20));
		netPanel.setBounds(0, SIZE-65, SIZE, 100);
		netLabel.setAlignmentX(SwingConstants.CENTER);
		netPanel.setBackground(this.getBackground());
		netPanel.add(netLabel);
		
		titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
		titleLabel.setBounds(SIZE/2-70, 40, 200, 50);
		
		backButton.setBounds(SIZE-100, 30, 100, 60);
		
//		scrollPane.setBounds(0,100, SIZE, SIZE-200);
//		scrollPane.setViewportView(connectionPanel);
//		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		this.add(titleLabel);
		this.add(backButton);
		this.add(connectionPanel);
//		this.add(scrollPane);
		this.add(netPanel);
		
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
		Vertex lastVertex = null;
		
		for (String formula : splitMap.keySet()) {
			Vertex vertex = new Vertex(reactionID, formula, splitMap.get(formula));
			vertex.setBounds(x,y,100,35);
			System.out.println("Created vertex " + vertex);
			connectionPanel.add(vertex);
			vertexList.add(vertex);
			if (!vertexMap.isEmpty()) {
				for (Integer id : vertexMap.keySet()) {
					for (Vertex v : vertexMap.get(id)) {
						if (v.getFormula().equals(vertex.getFormula())) {
							boolean recursiveLink = (v.getFormula().equals(recursiveChem)) ? true : false;
							if (recursiveLink) { /* remove to get all links */
								connections.add(v.formLink(vertex, recursiveLink, connectionHighlightColor));
								System.out.println("Linked " + vertex + " with " + v);
								recursiveVertex = v;
								destVertex = vertex;
							}
						}
					}
				}
			}

			
			/*
			 * adding '+' and '-->' between vertices
			 */
			if (lastVertex != null) {
				JLabel opLabel = new JLabel();
				opLabel.setFont(new Font("Cambria", Font.BOLD, 16));
				opLabel.setBounds(x-15,y,15,35);
				
				if (vertex.getCoef() < 0) {
					if (lastVertex.getCoef() < 0)
						opLabel.setText("+");
				}
				else if (vertex.getCoef() > 0) {
					if (lastVertex.getCoef() < 0) 
						opLabel.setText("\u2192");
					else if (lastVertex.getCoef() > 0) 
						opLabel.setText("+");
				}
				
				connectionPanel.add(opLabel);
			}
			lastVertex = vertex;
			
			
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

	public void reset() {
		connectionPanel.removeAll();
		this.remove(connectionPanel);
		this.connectionPanel = new ConnectionPanel();
		this.netPanel = new JPanel();
		this.vertexMap = new HashMap<>();
		this.connections = new ArrayList<>();
		this.x = 20;
		this.y = 20;
		
		connectionPanel.setBounds(0,100, SIZE, SIZE-200);
		connectionPanel.setBackground(Color.WHITE);
		connectionPanel.setLayout(null);
		connectionPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		this.add(connectionPanel);
		
	}


	public JLabel getNetLabel() {
		return netLabel;
	}
	
}
