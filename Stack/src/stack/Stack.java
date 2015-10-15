package stack;

public class Stack {

	private static int[] stack = new int[10];
	private static int pointer = -1;
	public static boolean stackOverflow()
	{
		if(stack.length==pointer+1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static boolean stackunderflow()
	{
		if((pointer-1) == -1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static void push(int data)
	{
		if(stackOverflow())
		{
			System.out.println("Stack is full. Please pop some elements");
		}
		else
		{
			pointer++;
			stack[pointer]=data;		
		}

	}

	public static void pop()
	{
		if(stackunderflow())
		{
			System.out.println("Stack is empty. Please insert some elements");
		}
		else
		{
			System.out.println("Popped item:" + " " + stack[pointer]);
			stack[pointer]=0;
			pointer--;	

		}
	}

	public static int peek()
	{
		return stack[pointer];
	}
	

	public static void main(String args[])
	{
		push(1);
		push(2);
		push(3);
		push(4);
		push(5);
		System.out.println(peek());
		System.out.println();
		pop(); 
		pop();
		System.out.println();
		System.out.println(peek());
	}
}
