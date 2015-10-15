package List;

public class list {
	private static Node head = null;

	public static int length(Node head)
	{
		int count = 0;
		if(head == null)
		{
			return count;
		}
		else 
		{
			count++;
			while(head.next!=null)
			{
				head = head.next;
				count++;
			}
		}
		return count;
	}

	public static Node insert(int data)
	{
		Node temp = head;
		if(head==null)
		{
			head=new Node();
			head.data = data;
			head.next = null;
			return head;
 		}
		else 
		{
			while(head.next!=null)
			{
				head = head.next;
			}
			Node newNode = new Node();
			newNode.data=data;
			newNode.next=null;
			head.next = newNode;
		}
		return temp;
	}
	
	public static Node insertInPosition(int pos, int data)
	{
		Node temp = head;
		Node newNode = new Node();
		newNode.data=data;
		int i=1;
		if(pos==1)
		{
			newNode.next=head;
			temp=newNode;
			return temp;
		}
		else if(pos==length(head))
		{
			while(head.next!=null)
			{
				head = head.next;
			}
			newNode.next=null;
			head.next = newNode;
		}
		else
		{
		while(head.next!=null)
		{
			i++;			
			if(i==pos)
			{
				Node post = head.next;
				head.next=newNode;
				newNode.next=post;
				newNode.data=data;
				break;
			}
			else
			{
			head = head.next;
			}
		}
		}		

		return temp;
	}
 
	public static Node delete(int pos)
	{
		int i=1;
		if(pos==1&&head!=null)
		{
			return head.next;
		}
		else
		{
		while(head!=null)
		{
			i++;			
			if(i==pos)
			{
				head.next = head.next.next;
				break;
			}
			else
			{
			head = head.next;
			}
		}
		}		

		return null;
	}

	public static void display(Node head)
	{
		Node temp = new Node();
		temp = head;

		while(temp!=null)
		{
				System.out.println(temp.data);
				temp = temp.next;
			
		}
	}
	
	public static Node deleteNode(Node head, int position)
	{
		if(position==0)
		{
			return head.next;
		}
		else
		{
			int count=0;
			Node current=head;
			while(head!=null)
			{
				if(count==position-1)
				{
					head.next=head.next.next;
					return current;
				}
				else
				{
					head=head.next;
				}
				count++;
			}
		return null;
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		head = insert(1);
		head = insert(2);
		head = insert(3);
		head = insert(4);
		
		//delete(4);
		display(deleteNode(head,3));
		//System.out.println(length(head));
		
	}

}
