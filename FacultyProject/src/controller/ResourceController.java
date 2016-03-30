package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import model.Model;
import model.Node;
import model.Pair;
import view.View;

public class ResourceController implements FocusListener, ActionListener, KeyListener {

	private Model model;
	private View view;
	
	private Color CUSTOM_RED = new Color(213,103,106);
	private Color CUSTOM_GREEN = new Color(63,204,155);
	
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
			
			this.view.remove(view.getResourcePanel());
			this.view.add(view.getSynthPanel());
			this.view.pack();
			this.view.setLocationRelativeTo(null);
			
			String desired = view.getResourcePanel().getDesiredTextField().getText().trim();
			model.setDesiredChemical(desired);
			model.setUpSynth(view.getResourcePanel().getResourceList(), desired);
			view.getSynthPanel().runAnimation();
				
			System.out.println();
			printNetReaction();
			
			
		}
	}

	private void printNetReaction() {
		StringBuilder builder = new StringBuilder();
		StringBuilder reactantBuilder = new StringBuilder();
		StringBuilder productBuilder = new StringBuilder();
		
		for (String formula : model.getNetReactionMap().keySet()) {
			int coef = model.getNetReactionMap().get(formula);
			if (coef != 0) {
				if (coef < 0) reactantBuilder.append(Math.abs(coef) + formula + " + ");
				else if (coef > 0) productBuilder.append(Math.abs(coef) + formula + " + ");
				
			}
		}
		builder.append(reactantBuilder.toString().substring(0,reactantBuilder.toString().length()-3) 
				+ " --> " 
				+ productBuilder.toString().substring(0,productBuilder.toString().length()-3));
		System.out.println(builder.toString());
		
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
				view.getResourcePanel().getReadyLabel().setForeground(CUSTOM_GREEN);
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
			// TODO: Better way of catching these if statements?
			// could just merge with the below else statement and NOT it (!)  ???
			
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
					view.getResourcePanel().getReadyLabel().setForeground(CUSTOM_GREEN);
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
		
//		for (JTextField tf : view.getResourcePanel().getChemList()){
//			if (tf.getBackground().equals(CUSTOM_RED)) {
//				ready = false;
//				break;
//			}
//		}
		
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
					this.view.getResourcePanel().getErrorLabel().setForeground(CUSTOM_GREEN);
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
		}	
	}

	private boolean existsInDatabase(String resource) {
		boolean exists = false;
		
		String sql = "select formula from (select distinct formula from reactants UNION select distinct formula from products) where formula=?;"; 
		
		// checks if there is any information returned from the sql statement
		exists = model.getDatabase().checkResource(sql, resource.trim());
		
//		System.out.println(lookups);
		return exists;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}
