package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import model.Model;
import view.View;

public class SynthController implements PropertyChangeListener, ActionListener, KeyListener{

	private Model model;
	private View view;
	
	public SynthController(Model model, View view){
		this.model = model;
		this.view = view;
		
		model.registerListeners(this);
		view.getSynthPanel().registerListeners(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		/*
		 * newValue = reaction
		 * oldValue = recursiveOnFormula
		 */
		view.getSynthPanel().addReactionToPath(Integer.parseInt(e.getPropertyName()), (String) e.getOldValue(), (String) e.getNewValue());
		view.getSynthPanel().requestFocus();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("Back")) 
			reset();
		if (actionCommand.equals("Next"))
			view.getSynthPanel().showNextReaction();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {

		int key = e.getKeyCode();
		if (key == KeyEvent.VK_B) 
			reset();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void reset() {
		model.reset();
		view.getSynthPanel().reset();
		
		view.remove(view.getSynthPanel());
		view.add(view.getResourcePanel());
		view.pack();
		view.getResourcePanel().requestFocus();
	}
}
