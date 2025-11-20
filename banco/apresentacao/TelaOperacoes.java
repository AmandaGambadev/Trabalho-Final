package banco.apresentacao;

import banco.modelo.Conta;
import banco.negocio.GerenciadorContas;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.*;

public class TelaOperacoes extends JFrame {
    
    private final GerenciadorContas gerenciadorContas;
    private Conta contaAtual;
    
    private JTextField txtCpfBusca;
    private JButton btnBuscar;
    private JLabel lblInfoConta;
    
    private JTextField txtValorOperacao;
    private JButton btnSaque, btnDeposito, btnVerSaldo, btnRemunera;

    public TelaOperacoes(GerenciadorContas gco) {
        this.gerenciadorContas = gco;
        initComponents();
        setTitle("Operações em Conta");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        desabilitarOperacoes();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // --- Painel de Busca (Norte) ---
        JPanel pnlBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtCpfBusca = new JTextField(15);
        btnBuscar = new JButton("Buscar Conta por CPF");
        btnBuscar.addActionListener(e -> buscarConta());
        
        pnlBusca.add(new JLabel("CPF do Cliente:"));
        pnlBusca.add(txtCpfBusca);
        pnlBusca.add(btnBuscar);
        
        add(pnlBusca, BorderLayout.NORTH);
        
        // --- Informações da Conta (Centro - Superior) ---
        lblInfoConta = new JLabel("Nenhuma conta selecionada.", SwingConstants.CENTER);
        add(lblInfoConta, BorderLayout.CENTER);
        
        // --- Painel de Operações (Sul) ---
        JPanel pnlOperacoes = new JPanel(new GridLayout(3, 1, 5, 5));
        
        // Linha 1: Valor e Botões de Operação
        JPanel pnlValorOperacao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtValorOperacao = new JTextField(10);
        btnSaque = new JButton("Saque");
        btnSaque.addActionListener(e -> realizarSaque());
        btnDeposito = new JButton("Depósito");
        btnDeposito.addActionListener(e -> realizarDeposito());
        
        pnlValorOperacao.add(new JLabel("Valor (R$):"));
        pnlValorOperacao.add(txtValorOperacao);
        pnlValorOperacao.add(btnSaque);
        pnlValorOperacao.add(btnDeposito);
        
        // Linha 2: Outras Operações
        JPanel pnlOutrasOperacoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnVerSaldo = new JButton("Ver Saldo");
        btnVerSaldo.addActionListener(e -> verSaldo());
        btnRemunera = new JButton("Remunera");
        btnRemunera.addActionListener(e -> remunerarConta());
        
        pnlOutrasOperacoes.add(btnVerSaldo);
        pnlOutrasOperacoes.add(btnRemunera);
        
        pnlOperacoes.add(pnlValorOperacao);
        pnlOperacoes.add(pnlOutrasOperacoes);
        
        add(pnlOperacoes, BorderLayout.SOUTH);
    }
    
    private void desabilitarOperacoes() {
        txtValorOperacao.setEnabled(false);
        btnSaque.setEnabled(false);
        btnDeposito.setEnabled(false);
        btnVerSaldo.setEnabled(false);
        btnRemunera.setEnabled(false);
    }
    
    private void habilitarOperacoes() {
        txtValorOperacao.setEnabled(true);
        btnSaque.setEnabled(true);
        btnDeposito.setEnabled(true);
        btnVerSaldo.setEnabled(true);
        btnRemunera.setEnabled(true);
    }

    // Regra 3.a: Buscar Conta
    private void buscarConta() {
        String cpf = txtCpfBusca.getText();
        contaAtual = gerenciadorContas.buscarContaPorCpfCliente(cpf);
        
        if (contaAtual != null) {
            lblInfoConta.setText("<html>Conta encontrada: <b>" + contaAtual.getClass().getSimpleName() + " Nº " + contaAtual.getNumero() + "</b><br>Dono: " + contaAtual.getDono().getNome() + " " + contaAtual.getDono().getSobrenome() + "</html>");
            habilitarOperacoes();
        } else {
            lblInfoConta.setText("Nenhuma conta encontrada para o CPF: " + cpf);
            desabilitarOperacoes();
        }
    }
    
    private double getValorOperacao() throws NumberFormatException {
        String valorStr = txtValorOperacao.getText().replace(",", "."); // Aceita vírgula como separador decimal
        if (valorStr.isEmpty()) {
            throw new NumberFormatException("O campo de valor não pode estar vazio.");
        }
        return Double.parseDouble(valorStr);
    }
    
    // Regra 3.b.i: Saque
    private void realizarSaque() {
        if (contaAtual == null) return;
        try {
            double valor = getValorOperacao();
            
            if (gerenciadorContas.sacar(contaAtual, valor)) {
                JOptionPane.showMessageDialog(this, "Saque de R$ " + String.format("%.2f", valor) + " realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                verSaldo();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido para saque.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Regra 3.b.ii: Depósito
    private void realizarDeposito() {
        if (contaAtual == null) return;
        try {
            double valor = getValorOperacao();
            
            if (gerenciadorContas.depositar(contaAtual, valor)) {
                JOptionPane.showMessageDialog(this, "Depósito de R$ " + String.format("%.2f", valor) + " realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                verSaldo();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido para depósito.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Regra 3.b.iii: Ver Saldo
    private void verSaldo() {
        if (contaAtual == null) return;
        JOptionPane.showMessageDialog(this, 
                "Saldo da Conta Nº " + contaAtual.getNumero() + ": R$ " + String.format("%.2f", contaAtual.getSaldo()), 
                "Saldo Atual", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Regra 3.b.iv: Remunera
    private void remunerarConta() {
        if (contaAtual == null) return;
        
        gerenciadorContas.remunerar(contaAtual);
        verSaldo();
    }
}
