package toDoList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Project: To Do List with GUI using Swing
 * Description: Generates a to do list and GUI using swing.
 * Version: 1.0
 * 
 * Note: Used as a project to refresh past knowledge and expand into GUI work before finding Window Builder. 
 */

public class toDoMain { 
	static toDoGUI tdg;// = new toDoGUI();
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tdg = new toDoGUI();
			}
		});	
	}	

	/**
	 * Generates the GUI for displaying a basic to do list.
	 * Generates JButtons to add, complete and delete task as well as exit the program. Auto-loads task
	 * list when started and saves when closed.
	 */
	static class toDoGUI extends JFrame implements ActionListener, MouseListener, WindowListener {
		static final long serialVersionUID = 1;
		JButton jbAdd;
		JButton jbComplete;
		JButton jbDelete;
		JButton jbExit;
		JPanel buttons;
		JPanel viewPanel;
		JScrollPane scrollingPane;
		taskManager tManager;
		toDoTask target;
		
		/**
		 * Default constructor for generating list GUI. 
		 * */
		protected toDoGUI() {
			this.setTitle("To Do List");
			this.setPreferredSize(new Dimension(500,600));
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			addWindowListener(this);
			
			viewPanel = new JPanel();
			scrollingPane = new JScrollPane(viewPanel);
			viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS));
			this.getContentPane().add(scrollingPane,BorderLayout.CENTER);
			
			scrollingPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollingPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			
			//Button panel
			buttons = new JPanel();
			this.add(buttons, BorderLayout.PAGE_END);
		
			jbAdd = new JButton("Add Task");
			jbAdd.addActionListener(this);
			buttons.add(jbAdd);
			
			jbComplete = new JButton("Complete Task");
			jbComplete.addActionListener(this);
			buttons.add(jbComplete);
			
			jbDelete = new JButton("Delete");
			jbDelete.addActionListener(this);
			buttons.add(jbDelete);
		
			jbExit = new JButton("Exit");
			jbExit.addActionListener(this);
			buttons.add(jbExit);
			
			tManager = new taskManager(viewPanel, this);
			
			addMouseListener(this);
			viewPanel.addMouseListener(this);
		
			this.setResizable(false);
			this.pack();
			this.setVisible(true);}
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == jbAdd) {
				//adds new default task
				System.out.println("Adding task!");
				tManager.createTask();
			}
			else if (e.getSource() == jbComplete) {
				//updates task state
				tManager.completetTask(target);
			}
			else if (e.getSource() == jbDelete) {
				//removes targeted task from list
				if (target != null) {
					tManager.deleteTask(target);
				}
			}
			else if (e.getSource() == jbExit) {
				/*exit program*/
				tManager.saveList();
				System.exit(NORMAL);
			}
		}
		
		public void mousePressed(MouseEvent e) {}
		
		public void mouseReleased(MouseEvent e) {}
		
		public void mouseEntered(MouseEvent e) {}
		
		public void mouseExited(MouseEvent e) {}
		
		//used to highlight selected task to interact with it
		public void mouseClicked(MouseEvent e) {
			System.out.println(e.getSource());
			target = tManager.highLightTask(e.getSource());
		}
		
		public void windowActivated(WindowEvent e) {}
		
		//used to save data when program is closed
		public void windowClosed(WindowEvent e) {
			tManager.saveList();
		}
		
		//used to save data when program is closed
		public void windowClosing(WindowEvent e) {
			tManager.saveList();
		}
		
		public void windowDeactivated(WindowEvent e) {}
		
		public void windowIconified(WindowEvent e) {}
		
		public void windowDeiconified(WindowEvent e) {}
		
		//used to load save file when program starts
		public void windowOpened(WindowEvent e) {
			tManager.loadList();
		}
	}
	
	/**
	 * Internal class used to manage the creation, interaction and removal of task.
	 */
	static private class taskManager {
		ArrayList<toDoTask> taskList;
		JPanel displayPanel;
		toDoTask target;
		String filename = "taskList.txt";
		MouseListener mL;
		
		/**
		 * Default constructor, takes in pointer to the panel, scene, that task will be added to
		 * and the MouseListener used to detect when a task is clicked on.
		 * 
		 * @param scene				JPanel used as the container that task are added to.
		 * @param mouseListener		MouseListener supplied by the GUI to be added to each task.
		 */
		taskManager(JPanel scene, MouseListener mouseListener) {
			taskList = new ArrayList<toDoTask>();
			displayPanel = scene;
			mL = mouseListener;
		}
		
		/**
		 * Called to set the targeted task as completed.
		 * 
		 * @param task	toDoTask, The toDoTask that is being interacted with.
		 */
		void completetTask(toDoTask task) {
			if (task != null) {
				taskList.get(taskList.indexOf(task)).setComplete();
			}
		}
		
		/**
		 * Removes connects to the targeted task and updates the display.
		 * 
		 * @param task	toDoTask, The toDoTask that is being interacted with.
		 */
		void deleteTask(toDoTask task) {
			if (task != null) {
				taskList.remove(task);
				displayPanel.remove(task);
				
				displayPanel.updateUI();
			}
		}
		
		/**
		 * Creates a blank task and adds it to the list and display. 
		 */
		void createTask() {
			target = new toDoTask();
			
			target.addMouseListener(mL);
			
			taskList.add(target);
			displayPanel.add(target);
			target = null;
			
			displayPanel.updateUI();
		}
		
		/**
		 * Called when loading task from save file into memory.
		 * 
		 * @param tState		Boolean, The completed state of the task, false is pending and true is completed.
		 * @param tStart		String, The time that the task was started.
		 * @param tComplete		String, The time that the task is completed.
		 * @param tText			String, The text of the task.
		 */
		void createTask(boolean tState, String tStart, String tComplete, String tText) {
			target = new toDoTask(tState, tStart, tComplete, tText);
			
			target.addMouseListener(mL);
			
			taskList.add(target);
			displayPanel.add(target);
			target = null;
			
			displayPanel.updateUI();
		}
		
		/**
		 * Called to either highlight the selected task or unhighlight the task.
		 * 
		 * @param task	The task that is to be highlighted or unhighlighted.
		 * @return	Returns pointer to highlighted task or null if no task is highlighted.
		 */
		toDoTask highLightTask(Object task) {
			if (task instanceof toDoTask) {
				if (target != null) { target.highlightTask(); }
				target = (toDoTask)task;
				target.highlightTask();
				return target;
			}
			return null;
		}
		
		//used to load task data from hard drive
		void loadList() {
			try {
				File taskFile = new File(filename);
				if (taskFile.exists() && !taskFile.isDirectory()) {
					String data;
					String[] dataArray;
					Scanner fileReader = new Scanner(taskFile);
					while (fileReader.hasNextLine()) {
						data = fileReader.nextLine();
						//System.out.println(data);
						dataArray = data.split(",");
						createTask(Boolean.parseBoolean(dataArray[0]), dataArray[1], dataArray[2], dataArray[3]);
					}
					fileReader.close();				    
				}
			}
			catch (FileNotFoundException e) {
				System.out.println("An error occurred loading task data.");
			    e.printStackTrace();
			}
		}
		
		//used to save task data to hard drive
		void saveList() {
			try {
				//creates task file if missing
				File taskFile = new File(filename);
			    taskFile.createNewFile();
			    //writes task to file
			    FileWriter fileWriter = new FileWriter(filename);
			    taskList.forEach(task -> {
			    	try {
			    		fileWriter.write(task.getData());
			    	}
			    	catch (IOException e) {
						System.out.println("An error occurred saving task list.");
					    e.printStackTrace();
			    	}
			    }) ;
			    fileWriter.close();
			} 
			catch (IOException e) {
				System.out.println("An error occurred creating task file.");
			    e.printStackTrace();
			}
		}
	}
	
	/**
	 * toDoTask class that defines and manages the appearance of the individual task.
	 */	
	static private class toDoTask extends JPanel {
		static final long serialVersionUID = 1;
		private JLabel startTime; //set when task is created
		private JLabel completeTime; //set when task is completed
		private JLabel taskState; //displays current state based on state variable
		private JPanel times; //holds startTime and completeTime in vertical form for formatting
		private JTextArea jtaTask; //starts with default text and is cleared when user clicks
		//state false is pending, state true is completed
		private boolean state;
		
		
		/**
		 * Called to generate the interactive appearance of the task.
		 */
		private void generateAppearance() {			
			//sets appearance
			this.setBorder(BorderFactory.createLineBorder(Color.black));
			this.setPreferredSize(new Dimension(450,100));
			this.setMaximumSize(new Dimension(450,100));
		
			//generate appearance using setLocation
			this.setLayout(new BorderLayout());
			this.add(times, BorderLayout.LINE_START);
			
			//status label
			this.add(taskState, BorderLayout.PAGE_START);
			
			//left side times
			//start time
			times.add(startTime);
			
			//completed time
			times.add(completeTime);
			
			//text area
			this.add(jtaTask,BorderLayout.CENTER);
		}
		
		/**
		 * Default constructor for the task object.
		 */
		protected toDoTask() {
			jtaTask = new JTextArea("Add task here.");
			startTime = new JLabel("Started: " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
			taskState = new JLabel("Pending");
			taskState.setOpaque(true);
			completeTime = new JLabel("Complete: ");
			times = new JPanel();
			times.setLayout(new BoxLayout(times, BoxLayout.PAGE_AXIS));
			
			taskState.setText("Pending");
			taskState.setBackground(Color.CYAN);
			completeTime.setText("Completed: Pending");
			
			generateAppearance();
		}
		
		/**
		 * Constructor called when loading task data from file into memory.
		 * 
		 * @param tState		boolean, Current state of the task, false for pending, true for completed.
		 * @param tStart		String, Time task was started.
		 * @param tComplete		String, Time task was completed.
		 * @param tText			String, Text of task.
		 */
		protected toDoTask(boolean tState, String tStart, String tComplete, String tText) {
			jtaTask = new JTextArea(tText);
			startTime = new JLabel(tStart);
			taskState = new JLabel("Pending");
			taskState.setOpaque(true);
			completeTime = new JLabel(tComplete);
			times = new JPanel();
			times.setLayout(new BoxLayout(times, BoxLayout.PAGE_AXIS));
			state = tState;
			
			if (state) {
				setComplete();
			}
			else {
				taskState.setText("Pending");
				taskState.setBackground(Color.CYAN);	
			}
						
			generateAppearance();
		}
		
		/**
		 * Sets the taskState or "pending" labels background color based on the current selected state.
		 * Background is set to yellow if selected or, if unselected, green for completed and cyan for pending.
		 */
		void highlightTask() {
			if (taskState.getBackground() == Color.YELLOW) {
				if (state) {taskState.setBackground(Color.GREEN);}
				else {taskState.setBackground(Color.CYAN);}
			}
			else {
				taskState.setBackground(Color.YELLOW);
			}
		}
		
		/**
		 * Sets the task to complete and updates the state, taskState label and completed time.
		 */		
		void setComplete() {
			taskState.setText("Complete");
			taskState.setBackground(Color.GREEN);
			completeTime.setText("Completed: " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
			state = true;
		}
		
		/**
		 * Formats the task data into a string that is then returned to be saved into a file.
		 * 
		 * @return String, task state separated by commas, state, start time, complete time, task text.
		 */
		String getData() {
			return Boolean.toString(state) + "," + startTime.getText() + "," + completeTime.getText() + "," + jtaTask.getText() + "\n";
		}
	}
}
