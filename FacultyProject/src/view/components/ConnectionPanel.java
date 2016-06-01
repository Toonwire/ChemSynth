package view.components;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.util.List;

import javax.swing.JPanel;

public class ConnectionPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<Connection> connections;
	
	public ConnectionPanel(LayoutManager layoutManager) {
		this.setLayout(layoutManager);
	}

	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}

//	public List<Connection> getConnections() {
//		return connections;
//	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (connections != null) {
			for (Connection connection : connections) {
				if (connection != null)
					connection.paintComponent(g);
        	}
        }
    }
}
