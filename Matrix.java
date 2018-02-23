package matrixmult;



import java.io.IOException;
import java.util.Scanner;

/**
 * This program will run the algorithm chosen until we want to end by pushing enter.
 * This was created with the intent to compare the 3 algorithms, thus the average time.
 * The time for each instance is included to compare each algorithm to each other.
 * 
 * @author Alex
 *
 */
public class Matrix
{
	
	public static void main(String[] args) throws IOException
	{
		
		Scanner kb = new Scanner(System.in);
		
		System.out.println("Choose an algorithm. 1. Classic 2. Divide and Conquer 3. Strassen");
		System.out.println("Push Enter to Stop");
		int choice = kb.nextInt();
		
		long startTime = 0;
		long begTime = 0;
		long lastTime = 0;
		int n = 2;
		do
		{
			int [][] matA = new int[n][n];
			int [][] matB = new int[n][n];
			int a = 1;
			for(int i = 0; i < n; i++)
			{
				for(int j = 0; j < n; j++)
				{
					matA[i][j] = a;
					matB[i][j] = a++;
				}
			} 
			int [][] d = new int[n][n];
			//Classical Matrix method
			if (choice == 1){
				startTime = System.nanoTime(); 
				begTime = System.nanoTime();
				d = Classical(matA, matB, n);
				lastTime = System.nanoTime();

			}
			//Divide and conquer method
			else if(choice == 2)
			{
				startTime = System.nanoTime();	
				begTime = System.nanoTime();
				d = divConq(matA,matB, 0,0,0,0,n);
				lastTime = System.nanoTime();
			}
			//Strassen method
			else
			{
				startTime = System.nanoTime();	
				begTime = System.nanoTime();
				d= strassen(n,matA,matB);
				lastTime = System.nanoTime();
			}
			//Find the time of the instance
			long nTime = (lastTime - begTime);
			System.out.println("n = " + n + "     Time: " + nTime);
			n=n*2;
		} while(System.in.available() == 0);
		//end the total time
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); // duration of the full program run
		System.out.println("Average Time: " + (duration/(n/2)) + "ns");
	}
	
	/**
	 * This method is the classical matrix multiplication method.
	 * We have 3 for loops which causes a O(n^3)
	 * I include an error catch however it should not be needed since we
	 * set the size of the two matrices to nxn. However, I include it
	 * just in case I need this method in future programs.
	 * @param a
	 * @param b
	 * @param n
	 * @return
	 */
	public static int [][] Classical(int[][] a, int[][] b, int n)
	{
		if(a.length != b.length)
		{ 
			return null;
		}
		int [][] d = new int[n][n];
		
		for(int r = 0; r < n; r++)
		{
			for(int c = 0; c < n; c++)
			{
				for(int i = 0; i <n ; i++)
				{
					d[r][c] += a[r][i] * b[i][c];
				}
			}
		}
	
		return d;
	}
	
	/**
	 * Strassens algorithm. With the Psuedo code provided from 
	 * the lecture notes we can see how it translates to real code.
	 * This method is recursive and will partition until the size of 2. 
	 * @param n
	 * @param a
	 * @param b
	 * @return
	 */
	public static int[][] strassen(int n, int[][] a, int[][] b)
	{
		int [][] c = new int[n][n];
		
		if(n ==2)
		{
			c[0][0] = (a[0][0] * b[0][0]) + (a[0][1]*b[1][0]);
			c[0][1] = (a[0][0] * b[0][1]) + (a[0][1]*b[1][1]);
			c[1][0] = (a[1][0] * b[0][0]) + (a[1][1]*b[1][0]);
			c[1][1] = (a[1][0] * b[0][1]) + (a[1][1]*b[1][1]);
		}
		else
		{
			int size = n/2;
			int[][] A11 = new int[size][size];
			int[][] A12 = new int[size][size];
			int[][] A21 = new int[size][size];
			int[][] A22 = new int[size][size];
			int[][] B11 = new int[size][size];
			int[][] B12 = new int[size][size];
			int[][] B21 = new int[size][size];
			int[][] B22 = new int[size][size];
			
			partition(a, A11, 0, 0);
			partition(a, A12, 0, size);
			partition(a, A21, size, 0);
			partition(a, A22, size, size);
			partition(b, B11, 0, 0);
			partition(b, B12, 0, size);
			partition(b, B21, size, 0);
			partition(b, B22, size, size);
			
			int[][] P = new int[size][size];
			int[][] Q = new int[size][size];
			int[][] R = new int[size][size];
			int[][] S = new int[size][size];
			int[][] T = new int[size][size];
			int[][] U = new int[size][size];
			int[][] V = new int[size][size];
			
			P = strassen(size,add(A11,A22),add(B11,B22));
			Q = strassen(size,add(A21, A22), B11);
            R = strassen(size,A11, sub(B12, B22));
            S = strassen(size,A22, sub(B21, B11));
            T = strassen(size,add(A11, A12), B22);
            U = strassen(size,sub(A21, A11), add(B11, B12));
            V = strassen(size, sub(A12, A22), add(B21, B22));
            
            int[][] C11 = add(sub(add(P,S), T),V);
            int[][] C12 = add(R,T);
            int[][] C21 = add(Q,S);
            int[][] C22 = add(sub(add(P, R), Q),U);
            
            join(C11, c, 0 , 0);
            join(C12, c, 0 , size);
            join(C21, c, size, 0);
            join(C22, c, size, size);
            
		}
        return c;
	}
	
	/**
	 * This method is used in the Strassens in order to 
	 * join all of the partitions together
	 * @param C
	 * @param P
	 * @param a
	 * @param b
	 */
	public static void join(int[][] C, int[][] P, int a, int b) 
    {
        for(int i1 = 0, i2 = a; i1 < C.length; i1++, i2++)
        {
            for(int j1 = 0, j2 = b; j1 < C.length; j1++, j2++)
            {
                P[i2][j2] = C[i1][j1];
            }
        }
    }   
	
	/**
	 * This is the subtraction method for the matrices in Strassens
	 * @param a
	 * @param b
	 * @return
	 */
	public static int[][] sub(int[][] a, int[][]b)
	{
		int n = a.length;
		int[][] c = new int [n][n];
		for (int i = 0; i < n; i++)
		{
            for (int j = 0; j < n; j++)
            {
            	c[i][j] = a[i][j] - b[i][j];
            }
		}
        return c;
	}
	
	/**
	 * This is the addition method from the matrices in Strassens
	 * @param a
	 * @param b
	 * @return
	 */
	public static int[][] add(int[][] a, int [][] b)
	{
		int n = a.length;
		int[][] c = new int [n][n];
		for (int i = 0; i < n; i++)
		{
            for (int j = 0; j < n; j++)
            {
            	c[i][j] = a[i][j] + b[i][j];
            }
		}
        return c;
	}
	
	/**
	 * The method used to split the matrices in strassens
	 * @param a
	 * @param b
	 * @param n
	 * @param nB
	 */
	public static void partition(int[][] a, int[][] b,int n, int nB)
	{
		for(int i1 = 0, i2 = n; i1 < b.length; i1++, i2++)
		{
			for(int j1= 0, j2= nB; j1 < b.length; j1++, j2++)
			{
				b[i1][j1] = a[i2][j2];
			}
		}
		
		
	}
	
	/**
	 * The divide and Conquer method is recursive. It splits the matrices until the size is 1
	 * returns c.
	 * @param a
	 * @param b
	 * @param rowA
	 * @param colA
	 * @param rowB
	 * @param colB
	 * @param n
	 * @return
	 */
	public static int[][] divConq(int[][] a, int[][]b, int rowA, int colA, int rowB, int colB, int n)
	{
		int [][] c = new int[n][n];
		if(n == 1)
		{
			c[0][0] = a[rowA][colA] * b[rowB][colB];
		}
		else
		{
			int newSize = n/2;
			addMatrix(c,divConq(a,b,rowA,colA, rowB, colB, newSize), divConq(a,b,rowA,colA+newSize, rowB+newSize, colB, newSize),0,0);
			
			addMatrix(c,divConq(a,b,rowA,colA,rowB,colB+newSize,newSize),divConq(a,b,rowA,colA+newSize,rowB+newSize, colB+newSize, newSize), 0, newSize);
			
			addMatrix(c, divConq(a, b, rowA+ newSize, colA, rowB, colB, newSize), divConq(a, b, rowA+ newSize, colA+newSize, rowB+ newSize, colB, newSize), newSize, 0);
		
			addMatrix(c, divConq(a,b,rowA+newSize, colA, rowB, colB+newSize, newSize),divConq(a,b,rowA+newSize, colA +newSize, rowB+newSize, colB+newSize, newSize),newSize, newSize);
		}
		return c;
	}
	

	/**
	 * Method used to add the matrices in divide and conquer
	 * @param c
	 * @param a
	 * @param b
	 * @param rowC
	 * @param colC
	 */
	public static void addMatrix(int[][]c, int[][]a, int[][]b, int rowC, int colC)
	{
		int n = a.length;
		for(int i = 0; i <n; i++)
		{
			for(int j=0; j<n; j++)
			{
				c[i+rowC][j+colC] = a[i][j] + b[i][j];
			}
		}
	}
	
	/**
	 * Print the array given. Method was used in the developement of the program
	 * @param a
	 */
	public static void printArray(int[][] a)
	{
		for(int r=0; r < a.length; r++)
		{
			for(int c = 0; c < a[0].length; c++)
			{
				System.out.print(" |" + a[r][c] + "| ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Resetting the output array. Used in the development of the program
	 * @param a
	 */
	public static void resetArray(int[][] a)
	{
		for(int r=0; r < a.length; r++)
		{
			for(int c = 0; c < a[0].length; c++)
			{
				a[r][c] = 0;
			}
		}
	}


        
   }
