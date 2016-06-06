package view.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

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
	private Color vertexColor = new Color(92,184,92);		// Green
	private Color sourceColor = new Color(217,83,79);		// Red
	private Color destColor = new Color(240,173,78);		// Golden
	private Color linkColor = Color.BLUE;
	
	private JLabel coefLabel, formulaLabel;	
	
	public Vertex(int reactionID, String formula, int coef) {
		this.reactionID = reactionID;
		this.formula = formula;
		this.coef = coef;
		this.setLayout(new FlowLayout());
        this.setBackground(vertexColor);
		System.out.println(formula);
		this.coefLabel = new JLabel("" + Math.abs(coef));
		if (formula.matches(".*\\d.*")) {
			this.formulaLabel = new JLabel("<html><body> " + formula.replaceAll("(\\d+)", "<sub>$1</sub>") + "</body></html>");
			formulaLabel.setBorder(BorderFactory.createEmptyBorder( 0, 0, -4, 0 ));
			coefLabel.setBorder(BorderFactory.createEmptyBorder( 0, 0, 4, 0 ));
		} else 
			this.formulaLabel = new JLabel(formula);
		formulaLabel.setForeground(Color.WHITE);
		coefLabel.setForeground(Color.WHITE);

		coefLabel.setBounds(10,0,15,35);
		formulaLabel.setBounds(30,0,85,35);
		this.add(coefLabel);
		this.add(formulaLabel);
		Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
		Border margin = new EmptyBorder(10, 10, 10, 10);
		this.setBorder(new CompoundBorder(border, margin));
		
	}
	
	public String getFormula() {
		return formula;
	}

	public String toString() {
		return "<" + reactionID + ", " + formula + ">";
	}
	
	public int getCoef() {
		return coef;
	}
	
	public int getReactionID() {
		return reactionID;
	}
	
	public void setCoef(int coef) {
		this.coef = coef;
		this.coefLabel.setText("" + coef);
	}

	public boolean isRecursiveLink() {
		return recursiveLink;
	}
	
	public Vertex getLinkedVertex() {
		return linkedVertex;
	}

	public Connection formLink(Vertex linkedVertex) {
		this.linkedVertex = linkedVertex;
		this.setBackground(sourceColor);
		linkedVertex.setBackground(destColor);
		this.setPrimaryBorder(linkColor);
		linkedVertex.setPrimaryBorder(linkColor);
		
		Connection connection = new Connection(this, linkedVertex);
		connection.setLineColor(linkColor);
		return connection;
	}

	public void setPrimaryBorder(Color linkColor) {
		this.getBorder();
		Border linkBorder = BorderFactory.createLineBorder(linkColor, 3);
//		this.setBorder(BorderFactory.createLineBorder(linkColor, 3));
		this.setBorder(new CompoundBorder(linkBorder, this.getBorder()));
	}
	
}
