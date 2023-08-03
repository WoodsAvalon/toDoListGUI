package toDoList;

import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JLabel;

class toDoListing {
	private boolean complete = false;
	private String content;
	
	protected toDoListing() {}
	
	protected toDoListing(String listing) {
		content = listing;
	}	
	
	protected void setComplete(boolean val) {
		complete = val;
	}
	
	protected boolean getComplete() {
		return complete;
	}
	
	protected void setContent(String val) {
		content = val;
	}
	
	protected String getContent() {
		return content;
	}
}
