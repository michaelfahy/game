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
	private Game mygame = null;

	ClientHandler(Socket sock, ArrayList<Socket> socketList, Game mygame)
	{
		this.connectionSock = sock;
		this.socketList = socketList;	// Keep reference to master list
		this.mygame = mygame;
	}

	public void run()
	{
        		// Get data from a client and send it to everyone else
		try
		{
			System.out.println("Connection made with socket " + connectionSock);
			BufferedReader clientInput = new BufferedReader(
				new InputStreamReader(connectionSock.getInputStream()));
				System.out.println("In MTClientHandler: ");
				System.out.println(mygame.buzzed);
			while (true)
			{
				// Get data sent from a client
				String clientText = clientInput.readLine();
				if (clientText != null)
				{
					System.out.println("Received: " + clientText);
					System.out.println(connectionSock);
					System.out.println("In Client Handler line 48" +mygame.buzzed);
					//mygame.buzzed = connectionSock;
					System.out.println("In Client Handler line 50" +mygame.buzzed);
					if (mygame.buzzed ==null){
						System.out.println("mygame.buzzed is null so we are first");
						mygame.buzzed = connectionSock;
						System.out.println(mygame.buzzed);
						String message = connectionSock + " has connected first";

					// Turn around and output this data
					// to all other clients except the one
					// that sent us this information
					for (Socket s : socketList)
					{
						if (s != connectionSock)
						{
							DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
							clientOutput.writeBytes(message + "\n");
						}
					}
					message = "You buzzed first.  Waht is your answer?";
					DataOutputStream clientOutput = new DataOutputStream(connectionSock.getOutputStream());
					clientOutput.writeBytes(message + "\n");
					clientText = clientInput.readLine();
					System.out.println(clientText);

				}
				}
				else
				{
				  // Connection was lost
				  System.out.println("Closing connection for socket " + connectionSock);
				   // Remove from arraylist
				   socketList.remove(connectionSock);
				   connectionSock.close();
				   break;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
			// Remove from arraylist
			socketList.remove(connectionSock);
		}
	}
} // ClientHandler for MTServer.java
