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

// Tela de interface gráfica (JFrame) para realizar operações em uma conta bancária, selecionada por CPF do cliente.
public class TelaOperacoes extends JFrame {
    
    // Gerenciadores de Negócio
    private final GerenciadorContas gerenciadorContas; // Referência ao GerenciadorContas para operações bancárias 
    private final GerenciadorClientes gerenciadorClientes; // Referência ao GerenciadorClientes para busca de clientes
    private Conta contaAtual; // Objeto Conta atualmente selecionado
    
    // Componentes de Busca
    private JFormattedTextField txtCpfBusca; // Campo de texto formatado para entrada do CPF
    private JButton btnBuscar; // Botão para buscar uma conta
    private JLabel lblInfoConta; // Exibe as informações da conta encontrada
    
    // Componentes de Operação
    private JFormattedTextField txtValorOperacao; // Campo de texto formatado para entrada do valor da operação
    private JButton btnSaque, btnDeposito, btnVerSaldo, btnRemunera; // Botões para as operações bancárias

    
    // Construtor da tela de operações.
    // Recebe os gerenciadores de negócio como parâmetros para permitir operações em contas e busca de clientes.
    // Inicializa os componentes da interface e configura a janela.
    public TelaOperacoes(GerenciadorContas gco, GerenciadorClientes gcl) {
        this.gerenciadorContas = gco; // Referência ao GerenciadorContas
        this.gerenciadorClientes = gcl; // Referência ao GerenciadorClientes
        initComponents(); // Inicializa os componentes da interface
        setTitle("Sistema Bancário - Operações em Conta"); // Define o título da janela
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Fecha apenas esta janela ao clicar no X
        setSize(500, 350); // Define o tamanho da janela
        setLocationRelativeTo(null); // Centraliza a janela
        desabilitarOperacoes(); // Começa com os botões de operação desabilitados
    }

    // Inicializa e configura os componentes visuais da tela. 
    private void initComponents() {
        setLayout(new BorderLayout(10, 10)); // Layout principal com espaçamento

        // Inicialização dos Componentes
        // Configuração de Campos com Formatação e Máscaras
        try {
            // Máscara de CPF
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##"); // Define a máscara de CPF
            cpfMask.setPlaceholderCharacter('_'); // Caractere de placeholder
            txtCpfBusca = new JFormattedTextField(cpfMask); // Campo de texto formatado para CPF
            txtCpfBusca.setColumns(15); // Define a largura do campo

            // Formatação de Valor (Decimal: #,##0.00)
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); // Define o formato decimal
            decimalFormat.setMinimumFractionDigits(2); // Garante 2 casas decimais
            decimalFormat.setGroupingUsed(false); // Desabilita o ponto de milhar para simplificar a leitura

            NumberFormatter valueFormatter = new NumberFormatter(decimalFormat); // Formatação numérica
            valueFormatter.setValueClass(Double.class); // Define o tipo de valor como Double
            valueFormatter.setAllowsInvalid(false); // Não permite valores inválidos
            valueFormatter.setOverwriteMode(false); // Não substitui caracteres durante a digitação

            txtValorOperacao = new JFormattedTextField(valueFormatter); // Campo de texto formatado para valor
            txtValorOperacao.setColumns(10); // Define a largura do campo

        } catch (java.text.ParseException e) {
            e.printStackTrace();
            // Fallback para campos simples em caso de erro na máscara/formatação
            txtCpfBusca = new JFormattedTextField();
            txtValorOperacao = new JFormattedTextField();
        }

        // Inicialização de JButtons
        btnBuscar = new JButton("Buscar Conta por CPF"); // Botão de busca
        btnSaque = new JButton("Saque"); // Botão de saque
        btnDeposito = new JButton("Depósito"); // Botão de depósito
        btnVerSaldo = new JButton("Ver Saldo"); // Botão de ver saldo
        btnRemunera = new JButton("Remunera"); // Botão de remuneração

        // Montagem do Layout e Listeners
        // --- Painel de Busca (Norte) ---
        JPanel pnlBusca = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Painel com FlowLayout para centralizar
        btnBuscar.addActionListener(e -> buscarConta()); // Adiciona o listener de busca
        pnlBusca.add(new JLabel("CPF do Cliente:")); // Cria label para o campo de CPF
        pnlBusca.add(txtCpfBusca); // Adiciona o campo de CPF ao painel
        pnlBusca.add(btnBuscar); // Adiciona o botão de busca ao painel
        add(pnlBusca, BorderLayout.NORTH); // Adiciona o painel de busca à região norte

