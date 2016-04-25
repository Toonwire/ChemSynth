package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import model.Model;

public class SynthController implements PropertyChangeListener, ActionListener{

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
		view.getSynthPanel().addReactionToPath((String) e.getNewValue());
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		System.out.println(actionCommand);
		if (actionCommand.equals("Back")) {
			model.reset();
			view.remove(view.getSynthPanel());
			this.view.add(view.getResourcePanel());
			this.view.pack();
			
		}
	}
}
