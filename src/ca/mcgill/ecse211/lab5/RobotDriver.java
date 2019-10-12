package ca.mcgill.ecse211.lab5;

import java.util.Arrays;

import ca.mcgill.ecse211.lab5.Navigation.*;
import ca.mcgill.ecse211.lab5.Resources;


public class RobotDriver {
	
	/**
	 * assuming the robot starts from the middle of the 0,0 square, this moves the robot
	 * to a square where is is 4 tiles away, adjusts its angle and gets ready to shoot
	 * @param x x coordinate of target square
	 * @param y y coordinate of target square
	 */
	public void getReadyToShoot(int x, int y) {
		(new Thread () {
			public void run() {
				int target [] = findTarget(x,y);
				int[][]moveList =  listOfMoves (target);
				for (int[] move : moveList) { 
				//YP: this is subject to change because it does not account for negative moves, 
				//since we always start from 0,0, so subject to change...
					Navigation.moveForwardByTile(move[1]);
					Navigation.turnRight();
					Navigation.moveForwardByTile(move[0]);
					Navigation.turnLeft();
				}
			}
		}).start();
	}
	
	/**
	 * returns a move in a zig-zag pattern to maximize correction and minimize error accumulated each step
	 * @param target
	 * @return
	 */
	private static int [][] listOfMoves(int[] target){
		int [][] result;
		boolean xBigger;
		if (target[0] == 0 || target [1] == 0) {
			result = new int [1][2];
			result[0] = target;
		}
		else {
			int stepSize;
			xBigger = target [0] > target[1];
			result = new int [(xBigger? target[1]:target[0])][2];
			if (xBigger) { //per y step, x will move by stepSize
				stepSize = target[0] / target[1];
				int i = 0,j = 0;
				while (j < target[1]-1) {
					result[j] = new int [] {stepSize,1};
					i+= stepSize;
					j++;
				}
				result[j] = new int [] {target[0] - i,1};
				return result;
			}
			else { //per x step, y will move by stepSize
				stepSize = target[1] / target[0];
				int i = 0,j = 0;
				while (j < target[0]-1) {
					result[j] = new int [] {1,stepSize};
					i+= stepSize;
					j++;
				}
				result[j] = new int [] {1,target[1] - i};
				return result;
			}
		}
		return result;
	}
	
	/**
	 * returns the nearest available square to shoot from which is N square away form the target
	 * @param targetX x coordinates of the target square, starting from 0
	 * @param targetY y coordinates of the target square, starting from 0
	 * @return the target square coordinates in an int array of size [2]
	 */
	private static int [] findTarget(int targetX, int targetY){
		
		int [] result = new int [2];
		double shortest_dist = 100;
		int [][] notableSquares = {{-3,3},{0,4},{3,3},{-4,0},{4,0},{-3,-3},{0,-4},{3,-3}};
		for (int [] pair:notableSquares) {
			boolean ooX = pair[0]+targetX > Resources.ARENA_X || pair[0]+targetX < 0;
			boolean ooY = pair[1]+targetY > Resources.ARENA_Y || pair[1]+targetY < 0;
			if (ooX || ooY) {
				continue;
			}
			else {
				double dist  = Math.sqrt((pair[0]+targetX)*(pair[0]+targetX)+(pair[1]+targetY)*(pair[1]+targetY));
				if (dist < shortest_dist) {
					result [0] = pair[0] + targetX;
					result [1] = pair[1] + targetY;
					shortest_dist = dist;
				}
			}
		}
		System.out.println("target is : "+Arrays.toString(result));
		return result;
	}
	
}
