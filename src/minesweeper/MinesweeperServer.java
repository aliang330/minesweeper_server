package minesweeper;
import java.awt.datatransfer.Clipboard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MinesweeperServer {

	private static final int PORT = 4444;
	private static int DEFAULT_BOARD_SIZE = 5;
	private final ServerSocket SERVER_SOCKET;
	private final Board BOARD;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final int HEADER_SIZE = 10;
	private Map<PrintWriter, String> players = new HashMap<>();
	private Set<PrintWriter> clientOuts = new HashSet<PrintWriter>();

	public MinesweeperServer(int port) throws IOException {
		SERVER_SOCKET = new ServerSocket(port);
		this.BOARD = new Board(DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}

	public static void runServer() throws IOException {
		MinesweeperServer server = new MinesweeperServer(PORT);
		server.start();
	}

	public void start() throws IOException {
		while(true) {
			Socket clientSocket = SERVER_SOCKET.accept();

			new Thread(() -> {
				try {
					handleConnection(clientSocket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						clientSocket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}).start();


		}
	}

	private void handleConnection(Socket socket) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		
		clientOuts.add(out);

		String boardJson = convertBoardToJsonString();
		String serverResponseString = generateRawBoardReponse();
		System.out.println(serverResponseString);
		out.println(serverResponseString);

		for (String line = in.readLine(); line != null; line = in.readLine()) {
			handleClientRequest(line);
		}
	}

	private String generateServerReponse(String boardJson) {
		String jsonSize = String.valueOf(boardJson.length());
		String serverResponseString = String.format("%1$-" + HEADER_SIZE + "s", jsonSize) + boardJson;

		return serverResponseString;
	}

	private String generateRawBoardReponse() {
		String boardString = BOARD.getRawBoard();
		String boardStringSize = String.valueOf(boardString.length());
		return  String.format("%1$-" + HEADER_SIZE + "s", boardStringSize) + boardString;
	}

	private void handleClientRequest(String requestString) {
		String regex = "(look)|(help)|(bye)|"
				+ "(d -?\\d+ -?\\d+)|(f -?\\d+ -?\\d+)|(df -?\\d+ -?\\d+)|(new -?\\d+ -?\\d+ -?\\d+)";
		if ( ! requestString.matches(regex)) {
			// invalid input
			;
		} else {
			String[] tokens = requestString.split(" ");
			if (tokens[0].equals("new")) {
				int x = Integer.parseInt(tokens[1]);
				int y = Integer.parseInt(tokens[2]);
				int bombs = Integer.parseInt(tokens[3]);

				if (BOARD.newBoard(x, y, bombs)) {
					updateBoardForAllClients();
				} else {
					//failed to get new board
				}
			} else {
				int y = Integer.parseInt(tokens[1]);
				int x = Integer.parseInt(tokens[2]);
				if (tokens[0].equals("d")) {
					// 'dig x y' request
					try {
						if (BOARD.dig(x, y)) {
							BOARD.getNewBoard();
							updateBoardForAllClients();
						} else {
							if (BOARD.checkBoard()) {
								DEFAULT_BOARD_SIZE += 5;
								BOARD.newBoard(DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE, 5);
								updateBoardForAllClients();
							} else {
								updateBoardForAllClients();
							}
						}
					} catch (IllegalArgumentException iae){
						//catch iae
					}

					// TODO Problem 5
				} else if (tokens[0].equals("f")) {
					if (BOARD.toggleFlag(x, y, true)) {
						updateBoardForAllClients();
					} else {
						//already flagged or invalid coords
					}
				} else if (tokens[0].equals("df")) {
					if (BOARD.toggleFlag(x, y, false)) {
						updateBoardForAllClients();
					} else {
						//already dug or invalid coords
					}
				} else {
					//invalid command
				}
			}
		}

	}

	private void updateBoardForAllClients() {
		for (PrintWriter clientOut : clientOuts) {
			String server_reponse = generateRawBoardReponse();
			clientOut.println(server_reponse);
		}
		System.out.println(BOARD.getRawBoard());
	}

	private String convertBoardToJsonString() throws JsonProcessingException{
		return objectMapper.writeValueAsString(BOARD);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			runServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
