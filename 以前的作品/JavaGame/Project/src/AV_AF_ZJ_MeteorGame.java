/**
 * Program Name: AV_AF_ZJ_MeteorGame.java
 * Purpose: Meteor game using meteor class to create objects.
 * 			This class is using images, sounds, keyboard and mouse detection
 * 			for the game to be playable.
 * Coder: Andres Villamarin, Zijian Zheng, Augustine Fayomi
 * Date: Aug 13, 2015
 */

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Date;

import javax.swing.*;

public class AV_AF_ZJ_MeteorGame extends JApplet implements Runnable
{
	// data members
	private static final long serialVersionUID = 1L;
	// screen size
	
	
	
	int screenWidth = 700;
	int screenHeight = 700;
	
	// array of number of meteors
	final int ARRAY_SIZE = 20;
	// meteor object
	Meteor meteor;
	
	// initial point
	private int currentX = 700; //mouse cursor co-ordinates
	private int currentY = 700; 
	
	// base coordinates for laser
	private final int BASE_X = 350; //originating point of the line
	private final int BASE_Y = 700;
	
	// string labels for score, time, win, and lose
	String scoreS;
	String timerS;
	
	// int values for score, time, win, and lose
	int y,z,score,nuke;
	int x = 100;
	
	// date objects
	long startTime;
	long playingTime;
	long finishTime;
	double elapsedTime;
	Boolean nukea = false;
	// image objects
	private Image im;
	private Image explosion;
	private Image winnerImg;
	private Image nukeImg;
	
	boolean nukeLaunched = false;
	//create an Image object to hold the off-screen image
	private Image bufferImage;
	
	//need a second 'paint brush' to draw in the buffer
	private Graphics bufferBrush;	
	
	//Create an object of the Class class as a 'meta-object'
	Class metaObject = this.getClass();
	
	// font description
	Font font = new Font ("Bauhaus 93", Font.PLAIN, 30);
	Font scoreFont = new Font ("Bauhaus 93", Font.PLAIN, 24);
	
	//Create a URL object that will store the location of the sound file
	
	//if the sound file is in your source folder, this will work
	URL url1 = metaObject.getResource("shoot.wav");
	URL url2 = metaObject.getResource("background.wav");
	URL url3 = metaObject.getResource("end.wav");
	URL url4 = metaObject.getResource("winner.wav");
	URL url5 = metaObject.getResource("nuke.wav");
	
	//Create an AudioClip object
	AudioClip laserSound = Applet.newAudioClip(url1);	
	AudioClip backgroundSound = Applet.newAudioClip(url2);	
	AudioClip endSound = Applet.newAudioClip(url3);	
	AudioClip winSound = Applet.newAudioClip(url4);
	AudioClip nukeSound = Applet.newAudioClip(url5);
	
	// array to hold objects been created
	ArrayList<Meteor> meteorArray = new ArrayList();
	
	// declare a Thread object to act as our "pick-up" truck to do the calculations of 
	// the ball positions on screen. 
	Thread calcThread;
	
	//control flags for respective use
	Boolean isButtonpressed = false;
	boolean isRunning = true;
	boolean isOver = false;
	boolean isWin = false;
	boolean isButtonPressed = false;
	boolean isSpace = false;
	boolean isNuke = false;
	
	Date timer, curTime;
	long flashTimer, curTimer;
	
