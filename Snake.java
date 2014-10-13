import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Snake{
    JLabel label,label1,label2;
    static String starFile = "images/galaxy.jpg";
    int score=0,level=1,hiScore=0;
	static JFrame frame;
	static GameArea area;
    	
    private void buildUI(Container container, ImageIcon image) {
        container.setLayout(null);
                                          
        area = new GameArea(image, this);
		area.setBounds(50,50,400,400);
        container.add(area);

        label = new JLabel("<html><h4 align=center><font color=red>SCORE<br></font><font color=black>"+score+"</font></h4></html>");
	  	label.setHorizontalAlignment(JLabel.CENTER);
		label.setBounds(500,50,70,45);
		label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setLabelFor(area);
        container.add(label);
		
		label1 = new JLabel("<html><h4 align=center><font color=red>HI SCORE<br></font><font color=black>"+hiScore+"</font></h4></html>");
	  	label1.setHorizontalAlignment(JLabel.CENTER);
		label1.setBounds(500,120,70,45);
		label1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        container.add(label1);
		
		label2 = new JLabel("<html><h4 align=center><font color=red>SPEED<br></font><font color=black>"+level+"</font></h4></html>");
	  	label2.setHorizontalAlignment(JLabel.CENTER);
		label2.setBounds(500,190,70,45);
		label2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        container.add(label2);		
		
		JLabel instructions = new JLabel("<html>Use the arrow keys to navigate.<br>F2 starts a new game.<br> Space toggles start/pause.<br> Use PgUp and PgDown to select a background.<br>'a' increases speed, 'z' decreases it</html>");
		instructions.setBounds(500,200,200,200);
		container.add(instructions);
		
   }
	
	public void resetScore(){
		score=0;
	}
	
	public void incrementScore(){
		score+=level*10;
		if(score>hiScore){
			hiScore=score;
			displayHiScore();
		}
		displayScore();
	}
	
	public void displayScore(){
		label.setText("<html><div align=center><font color=red>SCORE<br></font><font color=black>"+score+"</font></div></html>");
	}
	
	public void displayHiScore(){
		label1.setText("<html><div align=center><font color=red>HI SCORE<br></font><font color=black>"+hiScore+"</font></div></html>");
	}
	
	
	public int getLevel(){
		return level;
	}
	
	public void incrementLevel(){
		if(level<15)
		{
			level++;
			displaySpeed();
		}
	}
	
	public void decrementLevel(){
		if(level>1)
		{
			level--;
			displaySpeed();
		}
	}
	
	public void displaySpeed(){
		label2.setText("<html><h4 align=center><font color=red>SPEED<br></font><font color=black>"+level+"</font></h4></html>");
	}
	
	protected static ImageIcon createImageIcon(String path) {
    	java.net.URL imgURL = Snake.class.getResource(path);
    	if (imgURL != null) {
        	return new ImageIcon(imgURL);
    	} else {
    		System.err.println("Couldn't find file: " + path);
    		return null;
		}
	}
	
	private static void createAndShowGUI() {
		//Create and set up the window.
		frame = new JFrame("Snake");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Set up the content pane.
		Snake controller = new Snake();
		controller.buildUI(frame.getContentPane(),createImageIcon(starFile));

        //Display the window.
		frame.pack();
		
		frame.setVisible(true);
    }
	
	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(); 
			}
		});
	}
	

	
			
	
	private class GameArea extends JLabel {
		Snake controller;
		
		static final int NORTH=0,SOUTH=1,WEST=2,EAST=3; //values which direction can have
		
		int direction=EAST; //direction in which the snake moves
				
		int imageIndex=0;
		
		Point egg;
		int eggIndex;
		
		boolean justAte=false,paused=false;
		
		String[] imagePaths={"images/underwater.jpg","images/galaxy.jpg","images/galaxy2.jpg","images/blue_sky.jpg"};
		ImageIcon[] imageArray;
		
		Thread myThread;
		
		Point[][] pointArray = new Point[40][40];
		
		EmptyPoints emptyPoints;
		SnakePoints snakePoints;
		
		public void setImagePaths()
		{
			imageArray=new ImageIcon[imagePaths.length];
			for(int i=0;i<imageArray.length;i++)
			{
				imageArray[i]=createImageIcon(imagePaths[i]);
			}
		}
		
		public void pauseGame(){
			myThread.interrupt();
		}
		
		public void startGame(){
			myThread.interrupt();
		}
		
		public void newGame(){
			emptyPoints.initialise();
			snakePoints.initialise();
			
			setEgg();
			
			controller.resetScore();
			controller.displayScore();
			
			direction=EAST;
			if(paused)
				myThread.interrupt();			
		}
		
		public void setEgg()
		{
			eggIndex=(int)(Math.random()*emptyPoints.size());
			egg=(Point)emptyPoints.elementAt(eggIndex);
			justAte=false;
		}
				
		public void initialisePointArray()
		{	
			int j;
			for(int i=0;i<40;i++)
			{
				for(j=0;j<40;j++)
				{
					pointArray[i][j] = new Point(i*10+1,j*10+1);
				}
			}
		}
		
		public void setDirection(int dir){
			direction = dir;
		}
		
		public void resetSnake(){
			Point p = (Point)snakePoints.elementAt(0);
			
			int i=(p.x-1)/10;
			int j=(p.y-1)/10;
			
			
			if(direction==NORTH){
				j--;
			}
			if(direction==SOUTH){
				j++;
			}
			if(direction==WEST){
				i--;
			}
			if(direction==EAST){
				i++;
			}
			if(i>-1 && i<40 && j>-1 && j<40) //if the point is valid
			{
				
				if(snakePoints.contains(pointArray[i][j]))
				{
					endGame();
					return;
				}
				
				if(pointArray[i][j].x==egg.x && pointArray[i][j].y==egg.y){
					justAte=true;
				}
				
				if(!justAte)
				{
					snakePoints.pop();
				}
				else{
					incrementScore();
				}
				snakePoints.insert(pointArray[i][j]);
				p = (Point)snakePoints.elementAt(0);
			}
			else if(i==40 || i==-1 || j==40 || j==-1)
			{
				if(i==40)
				{
					i=0;
				}
				if(i==-1)
				{
					i=39;
				}
				if(j==40)
				{
					j=0;
				}
				if(j==-1)
				{
					j=39;
				}
				if(snakePoints.contains(pointArray[i][j]))
				{
					endGame();
					return;
				}
				if(pointArray[i][j].x==egg.x && pointArray[i][j].y==egg.y){
					justAte=true;
				}
				if(!justAte)
				{
					snakePoints.pop();
				}
				else{
					incrementScore();
				}
				snakePoints.insert(pointArray[i][j]);
			}
			else
			{
				endGame();
			}
		}
			
		public GameArea(ImageIcon image, Snake controller) {
		
            super(image); //This component displays an image.
            this.controller = controller;
			setImagePaths();
			setIcon(imageArray[0]);
            setOpaque(true);
			
            setMinimumSize(new Dimension(10,10)); 
			setFocusable(true);//to trap inputs from the keyboard
			
			initialisePointArray();
			
			emptyPoints = new EmptyPoints();
			snakePoints =  new SnakePoints(this);
			
			emptyPoints.initialise();
			snakePoints.initialise();
			
			setEgg();
				
            myThread = new Thread(new MyThread());
			myThread.start();
			myThread.interrupt();

			MyKeyListener keyListener = new MyKeyListener();

			addKeyListener(keyListener);
        }
		
		private class MyThread implements Runnable{
			
			public void run(){
				while(true)
				{
					try
					{
						Thread.sleep(200 - controller.getLevel()*10);
						//System.out.println(controller.getLevel());
						paused=false;
						resetSnake();
						if(justAte)
						{
							setEgg();
							justAte=false;
						}
						
						repaint();
					}
					catch(InterruptedException ie)
					{
						try{
							paused=true;
							Thread.sleep(1000000000); //Wait!
						}
						catch(InterruptedException ie1)
						{
							;
						}
					}
				}
			
			}
		}
	
		private class SnakePoints extends Vector{
			GameArea gameArea;
		
			public SnakePoints(GameArea gameArea){
				super(0,1);
				this.gameArea=gameArea;
			}
			
			public void pop()
			{
				gameArea.emptyPoints.addElement(elementAt(size()-1));
				removeElementAt(size()-1);
			}
			
			public void initialise()
			{
				while(size()!=0)
				{
					pop();
				}
				for(int i=0;i<=10;i++)
				{
					insert(pointArray[i][20]);
				}
				setDirection(EAST);
			}
			
			public void insert(Object n)
			{
				insertElementAt(n,0);
				gameArea.emptyPoints.remove(n);
			}
		}
		
		private class EmptyPoints extends Vector{
		
			public EmptyPoints()
			{
				super(0,1);
			}
			
			public void initialise()
			{
				int j;
				for(int i=0;i<40;i++)
				{
					for(j=0;j<40;j++)
					{
						addElement(pointArray[i][j]);
					}
				}
			}
				
		}
		
		private class MyKeyListener extends KeyAdapter{
			
			public void keyTyped(KeyEvent e) {
        		;
    		}
				
    		public void keyPressed(KeyEvent e) {
        		if(e.getKeyCode()==34 && imageIndex<(imageArray.length - 1)) //Page down key changes the background
				{
					imageIndex++;
					setIcon(imageArray[imageIndex]);
				}
				if(e.getKeyCode()==33 && imageIndex>0) //Page Up key also changes the background!
				{
					imageIndex--;
					setIcon(imageArray[imageIndex]);
				}
				if(e.getKeyCode()==38)
				{
					if(direction!=NORTH && direction!=SOUTH && !paused)
						setDirection(NORTH);
				}
				if(e.getKeyCode()==40)
				{
					if(direction!=NORTH && direction!=SOUTH && !paused)
						setDirection(SOUTH);
				}
				if(e.getKeyCode()==37)
				{
					if(direction!=EAST && direction!=WEST && !paused)
						setDirection(WEST);
				}
				if(e.getKeyCode()==39)
				{
					if(direction!=EAST && direction!=WEST && !paused)	
						setDirection(EAST);
				}
				if(e.getKeyCode()==32) //space toggles between playing and paused states.
				{
					if(paused)
					{
						startGame();
					}
					else
					{
						pauseGame();
					}
				}
				
				if(e.getKeyCode()==65) //increase speed
				{
					controller.incrementLevel();
				}
				if(e.getKeyCode()==90) //decrease speed
				{
					controller.decrementLevel();
				}
				
				if(e.getKeyCode()==113)
				{
					newGame();
				}
				
    		}
		}
		
		protected void paintComponent(Graphics g) {
			Point p;
			super.paintComponent(g); //paints the background and image
    
            //If currentRect exists, paint a box on top.
			g.setColor(Color.GREEN);
			
			p=(Point)snakePoints.elementAt(0);
			g.fillRect(p.x,p.y,8,8);
			g.setColor(Color.RED);
			for(int i=1;i<snakePoints.size();i++)
			{
				p=(Point)snakePoints.elementAt(i);
				g.fillRect(p.x,p.y,8,8);
			}    
			
			g.setColor(Color.PINK);      
			g.fillOval(egg.x+1,egg.y+1,6,6);
        }
		
		public void endGame(){
			JOptionPane.showMessageDialog(controller.frame, "Game over");
			if(!paused)
				myThread.interrupt();
		}
	}
}
