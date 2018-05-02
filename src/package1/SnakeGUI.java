package package1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.JobAttributes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.swing.*;

import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SnakeGUI extends JFrame {
	
	private final static int NORTH = 1, EAST = 2, SOUTH = 3, WEST = 4;
	private final static int NUMBER_OF_ROWS = 20, NUMBER_OF_COLUMNS = 20;
	private final static int[] TIMER_INTERVALS = {250, 225, 200, 175, 150, 125, 100, 75, 50, 25};
	
	private int timerIntervalIndex = 6;
	
	private int currentDirection;
	private int previousDirection;
	
	private ArrowKeyHandler arrowKeyHandler;
	
	private int rows, columns;
	private Deque<Square> snake;
	
	private MainPanel mainPanel;
	
	private Square[][] squares;
	private Board board;
	
	private Settings settings;
	
	private Timer timer;
	private int timerInterval;
	
	private boolean arrowKeysEnabled;
	
	public SnakeGUI() {
		super("Snake");
		setLayout(new FlowLayout());
		rows = NUMBER_OF_ROWS;
		columns = NUMBER_OF_COLUMNS;
		arrowKeysEnabled = false;
		
		timerInterval = TIMER_INTERVALS[timerIntervalIndex];
		
		arrowKeyHandler = new ArrowKeyHandler();
		addKeyListener(arrowKeyHandler);
		
		mainPanel = new MainPanel();
		settings = new Settings();
		goToMainMenu();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(new Dimension(200, 150));
		setVisible(true);
		toFront();
		requestFocus();
	}
	
	private class MainPanel extends JPanel implements ActionListener {
		private JButton newGameButton;
		private JButton settingsButton;
		private JButton quitButton;
		
		public MainPanel() {
			super(new BorderLayout());
			JPanel jp = new JPanel(new BorderLayout());
			newGameButton = new JButton("New Game");
			newGameButton.addActionListener(this);
			newGameButton.setToolTipText("Start a new game");
			settingsButton = new JButton("Settings");
			settingsButton.addActionListener(this);
			settingsButton.setToolTipText("Change settings such as level and speed");
			quitButton = new JButton("Quit");
			quitButton.addActionListener(this);
			quitButton.setToolTipText("Quit the game");
			add(newGameButton, BorderLayout.NORTH);
			add(settingsButton, BorderLayout.CENTER);
			add(quitButton, BorderLayout.SOUTH);
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			if(ae.getSource() == newGameButton)
				newGame();
			else if(ae.getSource() == settingsButton)
				goToSettings();
			else if(ae.getSource() == quitButton)
				System.exit(0);
		}
	}
	
	private void goToMainMenu() {
		arrowKeysEnabled = false;
		getContentPane().removeAll();
		getContentPane().add(mainPanel);
		setSize(new Dimension(200, 150));
		repaint();
	}
	
	private class Settings extends JPanel {
		private JLabel speedLabel;
		private JSlider speedSlider;
		private JButton confirmButton;
		
		public Settings() {
			super(new GridLayout(0, 1));
			speedLabel = new JLabel("Snake speed");
			speedSlider = new JSlider(JSlider.HORIZONTAL, 0, TIMER_INTERVALS.length-1, timerIntervalIndex);
			speedSlider.setMinorTickSpacing(1);
			speedSlider.setMajorTickSpacing(1);
			speedSlider.setSnapToTicks(true);
			speedSlider.setPaintTrack(true);
			speedSlider.setPaintLabels(true);
			speedSlider.setPaintTicks(true);
			speedSlider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					timerIntervalIndex = speedSlider.getValue();
				}
			});
			confirmButton = new JButton("Confirm");
			confirmButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					timerInterval = TIMER_INTERVALS[timerIntervalIndex];
					goToMainMenu();
				}
			});
			this.add(speedLabel);
			this.add(speedSlider);
			JPanel jp = new JPanel();
			jp.add(confirmButton);
			this.add(jp);
		}
	}
	
	private void goToSettings() {
		arrowKeysEnabled = false;
		getContentPane().removeAll();
		getContentPane().add(settings);
		pack();
		repaint();
	}
	
	private class Board extends JPanel {
		
		public Board() {
			setSize(new Dimension(columns * Square.SIZE.width, rows * Square.SIZE.height));
			setPreferredSize(new Dimension(columns * Square.SIZE.width, rows * Square.SIZE.height));
			snake = new ArrayDeque<Square>();
			Square s;
			squares = new Square[rows][columns];
			for(int y = 0; y < rows; y++) {
				for(int x = 0; x < columns; x++) {
					if((x == columns/2 - 1 || x == columns/2 || x == columns/2+1) && y == rows/2) {
						s = new Square(Square.SNAKE, x, y);
						snake.addFirst(s);
					} else {
						s = new Square(Square.EMPTY, x, y);
					}
					squares[y][x] = s;
				}
			}
			int foodX, foodY = (int)(Math.random()*rows);
			do {
				foodX = (int)(Math.random()*columns);
			} while((foodX == columns/2 - 1 || foodX == columns/2 || foodX == columns/2+1) && foodY == rows/2);
			squares[foodY][foodX] = new Square(Square.FOOD, foodX, foodY);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			for(int y = 0; y < rows; y++) {
				for(int x = 0; x < columns; x++) {
					g.setColor(squares[y][x].getColor());
					g.fillRect(x * Square.SIZE.width, y * Square.SIZE.height, Square.SIZE.width, Square.SIZE.height);
				}
			}
		}
	}
	
	private void newGame() {
		arrowKeysEnabled = true;
		board = new Board();
		getContentPane().removeAll();
		getContentPane().add(board);
		getContentPane().setSize(columns * Square.SIZE.width, rows * Square.SIZE.height);
		setLocation(500, 200);
		pack();
		repaint();
		timer = new Timer(timerInterval, new TimerHandler());
		timer.start();
		currentDirection = EAST;
		toFront();
		requestFocus();
	}
	
	private class TimerHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// Calculate coordinates of the square the snake is moving towards.
			// Depends on current direction and whether the snake is at the edge of the board.
			int nextX = snake.getFirst().getX();
			int nextY = snake.getFirst().getY();
			switch(currentDirection) {
			case NORTH:
				nextY--;
				if(nextY < 0)
					nextY = rows - 1;
				break;
			case EAST:
				nextX++;
				if(nextX > columns - 1)
					nextX = 0;
				break;
			case SOUTH:
				nextY++;
				if(nextY > rows - 1)
					nextY = 0;
				break;
			case WEST:
				nextX--;
				if(nextX < 0)
					nextX = columns - 1;
				break;
			}
			switch(squares[nextY][nextX].getType()) {
			case Square.EMPTY:
				// Hit an empty square. Move the tail of the snake into this square so that it now becomes the head of the snake.
				int tailX = snake.getLast().getX();
				int tailY = snake.getLast().getY();
				Square tail = snake.removeLast();
				Square temp = squares[nextY][nextX];
				temp.setCoordinates(tailX, tailY);
				tail.setCoordinates(nextX, nextY);
				squares[nextY][nextX] = tail;
				squares[tailY][tailX] = temp;
				snake.addFirst(tail);
				break;
			case Square.FOOD:
				// Hit a food square. Extend the length of the snake, and generate a new location for the food.
				timer.stop();
				Square food = squares[nextY][nextX];
				squares[nextY][nextX] = new Square(Square.SNAKE, nextX, nextY);
				snake.addFirst(squares[nextY][nextX]);
				
				// Find an empty square to move the food into.
				int foodX, foodY;
				do {
					foodX = (int)(Math.random()*columns);
					foodY = (int)(Math.random()*rows);
				}
				while(squares[foodY][foodX].getType() != Square.EMPTY);
				food.setCoordinates(foodX, foodY);
				squares[foodY][foodX] = food;
				
				timer.start();
				break;
			case Square.SNAKE:
				// It is possible to "beat the timer", causing the snake's head to turn inwards and collide with its neck.
				// This check prevents this.
				Square head = snake.removeFirst();
				if(squares[nextY][nextX].equals(snake.getFirst())) {
					// Collision with neck, revert to previous direction.
					currentDirection = previousDirection;
					snake.addFirst(head);
					break;
				}
			default:
				// The snake hit a wall or itself. The game is lost.
				System.out.println("The game was lost");
				timer.stop();
				int o = JOptionPane.showConfirmDialog(null, "Go to the main menu?", "You lost!", JOptionPane.OK_CANCEL_OPTION);
				if(o == JOptionPane.OK_OPTION) {
					goToMainMenu();
				}
				else {
					System.exit(0);
				}
				break;
			}
			repaint();
		}
	}
	
	private class ArrowKeyHandler extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent ke) {
			boolean northOrSouth = currentDirection == NORTH || currentDirection == SOUTH;
			previousDirection = currentDirection;
			switch(ke.getKeyCode()) {
			// An arrow key was pressed. Change direction of the snake to that of the arrow's. Nothing happens if the snake's already heading in that direction.
			case KeyEvent.VK_LEFT:
				if(arrowKeysEnabled && northOrSouth) {
					currentDirection = WEST;
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(arrowKeysEnabled && northOrSouth) {
					currentDirection = EAST;
				}
				break;
			case KeyEvent.VK_UP:
				if(arrowKeysEnabled && !northOrSouth) {
					currentDirection = NORTH;
				}
				break;
			case KeyEvent.VK_DOWN:
				if(arrowKeysEnabled && !northOrSouth) {
					currentDirection = SOUTH;
				}
				break;
			// The P key was pressed. Pause the game.
			case KeyEvent.VK_P:
				pause();
				break;
			// The ESC key was pressed.
			case KeyEvent.VK_ESCAPE:
				// In game. Pause the game and ask if the player wants to go to the main menu.
				if(getContentPane().getComponents()[0] instanceof Board) {
					pause();
					int o = JOptionPane.showConfirmDialog(null, "Go to the main menu?", null, JOptionPane.OK_CANCEL_OPTION);
					if(o == JOptionPane.OK_OPTION)
						goToMainMenu();
					else
						pause(); // Unpause.
				}
				// In the main menu. Ask if the player wants to quit the game.
				else if(getContentPane().getComponents()[0] instanceof MainPanel) {
					int o = JOptionPane.showConfirmDialog(null, "Quit?", null, JOptionPane.OK_CANCEL_OPTION);
					if(o == JOptionPane.OK_OPTION)
						System.exit(0);
				}
				// In the settings menu. Go directly back to the main menu.
				else if(getContentPane().getComponents()[0] instanceof Settings) {
					goToMainMenu();
				}
			default:
				break;
			}
		}
	}
	
	// Pause the game if the timer is running, start the game if the timer isn't.
	private void pause() {
		if(timer.isRunning()) {
			arrowKeysEnabled = false;
			timer.stop();
		}
		else {
			arrowKeysEnabled = true;
			timer.start();
		}
	}
	public static void main(String[] args) {
		new SnakeGUI();
	}
}
