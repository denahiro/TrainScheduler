/**
 * 
 */
package ch.sreng.schedule;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author koenigst
 *
 */
public class Scheduler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("What's your name?");
		try
		{
			BufferedReader tmpInput=new BufferedReader(new InputStreamReader(System.in));
			String name=tmpInput.readLine();
			tmpInput.close();
			String greeting=new String("Hello ");
			System.out.println(greeting.concat(name));
		}
		catch (java.io.IOException e)
		{
			System.out.println("Bang your dead");
		}
	}

}
