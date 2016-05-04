package controller;

import model.Model;
import view.View;

public class Controller {
	
	public Controller(Model model, View view){
		new ResourceController(model, view);
		new SynthController(model, view);
	}
}
