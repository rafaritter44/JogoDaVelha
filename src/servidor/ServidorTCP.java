package servidor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import model.Partida;

public class ServidorTCP {
	
	private static Map<String, Partida> salas = new HashMap<>();
	public static final String SALA_CRIADA = "Sala criada com sucesso";
	public static final String SALA_JA_EXISTE = "Já existe uma sala com esse nome";
	public static final String FIM_DE_JOGO = "Fim de jogo";
	public static final String ENTROU_SALA = "Você entrou na sala";
	public static final String SALA_NAO_EXISTE = "Essa sala não existe";
	
    public static void main(String[] args) throws IOException {
        Scanner leitor = new Scanner(System.in);
        System.out.print("Informe a porta: ");
        try {
        	rodar(Integer.parseInt(leitor.nextLine()));
        } catch(NumberFormatException excecao) {
        	System.out.println("Porta inválida");
        } finally {
        	leitor.close();
        }
    }
    
    private static void rodar(int porta) throws IOException {
    	String clientSentence;
        String capitalizedSentence;
        ServerSocket servidor = new ServerSocket(porta);
        while(true) {
        	Socket cliente = servidor.accept();
        	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
        	DataOutputStream outToClient = new DataOutputStream(cliente.getOutputStream());
        	clientSentence = inFromClient.readLine();
        	System.out.println("Received: " + clientSentence);
        	capitalizedSentence = clientSentence.toUpperCase() + 'n';
        	outToClient.writeBytes(capitalizedSentence);
        }
    }
}