package view.components;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Vertex extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Vertex linkedVertex;
	private String formula;
	private int coef;
	private int reactionID;
	private boolean recursiveLink;
	
	private JLabel coefLabel, formulaLabel;	

	
	public Vertex(int reactionID, String formula, int coef) {
		this.reactionID = reactionID;
		this.formula = formula;
		this.coef = coef;
		this.setLayout(null);
		
//		try {
//			this.coef = Integer.parseInt(formula.split("\\D")[0]);
//			this.formula = formula.split("\\d", 2)[1];
//			
//		} catch (Exception e) {
//			this.coef = 1;
//		}
		
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.setBackground(Color.GREEN);
		
		this.coefLabel = new JLabel("" + Math.abs(coef), SwingConstants.CENTER);
		this.formulaLabel = new JLabel(this.formula);

		coefLabel.setBounds(10,0,15,35);
		formulaLabel.setBounds(30,0,85,35);
		
		this.add(coefLabel);
		this.add(formulaLabel);
		
	}
	
	public String getFormula() {
		return formula;
	}

	public int getReactionID() {
		return reactionID;
	}
	
	public Vertex getLinkedVertex() {
		return linkedVertex;
	}
	
	public String toString() {
		return "<" + reactionID + ", " + formula + ">";
	}
	
	public int getCoef() {
		return coef;
	}
	
	public void setCoef(int coef) {
		this.coef = coef;
		this.coefLabel.setText("" + coef);
	}

	public boolean isRecursiveLink() {
		return recursiveLink;
	}

	public Connection formLink(Vertex linkedVertex, boolean recursiveLink, Color linkColor) {
		this.linkedVertex = linkedVertex;
		this.recursiveLink = recursiveLink;
		
		if (recursiveLink) {
			this.setBackground(Color.RED);
			linkedVertex.setBackground(Color.YELLOW);
			this.setPrimaryBorder(linkColor);
			linkedVertex.setPrimaryBorder(linkColor);
			
		} else {
			this.setWasteBorder();
			linkedVertex.setWasteBorder();
		}
		
		Connection connection = new Connection(this, linkedVertex);
		connection.setLineColor(linkColor);
		return connection;
	}

	public void setPrimaryBorder(Color linkColor) {
		this.setBorder(BorderFactory.createLineBorder(linkColor, 3));
	}
	
	public void setWasteBorder() {
		this.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
	}

	public JLabel getCoefLabel() {
		return coefLabel;
	}
	
	public JLabel getFormulaLabel() {
		return formulaLabel;
	}
	
	public boolean isUsed() {
		return coef == 0;
	}
	
}
