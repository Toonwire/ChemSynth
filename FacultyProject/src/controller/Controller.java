package controller;

import view.View;
import model.Model;

public class Controller {
	
	public Controller(Model model, View view){
		new ResourceController(model, view);
	}
}
