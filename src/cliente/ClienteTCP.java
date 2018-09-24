package cliente;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import servidor.ServidorTCP;

/*
 * Classe que representa um cliente numa conexão TCP
 */
public class ClienteTCP {
	
	private static final String HOST = "127.0.0.1"; //Host padrão
	private static int porta; //Número da porta
	private static String respostaDoServidor; //Guarda a mensagem enviada pelo servidor
	
	//Strings padrões
	private static final String INFORME_SALA = "Informe o nome da sala: ";
	private static final String ERRO_RESPOSTA = "Erro na resposta do servidor";
	private static final String OPCAO_INVALIDA = "Opção inválida";
	
	/*
	 * Método principal do cliente
	 * Solicita que o cliente informe o número da porta, e caso seja inválido encerra o programa
	 * Em seguida, chama o menu principal, e, ao final de sua execução, pergunta ao cliente se ele
	 * deseja continuar jogando: Em caso positivo chama novamente o menu principal, e segue esse fluxo
	 * até que o cliente informe que não deseja mais jogar; Em caso negativo, encerra o programa
	 */
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
	
	/*
	 * Estabelece a conexão com o servidor, e abre o menu principal do cliente, perguntando se ele deseja criar
	 * uma sala ou entrar em uma que já exista; Caso resolva criar uma nova sala (e não exista nenhuma outra sala
	 * com aquele nome, o servidor solicita que o cliente aguarde a entrada de outro cliente na sala para que o
	 * jogo possa começar, entrando num loop de troca de mensagens entre cliente e servidor até o final do jogo
	 * Caso o cliente resolva entrar em uma sala existente (e a sala exista mesmo), o jogo finalmente começa, e
	 * o mesmo loop de troca de mensagens entre cliente e servidor se mantém até o fim da partida
	 * Ao final da execução do método, a conexão com o servidor é finalmente encerrada
	 */
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
