
package status;

import java.io.*;
import java.util.*;
import java.math.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import java.nio.file.*;
import java.util.stream.*;

public class Status
{

	public static class User
	{
		int id;
		String name;
		List<Integer> powers;
		public void print()
		{
			System.out.println( "User{" + "id=" + id + ", name=" + name + ", powers=" + powers + "}" );
		}
	}

	public static class Submission
	{
		String created_at;
		int execute_id, id, length, memory_usage, problem_id, score, time_usage;
		String updated_at;
		int user_id, verdict_id;
		public void print()
		{
			System.out.println( "Submission{" + "created_at=" + created_at + ", execute_id=" + execute_id + ", id=" + id + ", length=" + length + ", memory_usage=" + memory_usage + ", problem_id=" + problem_id + ", score=" + score + ", time_usage=" + time_usage + ", updated_at=" + updated_at + ", user_id=" + user_id + ", verdict_id=" + verdict_id + "}" );
		}
	}

	public static class Student
	{
		String name, studentID;
		List<User> users;
		List<Submission> submissions;
		public void print()
		{
			System.out.println( name + " " + studentID );
			if ( users.isEmpty() )
				System.out.println( "users=[]" );
			else
				users.forEach( User::print );
			if ( submissions.isEmpty() )
				System.out.println( "submissions=[]" );
			else
				submissions.forEach( Submission::print );
		}
		public int getScore( List<Integer> problemIDs )
		{
			return submissions.stream().filter( x -> problemIDs.contains( x.problem_id ) )
				  .mapToInt( x -> x.score ).sum();
		}
	}

	public static List<Student> students;
	public static Gson gson = new Gson();

	public static void readStudents( String filename ) throws Exception
	{
		try ( BufferedReader br = new BufferedReader( new FileReader( filename ) ) )
		{
			students = gson.fromJson( br, new TypeToken<List<Student>>()
			{
			}.getType() );
		}
		assert students != null && students.size() > 0;
		System.out.println( "===============" );
		System.out.println( "read " + students.size() + " students from " + filename );
		System.out.println( "===============" );
	}

	public static void readAssignments( String filename ) throws Exception
	{
		int cnt = 0, cnt2 = 0, cnt3 = 0, cnt4 = 0;
		try ( BufferedReader br = new BufferedReader( new FileReader( filename ) ) )
		{
			while ( br.ready() )
			{
				cnt ++;
				String str = br.readLine();
				String[] arr = str.split( " " );
				assert arr.length >= 2;
				cnt2 += arr.length - 1;
				assert arr[0].length() == 3 || ( arr[0].length() == 7 && arr[0].matches( "[\\da-zA-Z]\\d{6}" ) );
				List<Student> list = students.stream().filter( x -> x.name.equals( arr[0] )
					  || x.studentID.equals( arr[0] ) ).collect( Collectors.toList() );
				assert list.size() == 1;
				for ( int i = 1; i < arr.length; i ++ )
				{
					int id = Integer.valueOf( arr[i] );
					if ( list.get( 0 ).submissions.stream().anyMatch( x -> x.id == id ) )
					{
						cnt3 ++;
						continue;
					}
					cnt4 ++;
					Submission submission = new Submission();
					submission.id = Integer.valueOf( arr[i] );
					list.get( 0 ).submissions.add( submission );
				}
			}
		}
		System.out.println( "===============" );
		System.out.println( "read from " + filename );
		System.out.println( "read " + cnt + " lines" );
		System.out.println( "read " + cnt2 + " assignments" );
		System.out.println( cnt3 + " assignments already exists" );
		System.out.println( "processed " + cnt4 + " assignments" );
		System.out.println( "===============" );
	}

	public static void matchSubmissionID( String filename ) throws Exception
	{
		List<Submission> submissions;
		try ( BufferedReader br = new BufferedReader( new FileReader( filename ) ) )
		{
			submissions = gson.fromJson( br, new TypeToken<List<Submission>>()
			{
			}.getType() );
		}
		assert submissions != null && submissions.size() > 0;
		int cnt = 0, cnt2 = 0;
		for ( Student student : students )
		{
			for ( int i = 0; i < student.submissions.size(); i ++ )
			{
				Submission submission = student.submissions.get( i );
				if ( submission.created_at != null )
				{
					cnt ++;
					continue;
				}
				cnt2 ++;
				List<Submission> list = submissions.stream().filter( x -> x.id == submission.id )
					  .collect( Collectors.toList() );
				assert list.size() == 1;
				student.submissions.set( i, list.get( 0 ) );
			}
		}
		System.out.println( "===============" );
		System.out.println( "read " + submissions.size() + " submissions from " + filename );
		System.out.println( cnt + " submissions already done" );
		System.out.println( "matched " + cnt2 + " submissions" );
		System.out.println( "===============" );
	}

