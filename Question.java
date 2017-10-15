import java.net.Socket;

public class Question
{
	// Maintain list of all client sockets for broadcast

	public String thequestion;
	public String[] answer;
  public int correct = 5;

	public Question()
	{
    {
      this.answer = new String[4];
      this.correct = -1;
    }
	}
}