        // --- Informações da Conta (Centro) ---
        lblInfoConta = new JLabel("Nenhuma conta selecionada.", SwingConstants.CENTER); // Label centralizado para informações da conta
        lblInfoConta.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0)); // Adiciona padding vertical
        add(lblInfoConta, BorderLayout.CENTER); // Adiciona o label ao centro da janela

        // --- Painel de Operações (Sul) ---
        JPanel pnlOperacoesContainer = new JPanel(new BorderLayout(10, 10)); // Container para organizar os painéis de operação
        pnlOperacoesContainer.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15)); // Padding interno

        // Valor e Botões de Ação
        JPanel pnlAcaoPrincipal = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Painel para ações principais 
        btnSaque.addActionListener(e -> realizarSaque()); // Adiciona o listener de Saque
        btnDeposito.addActionListener(e -> realizarDeposito()); // Adiciona o listener de Depósito
        pnlAcaoPrincipal.add(new JLabel("Valor (R$):")); // Label para o campo de valor
        pnlAcaoPrincipal.add(txtValorOperacao); // Adiciona o campo de valor ao painel
        pnlAcaoPrincipal.add(btnSaque); // Adiciona o botão de saque ao painel
        pnlAcaoPrincipal.add(btnDeposito); // Adiciona o botão de depósito ao painel

        // Outras Operações (Ver Saldo/Remunera)
        JPanel pnlOutrasOperacoes = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Painel para outras operações
        btnVerSaldo.addActionListener(e -> verSaldo()); // Adiciona o listener de Saldo
        btnRemunera.addActionListener(e -> remunerarConta()); // Adiciona o listener de Remuneração
        pnlOutrasOperacoes.add(btnVerSaldo); // Adiciona o botão de ver saldo ao painel
        pnlOutrasOperacoes.add(btnRemunera); // Adiciona o botão de remuneração ao painel

        // Organiza os painéis no Container Sul
        pnlOperacoesContainer.add(pnlAcaoPrincipal, BorderLayout.NORTH); // Adiciona o painel de ações principais na parte superior
        pnlOperacoesContainer.add(pnlOutrasOperacoes, BorderLayout.CENTER); // Adiciona o painel de outras operações na parte central
        add(pnlOperacoesContainer, BorderLayout.SOUTH); // Adiciona o container ao sul da janela
    }
    
    
    //Desabilita os campos e botões de operação da conta atual (usa quando não há conta selecionada)
    private void desabilitarOperacoes() {
        txtValorOperacao.setEnabled(false);
        btnSaque.setEnabled(false);
        btnDeposito.setEnabled(false);
        btnVerSaldo.setEnabled(false);
        btnRemunera.setEnabled(false);
    }
    
    // Habilita os campos e botões de operação.
    private void habilitarOperacoes() {
        txtValorOperacao.setEnabled(true);
        btnSaque.setEnabled(true);
        btnDeposito.setEnabled(true);
        btnVerSaldo.setEnabled(true);
        btnRemunera.setEnabled(true);
    }

    // Busca a conta pelo CPF do cliente e atualiza a interface.
    private void buscarConta() {
        String cpfComMascara = txtCpfBusca.getText(); // Obtém o CPF com máscara
        String cpfLimpo = cpfComMascara.replaceAll("[^0-9]", ""); // Remove a máscara
        txtCpfBusca.setValue(null); // Limpa o campo de busca

        // Validação do CPF
        if (cpfLimpo.length() < 11) { // Verifica se o CPF tem 11 dígitos
            lblInfoConta.setText("<html>CPF inválido. Certifique-se de que o CPF tem 11 dígitos.</html>");
            desabilitarOperacoes(); // Desabilita operações para CPF inválido
            return; // Sai do método
        }

        // Busca o Cliente pelo CPF
        Cliente cliente = gerenciadorClientes.buscarPorCpf(cpfLimpo);

        // Verifica se o Cliente Existe
        if (cliente == null) {
            lblInfoConta.setText("<html>CPF não cadastrado. O cliente não existe no sistema.</html>");
            desabilitarOperacoes(); // Desabilita operações para cliente inexistente
            return; // Sai do método
        }

        // Tenta encontrar a Conta
        contaAtual = gerenciadorContas.buscarContaPorCpfCliente(cpfLimpo); // Busca a conta vinculada ao CPF

        if (contaAtual != null) {
            // Conta encontrada: Exibe informações e habilita operações
            String nomeSimples = contaAtual.getClass().getSimpleName(); // Obtém o nome simples da classe (ContaCorrente/ContaInvestimento)
            lblInfoConta.setText("<html>Conta encontrada: <b>" + nomeSimples + " Nº " + contaAtual.getNumero() + "</b><br>Dono: " + contaAtual.getDono().getNome() + " " + contaAtual.getDono().getSobrenome() + "</html>");
            habilitarOperacoes(); // Habilita os botões de operação
        } else {
            // Cliente existe, mas não tem conta vinculada
            lblInfoConta.setText("<html><center>Nenhuma conta encontrada para o cliente: <b>" + cliente.getNome() + "</b><br>O cliente não possui contas ativas.</center></html>"); 
            desabilitarOperacoes(); // Desabilita operações se não houver conta
        }
    }
    
    
    // Converte o valor do campo de operação para Double.
    // Retorna o valor convertido ou lança NumberFormatException se inválido.
    private double getValorOperacao() throws NumberFormatException {
        String text = txtValorOperacao.getText(); // Obtém o texto do campo de valor

        if (text == null || text.trim().isEmpty()) { // Verifica se está vazio
            throw new NumberFormatException("O campo de valor não pode estar vazio.");
        }

        // Limpeza e conversão: troca vírgula por ponto e remove outros caracteres
        String valorStrLimpo = text.replace(',', '.').replaceAll("[^0-9.]", ""); // Remove caracteres não numéricos, exceto ponto

        if (valorStrLimpo.isEmpty()) { // Verifica se sobrou algo após a limpeza
            return 0.0; // Retorna 0.0 se estiver vazio
        }

        // Tenta fazer o parse para Double
        return Double.parseDouble(valorStrLimpo);
    }
    
    
    // Realiza a operação de saque na conta atual.
    private void realizarSaque() {
        if (contaAtual == null) return; // Verifica se há conta selecionada
        try { // Tenta obter o valor da operação
            double valor = getValorOperacao(); // Obtém o valor do campo de operação

            // Chama a lógica de saque do GerenciadorContas, que chama o polimórfico saca()
            if (gerenciadorContas.sacar(contaAtual, valor)) {
                // Sucesso: feedback e atualização de saldo
                JOptionPane.showMessageDialog(this, "Saque de R$ " + String.format("%.2f", valor) + " realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                verSaldo(); // Exibe o novo saldo
            } else {
                // Falha: O erro é reportado pelo modelo (ContaCorrente/ContaInvestimento)
            }

            txtValorOperacao.setValue(null); // Limpa o campo após a operação

        } catch (NumberFormatException ex) { // Trata valor inválido
            JOptionPane.showMessageDialog(this, "Valor inválido para saque.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtValorOperacao.setValue(null); // Limpa o campo 
        }
    }
    
    // Realiza a operação de depósito na conta atual.
    private void realizarDeposito() {
        if (contaAtual == null) return; // Verifica se há conta selecionada
        try {
            double valor = getValorOperacao(); // Obtém o valor do campo de operação

            // Chama a lógica de depósito (polimórfica)
            if (gerenciadorContas.depositar(contaAtual, valor)) { // Sucesso
                JOptionPane.showMessageDialog(this, "Depósito de R$ " + String.format("%.2f", valor) + " realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                verSaldo(); // Exibe o novo saldo
            }

            txtValorOperacao.setValue(null); // Limpa o campo após a operação

        } catch (NumberFormatException ex) { // Trata valor inválido
            JOptionPane.showMessageDialog(this, "Valor inválido para depósito.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtValorOperacao.setValue(null); // Limpa o campo
        }
    }

    // Exibe o saldo atual da conta em um pop-up
    private void verSaldo() {
        if (contaAtual == null) return; // Verifica se há conta selecionada
        JOptionPane.showMessageDialog(this, "Saldo da Conta Nº " + contaAtual.getNumero() + ": R$ " + String.format("%.2f", contaAtual.getSaldo()), "Saldo Atual", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Realiza a remuneração (juros/rendimento) na conta atual
    private void remunerarConta() {
        if (contaAtual == null) return; // Verifica se há conta selecionada

        double saldoAntes = contaAtual.getSaldo(); // Salva o saldo anterior

        // Remunera a conta (chama o método remunera() da subclasse - polimorfismo)
        gerenciadorContas.remunerar(contaAtual); 

        double saldoDepois = contaAtual.getSaldo(); // Saldo após a remuneração
        
        // Formata o nome da classe para exibição amigável ("Conta Investimento")
        String tipoConta = contaAtual.getClass().getSimpleName(); // Obtém o nome simples da classe
        String tipoContaFormatada = tipoConta.replaceAll("(?<=[a-z])(?=[A-Z])", " ");  // Insere espaço entre palavras maiúsculas/minúsculas

        // Monta a mensagem completa de feedback
        String msg = String.format("<html><b>Remuneração aplicada!</b><br>" +
                                   "Conta: %s Nº %d<br>" +
                                   "Saldo Anterior: R$ %.2f<br>" +
                                   "Novo Saldo: R$ %.2f</html>", 
                                   tipoContaFormatada, contaAtual.getNumero(), saldoAntes, saldoDepois);

        // 4. Exibe a mensagem
        JOptionPane.showMessageDialog(this, msg, "Remuneração Aplicada", JOptionPane.INFORMATION_MESSAGE); 
    }
}