	public static void matchUserID( String filename ) throws Exception
	{
		System.out.println( "===============" );
		List<User> users;
		try ( BufferedReader br = new BufferedReader( new FileReader( filename ) ) )
		{
			users = gson.fromJson( br, new TypeToken<List<User>>()
			{
			}.getType() );
		}
		assert users != null && users.size() > 0;
		int cnt = 0, cnt2 = 0;
		for ( Student student : students )
		{
			int[] arr = student.submissions.stream().mapToInt( x -> x.user_id ).distinct()
				  .toArray();
			if ( arr.length != 1 )
			{
				System.out.println(
					  "warning : number of distinct submissions' user_id=" + arr.length + "!=1" );
				System.out.println( student.name + " " + student.studentID );
			}
			cnt += student.users.size();
			for ( int id : arr )
			{
				if ( student.users.stream().noneMatch( x -> x.id == id ) )
				{
					List<User> list = users.stream().filter( x -> x.id == id ).collect( Collectors.toList() );
					assert list.size() == 1;
					student.users.add( list.get( 0 ) );
				}
			}
			cnt2 += student.users.size();
		}
		System.out.println( "read " + users.size() + " users from " + filename );
		System.out.println( "(before)totally " + cnt + " users" );
		System.out.println( "(after)totally " + cnt2 + " users" );
		System.out.println( "===============" );
	}

	public static void validityCheck()
	{
		System.out.println( "===============validity checking===============" );
		System.out.println( gson.toJson( students ) );
		assert students != null && students.size() > 0;
		for ( Student student : students )
		{
			assert student != null;
			assert student.name != null && student.name.length() > 0;
			assert student.studentID != null && student.studentID.length() > 0;
			assert student.users != null;
			if ( student.users.size() != 1 )
			{
				System.out.println( "warning : student.users.size()=" + student.users.size() + "!=1" );
				System.out.println( student.name + " " + student.studentID );
//				student.print();
			}
			for ( User user : student.users )
			{
				assert user.id > 0;
				assert user.name != null && user.name.length() > 0;
				assert user.powers != null && user.powers.isEmpty();
				assert student.users.stream().filter( x -> x.id == user.id ).count() == 1;
				if ( student.submissions.stream().filter( x -> x.user_id == user.id ).count() == 0 )
				{
					System.out.println( "warning : student's user(id=" + user.id + ") with no submission" );
					System.out.println( student.name + " " + student.studentID );
//					student.print();
				}
			}
			assert student.submissions != null;
			if ( student.submissions.isEmpty() )
			{
				System.out.println( "warning : student.submissions.isEmpty()" );
				System.out.println( student.name + " " + student.studentID );
//				student.print();
			}
			for ( Submission submission : student.submissions )
			{
				assert submission.created_at != null && submission.created_at.length() > 0;
				assert submission.execute_id > 0 && submission.id > 0;
				assert submission.length > 0 && submission.memory_usage >= 0;
				assert submission.problem_id > 0 && submission.score >= 0;
				assert submission.time_usage >= 0;
				assert submission.updated_at != null && submission.updated_at.length() > 0;
				assert submission.user_id > 0 && submission.verdict_id >= 0;
				assert student.submissions.stream()
					  .filter( x -> x.id == submission.id ).count() == 1;
				assert student.submissions.stream()
					  .filter( x -> x.problem_id == submission.problem_id ).count() == 1;
				if ( student.users.stream()
					  .filter( x -> x.id == submission.user_id ).count() != 1 )
					student.print();
				assert student.users.stream()
					  .filter( x -> x.id == submission.user_id ).count() == 1;
			}
		}
		Set<Integer> userIDs = new TreeSet<>();
		Set<Integer> submissionIDs = new TreeSet<>();
		for ( Student student : students )
		{
			for ( User user : student.users )
			{
				assert  ! userIDs.contains( user.id );
				userIDs.add( user.id );
			}
			for ( Submission submission : student.submissions )
			{
				assert  ! submissionIDs.contains( submission.id );
				submissionIDs.add( submission.id );
			}
		}
		System.out.println( "===============done===============" );
	}

	public static void printScores( List<Integer> problemIDs )
	{
		students.forEach( x ->
		{
			System.out.printf( "%s %s %3d\n", x.name, x.studentID, x.getScore( problemIDs ) );
		} );
		System.out.println( "problemIDs : " + gson.toJson( problemIDs ) );
		System.out.println( "students.size()=" + students.size() );
	}

