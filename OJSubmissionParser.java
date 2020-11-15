
package ojsubmissionparser;

import java.io.*;
import java.util.*;
import java.math.*;
import com.google.gson.*;

public class OJSubmissionParser
{
	public static class A
	{
		B msg;
	}

	public static class B
	{
		int count;
		List<Submission> submissions;
	}

	public static class Submission
	{
		String created_at;
		int execute_id, id, length, memory_usage, problem_id;
		@Override
		public String toString()
		{
			return "created_at=" + created_at + " execute_id=" + execute_id + " id=" + id + " length=" + length + " memory_usage=" + memory_usage + " problem_id=" + problem_id + " score=" + score + " time_usage=" + time_usage + " updated_at=" + updated_at + " user_id=" + user_id + " verdict_id=" + verdict_id;
		}
		int score, time_usage;
		String updated_at;
		int user_id, verdict_id;
	}

	public static void main( String[] args ) throws Exception
	{
		System.setIn( new FileInputStream( "submission list.txt" ) );
//		System.setOut(new PrintStream("submission list2.txt"));
		BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
		Gson gson = new Gson();
		A a = gson.fromJson( br, A.class );
		List<Submission> sbms = a.msg.submissions;
//			Collections.sort( sbms, (x, y) -> ( y.id - x.id ) );
		System.out.println( gson.toJson( sbms ) );
		System.out.println( "sbms.size=" + sbms.size() );

//		int problemID = 259;
//		Map<Integer, Submission> map = new TreeMap<>();
//		for ( Submission sbm : sbms )
//		{
//			if ( sbm.problem_id != problemID )
//				continue;
//			if (  ! map.containsKey( sbm.user_id ) )
//			{
//				map.put( sbm.user_id, sbm );
//				continue;
//			}
//			if ( map.get( sbm.user_id ).score < sbm.score )
//				map.put( sbm.user_id, sbm );
//		}
//		int cnt=0;
//		for ( Map.Entry<Integer, Submission> entry : map.entrySet() )
//		{
//			cnt++;
//			System.out.println( entry.getKey() + " " + entry.getValue().id );
//		}
//		System.out.println( "cnt="+cnt );
	}
}
