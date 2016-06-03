package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import model.Model;
import view.View;

public class SynthController implements ActionListener, KeyListener {

	private Model model;
	private View view;
	
	public SynthController(Model model, View view){
		this.model = model;
		this.view = view;
		
		view.getSynthPanel().registerListeners(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("Back")) 
			reset();
		
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
		view.getResourcePanel().getDesiredTextField().requestFocus();
	}

}
