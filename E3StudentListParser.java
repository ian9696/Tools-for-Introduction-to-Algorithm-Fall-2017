
package e3studentlistparser;

import java.io.*;
import java.util.*;
import java.math.*;

public class E3StudentListParser
{
	public static void main( String[] args ) throws Exception
	{
		System.setIn( new FileInputStream( "studentID list.txt" ) );
//		System.setOut( new PrintStream( "studentID list2.txt" ) );
		Scanner sc = new Scanner( System.in );
		int tmp = 0, cnt = 0;
		while ( sc.hasNextLine() )
		{
			if ( tmp == 0 )
				cnt ++;
			String str = sc.nextLine();
			if ( tmp == 1 )
				System.out.print( str.split( " " )[0] + " " );
			else if ( tmp == 2 )
				System.out.println( str );
			tmp = ( tmp + 1 ) % 8;
		}
		System.out.println( "cnt=" + cnt );
	}
}
