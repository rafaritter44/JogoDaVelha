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
	public static final String SALA_JA_EXISTE = "Já existe uma sala com esse nome";
	public static final String FIM_DE_JOGO = "Fim de jogo";
	public static final String ENTROU_SALA = "Você entrou na sala";
	public static final String SALA_NAO_EXISTE = "Essa sala não existe";
	public static final String CRIAR = "CRIAR=";
	public static final String ENTRAR = "ENTRAR=";
	public static final String SALA_CHEIA = "Essa sala está cheia";
	public static final String JOGADA_INVALIDA = "Jogada inválida";
	
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
        System.out.print("Informe a porta: ");
        try {
        	servidor = new ServerSocket(Integer.parseInt(leitor.nextLine()));
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
    		BufferedReader inputDoCliente = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
    		DataOutputStream outProCliente = new DataOutputStream(cliente.getOutputStream());
    		String mensagem = inputDoCliente.readLine();
    		if(mensagem.startsWith(CRIAR)) {
    			String nomeDaSala = mensagem.substring(6);
    			if(salas.containsKey(nomeDaSala)) {
    				outProCliente.writeBytes(SALA_JA_EXISTE);
    			} else {
    				salas.put(nomeDaSala, new ServidorTCP(cliente));
    				outProCliente.writeBytes(SALA_CRIADA);
    			}
    		} else if(mensagem.startsWith(ENTRAR)) {
    			String nomeDaSala = mensagem.substring(7);
    			if(salas.containsKey(nomeDaSala)) {
    				ServidorTCP sala = salas.get(nomeDaSala);
    				if(sala.salaCheia()) {
    					outProCliente.writeBytes(SALA_CHEIA);
    				} else {
    					sala.setJogadorBolinha(cliente);
    					Thread partida = new Thread(sala);
    					partida.start();
    				}
    			} else {
    				outProCliente.writeBytes(SALA_NAO_EXISTE);
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
				BufferedReader inputDoBolinha = new BufferedReader(new InputStreamReader(jogadorXis.getInputStream()));
		    	DataOutputStream outputProBolinha = new DataOutputStream(jogadorXis.getOutputStream())) {
			Partida partida = new Partida();
			@SuppressWarnings("resource") BufferedReader inputDoProximo = inputDoXis;
			@SuppressWarnings("resource") DataOutputStream outputProProximo = outputProXis;
			Optional<String> vencedor;
			do {
				outputProProximo.writeBytes(partida.enumeracaoDoTabuleiro() + "\n" +
						partida.toString() + "\nInforme a posição desejada: ");
				boolean jogadaValida;
				do {
					if(!(jogadaValida = partida.jogar(inputDoProximo.readLine())))
						outputProProximo.writeBytes(JOGADA_INVALIDA);
				} while(!jogadaValida);
				if(!(vencedor = partida.vencedor()).isPresent()) {
					outputProProximo.writeBytes(partida.toString() +
							"\nAguarde a jogada do adversário...");
				}
				if(inputDoProximo == inputDoXis) {
					inputDoProximo = inputDoBolinha;
					outputProProximo = outputProBolinha;
				} else {
					inputDoProximo = inputDoXis;
					outputProProximo = outputProXis;
				}
			} while(!vencedor.isPresent());
			outputProXis.writeBytes(FIM_DE_JOGO);
			outputProBolinha.writeBytes(FIM_DE_JOGO);
			String resultado = "VENCEDOR: " + vencedor.get();
			outputProXis.writeBytes(resultado);
			outputProBolinha.writeBytes(resultado);
		} catch(IOException excecao) {
			System.out.println(excecao.getMessage());
		}
	}
}