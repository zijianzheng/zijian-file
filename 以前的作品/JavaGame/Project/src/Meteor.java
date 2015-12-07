/**
 * Program Name: Meteor.java
 * Purpose: Meteor class defines meteor characteristics.
 * 			objects of this class will be used for game to display meteor
 * 			speed, coordinates and distance function between mouse and meteor.
 * Coder: Andres Villamarin, Zijian Zheng, Augustine Fayomi
 * Date: August 10, 2015
 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;


public class Meteor {
	
	//instance variables here…
	private static ArrayList<Meteor> meteorsArrayList = new ArrayList<Meteor>();
	double xCoord;
	double yCoord;
	double radius;
	double xVelocity;
	double yVelocity;
	int hardness;
	float easySpped = 0.02f;
	public float H;
	boolean twinFlag;
	

	// Constructor, create a new Meteor located at the top of the screen
	public Meteor()
	{
		Random r = new Random();
		this.yCoord = 0.0;
		this.xCoord = r.nextDouble()*600.0 + 50.0;
		this.yVelocity = r.nextDouble()*easySpped + 1.0;
		this.radius = r.nextDouble()*30.0 + 20;
		this.hardness = r.nextInt(4) + 1;
		int chance = r.nextInt(99) + 1;
		this.H = hardness;
		if (chance <= 10)
			this.twinFlag = true;
		
		
	}
			
	
	// Constructor, given another Meteor as its parent object, create a new Meteorlet
	// based on the parent Meteor. 
	public Meteor(Meteor mom, double deltaX)
	{
		this.yCoord = mom.yCoord;
		this.xCoord = deltaX;
		this.yVelocity = mom.yVelocity;
		this.radius = mom.radius;
		this.hardness = mom.hardness;
		this.twinFlag = false;
		this.H = hardness;
		
	}	
	
	// Is this Meteor a twin Meteor?
	public boolean isTwin()
	{
		if (this.twinFlag){
			return true;
		}
		return false;
	}

	public int scoredHit()
	{
		this.hardness--;
		return this.hardness;
	}
	// Getter method for the radius
	

	
	/*
	 * Method Name: updatePos()
	 * Purpose: calculates position of meteor to be drawn.
	 * Accepts: a Meteor object
	 * Returns: NOTHING! Void method.
	 */
	
	public void updatePos()
	{
		//determine flag variables status
		this.yCoord +=yVelocity;
			
	}
	

	// Getter for x-position
	public double getxCoord() {
		return xCoord;
	}

	// Getter for y-position
	public double getyCoord() {
		return yCoord;
	}

	// Getter for radius
	public double getRadius() {
		return radius;
	}
	
	/*
	 * Method Name: getTarget()
	 * Purpose: calculates distance of meteor position and cursor coordinates
	 * Accepts: mouse coordinates current location
	 * Returns: boolean flag is meteor was hit
	 * NOTE: using distance formula from 
	 */
	public boolean getTarget(int mouseX, int mouseY){
		
		int distance = (int) Math.sqrt((this.xCoord-mouseX)*(this.xCoord-mouseX) + (this.yCoord-mouseY)*(this.yCoord-mouseY));
		if(distance <= this.radius){
			return true;
		}
		return false;
	}
}