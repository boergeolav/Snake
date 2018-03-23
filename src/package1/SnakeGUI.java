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
	
	private JFrame thisRef;
	
	private final static int NORTH = 1, EAST = 2, SOUTH = 3, WEST = 4;
	private final static int NUMBER_OF_ROWS = 20, NUMBER_OF_COLUMNS = 20;
	private final static int[] TIMER_INTERVALS = {250, 225, 200, 175, 150, 125, 100, 75, 50, 25};
	
	private int timerIntervalIndex = 6;
	
	private int currentDirection;
	private int previousDirection;
	
	private ArrowKeyHandler arrowKeyHandler;
	
	private int rows, columns;
	private Deque<Square> snake;
	
	private Square[][] squares;
	
	private JPanel outerPanel;
	
	private Board board;
	
	private Settings settings;
	
	private Timer timer;
	private int timerInterval;
	
	private MainPanel mainPanel;
	
	
	public SnakeGUI() {
		super("Snake");
		thisRef = this;
		
		this.rows = NUMBER_OF_ROWS;
		this.columns = NUMBER_OF_COLUMNS;
		
		this.timerInterval = TIMER_INTERVALS[timerIntervalIndex];
		
		arrowKeyHandler = new ArrowKeyHandler();
		addKeyListener(arrowKeyHandler);
		
		mainPanel = new MainPanel();
		settings = new Settings();
		outerPanel = new JPanel();
		outerPanel.add(mainPanel);
		add(outerPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(new Dimension(200, 150));
//		setResizable(false);
		setVisible(true);
		toFront();
	}
	
	private class Settings extends JPanel {
		private JLabel speedLabel;
		private JSlider speedSlider;
		private JButton confirmButton;
		
		public Settings() {
			super(new BorderLayout());
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
					outerPanel.removeAll();
					outerPanel.add(mainPanel);
					thisRef.setSize(new Dimension(200, 150));
					thisRef.repaint();
				}
			});
			this.add(speedLabel, BorderLayout.NORTH);
			this.add(speedSlider, BorderLayout.CENTER);
			JPanel jp = new JPanel();
			jp.add(confirmButton);
			this.add(jp, BorderLayout.SOUTH);
		}
		
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
//					System.out.println("Painted a square (x: " + x * Square.SIZE.width + ", y: " + y * Square.SIZE.height + ", of size " + Square.SIZE.width);
				}
			}
		}
	}
	
	private void newGame() {
		board = new Board();
		outerPanel.removeAll();
		outerPanel.add(board);
		outerPanel.setSize(columns * Square.SIZE.width, rows * Square.SIZE.height);
		setLocation(500, 200);
		pack();
		repaint();
		timer = new Timer(timerInterval, new TimerHandler());
		timer.start();
		currentDirection = EAST;
		toFront();
		requestFocus();
	}
	
	private void settings() {
		outerPanel.removeAll();
		outerPanel.add(settings);
		pack();
		repaint();
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
			settingsButton = new JButton("Settings");
			settingsButton.addActionListener(this);
			settingsButton.setToolTipText("Change settings such as level and speed");
			quitButton = new JButton("Quit");
			quitButton.addActionListener(this);
			add(newGameButton, BorderLayout.NORTH);
			add(settingsButton, BorderLayout.CENTER);
			add(quitButton, BorderLayout.SOUTH);
//			setMaximumSize(new Dimension((newGameButton.getWidth()+5)*3, (newGameButton.getHeight()+5)*3));
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			if(ae.getSource() == newGameButton)
				newGame();
			else if(ae.getSource() == settingsButton)
				settings();
			else if(ae.getSource() == quitButton)
				System.exit(0);
		}

	}
	
	private class TimerHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int nextX = snake.getFirst().getX();
			int nextY = snake.getFirst().getY();
//			System.out.println("The snake's head is now at (x: " + nextX + ", y: " + nextY + ")");
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
//			case Square.SNAKE:
//			case Square.WALL:
			case Square.EMPTY:
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
				timer.stop();
				Square food = squares[nextY][nextX];
				squares[nextY][nextX] = new Square(Square.SNAKE, nextX, nextY);
				snake.addFirst(squares[nextY][nextX]);
				
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
				Square head = snake.removeFirst();
				if(squares[nextY][nextX].equals(snake.getFirst())) {
					currentDirection = previousDirection;
					snake.addFirst(head);
					break;
				}
			default:
				// Game is lost.
				System.out.println("The game was lost");
//				removeKeyListener(arrowKeyHandler);
				timer.stop();
				outerPanel.removeAll();
				int o = JOptionPane.showConfirmDialog(null, "Go to the main menu?", "You lost!", JOptionPane.OK_CANCEL_OPTION);
				if(o == JOptionPane.OK_OPTION) {
					outerPanel.add(mainPanel);
					thisRef.setSize(new Dimension(200, 150));
					thisRef.repaint();
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
			case KeyEvent.VK_LEFT:
				if(northOrSouth) {
					currentDirection = WEST;
					System.out.println("The snake is now heading west.");
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(northOrSouth) {
					currentDirection = EAST;
					System.out.println("The snake is now heading east.");
				}
				break;
			case KeyEvent.VK_UP:
				if(!northOrSouth) {
					currentDirection = NORTH;
					System.out.println("The snake is now heading north.");
				}
				break;
			case KeyEvent.VK_DOWN:
				if(!northOrSouth) {
					currentDirection = SOUTH;
					System.out.println("The snake is now heading south.");
				}
				break;
			default:
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		SnakeGUI snakeGui = new SnakeGUI();
	}

}
