import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;


public class IDE extends JFrame implements ActionListener, WindowListener, ComponentListener, KeyListener, DocumentListener, ProgramConstants{

	private static final long serialVersionUID = 1L;
	private static JMenuBar menubar;
	private static JMenu file_menu, edit_menu, tools_menu, options_menu, run_menu;
	private static JDesktopPane pane;
	private static JInternalFrame program, output, memory, playback;
	private static JTextArea program_text, output_text;
	private static JButton play_button,pause_button,next_button,prev_button,stop_button;
	
	private static JFrame stringToCodeMenu;
	private static String stringToCodeString;
	private static JRadioButton[] memory_buttons, usage_buttons;
	private static JComboBox maxMemoryCellBox;
	private static int cell_direction;
	private static int cell_usage;
	private static int max_num_memory_cells;
	
	private static JFrame optionsMenu;
	private static boolean auto_resize_internal_frames;
	
	private static BF currentProgram;


	/*****************
	 * PUBLICLY ACCESSIBLE METHODS
	 * -print: Prints a message to the output console.
	 * -IDE: A default constructor.
	 ****************/
	public static void print(char c){
		output_text.append(""+c);
	}
	
	public static void print(String s){
		for(int i = 0; i < s.length(); i++){
			print(s.charAt(i));
		}
	}
	
	//Returns true if the program should halt operation
	public static boolean update(int cell, int value, int pointer){
		return false;
	}

	public IDE(){

		//Initialize the screen elements
		auto_resize_internal_frames = DEFAULT_AUTO_RESIZE_FRAMES;
		initializeScreen();
		initializeMenu();
		initializeInputConsole();
		initializeOutputConsole();
		initializePlayback();
		initializeMemoryAnalyzer();
		
		//Initialize the brainfuck object
		currentProgram = new BF(INIT_BUFFER_SIZE);
		
		//Make the screen visible to start the program
		setVisible(true);
		resizeInternalFrames();
	}

	/*****************
	 * CONSTRUCTOR HELPER METHODS
	 * -initializeScreen: The JFrame options
	 * -initializeMenu
	 * -initializeInputConsole
	 * -initializeOutputConsole
	 * -initializePlayback
	 * -initializeMemoryAnalyzer
	 * -addJMenu
	 * -addButton
	 * -createImageIcon
	 * -resizeInternalFrames
	 ****************/
	
	private void initializeScreen(){
		setSize(APPLICATION_WIDTH,APPLICATION_HEIGHT);
		setResizable(true);
		setLocation(0,0);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		pane = new JDesktopPane();
		pane.setBackground(Color.LIGHT_GRAY);
		setContentPane(pane);
		addWindowListener(this);
		addComponentListener(this);
	}
	
	private void initializeMenu(){
		menubar = new JMenuBar();
		menubar.setBackground(Color.GRAY);
		addJMenu(menubar, file_menu, "File", file_options, MENU_COLOR);
		addJMenu(menubar, edit_menu, "Edit", edit_options, MENU_COLOR);
		addJMenu(menubar, tools_menu, "Tools", tools_options, MENU_COLOR);
		addJMenu(menubar, options_menu, "Options", options_options, MENU_COLOR);
		addJMenu(menubar, run_menu, "Run", run_options, MENU_COLOR);
		setJMenuBar(menubar);
	}
	
	private void initializeInputConsole(){
		program = new JInternalFrame("New Document",true,false,false,false);
		program_text = new JTextArea();
		program_text.setTabSize(SPACES_PER_TAB);
		program_text.setEditable(true);
		program_text.addKeyListener(this);
		program_text.getDocument().addDocumentListener(this);
		program.add(new JScrollPane(program_text));
		pane.add(program);
		program.setVisible(true);
	}
	
	private void initializeOutputConsole(){
		output = new JInternalFrame("Output",true,false,false,false);
		output_text = new JTextArea();
		output_text.setEditable(false);
		output.add(new JScrollPane(output_text));
		pane.add(output);
		output.setVisible(true);
	}
	
