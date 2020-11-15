
package e3assignmentparser;

import java.io.*;
import java.util.*;
import java.math.*;
import java.util.stream.*;

public class E3AssignmentParser
{

	public static void main( String[] args ) throws Exception
	{
		int minSubNum = 1, maxSubNum = 2;
		int maxFileSiz = 50;
		String bas = "作業";
//		System.setOut( new PrintStream( "assignment list2.txt" ) );

		File[] asms = new File( bas ).listFiles();
		assert asms != null && asms.length > 0;
		for ( File asm : asms )
		{
			assert asm.isDirectory();
			System.out.print( asm.getName().split( " " )[0] );
			File[] arr = asm.listFiles();
			assert arr != null && arr.length == 1;
			File tmp = arr[0];
			assert tmp.isFile();
			assert tmp.length() <= maxFileSiz;
			ArrayList<String> list = new ArrayList<>();
			try ( BufferedReader br = new BufferedReader( new FileReader( tmp ) ) )
			{
				while ( br.ready() )
				{
					String str = br.readLine();
//					System.out.println( str );
					list.addAll( Arrays.stream( str.replaceAll( "\\D", " " ).split( " " ) )
						  .filter( x ->  ! x.isEmpty() ).collect( Collectors.toList() ) );
				}
			}
			assert minSubNum <= list.size() && list.size() <= maxSubNum;
			System.out.println( " " + String.join( " ", list ) );
		}
		System.out.println( "asms.size=" + asms.length );
	}

}
