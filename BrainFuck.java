import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * class: BrainFuck
 * A brainfuck object stores all of the data that defines
 * a brainfuck program and includes the necessary methods
 * to run instances of that program.
 */
public class BrainFuck implements ProgramConstants{
	
	private byte[] tape;
	private int pointer;
	
	private String program;
	private int index;
	private String input_buffer;
	
	private BFProgram currentProgram;
	private Thread currentProgramThread;
	
	/*****************
	 * PUBLICLY ACCESSIBLE METHODS
	 * -BrainFuck: Constructor instantiates the object.
	 * -getBufferSize: Returns size of the program's tape.
	 * -setBufferSize: Reallocates a tape for the program
	 * -setProgram: Sets the program string
	 ****************/
	public BrainFuck(int bufferSize){
		tape = new byte[bufferSize];
		for(int i = 0; i < bufferSize; i++) tape[i] = 0;
		pointer = 0;
		program = "";
		index = 0;
		input_buffer = "";
		currentProgram = null;
	}
	
	public int getBufferSize(){
		return tape.length;
	}
	
	public void setBufferSize(int size){
		tape = new byte[size];
	}
	
	public void setProgram(String program){
		this.program = removeComments(program);
	}
	
	private String removeComments(String s){
		String reduced = "";
		for(int i = 0; i < s.length(); i++){
			char c = s.charAt(i);
			if(isValid(c)){
				reduced += c;
			}
		}
		return reduced;
	}
	
	private boolean isValid(char c){
		if (
			c == '+' || c == '-'
			|| c == '>' || c == '<'
			|| c == '.' || c == ','
			|| c == '[' || c == ']' || c == '?'
			) return true;
		return false;
	}
	
	public void addInput(String new_input){
		input_buffer += new_input;
	}
	
	public int getPointer(){
		return pointer;
	}
	
	public byte[] getMemory(){
		ArrayList<Byte> non_zero = new ArrayList<Byte>();
		byte[] b = new byte[non_zero.size()];
		for(int i = 0; i < non_zero.size(); i++) b[i] = non_zero.get(i);
		return b;
	}
	
	/**
	 * INTERFACE METHODS
	 * These methods respond the user pressing buttons.
	 * For example, "Play", "Next", and "Stop"
	 */
	
	public void play(){
		while (index != program.length()) executeInstruction();
	}
	
	public void nextStep(){}
	
	/**
	 * PROGRAM LOGIC:
	 * The methods that actually run a BF program.
	 */
	
	private void executeInstruction(){
		switch(program.charAt(index)){
		case '+':
			tape[pointer]++;
			break;
		case '-':
			tape[pointer]--;
			break;
		case '>':
			pointer++;
			break;
		case '<':
			pointer--;
			break;
		case '[':
			if (tape[pointer] == 0){
				int num_open_brackets = 1;
				while (num_open_brackets != 0){
					index++;
					if (program.charAt(index) == '[') num_open_brackets++;
					else if (program.charAt(index) == ']') num_open_brackets--;
				}
			}
			break;
		case ']':
			int num_open_brackets = 1;
			while (num_open_brackets != 0){
				index--;
				if (program.charAt(index) == ']') num_open_brackets++;
				else if (program.charAt(index) == '[') num_open_brackets--;
			}
			index--;
			break;
		case ',':
			tape[pointer] = (byte)(getNextCharacter());
			break;
		case '.':
			printByte(tape[pointer]);
			break;
		case '?':
			for (int i = 0; i < tape.length; i++){
				if (tape[i] != 0) System.out.println("Cell: " + i + " Value: " + tape[i]);
			}
			break;
		default:
			break;
		}
		index++;
	}
	
	private char getNextCharacter(){
		if (input_buffer.length() == 0){
			requestInput();
		}
		char c = input_buffer.charAt(0);
		input_buffer = input_buffer.substring(1);
		return c;
	}
	
	private void printByte(byte b){
		char c;
		if (b < 0) c = (char) ((int)(b) + 256);
		else c = (char) b;
		IDE.print(""+c);
	}
	
	private void requestInput(){
		addInput(JOptionPane.showInputDialog(null, "The Program Requests Additional Input", ""));
	}
	
	private class BFProgram implements Runnable{
		
		private byte[] tape;
		private int pointer;
		
		private String program;
		private int index;
		private String input_buffer;
		
		private boolean pauseExecution;
		
		public BFProgram(byte[] tape, int pointer, String program, int index, String input_buffer, boolean pauseExecution){
			this.tape = tape;
			this.pointer = pointer;
			this.program = program;
			this.index = index;
			this.input_buffer = input_buffer;
			this.pauseExecution = pauseExecution;
		}
		
		public void pause(){
			pauseExecution = true;
		}
		
		public void next(){
			if (index != program.length()) executeInstruction();
		}
		
		public void resume(){
			pauseExecution = false;
		}
		
		public void run() {
			System.out.println("Run");
		}
		

		
	}
}
