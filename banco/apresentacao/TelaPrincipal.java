package banco.apresentacao;

import banco.negocio.GerenciadorClientes;
import banco.negocio.GerenciadorContas;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class TelaPrincipal extends JFrame {
    
    // Instâncias dos gerenciadores (não são mais Singletons)
    private final GerenciadorClientes gerenciadorClientes = new GerenciadorClientes();
    private final GerenciadorContas gerenciadorContas = new GerenciadorContas(gerenciadorClientes);

    public TelaPrincipal() {
        initComponents();
        setTitle("Sistema Bancário - Menu Principal (Versão Simples)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Centraliza a tela
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuClientes = new JMenu("Clientes");
        JMenuItem itemManterClientes = new JMenuItem("Manter Clientes");
        itemManterClientes.addActionListener(this::abrirTelaClientes);
        menuClientes.add(itemManterClientes);
        
        JMenu menuContas = new JMenu("Contas");
        JMenuItem itemVincularConta = new JMenuItem("Vincular Conta");
        itemVincularConta.addActionListener(this::abrirTelaVincularConta);
        JMenuItem itemOperacoes = new JMenuItem("Operações em Conta");
        itemOperacoes.addActionListener(this::abrirTelaOperacoes);
        menuContas.add(itemVincularConta);
        menuContas.add(itemOperacoes);
        
        menuBar.add(menuClientes);
        menuBar.add(menuContas);
        
        setJMenuBar(menuBar);
        
        // Adiciona um painel simples para preencher o centro
        add(new JLabel("Selecione uma opção no menu acima.", SwingConstants.CENTER));
    }

    private void abrirTelaClientes(ActionEvent e) {
        // Passa a instância do gerenciador para a nova tela
        new TelaClientes(gerenciadorClientes, gerenciadorContas).setVisible(true);
    }

    private void abrirTelaVincularConta(ActionEvent e) {
        // Passa a instância dos gerenciadores para a nova tela
        new TelaVincularConta(gerenciadorClientes, gerenciadorContas).setVisible(true);
    }

    private void abrirTelaOperacoes(ActionEvent e) {
        // Passa a instância do gerenciador para a nova tela
        new TelaOperacoes(gerenciadorContas).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}
