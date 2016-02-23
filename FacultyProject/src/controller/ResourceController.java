package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.TreeMap;

import javax.swing.JTextField;

import view.View;
import model.Model;

public class ResourceController implements FocusListener, ActionListener, KeyListener {

	private Model model;
	private View view;
	
	private Color CUSTOM_RED = new Color(213,103,106);
	private Color CUSTOM_GREEN = new Color(63,204,155);
	
	
	private boolean focusOnPanel = false; // resourcePanel requestFocus flag
	
	public ResourceController(Model model, View view){
		this.model = model;
		this.view = view;
		
		this.view.getResourcePanel().registerListeners(this);
		this.view.getResourcePanel().getCurrentChemTextField().addKeyListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		if (actionCommand.equals("Synthesize")){
			
			
			model.setUpSynth();
			
			this.view.remove(view.getResourcePanel());
			this.view.add(view.getSynthPanel());
			this.view.pack();
			this.view.setLocationRelativeTo(null);

		}
	}

	@Override
	public void focusGained(FocusEvent e) {

		// resetting focus to panel after initial chemTextField is added
		if (!focusOnPanel){
			view.getResourcePanel().requestFocus();
			focusOnPanel = true;
		} else {
			JTextField tf = (JTextField) e.getComponent();
			
			if (tf.getText().equals(view.getResourcePanel().getDesiredPlaceholderText())){
				tf.setText("");
			}
			
			if (tf.getText().equals(view.getResourcePanel().getChemPlaceholderText()))
				tf.setText("");
			
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		JTextField tf = ((JTextField) e.getComponent());
		
		if (tf.getText().trim().isEmpty()) {
			tf.setText(view.getResourcePanel().getChemPlaceholderText());
			tf.setBackground(Color.WHITE);
			view.getResourcePanel().getErrorLabel().setText("");
			
			if (allSet()){
				view.getResourcePanel().getReadyLabel().setForeground(CUSTOM_GREEN);
				view.getResourcePanel().getReadyLabel().setText("Ready to go!");
				view.getResourcePanel().getSynthButton().setEnabled(true);
				
			} else {
				view.getResourcePanel().getSynthButton().setEnabled(false);
			}
			
		} else if (existsInDatabase(tf.getText())) {
			
			if (!view.getResourcePanel().getResourceMap().containsValue(tf.getText().trim())
					|| view.getResourcePanel().getResourceMap().get(tf).equals(tf.getText().trim())) {
				tf.setBackground(CUSTOM_GREEN);
				this.view.getResourcePanel().getResourceMap().put((JTextField) e.getComponent(), tf.getText().trim());
				this.view.getResourcePanel().getErrorLabel().setText("");
				
			} else {
				for (JTextField t : view.getResourcePanel().getChemList()) {
					if (tf != t && t.getText().trim().equals(tf.getText().trim())){
						// duplicate
						tf.setBackground(CUSTOM_RED);
						view.getResourcePanel().getErrorLabel().setText("Chemical already listed");
						view.getResourcePanel().getReadyLabel().setText("");
					}
				}
			}
				
			if (allSet()){
				view.getResourcePanel().getReadyLabel().setForeground(CUSTOM_GREEN);
				view.getResourcePanel().getReadyLabel().setText("Ready to go!");
				view.getResourcePanel().getSynthButton().setEnabled(true);
				
			} else {
				view.getResourcePanel().getSynthButton().setEnabled(false);
			}
			
		} else if (!existsInDatabase(tf.getText()) && !tf.getText().equals(view.getResourcePanel().getChemPlaceholderText())) {
			tf.setBackground(CUSTOM_RED);
			this.view.getResourcePanel().getErrorLabel().setText("Formula doesn't exist in database");
			view.getResourcePanel().getReadyLabel().setText("");

		} 
	}

	private boolean allSet() {
		boolean ready = true;
		
		for (JTextField tf : view.getResourcePanel().getChemList()){
			if (tf.getBackground().equals(CUSTOM_RED)) {
				ready = false;
				break;
			}
		}
		
		if (!view.getResourcePanel().getDesiredTextField().getBackground().equals(CUSTOM_GREEN))
			ready = false;
		
		return ready;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			JTextField tf = (JTextField) e.getComponent();
			
			// create a new text field
			if(!tf.getText().trim().isEmpty() 
					&& view.getResourcePanel().getChemList().contains(tf)
					&& (view.getResourcePanel().getChemList().size() < 5)
					&& existsInDatabase(tf.getText())
					&& !view.getResourcePanel().getResourceMap().containsValue(tf.getText())) {

				//add and register listeners to the new text field
				this.view.getResourcePanel().addChemTextField();
				this.view.getResourcePanel().getCurrentChemTextField().addFocusListener(this);
				this.view.getResourcePanel().getCurrentChemTextField().addKeyListener(this);
				this.view.getResourcePanel().getCurrentChemTextField().requestFocus();
				this.view.getResourcePanel().getErrorLabel().setText("");
			
				
//			} else if (!existsInDatabase(tf.getText()) 
//					&& !tf.getText().isEmpty()) {
//				this.view.getResourcePanel().setErrorColor(Color.RED);
//				this.view.getFocus().setBackground(CUSTOM_RED);
//				this.view.getResourcePanel().getErrorLabel().setText("Formula doesn't exist in database");
//				
//			} else if (view.getResourcePanel().getResourceMap().containsValue(view.getFocus().getText())) {
//				tf.setBackground(CUSTOM_RED);
//				this.view.getResourcePanel().getErrorLabel().setBackground(CUSTOM_RED);
//				this.view.getResourcePanel().getErrorLabel().setText("Chemical already listed");
//				
//			} else if (view.getResourcePanel().getChemList().size() == 5) {
//				tf.setBackground(CUSTOM_GREEN);
//				this.view.getResourcePanel().setErrorColor(CUSTOM_GREEN);
//				this.view.getResourcePanel().getErrorLabel().setText("Resource limit reached");	
				
			} else {
				this.view.getResourcePanel().requestFocus();
			}
		}		
	}

	private boolean existsInDatabase(String resource) {
		boolean exists = true;
		String sql = "select formula from reactants where formula='" + resource.trim() + "'";
		// checks if there is any information returned from the sql statement
		if (model.getDatabase().select("formula", sql).isEmpty()) {
			exists = false;
		}
		
		return exists;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}
