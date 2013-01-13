import java.awt.Color;


public interface ProgramConstants {
	
	//Application Constants
	static final int APPLICATION_WIDTH = 1024;
	static final int APPLICATION_HEIGHT = 600;
	static final int PLAYBACK_FRAME_HEIGHT = 118;
	static final boolean DEFAULT_AUTO_RESIZE_FRAMES = true;
	static final int SPACES_PER_TAB = 4;
	static final int OPTIONS_MENU_WIDTH = 500;
	static final int OPTIONS_MENU_HEIGHT = 360;
	
	//JMenu Constants
	static final Color MENU_COLOR = Color.gray;
	static final String[] file_options = {"Open Project", "Save Project", "Export Program", "Close Project"},
									edit_options = {"Undo", "Redo", "Cut", "Copy", "Paste", "Find"},
									tools_options = {"Set Buffer Size", "Auto-Indent Program", "String to BF Code"},
									options_options = {"Reset Internal Windows", "Open Options Menu"},
									run_options = {"Play", "Next", "Prev", "Stop"};
	
	//String to Code Option Constants
	static final String[] string_to_code_memory = {"Use only the necessary cells to the right of the pointer.",
								"Use only the necessary cells to the left of the pointer.",
								"Use only the current pointer to print the message."},
							string_to_code_action = {"Print out message immediately.", "Store the message in memory."};
	static final int CELLS_TO_RIGHT = 1;
	static final int CELLS_TO_LEFT = -1;
	static final int CURRENT_CELL = 0;
	static final int PRINT_MESSAGE = 1;
	static final int STORE_MESSAGE = -1;
	
	//Brainfuck constants
	static final int INIT_BUFFER_SIZE = 30000;
	static final int NO_UPDATE = -1;
}