	private void initializePlayback(){
		playback = new JInternalFrame("Playback",true,false,false,false);
		JPanel buttonsPanel = new JPanel();
		addButton(buttonsPanel, play_button, IMAGES_PATH + "play.png", "Play");
		addButton(buttonsPanel, pause_button, IMAGES_PATH + "pause.png", "Pause");
		addButton(buttonsPanel, next_button, IMAGES_PATH + "next.png", "Next");
		addButton(buttonsPanel, prev_button, IMAGES_PATH + "prev.png", "Prev");
		addButton(buttonsPanel, stop_button, IMAGES_PATH + "stop.png", "Stop");
		playback.add(buttonsPanel);
		pane.add(playback);
		playback.setVisible(true);
	}
	
	private void initializeMemoryAnalyzer(){
		memory = new JInternalFrame("Memory Analyzer",true,false,false,false);
		pane.add(memory);
		memory.setVisible(true);
	}
	
	private void addJMenu(JMenuBar menubar, JMenu menu, String title, String[] items, Color c){
		menu = new JMenu(title);
		menu.setBackground(c);
		for(String s: items){
			JMenuItem temp = new JMenuItem(s);
			temp.addActionListener(this);
			menu.add(temp);
		}
		menubar.add(menu);
	}

	private void addButton(JPanel p, JButton b, String icon_path, String title){
		ImageIcon i = createImageIcon(icon_path, title);
		b = new JButton(i);
		b.setActionCommand(title);
		b.addActionListener(this);
		p.add(b);
	}
	
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) return new ImageIcon(imgURL, description);
		return null;
	}

	private void resizeInternalFrames(){
		int width = this.getSize().width;
		int height = this.getSize().height;
		program.setSize(2*width/3, 2*height/3);
		program.setLocation(0, 0);
		output.setSize(2*width/3, height/3);
		output.setLocation(0, 2*height/3);
		playback.setSize(width/3, PLAYBACK_FRAME_HEIGHT);
		playback.setLocation(2*width/3, 0);
		memory.setSize(width/3, height - PLAYBACK_FRAME_HEIGHT);
		memory.setLocation(2*width/3, PLAYBACK_FRAME_HEIGHT);
//		try{
//			memory.setSelected(false);
//			program.setSelected(true);
//		}catch(Exception e){}
	}
	
	/*****************
	 * LISTENER HELPER METHODS
	 * -play: The play button runs the current program
	 * -resizeBuffer: Prompts the user to enter a new brainfuck buffer size
	 ****************/
	
	private void play(){
		output_text.setText("");
		currentProgram.setProgram(program_text.getText());
		currentProgram.play();
	}
	
	private void next(){}
	private void prev(){}
	private void stop(){}
	
	private void resizeBuffer(){
		String value = JOptionPane.showInputDialog(this,
				"Enter a size for the program buffer.\n(Between 1 and " + Integer.MAX_VALUE + ")", "Set Buffer Size");
		try{
			currentProgram.setBufferSize(Integer.parseInt(value));
			JOptionPane.showMessageDialog(this, "Buffer size set to: " + currentProgram.getBufferSize() + "bytes.");
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Invalid buffer size.");
		}
	}
	
	private void autoIndent(){
		
		ArrayList<String> lines = autoIndent(removeNonPrinting(program_text.getText()));
		program_text.setText("");
		for(int i = 0; i < lines.size(); i++){
			program_text.append(lines.get(i) + "\n");
		}
	}
	
	private ArrayList<String> autoIndent(String s){
		
		//Base Case
		ArrayList<String> temp = new ArrayList<String>();
		if (!s.contains("[")){
			if (s.equals("")) return temp;
			temp.add(s);
			return temp;
		}
		
		//Recursive search
		int last = 0;
		for(int i = 0; i < s.length(); i++){
			char c = s.charAt(i);
			if (c == '['){
				//Check here for short paren ignores
				if (i > 0) temp.add(s.substring(last,i));
				temp.add("[");
				last = findMatchingCloseParen(s,i);
				ArrayList<String> indents = autoIndent(s.substring(i+1,last));
				for(int j = 0; j < indents.size(); j++) temp.add("\t" + indents.get(j));
				temp.add("]");
				i = last;
				last++;
			}
		}
		String end = s.substring(last);
		if (!end.equals("")) temp.add(end);
		return temp;
	}
	
	private String removeNonPrinting(String s){
		String return_val = "";
		for(int i = 0; i < s.length(); i++){
			char c = s.charAt(i);
			if (c > 32 && c < 127) return_val += c;
		}
		return return_val;
	}
	
	//Starts at index of open paren, returns index of closing paren
	private int findMatchingCloseParen(String s, int i){
		int num_open_paren = 1;
		while (num_open_paren != 0){
			i++;
			if (s.charAt(i) == '['){
				num_open_paren++;
			}else if(s.charAt(i) == ']'){
				num_open_paren--;
			}
		}
		return i;
	}
	
	private void stringToCode(){
		
		//Disable the main program
		setEnabled(false);
		
		//Create an optionsMenu
		stringToCodeString = JOptionPane.showInputDialog(null, "Enter the text to be converted to BF code:", "Hello World!");
		if (stringToCodeString.equals("")) return;
		
		stringToCodeMenu = new JFrame("String to Code Options");
		stringToCodeMenu.setSize(OPTIONS_MENU_WIDTH, OPTIONS_MENU_HEIGHT);
		stringToCodeMenu.setLocationRelativeTo(null);
		stringToCodeMenu.setResizable(false);
		stringToCodeMenu.setAlwaysOnTop(true);
		stringToCodeMenu.addWindowListener(this);
		
		//Add the components
		JPanel panel = new JPanel();
		panel.add(new JLabel("The String that will be converted to BF code is:"));
		panel.add(new JLabel(stringToCodeString));
		panel.add(new JLabel("------------------------------------------------"));
		
		//Choose which memory to use
		panel.add(new JLabel("How would you like to use memory?"));
		cell_direction = CELLS_TO_RIGHT;
		memory_buttons = new JRadioButton[string_to_code_memory.length];
		ButtonGroup b = new ButtonGroup();
		for(int i = 0; i < string_to_code_memory.length; i++){
			memory_buttons[i] = new JRadioButton(string_to_code_memory[i]);
			memory_buttons[i].addActionListener(this);
			b.add(memory_buttons[i]);
			panel.add(memory_buttons[i]);
		}
		memory_buttons[0].setSelected(true);
		
		//Print out the string or store it?
		panel.add(new JLabel("What would you like to do with this message?"));
		cell_usage = PRINT_MESSAGE;
		usage_buttons = new JRadioButton[string_to_code_action.length];
		b = new ButtonGroup();
		for(int i = 0; i < string_to_code_action.length; i++){
			usage_buttons[i] = new JRadioButton(string_to_code_action[i]);
			usage_buttons[i].addActionListener(this);
			b.add(usage_buttons[i]);
			panel.add(usage_buttons[i]);
		}
		usage_buttons[0].setSelected(true);
		
		//The jcombo box for max memory available
		max_num_memory_cells = 1;
		panel.add(new JLabel("Selected the maximum number of memory cells to use."));
		maxMemoryCellBox = new JComboBox();
		setMaxMemoryCellBox();
		maxMemoryCellBox.setSelectedIndex(0);
		maxMemoryCellBox.setActionCommand("Set Max Memory Cells");
		maxMemoryCellBox.addActionListener(this);
		panel.add(maxMemoryCellBox);
		JButton go_button = new JButton("GO!");
		go_button.setActionCommand("String to Code Okay");
		go_button.addActionListener(this);
		panel.add(go_button, BorderLayout.SOUTH);
		stringToCodeMenu.add(panel);
		
		stringToCodeMenu.setVisible(true);
	}
	
	//Recreates the elements in the maxMemoryCellBox to account for the given values
	private void setMaxMemoryCellBox(){
		
		//Determine the new dimensions of the maxMemoryCellBox
		int size = 1;
		if (cell_direction != CURRENT_CELL){
			size = 2*stringToCodeString.length();
			if (halfRange()) size = stringToCodeString.length();
		}
		
		//Reinitialize jcombobox
		String[] numbers = new String[size];
		if (halfRange())
			for(int i = stringToCodeString.length(); i < 2*stringToCodeString.length(); i++) numbers[i - stringToCodeString.length()] = "" + i;
		else
			for(int i = 1; i <= numbers.length; i++) numbers[i-1] = ""+i;
		maxMemoryCellBox.removeAllItems();
		for(String s: numbers) maxMemoryCellBox.addItem(s);
		
		//Update memory values
		if (numbers.length < max_num_memory_cells) {
			maxMemoryCellBox.setSelectedIndex(0);
			if (halfRange()){
				if (max_num_memory_cells - stringToCodeString.length() > 0){
					maxMemoryCellBox.setSelectedIndex(max_num_memory_cells - stringToCodeString.length());
				}else
					max_num_memory_cells = stringToCodeString.length();
			}else
				max_num_memory_cells = 1;
		}else
			maxMemoryCellBox.setSelectedIndex(max_num_memory_cells - 1);
	}
	
	//This means use the second half of the maxMemoryCellBox's range
	private boolean halfRange(){
		return cell_usage == STORE_MESSAGE;
	}
	
	private void generateCode(){
		
		String insertion;
		
		if (cell_usage == PRINT_MESSAGE) insertion = generatePrintCode();
		else insertion = generateStoreCode();
		
		int currPosition = program_text.getCaretPosition();
		program_text.insert(insertion, currPosition);
		program_text.select(currPosition, currPosition + insertion.length());
	}
	
	//This code prints the string and then destroys itself
	private String generatePrintCode(){
		
		String insertion = "[-]";
		char prev = 0;
		char curr;
		int diff;
		char sign;
		
		for(int i = 0; i < stringToCodeString.length(); i++){
			curr = stringToCodeString.charAt(i);
			if (curr > prev) sign = '+';
			else sign = '-';
			diff = Math.abs(curr - prev);
			for(int j = 0; j < diff; j++) insertion += sign;
			insertion += '.';
			prev = curr;
		}
		
		return insertion;
	}
	
	//This code stores the char values sequentially in memory
	private String generateStoreCode(){
		return "";
	}

	private void openOptionsMenu(){
		
		//Disable the main program
		setEnabled(false);
		
		//Create an optionsMenu
		optionsMenu = new JFrame("Options Menu");
		optionsMenu.setSize(OPTIONS_MENU_WIDTH, OPTIONS_MENU_HEIGHT);
		optionsMenu.setLocationRelativeTo(null);
		optionsMenu.setResizable(false);
		optionsMenu.setAlwaysOnTop(true);
		optionsMenu.addWindowListener(this);
		
		JPanel panel = new JPanel();
		JCheckBox auto_resize = new JCheckBox("Automatically resize internal windows when application resizes.", auto_resize_internal_frames);
		auto_resize.addActionListener(this);
		auto_resize.setActionCommand("Options Auto Resize Checkbox");
		panel.add(auto_resize);
		JButton okay_button = new JButton("Okay");
		okay_button.addActionListener(this);
		okay_button.setActionCommand("Options Okay");
		panel.add(okay_button);
		optionsMenu.add(panel);
		
		//Make it visible to start the options query
		optionsMenu.setVisible(true);
	}
	
	private void closeMenu(JFrame frame){
		frame.dispose();
		setEnabled(true);
	}
	
	/*****************
	 * LISTENERS
	 * -ActionListener: Button, JMenuItem Events
	 * -WindowListener: Closing Events
	 * -ComponentListener: Resizing Events
	 * -KeyListener: Text input events
	 * -Document Listener: Text input events
	 ****************/
	
	//ActionListener
	public void actionPerformed(ActionEvent event) {

		String description = event.getActionCommand();
		
		if (event.getSource().equals(maxMemoryCellBox)){
			if (!event.toString().contains("invalid")) max_num_memory_cells = maxMemoryCellBox.getSelectedIndex() + 1;
		}
		
		if (description.equals("Play")){
			play();
		}else if(description.equals("Pause")){
			//currentProgram.pause();
		}else if (description.equals("Next")){
			next();
		}else if (description.equals("Prev")){
			prev();
		}else if (description.equals("Stop")){
			stop();
		}else if (description.equals("Set Buffer Size")){
			resizeBuffer();
		}else if(description.equals("Auto-Indent Program")){
			autoIndent();
		}else if(description.equals("String to BF Code")){
			stringToCode();
		}else if (description.equals("Reset Internal Windows")){
			resizeInternalFrames();
		}else if (description.equals("Open Options Menu")){
			openOptionsMenu();
			
		/***
		 * String To Code Menu Listener
		 */
		}else if(description.equals("Use only the necessary cells to the right of the pointer.")){
			cell_direction = CELLS_TO_RIGHT;
			setMaxMemoryCellBox();
		}else if(description.equals("Use only the necessary cells to the left of the pointer.")){
			cell_direction = CELLS_TO_LEFT;
			setMaxMemoryCellBox();
		}else if(description.equals("Use only the current pointer to print the message.")){
			cell_direction = CURRENT_CELL;
			if (stringToCodeString.length() > 1){
				cell_usage = PRINT_MESSAGE;
				usage_buttons[0].setSelected(true);
			}
			setMaxMemoryCellBox();
		}else if(description.equals("Print out message immediately.")){
			cell_usage = PRINT_MESSAGE;
		}else if(description.equals("Store the message in memory.")){
			if (cell_direction == CURRENT_CELL) usage_buttons[0].setSelected(true);
			else {
				//if (cell_direction != CURRENT_CELL || stringToCodeString.length() == 1){
				cell_usage = STORE_MESSAGE;
				setMaxMemoryCellBox();
			}
		}else if(description.equals("String to Code Okay")){
			generateCode();
			closeMenu(stringToCodeMenu);
			
		/**
		 * Options Menu Listener
		 */
		}else if (description.equals("Options Auto Resize Checkbox")){
			auto_resize_internal_frames = !auto_resize_internal_frames;
		}else if (description.equals("Options Okay")){
			closeMenu(optionsMenu);
		}
	}

	//Window Listener
	public void windowActivated(WindowEvent event) {}
	public void windowClosed(WindowEvent event) {}
	public void windowClosing(WindowEvent event) {
		if (event.getComponent().equals(optionsMenu)) closeMenu(optionsMenu);
		else if (event.getComponent().equals(stringToCodeMenu)) closeMenu(stringToCodeMenu);
		else System.exit(0);
	}
	public void windowDeactivated(WindowEvent event) {}
	public void windowDeiconified(WindowEvent event) {}
	public void windowIconified(WindowEvent event) {}
	public void windowOpened(WindowEvent event) {}

	//Component Listener
	public void componentHidden(ComponentEvent arg0) {}
	public void componentMoved(ComponentEvent arg0) {}
	public void componentResized(ComponentEvent arg0) {if(auto_resize_internal_frames)resizeInternalFrames();}
	public void componentShown(ComponentEvent arg0) {}

	//KeyListener
	public void keyPressed(KeyEvent event) {}
	public void keyReleased(KeyEvent event) {}
	public void keyTyped(KeyEvent event) {
		//System.out.println("Key pressed: " + event.getKeyChar());
		//System.out.println("Action key: " + event.isActionKey() + " Alt: " + event.isAltDown()
		//		+ " AltGraph: " + event.isAltGraphDown() + " Consumed: " + event.isConsumed() + " Ctrl: " + event.isControlDown()
		//		 + " Meta: " + event.isMetaDown() + " Shift: " + event.isShiftDown());
	}

	//Document Listener
	public void changedUpdate(DocumentEvent e) {}
	public void insertUpdate(DocumentEvent e) {}
	public void removeUpdate(DocumentEvent e) {}

	/**
	 * main: Starts the program.
	 */
	public static void main(String[] args){
		new IDE();
	}
}
