/**
 * MTServer.java
 *
 * This program implements a simple multithreaded chat server.  Every client that
 * connects to the server can broadcast data to all other clients.
 * The server stores an ArrayList of sockets to perform the broadcast.
 *
 * The MTServer uses a ClientHandler whose code is in a separate file.
 * When a client connects, the MTServer starts a ClientHandler in a separate thread
 * to receive messages from the client.
 *
 * To test, start the server first, then start multiple clients and type messages
 * in the client windows.
 *
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MTServer
{
	// Maintain list of all client sockets for broadcast
	private ArrayList<Socket> socketList;
	private ArrayList<Player> playerList;
	private ServerSocket serverSock = null;
	private int numberOfPlayers = 2;
	private int numberConnected = 0;
	private String question = "";
	private Game mygame = null;
	private String message;
	private int questionNumber = 1;
	private int numberOfQuestions = 0;
	private int state = 0;

	public MTServer()
	{
		socketList = new ArrayList<Socket>();
		playerList = new ArrayList<Player>();
		mygame = new Game(playerList);
	}

	public void showScores()
	{
		try
		{
			for (Player p :playerList){
				DataOutputStream clientOutput = new DataOutputStream(p.connectionSock.getOutputStream());

				for (Player pp :playerList){
					if (pp.name.length() >0)
					{
						String message = pp.name + " has " + pp.score + " points. \n";
						clientOutput.writeBytes(message + "\n");
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
	}

	private void getConnection()
	{
		// Wait for a connection from the client
		try
		{
			System.out.println("Waiting for client connections on port 7654.");
			serverSock = new ServerSocket(7654);
			System.out.print("In MTServer, buzzed is ");
			System.out.println(mygame.buzzed);
			// This is an infinite loop, the user will have to shut it down
			// using control-c
			while (numberConnected < numberOfPlayers)
			{
				Socket connectionSock = serverSock.accept();
				// Add this socket to the list
				Player newplayer= new Player(connectionSock, "");
				//socketList.add(connectionSock);
				playerList.add(newplayer);
				numberConnected++;
				System.out.print("The number of connected players is ");
				System.out.println(numberConnected);
				// Send to ClientHandler the socket and arraylist of all sockets
				ClientHandler handler = new ClientHandler(newplayer, this.playerList,mygame);
				Thread theThread = new Thread(handler);
				theThread.start();

			}
			System.out.println("All players have connected\n");
			//Wait until all clients have given us their names.
			int ready = 0;
			while (ready == 0)
			{
				ready = 1;								//Wait for players to enter their names
				for (Player p:playerList)
				{
					if (p.name.equals(""))
					{
						ready = 0;
					}
				}
			};

			showScores();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

public void askQuestions(int numberOfQuestions) throws java.io.IOException
{

			//while (true){
			//numberOfQuestions = mygame.questionList.size();
			for (Question q : mygame.questionList)
			{ // If this is the last questions, game will be over
				System.out.println(numberOfQuestions);
				System.out.println(questionNumber);

				switch (state) {

					case 1:{ //build a question and send it to all players
						if (questionNumber == numberOfQuestions){
							mygame.over = 1;
						}
						questionNumber++;
						//Build a message with a question
						mygame.currentCorrect = q.correct;
						question = "\n\n"+q.thequestion + "\n\n";
						for (int i=0; i<4; i++){
							question = question + q.answer[i] + "\n";
						}
						question = question + "\nBuzz in!\n\n";

						for (Player p : playerList)							//send the question to all players
						{
							DataOutputStream clientOutput = new DataOutputStream(p.connectionSock.getOutputStream());
							clientOutput.writeBytes(question + "\n");
						}
						System.out.println("Question has been sent to all players.\n");
						//System.out.println(mygame.nextQuestion);
						state = 2;
						break;
					}

					case 2:	{ //wait for buzz and answer, signaled by mygame.nextQuestions == 1

					while (mygame.nextQuestion == 0){		//Wait for a player to answer
						try
							{
    						Thread.sleep(2000);
							}
							catch(InterruptedException ex)
							{
    						Thread.currentThread().interrupt();
							}
							System.out.println("Server sleeping \n");
						}
						try
							{
    						Thread.sleep(2000);
							}
							catch(InterruptedException ex)
							{
    						Thread.currentThread().interrupt();
							}
							System.out.println("Server sleeping \n");
						

						mygame.nextQuestion = 0;  //reset
						showScores();

						if (mygame.over == 1){
							state = 3;
						}
						else{
							state = 1;
							break;
						}

					}

					case 3:{ //all questions have been answered

					System.out.println("That was the last question!");


					for (Player pp :playerList)
					{
						DataOutputStream clientOutput = new DataOutputStream(pp.connectionSock.getOutputStream());
						message = "Game Over \n";
						clientOutput.writeBytes(message + "\n");
						message = "Final Scores";
						message = pp.name + " has " + pp.score + " points. \n";
						clientOutput.writeBytes(message + "\n");
						clientOutput.writeBytes("Goodbye\n");
						//pp.connectionSock.close();
						//playerList.remove(pp);
					}

					while (playerList.size() > 0){		//Wait for a players to exit
						try
						{
							Thread.sleep(2000);
						}
						catch(InterruptedException ex)
						{
							Thread.currentThread().interrupt();
						}
						//System.out.println("Server sleeping \n");
					}

					// Will never get here, but if the above loop is given
					// an exit condition then we'll go ahead and close the socket
					serverSock.close();
					break;
				}
			}
		}

}

	public static void main(String[] args)
	{
		MTServer server = new MTServer();
		server.getConnection();
		server.state = 1;
		try{
  			server.askQuestions(server.mygame.questionList.size());
		}
		catch(IOException e){
  		e.printStackTrace();
		}

	}
} // MTServer
