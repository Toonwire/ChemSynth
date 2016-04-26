package view.components;

import java.awt.Graphics;

import javax.swing.JPanel;

public class ConnectionPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Connection[] connections;
    public ConnectionPanel() {
    }

    public ConnectionPanel(Connection[] connections) {
        this.connections = connections;
    }

    public void setConnectors(Connection[] connections) {
        this.connections = connections;
    }

    public Connection[] getConnectors() {
        return connections;
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (connections != null) {
            for (int i = 0; i < connections.length; i++) {
                if (connections[i] != null)
                	connections[i].paint(g);
            }
        }
    }
}
