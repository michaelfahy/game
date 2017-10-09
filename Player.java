import java.net.Socket;

public class Player
{
	// Maintain list of all client sockets for broadcast

	private int score = 0;
	private String name = "";
  public Socket connectionSock = null;

	public Player(Socket connectionSock,String name)
	{
    {
      this.connectionSock  = connectionSock;	// Keep reference to master list
      this.name = name;
    }
	}
}
