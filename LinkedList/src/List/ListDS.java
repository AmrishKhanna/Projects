package List;

import java.util.HashMap;
import java.util.Scanner;

public class ListDS {

public static boolean wordPattern(String pattern, String str) {
	char patternArray[] = pattern.toCharArray();
	String[] strarray = str.split(" ");
	HashMap<Character, String> mapping = new HashMap<>();
	boolean iscorrect = true;
	int i=0;
	if(patternArray.length == strarray.length)
	{
	for(char ch : patternArray)
	{
		if(mapping.containsKey(ch))
		{
			if(mapping.get(ch).equalsIgnoreCase((strarray[i])))
			{
				iscorrect = true;
			}
			else
			{
				iscorrect = false;
				break;
			}
			i++;
		}
		else
		{
			if(mapping.containsValue(strarray[i]))
			{
				iscorrect = false;
				break;
			}
			else{
			mapping.put(ch, strarray[i]);
			iscorrect = true;
			}
			i++;
		}
	}
	
     return iscorrect;
	}
	else
	{
		return false;
	}
	
	
    }
	
public static void main(String args[])
{
	System.out.println(wordPattern("abba","dog dog dog dog"));
}
}
