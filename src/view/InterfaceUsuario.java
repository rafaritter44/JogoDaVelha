package view;

import java.util.Scanner;
import model.Partida;

public class InterfaceUsuario {
    
    private Partida partida;
    private Scanner leitor;
    
    public InterfaceUsuario() {
        partida = new Partida("Jogador1", "Jogador2");
        leitor = new Scanner(System.in);
    }
    
    public void imprimeTabuleiro() {
        System.out.print(partida);
    }
    
}
