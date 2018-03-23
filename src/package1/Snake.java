package package1;

import java.util.List;

public class Snake {
	
	private List<Square> snake;
	
	public void insertHead(Square head) {
		snake.add(head);
	}

}
