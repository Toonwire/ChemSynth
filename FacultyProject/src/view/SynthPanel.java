package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import model.Model;
import model.NetReaction;
import view.components.Connection;
import view.components.ConnectionPanel;
import view.components.Vertex;
import controller.SynthController;

public class SynthPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int SIZE = 800;
	
	private Model model;
	
	private JLabel titleLabel = new JLabel("Synthesizer");
	private JButton backButton = new JButton("Back");
	private ConnectionPanel connectionPanel;
	private JPanel netPanel;
	private JPanel drawingPanel;
	private JLabel netLabel = new JLabel("");
	
	private Map<Integer, List<Vertex>> vertexMap = new HashMap<>();
	private Map<Integer, Integer> rIDSeqMap = new HashMap<>();
	private List<Connection> connections = new ArrayList<>();
	private Map<String, JScrollPane> scrollMap = new HashMap<>();
	private Map<JScrollPane, NetReaction> netScrollMap = new HashMap<>();	
	private JScrollPane scrollPane = new JScrollPane();
	private GridBagConstraints c;
	
	private Color connectionHighlightColor = Color.BLUE;
	private Color connectionPanelColor = new Color(91,192,222);
	
	private Font operatorFont = new Font("Cambria", Font.BOLD, 16);
	private CardLayout cardLayout = new CardLayout();
	private int flipCount = 1;
	
	public SynthPanel(Model model){
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		this.setLayout(null);
		this.setBackground(new Color(51,122,183));
		
		this.model = model;
		
		connectionPanel = new ConnectionPanel(new GridBagLayout());
		connectionPanel.setBounds(0,100, SIZE, SIZE-200);
		connectionPanel.setBackground(connectionPanelColor);
		connectionPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
		this.c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		
		netLabel.setFont(new Font("Arial", Font.BOLD, 20));
		netLabel.setForeground(Color.WHITE);
		netLabel.setHorizontalAlignment(SwingConstants.CENTER);

		this.netPanel = new JPanel(new BorderLayout());
		netPanel.setBounds(0, SIZE-65, SIZE, 100);
		netPanel.setBackground(this.getBackground());
		netPanel.add(netLabel, BorderLayout.NORTH);
		
		titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
		titleLabel.setBounds(SIZE/2-70, 40, 200, 50);
		
		backButton.setBounds(SIZE-100, 40, 80, 30);
		backButton.setBackground(Color.CYAN);
		
		drawingPanel = new JPanel(cardLayout);
		drawingPanel.setBackground(connectionPanelColor);
		drawingPanel.setBounds(0,100, SIZE, SIZE-200);		
		
		this.add(titleLabel);
		this.add(backButton);
		this.add(drawingPanel);
		this.add(netPanel);
		
	}


	public void registerListeners(SynthController controller) {
		backButton.addActionListener(controller);
		this.addKeyListener(controller);
	}


	public void addReactionToPath(int reactionID, String recursiveOnFormula, String reaction) {
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
			connectionPanel.add(vertex, c);
			vertexList.add(vertex);
			if (!vertexMap.isEmpty()) {
				if (vertex.getFormula().equals(recursiveOnFormula)) {
					for (Integer id : vertexMap.keySet()) {
						for (Vertex v : vertexMap.get(id)) {
							if (v.getFormula().equals(vertex.getFormula())) {
								connections.add(v.formLink(vertex));
								recursiveVertex = v;
								destVertex = vertex;
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
					v.setCoef(Math.abs(recursiveVertex.getCoef())*v.getCoef()/gcd(v.getCoef(), Math.abs(recursiveVertex.getCoef())));
				}
			} 
			else if (Math.abs(recursiveVertex.getCoef()) < Math.abs(destVertex.getCoef())) {
				int rCoef = Math.abs(recursiveVertex.getCoef());
				int dCoef = destVertex.getCoef();
				for (List<Vertex> list : vertexMap.values()) {
					for (Vertex v : list) {
						v.setCoef(Math.abs(v.getCoef())*dCoef/rCoef);
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
		connectionPanel.removeAll();
		scrollMap.clear();
		netScrollMap.clear();
		drawingPanel.removeAll();
		vertexMap.clear();
		connections.clear();
		
		flipCount = 1;
		netLabel.setText("");
		netLabel.setFont(new Font("Arial", Font.BOLD, 20));
		
		c.gridx = 0;
		c.gridy = 0;
		
	}


	public JLabel getNetLabel() {
		return netLabel;
	}


	public ConnectionPanel getConnectionPanel() {
		return connectionPanel;
	}


	public void showNextReaction() {
		cardLayout.show(drawingPanel, "scrollPane" + flipCount%drawingPanel.getComponentCount());
		String netString = netScrollMap.get(scrollMap.get("scrollPane"+flipCount%drawingPanel.getComponentCount())).toString();
		netLabel.setText(netString);
		scaleFont(netLabel);
		flipCount++;
	}
	
	public JPanel getDrawingPanel() {
		return drawingPanel;
	}


	public JScrollPane getScrollPane() {
		return scrollPane;
	}


	public void drawReactions() {
		if (model.getNetMap().isEmpty()) {
			
		}
		int i = 0;
		for (NetReaction nr : model.getNetMap().keySet()) {
			if (model.getNetMap().get(nr) == model.getMinCost()) {
				if (!nr.getUsedReactions().isEmpty()) {
					for (int usedID = 0; usedID < nr.getUsedReactions().size(); usedID++) {
						int reactionID = nr.getUsedReactions().get(usedID);
						rIDSeqMap.put(reactionID, usedID);
						addReactionToPath(reactionID, nr.getRecursiveList().get(usedID), model.getReactionsIDMap().get(nr.getUsedReactions().get(usedID)));
					}
					if (i == 0) {
						netLabel.setText(nr.toString());
						scaleFont(netLabel);
					}
					scrollMap.put("scrollPane"+i, scrollPane);
					netScrollMap.put(scrollPane, nr);
					drawingPanel.add(scrollPane, "scrollPane" + i);
					i++;
					
					this.scrollPane.addMouseListener(new MouseAdapter() {
						public void mousePressed(MouseEvent e) {
							showNextReaction();
						}
					});
					this.scrollPane = new JScrollPane();
					
					this.connectionPanel = new ConnectionPanel(new GridBagLayout());
					this.connections = new ArrayList<>();
					this.vertexMap = new HashMap<>();
					this.rIDSeqMap = new HashMap<>();
					
					connectionPanel.setBackground(connectionPanelColor);
					connectionPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
//					System.out.println("\n");
				}
			}
		}
	}

	public void scaleFont(JLabel label) {
	    int maxWidth = netPanel.getWidth();
		if(label.getFontMetrics(label.getFont()).stringWidth(label.getText()) > maxWidth){
			Font labelFont = label.getFont();
			String labelText = label.getText();

			int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
			int componentWidth = netPanel.getWidth();

			double widthRatio = (double) componentWidth / (double) stringWidth;
			int newFontSize = (int) (labelFont.getSize() * widthRatio);

			label.setFont(new Font(labelFont.getName(), Font.BOLD, newFontSize));
			
		}
		if (label.getText().matches(".*\\d.*")) {
			label.setText("<html><body>" + label.getText().replaceAll("(\\d+(?<!(^|\\s)\\d{1,2})(?=\\D|$))", "<sub>$1</sub>") + "</body></html>");
		}
	}
}
