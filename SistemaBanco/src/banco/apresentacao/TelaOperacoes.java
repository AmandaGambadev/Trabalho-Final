package banco.apresentacao;

import banco.modelo.Conta;
import banco.modelo.Cliente;
import banco.negocio.GerenciadorContas;
import banco.negocio.GerenciadorClientes;

import java.text.DecimalFormat;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.*;

/**
 * Tela de interface gráfica (JFrame) para realizar operações (Saque, Depósito, Saldo, Remuneração)
 * em uma conta bancária, selecionada por CPF do cliente.
 */
public class TelaOperacoes extends JFrame {
    
    // Gerenciadores de Negócio
    private final GerenciadorContas gerenciadorContas;
    private final GerenciadorClientes gerenciadorClientes;
    private Conta contaAtual; // Objeto Conta atualmente selecionado
    
    // Componentes de Busca
    private JFormattedTextField txtCpfBusca; 
    private JButton btnBuscar;
    private JLabel lblInfoConta; // Exibe as informações da conta encontrada
    
    // Componentes de Operação
    private JFormattedTextField txtValorOperacao;
    private JButton btnSaque, btnDeposito, btnVerSaldo, btnRemunera;

    /**
     * Construtor da tela de operações.
     * @param gco GerenciadorContas (para realizar as operações na conta).
     * @param gcl GerenciadorClientes (para buscar o cliente pelo CPF).
     */
    public TelaOperacoes(GerenciadorContas gco, GerenciadorClientes gcl) {
        this.gerenciadorContas = gco;
        this.gerenciadorClientes = gcl;
        initComponents();
        setTitle("Sistema Bancário - Operações em Conta");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null); // Centraliza a janela
        desabilitarOperacoes(); // Começa com os botões de operação desabilitados
    }

    /**
     * Inicializa e configura todos os componentes visuais (UI) da tela.
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // 1. Inicialização dos Componentes
        
        // a) Configuração de Campos com Formatação e Máscaras
        try {
            // MÁSCARA DE CPF
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
            cpfMask.setPlaceholderCharacter('_');
            txtCpfBusca = new JFormattedTextField(cpfMask);
            txtCpfBusca.setColumns(15);

            // FORMATAÇÃO DE VALOR (Decimal: #,##0.00)
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            decimalFormat.setMinimumFractionDigits(2); // Garante 2 casas decimais
            decimalFormat.setGroupingUsed(false); // Desabilita o ponto de milhar para simplificar a leitura

            NumberFormatter valueFormatter = new NumberFormatter(decimalFormat);
            valueFormatter.setValueClass(Double.class);
            valueFormatter.setAllowsInvalid(false);
            valueFormatter.setOverwriteMode(false); // Não substitui caracteres durante a digitação

            txtValorOperacao = new JFormattedTextField(valueFormatter);
            txtValorOperacao.setColumns(10);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
            // Fallback para campos simples em caso de erro na máscara/formatação
            txtCpfBusca = new JFormattedTextField();
            txtValorOperacao = new JFormattedTextField();
        }

        // b) Inicialização de JButtons
        btnBuscar = new JButton("Buscar Conta por CPF");
        btnSaque = new JButton("Saque");
        btnDeposito = new JButton("Depósito");
        btnVerSaldo = new JButton("Ver Saldo");
        btnRemunera = new JButton("Remunera");

        // 2. Montagem do Layout e Listeners

        // --- Painel de Busca (Norte) ---
        JPanel pnlBusca = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnBuscar.addActionListener(e -> buscarConta()); // Adiciona o listener de busca
        pnlBusca.add(new JLabel("CPF do Cliente:"));
        pnlBusca.add(txtCpfBusca);
        pnlBusca.add(btnBuscar);
        add(pnlBusca, BorderLayout.NORTH);

        // --- Informações da Conta (Centro) ---
        lblInfoConta = new JLabel("Nenhuma conta selecionada.", SwingConstants.CENTER);
        lblInfoConta.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0)); 
        add(lblInfoConta, BorderLayout.CENTER);

        // --- Painel de Operações (Sul) ---
        JPanel pnlOperacoesContainer = new JPanel(new BorderLayout(10, 10)); 
        pnlOperacoesContainer.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15)); 

        // Linha 1: Valor e Botões de Ação (Saque/Depósito)
        JPanel pnlAcaoPrincipal = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        btnSaque.addActionListener(e -> realizarSaque()); // Adiciona o listener de Saque
        btnDeposito.addActionListener(e -> realizarDeposito()); // Adiciona o listener de Depósito
        pnlAcaoPrincipal.add(new JLabel("Valor (R$):"));
        pnlAcaoPrincipal.add(txtValorOperacao);
        pnlAcaoPrincipal.add(btnSaque);
        pnlAcaoPrincipal.add(btnDeposito);

        // Linha 2: Outras Operações (Ver Saldo/Remunera)
        JPanel pnlOutrasOperacoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnVerSaldo.addActionListener(e -> verSaldo()); // Adiciona o listener de Saldo
        btnRemunera.addActionListener(e -> remunerarConta()); // Adiciona o listener de Remuneração
        pnlOutrasOperacoes.add(btnVerSaldo);
        pnlOutrasOperacoes.add(btnRemunera);

        // Organiza os painéis no Container Sul
        pnlOperacoesContainer.add(pnlAcaoPrincipal, BorderLayout.NORTH); 
        pnlOperacoesContainer.add(pnlOutrasOperacoes, BorderLayout.CENTER); 
        add(pnlOperacoesContainer, BorderLayout.SOUTH);
    }
    
    /**
     * Desabilita os campos e botões de operação.
     */
    private void desabilitarOperacoes() {
        txtValorOperacao.setEnabled(false);
        btnSaque.setEnabled(false);
        btnDeposito.setEnabled(false);
        btnVerSaldo.setEnabled(false);
        btnRemunera.setEnabled(false);
    }
    
    /**
     * Habilita os campos e botões de operação.
     */
    private void habilitarOperacoes() {
        txtValorOperacao.setEnabled(true);
        btnSaque.setEnabled(true);
        btnDeposito.setEnabled(true);
        btnVerSaldo.setEnabled(true);
        btnRemunera.setEnabled(true);
    }

    /**
     * Busca a conta pelo CPF do cliente e atualiza a interface.
     */
    private void buscarConta() {
        String cpfComMascara = txtCpfBusca.getText();
        String cpfLimpo = cpfComMascara.replaceAll("[^0-9]", ""); // Remove a máscara
        txtCpfBusca.setValue(null); // Limpa o campo de busca

        // 1. Validação do CPF
        if (cpfLimpo.length() < 11) {
            lblInfoConta.setText("<html>CPF inválido. Certifique-se de que o CPF tem 11 dígitos.</html>");
            desabilitarOperacoes();
            return; 
        }

        // Busca o Cliente primeiro
        Cliente cliente = gerenciadorClientes.buscarPorCpf(cpfLimpo); 

        // 2. Verifica se o Cliente Existe
        if (cliente == null) {
            lblInfoConta.setText("<html>CPF não cadastrado. O cliente não existe no sistema.</html>");
            desabilitarOperacoes();
            return;
        }

        // 3. Tenta encontrar a Conta
        contaAtual = gerenciadorContas.buscarContaPorCpfCliente(cpfLimpo);

        if (contaAtual != null) {
            // Conta encontrada: Exibe informações e habilita operações
            String nomeSimples = contaAtual.getClass().getSimpleName();
            lblInfoConta.setText("<html>Conta encontrada: <b>" + nomeSimples + " Nº " + contaAtual.getNumero() + "</b><br>Dono: " + contaAtual.getDono().getNome() + " " + contaAtual.getDono().getSobrenome() + "</html>");
            habilitarOperacoes();
        } else {
            // Cliente existe, mas não tem conta vinculada
            lblInfoConta.setText("<html><center>Nenhuma conta encontrada para o cliente: <b>" + cliente.getNome() + "</b><br>O cliente não possui contas ativas.</center></html>"); 
            desabilitarOperacoes();
        }
    }
    
    /**
     * Converte o valor do campo de operação para Double.
     * @return O valor da operação como Double.
     * @throws NumberFormatException Se o valor for inválido ou vazio.
     */
    private double getValorOperacao() throws NumberFormatException {
        String text = txtValorOperacao.getText();

        if (text == null || text.trim().isEmpty()) {
            throw new NumberFormatException("O campo de valor não pode estar vazio.");
        }

        // Limpeza e conversão: troca vírgula por ponto e remove outros caracteres
        String valorStrLimpo = text.replace(',', '.').replaceAll("[^0-9.]", ""); 

        if (valorStrLimpo.isEmpty()) {
            return 0.0;
        }

        // Tenta fazer o parse para Double
        return Double.parseDouble(valorStrLimpo);
    }
    
    /**
     * Realiza a operação de saque na conta atual.
     */
    private void realizarSaque() {
        if (contaAtual == null) return;
        try {
            double valor = getValorOperacao();

            // Chama a lógica de saque do GerenciadorContas, que chama o polimórfico saca()
            if (gerenciadorContas.sacar(contaAtual, valor)) {
                // Sucesso: feedback e atualização de saldo
                JOptionPane.showMessageDialog(this, "Saque de R$ " + String.format("%.2f", valor) + " realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                verSaldo(); // Exibe o novo saldo
            } else {
                // Falha: O erro foi reportado pelo modelo (ContaCorrente/ContaInvestimento)
            }

            txtValorOperacao.setValue(null); // Limpa o campo após a operação

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido para saque.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtValorOperacao.setValue(null); 
        }
    }
    
    /**
     * Realiza a operação de depósito na conta atual.
     */
    private void realizarDeposito() {
        if (contaAtual == null) return;
        try {
            double valor = getValorOperacao();

            // Chama a lógica de depósito (polimórfica)
            if (gerenciadorContas.depositar(contaAtual, valor)) {
                JOptionPane.showMessageDialog(this, "Depósito de R$ " + String.format("%.2f", valor) + " realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                verSaldo(); // Exibe o novo saldo
            }

            txtValorOperacao.setValue(null); // Limpa o campo após a operação

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido para depósito.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtValorOperacao.setValue(null); 
        }
    }

    /**
     * Exibe o saldo atual da conta em um pop-up.
     */
    private void verSaldo() {
        if (contaAtual == null) return;
        JOptionPane.showMessageDialog(this, 
                "Saldo da Conta Nº " + contaAtual.getNumero() + ": R$ " + String.format("%.2f", contaAtual.getSaldo()), 
                "Saldo Atual", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Realiza a remuneração (juros/rendimento) na conta atual.
     */
    private void remunerarConta() {
        if (contaAtual == null) return;

        double saldoAntes = contaAtual.getSaldo(); // Salva o saldo anterior

        // 1. Remunera a conta (chama o método remunera() da subclasse - polimorfismo)
        gerenciadorContas.remunerar(contaAtual); 

        double saldoDepois = contaAtual.getSaldo(); // Saldo após a remuneração
        
        // 2. Formata o nome da classe para exibição amigável ("Conta Investimento")
        String tipoConta = contaAtual.getClass().getSimpleName();
        String tipoContaFormatada = tipoConta.replaceAll("(?<=[a-z])(?=[A-Z])", " "); 

        // 3. Monta a mensagem completa de feedback
        String msg = String.format("<html><b>Remuneração aplicada!</b><br>" +
                                   "Conta: %s Nº %d<br>" +
                                   "Saldo Anterior: R$ %.2f<br>" +
                                   "Novo Saldo: R$ %.2f</html>", 
                                   tipoContaFormatada, contaAtual.getNumero(), saldoAntes, saldoDepois);

        // 4. Exibe a mensagem
        JOptionPane.showMessageDialog(this, msg, "Remuneração Aplicada", JOptionPane.INFORMATION_MESSAGE); 
    }
}