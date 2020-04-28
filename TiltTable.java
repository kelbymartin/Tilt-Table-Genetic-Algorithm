package v1;

import java.util.*;

public class TiltTable {

	static Random r = new Random();
	
	public static void main(String[] args) {
		
		//Welcome and instructions/description messages
		System.out.println("Welcome to my Balance Board Genetic Algorithm.");
		System.out.println();
		System.out.println("You will be asked to enter n which will be the height/width of the board.");
		System.out.println();
		System.out.println("The program will then automatically fill the board with weights from 1 to n");
		System.out.println("until the board is:");
		System.out.println("	a) In a balanced state");
		System.out.println("	b) Too many generations have passed (current max: 100)");
		System.out.println("Weights will persist from one generation to the next if:");
		System.out.println("	a) Weights 1,2,3,4 are placed in one of the four corners");
		System.out.println("	b) One of the n largest weights is near the center");
		System.out.println();
		System.out.println("Good luck!!!");
		System.out.println();
	
		
		Scanner in = new Scanner(System.in);

		//weight values will be transferred back and forth between generations
		//so only one of each weight can be on the table
		ArrayList<Integer> weights1 = new ArrayList<Integer>();
		ArrayList<Integer> weights2 = new ArrayList<Integer>();
		
		int[][] board;
		int n, nsq;
		
		
		//Setup
		
		System.out.println("Enter n: ");
		n = in.nextInt();
		nsq = n*n;
		
		board = new int[n][n];

		//fill weights array with 1 to n weights
		for(int i = 1; i<nsq+1; i++) {
			weights1.add(i);
		}
		

		//Generations start
		
		int generation = 1;
		boolean notBalanced = true;
		
		while(generation <= 100 && notBalanced) {
			
			//swap active weight arraylist
			if(generation % 2 != 0) {
				board = simGen(n, board, weights1, weights2);
			}
			else {
				board = simGen(n, board, weights2, weights1);
	    	}
			
			
			//Print board
			System.out.println();
			System.out.println("GENERATION #" + generation);
			System.out.println("-------------");
			System.out.println(Arrays.deepToString(board).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
			
			
			//Check balance
			if(checkBalance(n, board)) {
				notBalanced = false;
			}
			else if(n == 4) {
				//i have discovered that a 4x4 board is too small to benefit from my genetics
				resetBoard(n, board);
			}
			else{
				//check if any placed weights are fit to carry over
				board = checkCarryOver(n, board);
			}
			
			++generation;
			
		}//while (generation loops)
		

		System.out.println();
		System.out.println();
		if(notBalanced) {
			System.out.printf("%d-by-%d Board was not successfully balanced in %d Generation(s).", n, n, generation-1);
			System.out.println();
			System.out.printf("Maybe try again.");
		}
		else {
			System.out.printf("%d-by-%d Board was successfully balanced in %d Generation(s)!!!", n, n, generation-1);
		}
		System.out.println();
		System.out.println();
		
		System.out.println("Type exit to exit.");
		in.next();
		
		in.close();
		
	}//main
	
	
	/**
	 * Checks the board to see if it is balanced
	 * @param n - int n*n board
	 * @param board - int[][] - current generation board
	 * @return boolean - true if board is balanced, false if unbalanced
	 */
	public static boolean checkBalance(int n, int[][] board) {
		boolean check = false;
		
		double distance = 0;
		double topMoments = 0;
		double bottomMoments = 0;
		double leftMoments = 0;
		double rightMoments = 0;
		double topWeight = 0;
		double bottomWeight = 0;
		double leftWeight = 0;
		double rightWeight = 0;
		double verticalOffset = 0;
		double horizontalOffset = 0;
		double breakingPoint = 0;
		double breakingPointNegative = 0;
		
		//iterate through board
		for(int i = 0; i<n; ++i) {
			   for(int j = 0; j<n; ++j) {
				   
				   //distance needs to be calculated differently for an even n
				   if(n%2==0) {
					 //average distance formulas from each 4 center squares
					   distance = ((Math.sqrt((j - n/2) * (j - n/2) + (i - n/2) * (i - n/2))) + (Math.sqrt((j - (n/2-1)) * (j - (n/2-1)) + (i - n/2) * (i - n/2))) + (Math.sqrt((j - (n/2-1)) * (j - (n/2-1)) + (i - (n/2-1)) * (i - (n/2-1)))) + (Math.sqrt((j - n/2) * (j - n/2) + (i - (n/2-1)) * (i - (n/2-1))))) / 4;
				   }
				   else {
					 //distance formula
					   distance = Math.sqrt((j - n/2) * (j - n/2) + (i - n/2) * (i - n/2));
				   }
				   
				   //BreakingPoint is anything beyond the distance to the inner ring which can be found at i=n/2-1 j=n/2-1
				   if(i==(n/2-1) && j==(n/2-1)) {
					   breakingPoint = distance;
					   breakingPointNegative = distance * -1;
				   }
				   
				   //quadrants of the grid
				   if(i>n/2) {
					   distance = distance * -1;
				   }
				   if(j<n/2) {
					   distance = distance * -1;
				   }
				   
				   //Sum up top,bot,left,right moments
				   if(j<n/2) {
					   leftMoments = leftMoments + (distance * board[i][j]);
					   leftWeight = leftWeight + board[i][j];
				   }
				   if(j>n/2) {
					   rightMoments = rightMoments + (distance * board[i][j]);
					   rightWeight = rightWeight + board[i][j];
				   }
				   if(i<n/2) {
					   topMoments = topMoments + (distance * board[i][j]);
					   topWeight = topWeight + board[i][j];
				   }
				   if(i>n/2) {
					   bottomMoments = bottomMoments + (distance * board[i][j]);
					   bottomWeight = bottomWeight + board[i][j];
				   }
			   }//for j
		}//for i
		
		
		//horiOff will be + if to the right, - if to the left
		horizontalOffset = (rightMoments / rightWeight) - (leftMoments / leftWeight);
		//vertOff will be + if to the top, - if to the bottom
		verticalOffset = (topMoments / topWeight) - (bottomMoments / bottomWeight);
		
		System.out.println();
		System.out.println("Breaking Point: " + breakingPoint);
		System.out.println("Vertical, Horizontal:  " + verticalOffset + ", " + horizontalOffset);
		
		//if offsets are less than breaking point (Greater than negative breaking point) the table is balanced
		if(horizontalOffset <= breakingPoint && horizontalOffset >= breakingPointNegative) {
			if(verticalOffset <= breakingPoint && verticalOffset >= breakingPointNegative) {
				check = true;
			}
		}
		
		return check;
	}//method
	
	/**
	 * Determines what weights are fit to carry over to next generation
	 * @param n - int - n*n board
	 * @param board - int[][] - previous generation board
	 * @return - int[][] - board with fit weights to carry over
	 */
	public static int[][] checkCarryOver(int n, int[][] board){
		
		for(int i = 0; i<n; ++i) {
			   for(int j = 0; j<n; ++j) {
				   
				   //if weights in the center are large, keep them there for next generation(large will be classified as n*n-n, or the n largest weights)
				   if(n%2==0) {
					   if(i==n/2 && j==n/2) {
						   if(board[i][j] <= (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					   else if(i==n/2-1 && j==n/2) {
						   if(board[i][j] <= (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					   else if(i==n/2 && j==n/2-1) {
						   if(board[i][j] <= (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					   else if(i==n/2-1 && j==n/2-1) {
						   if(board[i][j] <= (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					 //if corners are 1-4, keep
					   else if(i == 0 && j == 0 || i == 0 && j == n-1 || i == n-1 && j == 0 || i == n-1 && j == n-1) {
						   if(board[i][j] > 4) {
							   board[i][j] = 0;
						   }
					   }
					   else {
						   board[i][j] = 0;
					   }
				   }//if
				   else {
					 //if weights in the center are large, keep them there for next generation(large will be classified as n*n-n, or the n largest weights)
					   if(i==n/2 && j==n/2) {
						   if(board[i][j] < (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					   else if(i==n/2-1 && j==n/2) {
						   if(board[i][j] < (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					   else if(i==n/2 && j==n/2-1) {
						   if(board[i][j] < (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					   else if(i==n/2-1 && j==n/2-1) {
						   if(board[i][j] < (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					   else if(i==n/2+1 && j==n/2) {
						   if(board[i][j] < (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					   else if(i==n/2 && j==n/2+1) {
						   if(board[i][j] < (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					   else if(i==n/2+1 && j==n/2+1) {
						   if(board[i][j] < (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					   else if(i==n/2-1 && j==n/2+1) {
						   if(board[i][j] < (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					   else if(i==n/2+1 && j==n/2-1) {
						   if(board[i][j] < (n*n-n)) {
							   board[i][j] = 0;
						   }
					   }
					 //if outer edges are a low weight, keep that for next generation
					   else if(i == 0 && j == 0 || i == 0 && j == n-1 || i == n-1 && j == 0 || i == n-1 && j == n-1) {
						   if(board[i][j] > n) {
							   board[i][j] = 0;
						   }
					   }
					   else {
						   board[i][j] = 0;
					   }
					   
				   }//else
			   }//for
		}//for
		
		return board;
	}//method
	
	/**
	 * Simulates a generation of the balance board. Removes a random weight from the active arraylist, puts it onto the tilt
	 * table, and places it into the copy of weights for the next generation to use
	 * @param n - int - n*n board
	 * @param board - int[][] - previous generation board
	 * @param weights - arraylist - active list of weights
	 * @param weightsCopy - arraylist - backup list of weights
	 * @return - int[][] - next generation board
	 */
	public static int[][] simGen(int n, int[][] board, ArrayList<Integer> weights, ArrayList<Integer> weightsCopy){
		
		int index = 0;
		
		int size = 1;
		 //Find and remove weights carried over from previous generation
		for(int i = 0; i<n; ++i) {
			   for(int j = 0; j<n; ++j) {
				   if(board[i][j] != 0) {
					   size = weights.size();
					   index = weights.indexOf(board[i][j]);
					   weightsCopy.add(weights.remove(index));
				   }
			   }
		}//for
		   
		//fill next generation
		for(int i = 0; i<n; ++i) {
			   for(int j = 0; j<n; ++j) {
				   if(weights.size() > 0 && board[i][j] == 0) {
					   size = weights.size();
					   index = r.nextInt(size);
					   board[i][j] = weights.get(index);
					   weightsCopy.add(weights.remove(index));
				   }
			   }
		}//for
		
		return board;
	}//method
	
	public static int[][] resetBoard(int n, int[][] board){
		for(int i = 0; i<n; ++i) {
			   for(int j = 0; j<n; ++j) {
				   board[i][j] = 0;
			   }
		}
		return board;
	}
	
	/**
	 * returns a perfect balanced board with n = 4 for test
	 * @return - int[][] - next generation board
	 */
	public static int[][] createPerfectBoard(){
		int[][] perf = {{ 2, 5, 10, 3}, 
						{ 11, 16, 13, 8}, 
						{ 7, 14, 15, 12}, 
						{ 4, 9, 6, 1}};
		return perf;
		
	}
	
	/**
	 * returns a custom balanced board with for test
	 * @return - int[][] - next generation board
	 */
	public static int[][] customTestBoard(){
		int[][] cust = {{ 0, 0, 0, 0, 10, 0}, 
						{ 0, 6, 0, 0, 0, 0}, 
						{ 0, 0, 0, 0, 0, 0}, 
						{ 0, 0, 0, 0, 0, 0},
						{ 0, 0, 0, 0, 0, 0},
						{ 0, 0, 24, 0, 0, 0}};
		return cust;
		
	}
}
