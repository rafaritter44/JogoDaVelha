package servidor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import service.Partida;

/*
 * Classe que representa o lado do servidor numa conexão TCP
 * Para que várias partidas possam ser jogadas simultaneamente, essa classe implementa threads
 */
public class ServidorTCP implements Runnable {
	
	//Clientes que representam o jogador xis e bolinha de cada partida em andamento
	private Socket jogadorXis;
	private Socket jogadorBolinha;
	
	//Strings padrões
	public static final String SALA_CRIADA = "Sala criada com sucesso";
	public static final String SALA_JA_EXISTE = "Ja existe uma sala com esse nome";
	public static final String FIM_DE_JOGO = "Fim de jogo";
	public static final String ENTROU_SALA = "Voce entrou na sala";
	public static final String SALA_NAO_EXISTE = "Essa sala nao existe";
	public static final String CRIAR = "CRIAR=";
	public static final String ENTRAR = "ENTRAR=";
	public static final String SALA_CHEIA = "Essa sala esta cheia";
	public static final String JOGADA_INVALIDA = "Jogada invalida! Por favor, informe uma jogada valida: ";
	
	/*
	 * Construtor que cria uma nova instância paralela do servidor, responsável por tratar a partida criada
	 * pelo jogador (passado por parâmetro) que criou a sala, sendo por padrão o jogador (xis) que começa
	 */
	private ServidorTCP(Socket jogadorXis) {
		this.jogadorXis = jogadorXis;
	}
	
	/*
	 * Método que configura o cliente passado por parâmetro como o jogador que controla o elemento bolinha
	 */
	private void setJogadorBolinha(Socket jogadorBolinha) {
		this.jogadorBolinha = jogadorBolinha;
	}
	
	/*
	 * Retorna verdadeiro caso ambos os jogadores já tenham entrado na sala (o jogador xis obrigatoriamente
	 * já entrou, pois sua entrada foi definida no método construtor)
	 */
	private boolean salaCheia() {
		return jogadorBolinha != null;
	}
	
	/*
	 * Método principal do servidor
	 * Solicita que seja informada o número da porta que o servidor deverá escutar, e, em seguida, caso seja
	 * uma porta válida, chama o método "tratamentoPrincipal"
	 */
    public static void main(String[] args) throws IOException {
    	ServerSocket servidor = null;
        Scanner leitor = new Scanner(System.in);
        System.out.print("----------SERVIDOR----------\nInforme a porta: ");
        try {
        	servidor = new ServerSocket(Integer.parseInt(leitor.nextLine()));
        	System.out.println("Porta " + servidor.getLocalPort() + " aberta!");
        	tratamentoPrincipal(servidor);
        } catch(NumberFormatException excecao) {
        	System.out.println("Porta inválida");
        } finally {
        	if(servidor != null)
        		servidor.close();
        	leitor.close();
        }
    }
    
    /*
     * Fica escutando a porta definida pelo método principal e inicia uma conexão com cada cliente que tente
     * se conectar com este servidor (loop infinito de aceitação de clientes); Assim que se conecta com um
     * cliente, recebe sua mensagem inicial, que determina se ele deseja criar ou entrar em uma sala; Caso
     * ele queira criar, e ainda não exista nenhuma sala com o nome informado, cria a sala e a aloca, junto
     * com o cliente, numa estrutura de salas criadas; Caso ele queira entrar numa sala pré-existente (e essa
     * sala realmente exista e esteja livre), cria uma nova thread com o cliente que está entrando e com aquele
     * que criou a sala inicialmente, e começa a rodar a thread; Essa thread representa uma partida executando
     * simulaneamente com várias outras e com o tratamento de conexões e comunicações com outros clientes 
     */
    private static void tratamentoPrincipal(ServerSocket servidor) throws IOException {
    	Map<String, ServidorTCP> salas = new HashMap<>();
    	while(true) {
    		Socket cliente = servidor.accept();
    		System.out.println("Conexão realizada com o cliente " + cliente.getInetAddress().getHostAddress());
    		BufferedReader inputDoCliente = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
    		DataOutputStream outputProCliente = new DataOutputStream(cliente.getOutputStream());
    		String mensagem = inputDoCliente.readLine();
    		if(mensagem.startsWith(CRIAR)) {
    			String nomeDaSala = mensagem.substring(CRIAR.length());
    			if(salas.containsKey(nomeDaSala)) {
    				outputProCliente.writeBytes(SALA_JA_EXISTE + "\n");
    			} else {
    				salas.put(nomeDaSala, new ServidorTCP(cliente));
    				outputProCliente.writeBytes(SALA_CRIADA + "\n");
    				System.out.println(SALA_CRIADA + " -- nome = \"" + nomeDaSala
    						+ "\" / criador = " + cliente.getInetAddress().getHostAddress());
    			}
    		} else if(mensagem.startsWith(ENTRAR)) {
    			String nomeDaSala = mensagem.substring(ENTRAR.length());
    			if(salas.containsKey(nomeDaSala)) {
    				ServidorTCP sala = salas.get(nomeDaSala);
    				if(sala.salaCheia()) {
    					outputProCliente.writeBytes(SALA_CHEIA + "\n");
    				} else {
    					outputProCliente.writeBytes(ENTROU_SALA + "\n");
    					System.out.println(cliente.getInetAddress().getHostAddress() +
    							" entrou na sala \"" + nomeDaSala + "\"");
    					sala.setJogadorBolinha(cliente);
    					Thread partida = new Thread(sala);
    					partida.start();
    				}
    			} else {
    				outputProCliente.writeBytes(SALA_NAO_EXISTE + "\n");
    			}    			
    		} else {
    			System.out.println("Mensagem inválida do cliente");
    		}
    	}
    }