	public static void printGradesByStudentID( List<Integer> problemIDs )
	{
		Scanner sc = new Scanner( System.in );
		int cnt = 0;
		while ( sc.hasNextLine() )
		{
			String str = sc.nextLine();
			if ( str.length() != 7 )
				break;
			List<Student> list = students.stream().filter( x -> x.studentID.equals( str ) )
				  .collect( Collectors.toList() );
			assert list.size() == 1;
			System.out.println( list.get( 0 ).getScore( problemIDs ) );
//			System.out.println( list.get( 0 ).getScore( problemIDs ) / ( double ) problemIDs.size() );
			cnt ++;
		}
		System.out.println( "cnt=" + cnt );
	}

	public static void downloadSubmission( int submissionID, String filenamePart ) throws Exception
	{
		final Path base = Paths.get( "downloaded submissions\\" );
		final Path base2 = Paths.get( "renamed submissions\\" );
		assert Files.isDirectory( base ) && Files.isDirectory( base2 );
		String url = "https://api.oj.nctu.me/submissions/" + submissionID + "/file/";
		System.out.println( "start downloading : submissionID=" + submissionID );
		assert Files.list( base ).count() == 0;
		Runtime.getRuntime().exec( new String[]
		{
			"cmd", "/c", "start chrome " + url
		} );
		Thread.sleep( 500 );
		int cnt = 0;
		while ( Files.list( base ).noneMatch( x -> x.toString().matches( ".+\\.(c|cpp)" ) ) )
		{
			cnt ++;
			assert cnt <= 6;
			System.out.println( "downloading..." );
			Thread.sleep( 500 );
		}
		System.out.println( "download finished" );
		assert Files.list( base ).count() == 1;
		Path from = Files.list( base ).findFirst().get();
		Path to = base2.resolve(
			  filenamePart + from.toString().substring( from.toString().lastIndexOf( '.' ) ) );
		System.out.println( "from : " + from );
		System.out.println( "to   : " + to );
		assert  ! Files.exists( to );
		Files.move( from, to );
		System.out.println( "move finished" );
		assert Files.list( base ).count() == 0;
		assert Files.exists( to );
	}

	public static void downloadSubmissions( List<Integer> problemIDs ) throws Exception
	{
		System.out.println( "===============downloadSubmissions===============" );
		int cnt = 0;
		for ( Student student : students )
		{
			List<Submission> list = student.submissions.stream().filter( x -> problemIDs.contains( x.problem_id ) )
				  .collect( Collectors.toList() );
			for ( Submission submission : list )
			{
				cnt ++;
				downloadSubmission( submission.id, submission.problem_id + " " + student.studentID
					  + " " + submission.id + " " + submission.score );
			}
		}
		System.out.println( "problemsID : " + problemIDs );
		System.out.println( cnt + " submissions downloaded" );
		System.out.println( "===============done===============" );
	}

	public static void checkPlagiarism( String filename ) throws Exception
	{
		try ( BufferedReader br = new BufferedReader( new FileReader( filename ) ) )
		{
			int cnt = 0;
			while ( br.ready() )
			{
				cnt ++;
				String str = br.readLine();
				if ( str.isEmpty() )
					break;
				assert students.stream().filter( x -> x.studentID.equals( str.split( " " )[0] ) )
					  .count() == 1;
				assert str.split( " " ).length == 2;
				downloadSubmission( Integer.valueOf( str.split( " " )[1] ),
					  259
					  + " " + str.split( " " )[0]
					  + " " + str.split( " " )[1] );
			}
			System.out.println( "cnt=" + cnt );
		}
	}

	public static void checkOJUserList( String filename ) throws Exception
	{
		try ( BufferedReader br = new BufferedReader( new FileReader( filename ) ) )
		{
			List<String> names = new ArrayList<>();
			while ( br.ready() )
			{
				String str = br.readLine().split( " ", 2 )[1];
				names.add( str );
			}
			students.stream().filter( x -> names.stream().noneMatch( y -> y.equals( x.studentID ) ) )
				  .forEach( x -> System.out.println( x.studentID ) );
		}
	}

	public static void main( String[] args ) throws Exception
	{
//		System.setOut(new PrintStream("student list2.txt"));
		readStudents( "student list.txt" );
		validityCheck();

//		
//		
//		
//
//
//		students.forEach( x ->
//		{
//			x.print();
//			System.out.println( "" );
//		} );
//
//		readAssignments( "assignment list2.txt" );
//		System.out.println( gson.toJson( students ) );
//
//		matchSubmissionID( "submission list2.txt" );
//		System.out.println( gson.toJson( students ) );
//
//		matchUserID( "user list2.txt" );
//		System.out.println( gson.toJson( students ) );
//
//		List<Integer> problemIDs = Arrays.asList( 213, 214 );
//		printScores( problemIDs );
//		printGradesByStudentID( problemIDs );
//		downloadSubmissions( problemIDs );
//
//		checkOJUserList("user list2.txt");
//		
//
//
//
//
	}
}
