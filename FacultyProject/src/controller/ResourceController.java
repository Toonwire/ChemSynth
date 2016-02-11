package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import view.View;
import model.Model;

public class ResourceController implements FocusListener, ActionListener, KeyListener {

	private Model model;
	private View view;
	
	private boolean hackOnce = true; // resourcePanel requestFocus hack
	
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

		}
		
	}

	@Override
	public void focusGained(FocusEvent e) {

		if (hackOnce){
			view.getResourcePanel().requestFocus();
			hackOnce = false;
		} else {
			
			for (JTextField t : view.getResourcePanel().getChemList()){
				if (t.getText().equals(view.getResourcePanel().getChemPlaceholderText()) && t.isFocusOwner()) {
					t.setText("");
				}
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		
		for (JTextField t : view.getResourcePanel().getChemList()){
			if (t.getText().trim().isEmpty()) {
				t.setText(view.getResourcePanel().getChemPlaceholderText());
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			
			if(!view.getResourcePanel().getCurrentChemTextField().getText().trim().isEmpty() 
					&& !view.getResourcePanel().getCurrentChemTextField().getText().equals(view.getResourcePanel().getChemPlaceholderText())
					&& (view.getResourcePanel().getChemList().size() < 5)) {
				this.view.getResourcePanel().addChemTextField();
				this.view.getResourcePanel().getCurrentChemTextField().addFocusListener(this);
				this.view.getResourcePanel().getCurrentChemTextField().addKeyListener(this);
				this.view.getResourcePanel().getCurrentChemTextField().requestFocus();
				
			} else {
				this.view.getResourcePanel().requestFocus();
//				this.view.getResourcePanel().addLimitErrorLabel();	// no work
			}
		}		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}
