package banco.apresentacao;

import banco.modelo.Cliente;
import banco.modelo.ContaCorrente;
import banco.modelo.ContaInvestimento;
import banco.negocio.GerenciadorClientes;
import banco.negocio.GerenciadorContas;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;

public class TelaVincularConta extends JFrame {
    
    private final GerenciadorClientes gerenciadorClientes;
    private final GerenciadorContas gerenciadorContas;
    
    private JComboBox<Cliente> cmbClientes;
    private JComboBox<String> cmbTipoConta;
    private JPanel pnlCamposConta;
    private CardLayout cardLayout;
    
    // Campos para Conta Corrente
    private JTextField txtCC_DepInicial, txtCC_Limite;
    
    // Campos para Conta Investimento
    private JTextField txtCI_MontanteMinimo, txtCI_DepMinimo, txtCI_DepInicial;
    
    private JButton btnVincular;

    public TelaVincularConta(GerenciadorClientes gc, GerenciadorContas gco) {
        this.gerenciadorClientes = gc;
        this.gerenciadorContas = gco;
        initComponents();
        carregarClientes();
        setTitle("Vincular Conta a Cliente");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // --- Painel Superior (Seleção de Cliente e Tipo) ---
        JPanel pnlSelecao = new JPanel(new GridLayout(2, 2, 5, 5));
        
        cmbClientes = new JComboBox<>();
        cmbTipoConta = new JComboBox<>(new String[]{"Conta Corrente", "Conta Investimento"});
        cmbTipoConta.addActionListener(e -> atualizarCamposConta());
        
        pnlSelecao.add(new JLabel("Cliente:"));
        pnlSelecao.add(cmbClientes);
        pnlSelecao.add(new JLabel("Tipo de Conta:"));
        pnlSelecao.add(cmbTipoConta);
        
        add(pnlSelecao, BorderLayout.NORTH);
        
        // --- Painel Central (Campos Dinâmicos) ---
        cardLayout = new CardLayout();
        pnlCamposConta = new JPanel(cardLayout);
        
        pnlCamposConta.add(criarPainelContaCorrente(), "Conta Corrente");
        pnlCamposConta.add(criarPainelContaInvestimento(), "Conta Investimento");
        
        add(pnlCamposConta, BorderLayout.CENTER);
        
        // --- Painel Inferior (Botão) ---
        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnVincular = new JButton("Vincular Conta");
        btnVincular.addActionListener(e -> vincularConta());
        pnlBotoes.add(btnVincular);
        
        add(pnlBotoes, BorderLayout.SOUTH);
        
        // Inicializa a visualização correta
        atualizarCamposConta();
    }
    
    private void carregarClientes() {
        cmbClientes.removeAllItems();
        List<Cliente> clientes = gerenciadorClientes.listarTodos();
        for (Cliente cliente : clientes) {
            cmbClientes.addItem(cliente);
        }
    }
    
    private JPanel criarPainelContaCorrente() {
        JPanel pnl = new JPanel(new GridLayout(3, 2, 5, 5));
        txtCC_DepInicial = new JTextField(10);
        txtCC_Limite = new JTextField(10);
        
        pnl.add(new JLabel("Depósito Inicial (R$):"));
        pnl.add(txtCC_DepInicial);
        pnl.add(new JLabel("Limite (R$):"));
        pnl.add(txtCC_Limite);
        pnl.add(new JLabel("")); // Espaço vazio
        pnl.add(new JLabel("")); // Espaço vazio
        
        return pnl;
    }
    
    private JPanel criarPainelContaInvestimento() {
        JPanel pnl = new JPanel(new GridLayout(3, 2, 5, 5));
        txtCI_MontanteMinimo = new JTextField(10);
        txtCI_DepMinimo = new JTextField(10);
        txtCI_DepInicial = new JTextField(10);
        
        pnl.add(new JLabel("Montante Mínimo (R$):"));
        pnl.add(txtCI_MontanteMinimo);
        pnl.add(new JLabel("Depósito Mínimo (R$):"));
        pnl.add(txtCI_DepMinimo);
        pnl.add(new JLabel("Depósito Inicial (R$):"));
        pnl.add(txtCI_DepInicial);
        
        return pnl;
    }
    
    private void atualizarCamposConta() {
        String tipo = (String) cmbTipoConta.getSelectedItem();
        cardLayout.show(pnlCamposConta, tipo);
    }
    
    private void vincularConta() {
        Cliente clienteSelecionado = (Cliente) cmbClientes.getSelectedItem();
        String tipoConta = (String) cmbTipoConta.getSelectedItem();
        
        if (clienteSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verifica se o cliente já tem uma conta (Regra 2: "um cliente pode estar vinculado à apenas um tipo de conta")
        if (gerenciadorContas.buscarContaPorCpfCliente(clienteSelecionado.getCpf()) != null) {
            JOptionPane.showMessageDialog(this, "O cliente já possui uma conta vinculada.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if ("Conta Corrente".equals(tipoConta)) {
                double depInicial = Double.parseDouble(txtCC_DepInicial.getText());
                double limite = Double.parseDouble(txtCC_Limite.getText());
                
                ContaCorrente novaConta = new ContaCorrente(clienteSelecionado, depInicial, limite);
                gerenciadorContas.adicionar(novaConta);
                JOptionPane.showMessageDialog(this, "Conta Corrente Nº " + novaConta.getNumero() + " criada e vinculada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCamposCC();
                
            } else if ("Conta Investimento".equals(tipoConta)) {
                double montanteMinimo = Double.parseDouble(txtCI_MontanteMinimo.getText());
                double depMinimo = Double.parseDouble(txtCI_DepMinimo.getText());
                double depInicial = Double.parseDouble(txtCI_DepInicial.getText());
                
                ContaInvestimento novaConta = new ContaInvestimento(clienteSelecionado, depInicial, montanteMinimo, depMinimo);
                
                if (novaConta.getSaldo() == 0 && depInicial > 0) {
                     JOptionPane.showMessageDialog(this, "Conta Investimento criada, mas o Depósito Inicial falhou. Verifique se o valor é maior ou igual ao Depósito Mínimo.", "Atenção", JOptionPane.WARNING_MESSAGE);
                } else {
                    gerenciadorContas.adicionar(novaConta);
                    JOptionPane.showMessageDialog(this, "Conta Investimento Nº " + novaConta.getNumero() + " criada e vinculada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparCamposCI();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valores de depósito/limite/montante devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limparCamposCC() {
        txtCC_DepInicial.setText("");
        txtCC_Limite.setText("");
    }
    
    private void limparCamposCI() {
        txtCI_MontanteMinimo.setText("");
        txtCI_DepMinimo.setText("");
        txtCI_DepInicial.setText("");
    }
}
