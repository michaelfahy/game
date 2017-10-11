/**
 * ClientHandler.java
 *
 * This class handles communication between the client
 * and the server.  It runs in a separate thread but has a
 * link to a common list of sockets to handle broadcast.
 *
 */
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class ClientHandler implements Runnable
{
	private Socket connectionSock = null;
	private ArrayList<Socket> socketList;
	private Player player = null;
	private ArrayList<Player> playerList;
	private Game mygame = null;
	private int answer = -1;


	ClientHandler(Player player, ArrayList<Player> playerList, Game mygame)
	{
		this.player = player;
		this.playerList = playerList;	// Keep reference to master list
		this.mygame = mygame;
	}

	public void showScores(Player player)
	{
		try
		{
			DataOutputStream clientOutput = new DataOutputStream(player.connectionSock.getOutputStream());

			for (Player pp :playerList)
			{
				if (pp.name.length() >0)
				{
					String message = pp.name + " has " + pp.score + " points. \n";
					clientOutput.writeBytes(message + "\n");
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
	}

	public void getAnswer(Player player)
	{
		try
		{
		String message = "You buzzed first.  What is your answer?";
		DataOutputStream clientOutput = new DataOutputStream(player.connectionSock.getOutputStream());
		clientOutput.writeBytes(message + "\n");
		BufferedReader clientInput = new BufferedReader(
			new InputStreamReader(player.connectionSock.getInputStream()));
		String clientText = clientInput.readLine();
		System.out.println(clientText);
		int answer = (int)clientText.charAt(0)-97;
		System.out.print("Answer is ");
		System.out.println(answer);
		if (answer==mygame.currentCorrect)
		{
			message = "You are correct!\n";
			player.score++;
		}
		else
		{
			message = "Sorry, that is not correct";
			player.score--;
		}
		clientOutput.writeBytes(message + "\n");
		mygame.nextQuestion = 1;
		mygame.buzzed = null;
		}
		catch(Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
	}


	public void run()
	{
        		// Get data from a client and send it to everyone else
		try
		{
			System.out.println("Connection made with socket " + player.connectionSock);
			BufferedReader clientInput = new BufferedReader(
				new InputStreamReader(player.connectionSock.getInputStream()));
			//System.out.println("In MTClientHandler: ");
			//System.out.println(mygame.buzzed);
			String message = "What is your name? ";
			DataOutputStream clientOutput = new DataOutputStream(player.connectionSock.getOutputStream());
			clientOutput.writeBytes(message + "\n");
			String clientText = clientInput.readLine();
			player.name = clientText;
			//while (mygame.over == 0)
			//System.out.print("Size of Question list" );
			//System.out.println(mygame.questionList.size());
			for (int nq=0; nq< mygame.questionList.size();nq++ )
			{
				System.out.println("Top of for loop in ClientHandler");
				showScores(player);

				// see who buzzed in first.
				System.out.println("Waiting for player input: ");
				clientText = clientInput.readLine();
				if (clientText != null)
				{
					//System.out.println("Received: " + clientText);
					//System.out.println(player.connectionSock);
					//mygame.buzzed = connectionSock;
					if (mygame.buzzed == null)
					{
						System.out.println( player.name + " says mygame.buzzed is null so I am first");
						mygame.buzzed = player.connectionSock;

						//System.out.println(mygame.buzzed);
						message = player.name + " has buzzed in first. Press the enter key to continue.";

						// Turn around and output this data
						// to all other clients except the one
						// that sent us this information
						//for (Socket s : socketList)
						for (Player p :playerList)
						{
							if (p.connectionSock != player.connectionSock)
							{
								clientOutput = new DataOutputStream(p.connectionSock.getOutputStream());
								clientOutput.writeBytes(message + "\n");
							}
						}
						System.out.println("before getAnswer.");
						getAnswer(player);
						System.out.println("after getAnswer.");
					/*	message = "You buzzed first.  What is your answer?";
						clientOutput = new DataOutputStream(player.connectionSock.getOutputStream());
						clientOutput.writeBytes(message + "\n");
						clientText = clientInput.readLine();
						System.out.println(clientText);
						answer = (int)clientText.charAt(0)-97;
						System.out.println(answer);
						if (answer==mygame.currentCorrect)
						{
							message = "You are correct!\n";
							player.score++;
						}
						else
						{
							message = "Sorry, that is not correct";
							player.score--;
						}
						clientOutput.writeBytes(message + "\n");
						mygame.nextQuestion = 1;
						*/
						/*System.out.print("Score = ");
						System.out.println(player.score);
						System.out.print("NextQuestion = ");
						System.out.println(mygame.nextQuestion);
						*/
						showScores(player);
						System.out.println("After showScores.");
						/*for (Player pp :playerList)
						{
							message = pp.name + " has " + pp.score + " points. \n";
							clientOutput.writeBytes(message + "\n");
						}*/
	//	mygame.buzzed = null;
					} // end of if noone else buzzed yet
					System.out.println("After if mygame.buzzed = null");
				}//end of if client buzzed
				System.out.println("After if clientText is not null");
				/*else
				{
				  // Connection was lost
				  System.out.println("Closing connection for socket " + player.connectionSock);
				   // Remove from arraylist
				   playerList.remove(player);
				   player.connectionSock.close();
				   break;

			}*/


				System.out.print("Waiting for server:");
				System.out.println(mygame.over);
			} //end of for more questions
			message = "Game Over \n";
			clientOutput.writeBytes(message + "\n");
			message = "Final Scores";
			for (Player pp :playerList)
			{
				message = pp.name + " has " + pp.score + " points. \n";
				clientOutput.writeBytes(message + "\n");
			}
			//playerList.remove(player);
			//player.connectionSock.close();

		} //end of try
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
			// Remove from arraylist
			playerList.remove(player);
		} //end of catch
	} //end of run
} // ClientHandler for MTServer.java
