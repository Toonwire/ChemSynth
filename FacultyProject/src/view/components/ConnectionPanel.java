package view.components;

import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

public class ConnectionPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<Connection> connections;
	public ConnectionPanel() {
		
	}
	
	public ConnectionPanel(List<Connection> connections) {
		this.connections = connections;
	}

	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (connections != null) {
			for (Connection connection : connections) {
				if (connection != null)
					connection.paint(g);
        	}
        }
    }
}
