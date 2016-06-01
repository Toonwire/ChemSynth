package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JScrollPane;
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
	
	private JLabel titleLabel = new JLabel("Synthesizer");
	private JButton backButton = new JButton("Back");
	private ConnectionPanel connectionPanel;
	private JPanel netPanel;
	private JLabel netLabel = new JLabel("Net Reaction");
	
	private Map<Integer, List<Vertex>> vertexMap = new HashMap<>();
	private String recursiveChem = null;
	private List<Connection> connections = new ArrayList<>();
	private JScrollPane scrollPane = new JScrollPane();
	private GridBagConstraints c;
	
	private Color connectionHighlightColor = Color.BLUE;
	private Color connectionPanelColor = new Color(91,192,222);
	
	private Font operatorFont = new Font("Cambria", Font.BOLD, 16);
	
	public SynthPanel(Model model){
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		this.setLayout(null);
		this.setBackground(new Color(51,122,183));
		
		connectionPanel = new ConnectionPanel(new GridBagLayout());
		connectionPanel.setBounds(0,100, SIZE, SIZE-200);
		connectionPanel.setBackground(connectionPanelColor);
		connectionPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
		this.c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		
		netLabel.setFont(new Font("Arial", Font.BOLD, 20));
		netLabel.setHorizontalAlignment(SwingConstants.CENTER);

		this.netPanel = new JPanel(new BorderLayout());
		netPanel.setBounds(0, SIZE-65, SIZE, 100);
		netPanel.setBackground(this.getBackground());
		netPanel.add(netLabel, BorderLayout.NORTH);
		
		titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
		titleLabel.setBounds(SIZE/2-70, 40, 200, 50);
		
		backButton.setBounds(SIZE-100, 40, 80, 30);
		backButton.setBackground(Color.CYAN);
		
		scrollPane.setBounds(0,100, SIZE, SIZE-200);
		
		this.add(titleLabel);
		this.add(backButton);
		this.add(scrollPane);
		this.add(netPanel);
		
	}


	public void registerListeners(SynthController controller) {
		backButton.addActionListener(controller);
		this.addKeyListener(controller);
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
		
		c.gridx = 0;
		c.insets = new Insets(20, 10, 20, 0);
		c.anchor = GridBagConstraints.CENTER;
		
		for (String formula : splitMap.keySet()) {
			Vertex vertex = new Vertex(reactionID, formula, splitMap.get(formula));
//			vertex.setPreferredSize(new Dimension(100,35));
//			System.out.println("Created vertex " + vertex);
			connectionPanel.add(vertex, c);
			//System.out.println(c.gridx +"  " + c.gridy);
			vertexList.add(vertex);
			if (!vertexMap.isEmpty()) {
				for (Integer id : vertexMap.keySet()) {
					for (Vertex v : vertexMap.get(id)) {
//						System.out.println(v);
						if (v.getFormula().equals(vertex.getFormula())) {
							boolean recursiveLink = (v.getFormula().equals(recursiveChem)) ? true : false;
							if (recursiveLink) { /* remove to get all links */
								connections.add(v.formLink(vertex, recursiveLink, connectionHighlightColor));
//								System.out.println("Linked " + vertex + " with " + v);
								recursiveVertex = v;
								destVertex = vertex;
//								System.out.println("\n\n");
								break;
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
				opLabel.setFont(operatorFont);
//				opLabel.setPreferredSize(new Dimension(15,35));
				
				if (vertex.getCoef() < 0) {
					if (lastVertex.getCoef() < 0)
						opLabel.setText("+");
				}
				else if (vertex.getCoef() > 0) {
					if (lastVertex.getCoef() < 0) 
						opLabel.setText("\u2192");	// \rightarrow 
					else if (lastVertex.getCoef() > 0) 
						opLabel.setText("+");
				}
				c.gridx--;
				connectionPanel.add(opLabel, c);
//				System.out.println("Created " + opLabel.getText());
//				System.out.println(c.gridx +"  " + c.gridy);
				c.gridx += 3;
			} else {
				c.gridx += 2;
			}
			
			lastVertex = vertex;
			
		}
		
//		System.out.println("ConnectionPanel : " +connectionPanel.isVisible());
//		System.out.println("Vertex : " + lastVertex.isVisible());
		repaint();
		
		c.gridy++;
		
		updateCoefs(recursiveVertex, destVertex, vertexList);
		vertexMap.put(reactionID, vertexList);
		connectionPanel.setConnections(connections);
		scrollPane.setViewportView(connectionPanel);
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
				formula = formula.split("(?<=\\d)(?=\\D)", 2)[1];
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
				formula = formula.split("(?<=\\d)(?=\\D)", 2)[1];
			} catch (Exception e) {
				coef = 1;
			}
			map.put(formula, coef);
		}
		
		return map;
	}

	public void reset() {
//		scrollPane.remove(connectionPanel);
//		this.remove(scrollPane);
		
//		this.connectionPanel = new ConnectionPanel(new GridBagLayout());
//		connectionPanel.setBounds(0,100, SIZE, SIZE-200);
//		connectionPanel.setBackground(connectionPanelColor);
//		connectionPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
//		this.scrollPane = new JScrollPane();
//		scrollPane.setBounds(0,100, SIZE, SIZE-200);
//		scrollPane.setViewportView(connectionPanel);
		
//		this.netPanel = new JPanel();
//		this.vertexMap = new HashMap<>();
//		this.connections = new ArrayList<>();
//		this.add(scrollPane);
		
		connectionPanel.removeAll();
		vertexMap.clear();
		connections.clear();
		
		c.gridx = 0;
		c.gridy = 0;
		
		
	}


	public JLabel getNetLabel() {
		return netLabel;
	}


	public ConnectionPanel getConnectionPanel() {
		return connectionPanel;
	}

}
