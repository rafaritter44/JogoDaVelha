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

import model.Partida;

public class ServidorTCP implements Runnable {
	
	private Socket jogadorXis;
	private Socket jogadorBolinha;
	public static final String SALA_CRIADA = "Sala criada com sucesso";
	public static final String SALA_JA_EXISTE = "Ja existe uma sala com esse nome";
	public static final String FIM_DE_JOGO = "Fim de jogo";
	public static final String ENTROU_SALA = "Voce entrou na sala";
	public static final String SALA_NAO_EXISTE = "Essa sala nao existe";
	public static final String CRIAR = "CRIAR=";
	public static final String ENTRAR = "ENTRAR=";
	public static final String SALA_CHEIA = "Essa sala esta cheia";
	public static final String JOGADA_INVALIDA = "Jogada invalida! Por favor, informe uma jogada valida: ";
	
	private ServidorTCP(Socket jogadorXis) {
		this.jogadorXis = jogadorXis;
	}
	
	private void setJogadorBolinha(Socket jogadorBolinha) {
		this.jogadorBolinha = jogadorBolinha;
	}
	
	private boolean salaCheia() {
		return jogadorBolinha != null;
	}
	
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
				exibicaoDoTabuleiro = partida.enumeracaoDoTabuleiro() + "\n" + partida.toString();
				if(inputDoProximo == inputDoXis) {
					inputDoProximo = inputDoBolinha;
					outputProProximo = outputProBolinha;
				} else {
					inputDoProximo = inputDoXis;
					outputProProximo = outputProXis;
				}
				vencedor = partida.vencedor();
			} while(!vencedor.isPresent());
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