	public void init() 
	{
		Date start = new Date();
		startTime = start.getTime();
		
		this.addMouseListener(new MyMouseListener());
		this.addMouseMotionListener(new MyMouseMotionListener());
		this.addKeyListener(new MyKeyListener());
		// needed for space bar detection
		this.setFocusable(true);
		setBackground(Color.BLACK);
		
		bufferImage = this.createImage(screenWidth, screenHeight);
		bufferBrush = bufferImage.getGraphics();		
		
		// load images
	    im = this.getImage(getDocumentBase(), "background2.jpg");
	    explosion = this.getImage(getDocumentBase(), "explosion.png");
	    winnerImg = this.getImage(getDocumentBase(), "win.png");
	    nukeImg = this.getImage(getDocumentBase(), "nuke.jpg");

		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon("target.png").getImage(),
				new Point(15,15),"custom cursor"));
		
		//create the new Thread object and assign the applet to it.
		this.calcThread = new Thread(this);
		
		//Start the thread running
		this.calcThread.start();
		backgroundSound.play();
		
	}
	
	public void stop()
	{
		//if this were running on a web page, and user moved to another page, 
		//the browser would call this method to stop the thread.
		isRunning = false;
		calcThread = null;
	}
	
	
	public void paint(Graphics g)
	{
		// check if player didn't lose yet
		if(!isOver)
		{
			g.setFont (scoreFont);
			bufferBrush.setFont(scoreFont);
			//JHAVE THE BUFFERbRUSH CLEAR	clear its rectangle for its next paint job
			bufferBrush.clearRect(0, 0, screenWidth, screenHeight);
			this.setSize(screenWidth, screenHeight);
		
			bufferBrush.drawImage(im, -20, 0, 1000, 700, this);
			bufferBrush.setColor(Color.RED);
			bufferBrush.drawLine(BASE_X, BASE_Y, currentX, currentY);
			// display score
			scoreS = "Score = " + score;
			bufferBrush.drawString(scoreS, 20,80);
			bufferBrush.setColor(Color.RED);
			
			
			Date playing = new Date();
			playingTime = playing.getTime();
			elapsedTime = (playingTime - startTime)/ 1000.0;//converting to seconds
			
			timerS = "time = " + elapsedTime;
			bufferBrush.drawString(timerS, 20,120);
			bufferBrush.setColor(Color.BLUE);
			
			
			//paint the ball offscreen
			for(int i = 0; i < meteorArray.size();i++)
			{
				// set interpolation color for the ball
				bufferBrush.setColor(new Color(1.0f - ((float)meteorArray.get(i).hardness / (float)meteorArray.get(i).H), 0.0f, ((float)meteorArray.get(i).hardness/(float)meteorArray.get(i).H)));
				bufferBrush.fillOval((int)meteorArray.get(i).xCoord,(int)meteorArray.get(i).yCoord,(int)meteorArray.get(i).radius, (int)meteorArray.get(i).radius);
				
			}	
						
			if(nukeLaunched && nuke >= 0){       
	            curTime = new Date();
	            curTimer = curTime.getTime();
	            nukeSound.play();
				meteorArray.clear();
				
	            if((int)(curTimer - flashTimer / 100) % 2 == 0) {
	                bufferBrush.setColor(Color.WHITE);
	                bufferBrush.fillRect(0, 0, screenWidth, screenHeight);
	            }
	            else {
	                bufferBrush.setColor(Color.RED);
	                bufferBrush.fillRect(0, 0, screenWidth, screenHeight);
	            }
	           
	            if(curTimer - flashTimer >= 500) {
	                nukeLaunched = false;
	            }
	        }
			
			// logic for nukes been display and use
			
			
			
			if(nuke > 0) {
				for(int i = 1; i <= nuke; ++i){
//					
					int nukeP = 5;
					// draw nuke image
					bufferBrush.drawImage(nukeImg, (nukeP)+i*30, 12, 10, 30, this);
					
				}
			}
			
			// if the user win(50pts) display next:
			if(isWin){
				backgroundSound.stop();
				laserSound.stop();
				meteorArray.clear();
				bufferBrush.clearRect(0, 0, screenWidth, screenHeight);
				bufferBrush.setColor(Color.YELLOW);	
				bufferBrush.fillRect(0, 0, screenWidth, screenHeight);
				bufferBrush.setColor(Color.RED);
				bufferBrush.setFont(font);
				bufferBrush.setFont(scoreFont);
				bufferBrush.drawImage(winnerImg, 40, 30, 500, 500, this);
				bufferBrush.drawString(scoreS, 500,360);
				bufferBrush.drawString("YOU WIN!", 507, 320);
				bufferBrush.drawString("Andres", 60, 620);
				bufferBrush.drawString("Augustine", 270, 620);
				bufferBrush.drawString("Zijian", 520, 620);
				
			}
			g.drawImage(bufferImage, 0, 0, this);
		}
		// if player lost display:
		else
		{
			Date stop = new Date();
			finishTime = stop.getTime();
			elapsedTime = (finishTime - startTime)/ 1000.0;//converting to seconds
			timerS = "time = " + elapsedTime;
			bufferBrush.drawString(timerS, 20,120);
			bufferBrush.setColor(Color.RED);
			meteorArray.clear();
			bufferBrush.clearRect(0, 0, screenWidth, screenHeight);
			bufferBrush.setColor(Color.BLACK);
			bufferBrush.fillRect(0, 0, screenWidth, screenHeight);
			bufferBrush.setColor(Color.RED);
			bufferBrush.setFont(font);
			bufferBrush.drawString("Extinction Level Incident has occurred…BYE-BYE!", 20,320);
			bufferBrush.setFont(scoreFont);
			bufferBrush.drawString(scoreS, 300,260);
			g.drawImage(bufferImage, 0, 0, this);
		}
		repaint();
	}
	
	/*
	 * Method NAME: update()
	 * Purpose: over rides the update method in super class so that we don't get the
	 *          bucket of paint being thrown onto the applet screen.
	 */
	public void update(Graphics g)
	{
		//just call paint(g) directly, skip the paint throwing
		paint(g);
	}
	
	public void run()
    {
		while(isRunning)
		{
			// display meteor with a 1% chance
			int chance = (int) Math.ceil(Math.random()*x);
			if(chance == 1){
				meteorArray.add(new Meteor());
			}
			//update the position of all the meteors
			for(int i = 0; i <meteorArray.size();i++){
				meteorArray.get(i).updatePos();
				//check to see if the player has failed
				// check if meteor hit bottom line, if so game over!
                if(meteorArray.get(i).yCoord >= screenHeight)
                {
                	endSound.play();
                	isRunning = false;
                    isOver = true;
                }
			}
				
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
				System.out.println(ex.getMessage());
			}
		}
    }

		//INNER CLASSES HERE for mouse listener
		private class MyMouseListener implements MouseListener
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				// TODO Auto-generated method stub
				//System.out.println("mouseClicked");
				//repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				//play laser sound
				laserSound.play();
				// update coordinates for laser
				currentX = e.getX();
				currentY = e.getY();
				bufferBrush.setColor(Color.RED);
				isButtonPressed = true;
				
				//update main screen
				for(int i = 0; i < meteorArray.size(); i++)
				{
					
					if(meteorArray.get(i).getTarget(currentX, currentY))
					{
						// check if 10% twin method true
						if(meteorArray.get(i).isTwin())
						{
							//split meteor to specified coordinates based on mom meteor
							meteorArray.add(new Meteor(meteorArray.get(i), (int)meteorArray.get(i).xCoord - (int)meteorArray.get(i).getRadius()));
							meteorArray.add(new Meteor(meteorArray.get(i), (int)meteorArray.get(i).xCoord + (int)meteorArray.get(i).getRadius()));

							meteorArray.remove(i);
						}	
						else if(meteorArray.get(i).scoredHit() == 0) // if hardness 0 destroy meteor
						{
							meteorArray.remove(i);
							countScore();
						}
					}
				}
				repaint();
				e.consume();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				isButtonpressed = false;
				setBackground( Color.black );
				repaint();
			}
			
			/*
			 * Method NAME: countScore()
			 * Purpose: returns score of player
			 */
			private void countScore() {
				score++;
				
				if(score == 10){
					//increase speed rate
					//string difficulty medium
					nukea = true;	
					nuke++;
				}
				if(score == 20)
				{
					//diff medium
					x = 90;
					nukea =true;
					nuke++;
				}
				if(score == 30)
				{
					//diff medium
				x = 80;
					nukea =true;
					nuke++;
				}
				if(score == 40)
				{
					//diff High
				x = 50;
					nukea =true;
					nuke++;
				}
				if(score == 50){ // if 50 pts win game!
					//winner
					isWin = true;
					nukea = true;
					winSound.play();
				}
					
				repaint();
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				isButtonPressed = true;
				repaint();
				e.consume();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				isButtonPressed = false;
				bufferBrush.setColor(Color.BLACK);
				repaint();
				e.consume();
				
			}

		}//end inner class MyMouseListener
		
		//INNER CLASSES HERE for mouse motion Listener
		private class MyMouseMotionListener implements MouseMotionListener
		{

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				currentX = e.getX();
				currentY = e.getY();
				repaint();
				
			}
			
			
		}
		
		//INNER CLASSES HERE for keyboard (spacebar) listener
		private class MyKeyListener implements KeyListener
		{
			@Override
			public void keyPressed(KeyEvent ke) {
				
					
					timer = new Date();
		            flashTimer = timer.getTime();
		            nukeLaunched = true;
		            nuke--;
					repaint();
				}
				
			

			@Override
			public void keyReleased(KeyEvent ke) {
			}

			@Override
			public void keyTyped(KeyEvent ke) {
				
			}
			
		}
}

