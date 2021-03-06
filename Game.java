import java.net.Socket;
import java.util.ArrayList;
import java.io.*;

public class Game
{
	// Maintain list of all client sockets for broadcast
	private ArrayList<Player> playerList;
	public ArrayList<Question> questionList;
	private int numberOfPlayers = 2;
	private int numberConnected = 0;
	private String question = "";
  public Socket buzzed = null;
	public Question aquestion = null;
	public int nextQuestion = 0;
	public int currentCorrect = -1;
	public int over = 0;


	public Game(ArrayList<Player> playerList)
	{

      this.playerList = playerList;	// Keep reference to master list
			// The name of the file to open.
        String fileName = "questions.txt";

        // This will reference one line at a time
        String line = null;
				questionList = new ArrayList<Question>();

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =  new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
							aquestion = new Question();
							aquestion.thequestion = line;
							//System.out.println(aquestion.thequestion);
							for(int i=0; i<4; i++){
								line = bufferedReader.readLine();
								//Find the correct answer and store it
								if (line.substring(0,1).equals("*")){
									aquestion.correct = i;
									aquestion.answer[i]= line.substring(1,line.length());
								}
								else
									aquestion.answer[i] = line.substring(0,line.length());

								//System.out.println(aquestion.answer[i]);

								}
								//System.out.println(aquestion.correct);

							questionList.add(aquestion);
							line = bufferedReader.readLine();
              //System.out.println(line);
						}


            // Always close files.
            bufferedReader.close();
					}

        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" +  fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(  "Error reading file '"+ fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
			}
}
