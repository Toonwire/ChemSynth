package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import model.Model;
import model.NetReaction;
import view.View;

public class ResourceController implements FocusListener, ActionListener, KeyListener {

	private Model model;
	private View view;
	
	private Color CUSTOM_RED = new Color(213,103,106);
	private Color CUSTOM_GREEN = new Color(63,204,155);
	
	private String errorMsg = "<html><div style='text-align: center;'>"
			+ 	"</h1>"
			+ 	"<body>"
			+		"<i style='font-size:14;'> A retro synthesis was not deemed possible. </i><br>"
			+		"<i style='font-size:14;'> You have initiated a retro synthesis for an abundant chemical or no reaction product matches your desired chemical </i><br>"
			+ 	"</body>"
			+ "</html>"
			;
	
	public ResourceController(Model model, View view){
		this.model = model;
		this.view = view;
		
		view.getResourcePanel().registerListeners(this);
		view.getResourcePanel().getCurrentChemTextField().addKeyListener(this);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		if (actionCommand.equals("Synthesize")){
			startSynth();
			
		}
	}
	
	private void startSynth() {
		view.remove(view.getResourcePanel());
		view.add(view.getSynthPanel());
		view.pack();
		
		String desired = view.getResourcePanel().getDesiredTextField().getText().trim();
		model.setUpSynth(view.getResourcePanel().getResourceList(), desired);
		NetReaction netReaction = model.getNetReaction();
		String netString = netReaction.toString();
		
		if (netReaction.toString().isEmpty()) {
			view.getSynthPanel().getNetLabel().setForeground(new Color(255,128,0));
			view.getSynthPanel().getNetLabel().setText(errorMsg);
		}
		else {
			view.getSynthPanel().getNetLabel().setForeground(Color.WHITE);
			
			// clean up format
			netString = netString.replace("-->", "\u2192");
			netString = netString.replaceAll("(?<=\\D)*1(?=\\D)", "");
			view.getSynthPanel().getNetLabel().setText(netString);
		}
		
	}


	@Override
	public void focusGained(FocusEvent e) {

		JTextField tf = (JTextField) e.getComponent();
		
		if (tf.getText().equals(view.getResourcePanel().getDesiredPlaceholderText())){
			tf.setText("");
		}
		
		if (tf.getText().equals(view.getResourcePanel().getChemPlaceholderText()))
			tf.setText("");
			
	}

	@Override
	public void focusLost(FocusEvent e) {
		JTextField tf = ((JTextField) e.getComponent());
		
		if (tf.getText().trim().isEmpty()) {
			
			tf.setText(view.getResourcePanel().getChemPlaceholderText());
			tf.setBackground(Color.WHITE);
			view.getResourcePanel().getErrorLabel().setText("");
			
			if (allSet()){
				view.getResourcePanel().getReadyLabel().setForeground(Color.WHITE);
				view.getResourcePanel().getReadyLabel().setText("Ready to go!");
				view.getResourcePanel().getSynthButton().setEnabled(true);
				
			} else {
				view.getResourcePanel().getSynthButton().setEnabled(false);
			}
			
		// no need to look up in the database if the text of the text field losing focus has not changed.
		} else if (view.getResourcePanel().getResourceMap().containsKey(tf) 
				&& view.getResourcePanel().getResourceMap().get(tf).equals(tf.getText().trim())
				&& tf.getBackground().equals(CUSTOM_GREEN)){
			
			// do nothing
			
		} else {
			
			boolean exists = existsInDatabase(tf.getText());
			
			if (exists) {
				
				if (!view.getResourcePanel().getResourceMap().containsValue(tf.getText().trim())
						|| view.getResourcePanel().getResourceMap().get(tf).equals(tf.getText().trim())) {
					tf.setBackground(CUSTOM_GREEN);
					this.view.getResourcePanel().getResourceMap().put(tf, tf.getText().trim());
					this.view.getResourcePanel().getErrorLabel().setText("");
					
				} else {
								
					tf.setBackground(CUSTOM_RED);
					view.getResourcePanel().getErrorLabel().setText("Chemical already listed");
					view.getResourcePanel().getReadyLabel().setText("");
					
				}
					
				if (allSet()){
					view.getResourcePanel().getReadyLabel().setForeground(Color.WHITE);
					view.getResourcePanel().getReadyLabel().setText("Ready to go!");
					view.getResourcePanel().getSynthButton().setEnabled(true);
					
				} else {
					view.getResourcePanel().getSynthButton().setEnabled(false);
				}
				
			} else if (!exists && !tf.getText().equals(view.getResourcePanel().getChemPlaceholderText())) {
				tf.setBackground(CUSTOM_RED);
				this.view.getResourcePanel().getErrorLabel().setText("Formula doesn't exist in database");
				view.getResourcePanel().getReadyLabel().setText("");
				view.getResourcePanel().getSynthButton().setEnabled(false);
			} 
		}
	}

	private boolean allSet() {
		boolean ready = true;
		
		if (!view.getResourcePanel().getDesiredTextField().getBackground().equals(CUSTOM_GREEN)) {
			ready = false;
		} else {
			for (JTextField tf : view.getResourcePanel().getChemList()){
				if (tf.getBackground().equals(CUSTOM_RED)) {
					ready = false;
					break;
				}
			}
		}
		
		
		return ready;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			
			if (!e.getComponent().getName().equals("ResourcePanel")) {
				JTextField tf = (JTextField) e.getComponent();
				boolean exists = existsInDatabase(tf.getText());
				
				// create a new text field
				if(!tf.getText().trim().isEmpty() 
						&& view.getResourcePanel().getChemList().contains(tf)
						&& (view.getResourcePanel().getChemList().size() < 5)
						&& !view.getResourcePanel().getResourceMap().containsValue(tf.getText())
						&& tf.equals(view.getResourcePanel().getChemList().getLast())
						&& exists
						|| (tf.getBackground().equals(CUSTOM_GREEN) && view.getResourcePanel().getChemList().getLast().equals(tf))) {
					
					// to catch the '||' part of the above if statement in case resource limit reached.
					if (view.getResourcePanel().getChemList().size() == 5) {
						tf.setBackground(CUSTOM_GREEN);
						this.view.getResourcePanel().getErrorLabel().setForeground(new Color(37,141,105));
						this.view.getResourcePanel().getErrorLabel().setText("Resource limit reached");	
						
					} else {
						//add and register listeners to the new text field
						this.view.getResourcePanel().addChemTextField();
						this.view.getResourcePanel().getCurrentChemTextField().addFocusListener(this);
						this.view.getResourcePanel().getCurrentChemTextField().addKeyListener(this);
						this.view.getResourcePanel().getCurrentChemTextField().requestFocus();
						this.view.getResourcePanel().getErrorLabel().setText("");
						
					}
					
				} else {
					this.view.getResourcePanel().requestFocus();
				}
			} else if (e.getComponent().getName().equals("ResourcePanel") && view.getResourcePanel().getSynthButton().isEnabled()) {
				startSynth();
			}
		}	
	}

	private boolean existsInDatabase(String resource) {
		boolean exists = false;
		// checks if there is any information returned from the sql statement
		exists = model.getDatabase().checkResource(resource.trim());
		
		return exists;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}
