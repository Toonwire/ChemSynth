package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view.View;
import model.Model;

public class SynthController implements ActionListener {

	private Model model;
	private View view;
	
	public SynthController(Model model, View view){
		this.model = model;
		this.view = view;

		this.view.getSynthPanel().registerListeners(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("Back")) {
			view.remove(view.getSynthPanel());
			view.add(view.getResourcePanel());
			view.pack();
			view.setLocationRelativeTo(null);
		}
			
			
		
	}
}
