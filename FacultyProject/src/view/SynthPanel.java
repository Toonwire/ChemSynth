package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import model.Model;

public class SynthPanel extends JPanel {
	// Do something fancy in here, animation perhaps

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int SIZE = 800;
	
	private JLabel titleLabel = new JLabel("Synthesizer");
	private JLabel progressLabel = new JLabel("");
	private JProgressBar progressBar = new JProgressBar(0,100);
	
	public SynthPanel(Model model){
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		this.setLayout(null);
		this.setBackground(Color.LIGHT_GRAY);
		
		titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
		titleLabel.setBounds(SIZE/2-70, 40, 200, 50);
		
		progressLabel.setFont(new Font("Arial", Font.BOLD, 15));
		progressLabel.setBounds(SIZE/2+120, SIZE/2, 200, 50);
		progressLabel.setForeground(Color.BLACK);
		
		progressBar.setValue(0);
		progressBar.setBounds(SIZE/2-100, SIZE/2, 200, 50);
		progressBar.setBackground(Color.CYAN);
		progressBar.setForeground(Color.GREEN);
		progressBar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		
		this.add(titleLabel);
		this.add(progressLabel);
		this.add(progressBar);
				
	}


	public void runAnimation() {
		
		/* Animation is to be executed as an AsyncTask (Android reference)
		 * Meaning we will be running the algorithm in the background 
		 * while continuously feeding the view info of how far along in 
		 * the process we are. The view can then be updated withou having to wait
		 * for the algorithm to finish
		*/
		
		ProgressWorker worker = new ProgressWorker();
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if ("progress".equals(e.getPropertyName())) {
					progressBar.setValue((Integer) e.getNewValue());
				}
				
			}
		});
		worker.execute();
	}
	
	private class ProgressWorker extends SwingWorker<Void, Integer> {
		
		@Override
		protected Void doInBackground() throws Exception {
			for (int i = 1; i <= 100; i++) {
				this.setProgress(i);
				this.publish(i);
				Thread.sleep(100);
			}
			return null;
		}
		
		@Override
		protected void process(List<Integer> chunks) {
			for (int i : chunks) {
            	int c = (int) (i*2.55);
            	progressBar.setForeground(new Color(255-c, c, 0));
                String str = i==100 ? "Done!" : i + "%";
                if (i == 100){
                	str = "Search completed!";
                	progressBar.setCursor(Cursor.getDefaultCursor());
                } else {
                	progressBar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                }
                progressLabel.setText(str);
			}
		}
	}	
}
