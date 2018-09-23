package cliente;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import servidor.ServidorTCP;

public class ClienteTCP {
	
	private static final String HOST = "127.0.0.1";
	private static int porta;
	private static String respostaDoServidor;
	private static final String INFORME_SALA = "Informe o nome da sala: ";
	private static final String ERRO_RESPOSTA = "Erro na resposta do servidor";
	private static final String OPCAO_INVALIDA = "Opção inválida";
	
	public static void main(String args[]) throws UnknownHostException, IOException {
		try(BufferedReader inputDoUsuario = new BufferedReader(new InputStreamReader(System.in))) {
			System.out.print("----------CLIENTE----------\nInforme a porta: ");
			try {
				porta = Integer.parseInt(inputDoUsuario.readLine());
			} catch(NumberFormatException excecao) {
				System.out.println("Porta inválida");
				return;
			}
			menu(inputDoUsuario);
			String opcao;
			do {
				do {
					System.out.print("Continuar jogando? (S/N): ");
					switch(opcao = inputDoUsuario.readLine()) {
					case "S":
					case "s":
						menu(inputDoUsuario);
						break;
					case "N":
					case "n":
						System.out.println("Obrigado por jogar!");
						break;
						default:
							System.out.println(OPCAO_INVALIDA);
					}
				} while(!opcao.equalsIgnoreCase("s") && !opcao.equalsIgnoreCase("n"));
			} while(!opcao.equalsIgnoreCase("n"));
		}
	}
	
	private static void menu(BufferedReader inputDoUsuario) throws UnknownHostException, IOException {
		try(Socket cliente = new Socket(HOST, porta);
			DataOutputStream outputProServidor = new DataOutputStream(cliente.getOutputStream());
			BufferedReader inputDoServidor = new BufferedReader(new InputStreamReader(cliente.getInputStream()))) {
			System.out.print("CONEXÃO ESTABELECIDA\n"
					+ "(1) – Criar sala\n"
					+ "(2) – Entrar em sala\n"
					+ "Informe a opção: ");
			switch(inputDoUsuario.readLine()) {
			case "1":
				System.out.print(INFORME_SALA);
				outputProServidor.writeBytes(ServidorTCP.CRIAR + inputDoUsuario.readLine() + "\n");
				switch(respostaDoServidor = inputDoServidor.readLine()) {
				case ServidorTCP.SALA_CRIADA:
					System.out.println(respostaDoServidor + "\nAguarde outro jogador entrar...");
					for(int i=0; i<7; i++)
							System.out.println(inputDoServidor.readLine());
					do {
						outputProServidor.writeBytes(inputDoUsuario.readLine() + "\n");
						System.out.println("Aguarde...");
						for(int i=0; i<7; i++)
							System.out.println(respostaDoServidor = inputDoServidor.readLine());
					} while(!respostaDoServidor.equals(ServidorTCP.FIM_DE_JOGO));
					System.out.println(inputDoServidor.readLine());
					break;
				case ServidorTCP.SALA_JA_EXISTE:
					System.out.println(respostaDoServidor);
					break;
					default:
						System.out.println(ERRO_RESPOSTA);
				}
				break;
			case "2":
				System.out.print(INFORME_SALA);
				outputProServidor.writeBytes(ServidorTCP.ENTRAR + inputDoUsuario.readLine() + "\n");
				switch(respostaDoServidor = inputDoServidor.readLine()) {
				case ServidorTCP.ENTROU_SALA:
					System.out.println(respostaDoServidor + "\nAguarde a jogada do oponente...");
					for(int i=0; i<7; i++)
						System.out.println(inputDoServidor.readLine());
					do {
						outputProServidor.writeBytes(inputDoUsuario.readLine() + "\n");
						System.out.println("Aguarde...");
						for(int i=0; i<7; i++)
							System.out.println(respostaDoServidor = inputDoServidor.readLine());
					} while(!respostaDoServidor.equals(ServidorTCP.FIM_DE_JOGO));
					System.out.println(inputDoServidor.readLine());
					break;
				case ServidorTCP.SALA_NAO_EXISTE:
				case ServidorTCP.SALA_CHEIA:
					System.out.println(respostaDoServidor);
					break;
					default:
						System.out.println(ERRO_RESPOSTA);
				}
				break;
				default:
					System.out.println(OPCAO_INVALIDA);
					outputProServidor.writeBytes(OPCAO_INVALIDA + "\n");
			}
		}
		System.out.println("CONEXÃO ENCERRADA");
    }
	
}
