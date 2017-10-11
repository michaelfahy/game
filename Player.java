import java.net.Socket;

public class Player
{
	// Maintain list of all client sockets for broadcast

	public int score = 0;
	public String name = "";
  public Socket connectionSock = null;

	public Player(Socket connectionSock,String name)
	{
    {
      this.connectionSock  = connectionSock;	// Keep reference to master list
      this.name = name;
    }
	}
}