    /*
     * Trecho de código que pode rodar paralelamente com os demais, e que representa uma partida entre dois jogadores
     * Esse método tem acesso a ambos os clientes que estão jogando na sala, definidos como atributos da instância da
     * classe; O método começa enviando para o jogador xis (que criou a sala) uma mensagem contendo o tabuleiro inicial
     * e indicando que ele deve informar em qual espaço deseja realizar sua jogada; Em seguida, recebe uma mensagem do
     * cliente com a jogada desejada, e verifica se ela é válida ou inválida; Caso seja inválida, envia outra mensagem
     * pedindo que repita, e continua pedindo até que o cliente envie uma jogada válida; Quando o cliente envia uma
     * jogada válida, o método troca o jogador com o qual se está comunicando e manda/recebe a mesma sequência de
     * mensagens, seguindo esse fluxo até que alguém vença ou que empate; Ao fim, envia mensagens para ambos os jogadores,
     * exibindo o estado final do tabuleiro e indicando o resultado da partida (quem venceu, ou se houve empate)
     */
	@Override
	public void run() {
		try(BufferedReader inputDoXis = new BufferedReader(new InputStreamReader(jogadorXis.getInputStream()));
				DataOutputStream outputProXis = new DataOutputStream(jogadorXis.getOutputStream());
				BufferedReader inputDoBolinha = new BufferedReader(new InputStreamReader(jogadorBolinha.getInputStream()));
		    	DataOutputStream outputProBolinha = new DataOutputStream(jogadorBolinha.getOutputStream())) {
			Partida partida = new Partida();
			@SuppressWarnings("resource") BufferedReader inputDoProximo = inputDoXis;
			@SuppressWarnings("resource") DataOutputStream outputProProximo = outputProXis;
			Optional<String> vencedor;
			String exibicaoDoTabuleiro;
			do {
				exibicaoDoTabuleiro = partida.enumeracaoDoTabuleiro() + "\n" + partida.toString();
				outputProProximo.writeBytes(exibicaoDoTabuleiro + "Informe a jogada: \n");
				boolean jogadaValida;
				do {
					if(!(jogadaValida = partida.jogar(inputDoProximo.readLine())))
						outputProProximo.writeBytes(exibicaoDoTabuleiro + JOGADA_INVALIDA + "\n");
				} while(!jogadaValida);
				if(inputDoProximo == inputDoXis) {
					inputDoProximo = inputDoBolinha;
					outputProProximo = outputProBolinha;
				} else {
					inputDoProximo = inputDoXis;
					outputProProximo = outputProXis;
				}
				vencedor = partida.vencedor();
			} while(!vencedor.isPresent());
			exibicaoDoTabuleiro = partida.enumeracaoDoTabuleiro() + "\n" + partida.toString();
			String fimDeJogo = exibicaoDoTabuleiro + FIM_DE_JOGO + "\n";
			outputProXis.writeBytes(fimDeJogo);
			outputProBolinha.writeBytes(fimDeJogo);
			String resultado = "VENCEDOR: " + vencedor.get() + "\n";
			outputProXis.writeBytes(resultado);
			outputProBolinha.writeBytes(resultado);
		} catch(IOException excecao) {
			System.out.println(excecao.getMessage());
		}
	}
}