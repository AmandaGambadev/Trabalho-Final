package banco.apresentacao;

import banco.negocio.GerenciadorClientes;
import banco.negocio.GerenciadorContas;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;

// Tela de interface gráfica (JFrame) responsavel pelo Meni principal principal do Sistema Bancário.
// Responsável por inicializar os gerenciadores de negócio e oferecer acesso às telas de manutenção de clientes e operações de conta.
public class TelaPrincipal extends JFrame {
    
    // Instâncias dos Gerenciadores de Negócio
    private final GerenciadorClientes gerenciadorClientes; // Gerenciador de Clientes
    private final GerenciadorContas gerenciadorContas; // Gerenciador de Contas

    // Construtor da tela principal. Inicializa os gerenciadores e a interface 
    public TelaPrincipal() {
        // Inicialização dos gerenciadores na ordem correta:
        this.gerenciadorContas = new GerenciadorContas(); // Inicializa o Gerenciador de Contas
        this.gerenciadorClientes = new GerenciadorClientes(gerenciadorContas); // Inicializa o Gerenciador de Clientes (que precisa da ref. do contas)
        
        // Inicializa as contas de teste, garantindo que os clientes já existam
        gerenciadorContas.inicializarContasDeTeste(gerenciadorClientes); 
        
        initComponents(); // Configura os componentes visuais da tela
        setTitle("Sistema Bancário - Menu Principal"); // Define o título
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Encerra a aplicação ao fechar
        setSize(500, 300); // Define o tamanho
        setLocationRelativeTo(null); // Centraliza a tela
    }

    // Inicializa e configura todos os componentes visuais da tela principal
    private void initComponents() {
        setLayout(new BorderLayout(15, 15)); // Layout principal com espaçamento

        // --- Painel Central de Botões ---
        JPanel pnlBotoes = new JPanel(new GridLayout(1, 2, 20, 0)); // Divide em 1 linha e 2 colunas
        pnlBotoes.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // Adiciona padding

        // Painel de Clientes (Coluna Esquerda)
        JPanel pnlClientes = new JPanel(new GridLayout(3, 1, 10, 10)); // 3 linhas, 1 coluna
        pnlClientes.setBorder(BorderFactory.createTitledBorder("Clientes")); // Título da seção

        JButton btnManterClientes = new JButton("Manter Clientes"); // Botão para abrir a tela de clientes
        btnManterClientes.addActionListener(this::abrirTelaClientes); // Listener para abrir a tela

        pnlClientes.add(btnManterClientes); // Adiciona o botão de manter clientes ao painel
        pnlClientes.add(new JLabel("")); // Espaço vazio
        pnlClientes.add(new JLabel("")); // Espaço vazio

        // Painel de Contas (Coluna Direita)
        JPanel pnlContas = new JPanel(new GridLayout(3, 1, 10, 10)); // 3 linhas, 1 coluna
        pnlContas.setBorder(BorderFactory.createTitledBorder("Contas")); // Título da seção

        JButton btnVincularConta = new JButton("Vincular Conta"); // Botão para abrir a tela de vinculação de conta
        btnVincularConta.addActionListener(this::abrirTelaVincularConta); // Listener para abrir a tela

        JButton btnOperacoes = new JButton("Operações em Conta"); // Botão para abrir a tela de operações em conta
        btnOperacoes.addActionListener(this::abrirTelaOperacoes); // Listener para abrir a tela

        pnlContas.add(btnVincularConta); // Adiciona o botão de vincular conta ao painel
        pnlContas.add(btnOperacoes); // Adiciona o botão de operações em conta ao painel
        pnlContas.add(new JLabel("")); // Espaço vazio

        // Adiciona os painéis ao painel de botões central
        pnlBotoes.add(pnlClientes); // Adiciona o painel de clientes ao painel central
        pnlBotoes.add(pnlContas); // Adiciona o painel de contas ao painel central

        // Adiciona o painel de botões ao centro da janela principal
        add(pnlBotoes, BorderLayout.CENTER);

        // Adiciona um painel simples para preencher o topo da janela e melhorar o layout
        add(new JLabel(" ", SwingConstants.CENTER), BorderLayout.NORTH);
    }

    // Abre a tela Manter Clientes
    private void abrirTelaClientes(ActionEvent e) {
        // Passa as instâncias dos gerenciadores
        new TelaClientes(gerenciadorClientes, gerenciadorContas).setVisible(true);
    }

    // Abre a tela de vinculação de Conta a Cliente.
    private void abrirTelaVincularConta(ActionEvent e) {
        // Passa as instâncias dos gerenciadores
        new TelaVincularConta(gerenciadorClientes, gerenciadorContas).setVisible(true);
    }

    // Abre a tela de Operações em Conta.
    private void abrirTelaOperacoes(ActionEvent e) {
        // Passa a instância do gerenciador de contas
        new TelaOperacoes(gerenciadorContas, gerenciadorClientes).setVisible(true);
    }

    // Ponto de entrada (Main) da aplicação.
    // Inicia a interface gráfica do sistema bancário
    public static void main(String[] args) {
        // Garante que a UI seja executada na thread de despacho de eventos (EDT)
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}