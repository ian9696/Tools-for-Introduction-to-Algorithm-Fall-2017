
package ojuserparser;

import java.io.*;
import java.util.*;
import java.math.*;
import com.google.gson.*;

public class OJUserParser
{

	public static class C
	{
		List<User> msg;
	}

	public static class User
	{
		int id;
		String name;
		List<Integer> powers;
	}

	public static void main( String[] args ) throws Exception
	{
		System.setIn( new FileInputStream( "user list.txt" ) );
//		System.setOut(new PrintStream("user list2.txt"));
		BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
		Gson gson = new Gson();
		C c = gson.fromJson( br, C.class );
		List<User> usrs = c.msg;
		System.out.println( gson.toJson( usrs ) );
		System.out.println( "usrs.size=" + usrs.size() );
		for(User usr: usrs)
			System.out.println( usr.id+" "+usr.name );

	}
}
