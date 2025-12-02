package banco.apresentacao;

import banco.modelo.Cliente;
import banco.modelo.ContaCorrente;
import banco.modelo.ContaInvestimento;
import banco.negocio.GerenciadorClientes;
import banco.negocio.GerenciadorContas;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.SwingUtilities;
import java.text.DecimalFormat;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;

// Tela de interface gráfica (JFrame) responsável por Vincular uma nova Conta (Corrente ou Investimento) a um Cliente existente.
// Utiliza CardLayout para alternar campos específicos da conta.
// Responsável por coletar dados, validar e criar a conta vinculada ao cliente selecionado.
public class TelaVincularConta extends JFrame {
    
    // Gerenciadores de Negócio
    private final GerenciadorClientes gerenciadorClientes; // Referência ao Gerenciador de Clientes para listar clientes
    private final GerenciadorContas gerenciadorContas; // Referência ao Gerenciador de Contas para adicionar a nova conta
    
    // Componentes de Seleção
    private JComboBox<Cliente> cmbClientes; // Combobox para selecionar o cliente
    private JComboBox<String> cmbTipoConta; // Combobox para selecionar o tipo de conta
    
    // Componentes de Layout Dinâmico
    private JPanel pnlCamposConta; // Painel que contém os campos específicos da conta
    private CardLayout cardLayout; // Gerenciador de layout que alterna entre painéis
    
    // Campos para Conta Corrente
    private JTextField txtCC_DepInicial, txtCC_Limite; // Campos de depósito inicial e limite
    
    // Campos para Conta Investimento
    private JTextField txtCI_MontanteMinimo, txtCI_DepMinimo, txtCI_DepInicial; // Campos de montante mínimo, depósito mínimo e depósito inicial
    
    // Botão de Ação
    private JButton btnVincular; // Botão para vincular a conta ao cliente
    
    private boolean isUpdating = false; // Flag para evitar loops de DocumentListener

    // Construtor da tela de vinculação de contas
    public TelaVincularConta(GerenciadorClientes gc, GerenciadorContas gco) {
        this.gerenciadorClientes = gc; // Inicializa o gerenciador de clientes
        this.gerenciadorContas = gco; // Inicializa o gerenciador de contas
        initComponents(); // Configura os componentes visuais
        carregarClientes(); // Carrega os clientes na combobox ao iniciar
        getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Adiciona padding
        setTitle("Sistema Bancário - Vincular Conta a Cliente"); // Define o título da janela
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Fecha apenas esta janela ao clicar no X
        setSize(650, 350); // Define o tamanho da janela
        setLocationRelativeTo(null); // Centraliza a janela
    }

    // Inicializa e configura todos os componentes visuais da tela.
    private void initComponents() {
        setLayout(new BorderLayout(10, 10)); // Layout principal
        
        // --- Painel Superior (Seleção de Cliente e Tipo) ---
        JPanel pnlSelecao = new JPanel(new GridLayout(2, 2, 5, 5)); // 2 linhas, 2 colunas 
        
        cmbClientes = new JComboBox<>(); // Combobox para selecionar o cliente
        cmbTipoConta = new JComboBox<>(new String[]{"Conta Corrente", "Conta Investimento"}); // Combobox para selecionar o tipo de conta
        
        // Adiciona listener para alternar os campos ao mudar o tipo de conta
        cmbTipoConta.addActionListener(e -> atualizarCamposConta()); // Atualiza os campos exibidos conforme o tipo selecionado
        
        pnlSelecao.add(new JLabel("Cliente:")); // Label para seleção de cliente
        pnlSelecao.add(cmbClientes); // Adiciona a combobox de clientes
        pnlSelecao.add(new JLabel("Tipo de Conta:")); // Label para seleção do tipo de conta
        pnlSelecao.add(cmbTipoConta); // Adiciona a combobox de tipo de conta
        
        add(pnlSelecao, BorderLayout.NORTH); // Adiciona o painel superior
        
        // --- Painel Central (Campos Dinâmicos) ---
        cardLayout = new CardLayout(); // Inicializa o CardLayout
        pnlCamposConta = new JPanel(cardLayout); // Aplica o CardLayout ao painel central
        
        // Adiciona os painéis específicos ao CardLayout com seus respectivos nomes
        pnlCamposConta.add(criarPainelContaCorrente(), "Conta Corrente");
        pnlCamposConta.add(criarPainelContaInvestimento(), "Conta Investimento");
        
        add(pnlCamposConta, BorderLayout.CENTER); // Adiciona ao centro
        
        // --- Painel Inferior (Botão) ---
        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Layout centralizado para o botão
        
        btnVincular = new JButton("Vincular Conta"); // Botão para vincular a conta
        btnVincular.addActionListener(e -> vincularConta()); // Listener para a ação de vincular
        pnlBotoes.add(btnVincular);
        
        add(pnlBotoes, BorderLayout.SOUTH); // Adiciona o painel inferior
        
        // Inicializa a visualização correta dos campos
        atualizarCamposConta();
    }
    
    // Formata o valor digitado no modo centavos para casas maiores
    private void formatarEntradaValor(JTextField field) {
        if (isUpdating) return; // Bloqueia se a atualização for interna
        
        // Limita o número máximo de dígitos para 14 (para evitar o estouro de Long)
        final int MAX_DIGITOS = 14;
        
        // Remove tudo que não for dígito
        String texto = field.getText().replaceAll("[^0-9]", "");

        // Lógica de Limitação de Input
        if (texto.length() > MAX_DIGITOS) {
            // Trunca o texto
            texto = texto.substring(0, MAX_DIGITOS);
        }
        
        // Lógica de limpeza e validação
        if (texto.isEmpty()) {
            // Lógica para limpar o campo
            if (!field.getText().isEmpty()) {
                isUpdating = true; // Ativa flag antes de mudar o texto
                field.setText("");
                isUpdating = false;
            }
            return;
        }
        
        //Conversão e formatação segura devido a limitação
        try {
            // Pega o valor como um número inteiro (centavos)
            long valorInteiro = Long.parseLong(texto);
            // Converte para decimal (o formato 0.00)
            double valorDecimal = valorInteiro / 100.0;

            // Formata o valor para a exibição no campo (Ex: 1,23)
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String valorFormatado = df.format(valorDecimal);

            // Atualiza o campo com o valor formatado
            if (!field.getText().equals(valorFormatado)) {
                isUpdating = true; // Liga o flag antes da atualização
                
                // SetText é direto e a flag impede o re-trigger imediato
                SwingUtilities.invokeLater(() -> {
                    field.setText(valorFormatado);
                    isUpdating = false; // Desliga o flag APÓS a operação
                });
            }
        } catch (NumberFormatException e) {
            isUpdating = true;
            field.setText(""); // Limpa o campo em caso de falha crítica
            isUpdating = false; // Desliga o flag APÓS a operação
        }
    }

    // Carrega a lista de clientes do gerenciador para a JComboBox de clientes.
    private void carregarClientes() {
        cmbClientes.removeAllItems(); // Limpa itens existentes
        List<Cliente> clientes = gerenciadorClientes.listarTodos(); // Obtém a lista de clientes do gerenciador
        for (Cliente cliente : clientes) { // Itera sobre cada cliente
            cmbClientes.addItem(cliente); // Adiciona cada cliente
        }
    }
    
    // Cria e retorna o painel com os campos específicos para Conta Corrente.
    // Retorna JPanel para Conta Corrente
    private JPanel criarPainelContaCorrente() {
        JPanel pnl = new JPanel(new GridLayout(3, 2, 5, 5)); // Layout de 3 linhas, 2 colunas
        txtCC_DepInicial = new JTextField(10); // Campo para Depósito Inicial
        txtCC_Limite = new JTextField(10); // Define a largura do campo
        
        // Adiciona Listener formatarEntradaValor ao Depósito Inicial
        txtCC_DepInicial.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { formatarEntradaValor(txtCC_DepInicial); }
            public void removeUpdate(DocumentEvent e) { formatarEntradaValor(txtCC_DepInicial); }
            public void insertUpdate(DocumentEvent e) { formatarEntradaValor(txtCC_DepInicial); }
        });
        
        // Adiciona Listener formatarEntradaValor ao Limite
        txtCC_Limite.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { formatarEntradaValor(txtCC_Limite); }
            public void removeUpdate(DocumentEvent e) { formatarEntradaValor(txtCC_Limite); }
            public void insertUpdate(DocumentEvent e) { formatarEntradaValor(txtCC_Limite); }
        });
        
        pnl.add(new JLabel("Depósito Inicial (R$):")); // Label para Depósito Inicial
        pnl.add(txtCC_DepInicial); // Adiciona o campo ao painel
        pnl.add(new JLabel("Limite (R$):")); // Label para Limite
        pnl.add(txtCC_Limite); // Adiciona o campo ao painel
        pnl.add(new JLabel("")); // Espaço vazio para alinhamento
        pnl.add(new JLabel("")); // Espaço vazio para alinhamento
        
        return pnl; // Retorna o painel configurado
    }

    // Cria e retorna o painel com os campos específicos para Conta Investimento.
    // Retorna JPanel para Conta Investimento
    private JPanel criarPainelContaInvestimento() {
        JPanel pnl = new JPanel(new GridLayout(3, 2, 5, 5)); // Layout de 3 linhas, 2 colunas
        txtCI_MontanteMinimo = new JTextField(10); // Campo para Montante Mínimo
        txtCI_DepMinimo = new JTextField(10); // Campo para Depósito Mínimo
        txtCI_DepInicial = new JTextField(10); // Campo para Depósito Inicial 
        
        // Adiciona Listener formatarEntradaValor ao Montante minimo
        txtCI_MontanteMinimo.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { formatarEntradaValor(txtCI_MontanteMinimo); }
            public void removeUpdate(DocumentEvent e) { formatarEntradaValor(txtCI_MontanteMinimo); }
            public void insertUpdate(DocumentEvent e) { formatarEntradaValor(txtCI_MontanteMinimo); }
        });

        // Adiciona Listener formatarEntradaValor ao Deposito minimo
        txtCI_DepMinimo.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { formatarEntradaValor(txtCI_DepMinimo); }
            public void removeUpdate(DocumentEvent e) { formatarEntradaValor(txtCI_DepMinimo); }
            public void insertUpdate(DocumentEvent e) { formatarEntradaValor(txtCI_DepMinimo); }
        });

        // Adiciona Listener formatarEntradaValor ao Deposito inicial
        txtCI_DepInicial.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { formatarEntradaValor(txtCI_DepInicial); }
            public void removeUpdate(DocumentEvent e) { formatarEntradaValor(txtCI_DepInicial); }
            public void insertUpdate(DocumentEvent e) { formatarEntradaValor(txtCI_DepInicial); }
        });
        
        pnl.add(new JLabel("Montante Mínimo (R$):")); // Label para Montante Mínimo
        pnl.add(txtCI_MontanteMinimo); // Adiciona o campo ao painel
        pnl.add(new JLabel("Depósito Mínimo (R$):")); // Label para Depósito Mínimo
        pnl.add(txtCI_DepMinimo); // Adiciona o campo ao painel
        pnl.add(new JLabel("Depósito Inicial (R$):")); // Label para Depósito Inicial
        pnl.add(txtCI_DepInicial); // Adiciona o campo ao painel
        
        return pnl; // Retorna o painel configurado
    }
    
    // Alterna a visualização do painel central de campos de acordo com o tipo de conta selecionado.
    private void atualizarCamposConta() {
        String tipo = (String) cmbTipoConta.getSelectedItem(); // Obtém o tipo selecionado no combo box
        cardLayout.show(pnlCamposConta, tipo); // Exibe painel correspondente ao tipo
    }
    
    // Converte o texto formatado do JTextField para um valor Double.
    // Retorna o valor como double ou lança NumberFormatException se inválido
    private double getDoubleFromTextField(JTextField field) throws NumberFormatException {
        String text = field.getText(); // Obtém o texto do campo

        if (text == null || text.trim().isEmpty()) { // Verifica se o campo está vazio
            return 0.0;
        }

        // Remove pontos de milhar (Ex: 1.000,00 -> 1000,00)
        String valorSemMilhar = text.replace(".", ""); 
        // Substitui a virgula por ponto (Ex: 1000,00 -> 1000.00)
        String valorFormatado = valorSemMilhar.replace(',', '.'); 
        // Remove quaisquer caracteres não numéricos restantes que não sejam o separador decimal
        String valorLimpo = valorFormatado.replaceAll("[^0-9.]", "");

        if (valorLimpo.isEmpty()) { // Se não restar nenhum número válido
            return 0.0;
        }

        // Converte para Double
        return Double.parseDouble(valorLimpo); // Retorna o valor convertido
    }
    
    // Realiza a lógica de criação e vinculação da conta ao cliente selecionado.
    private void vincularConta() {
        // Obtém os objetos selecionados: Cliente e Tipo de Conta.
        Cliente clienteSelecionado = (Cliente) cmbClientes.getSelectedItem();
        String tipoConta = (String) cmbTipoConta.getSelectedItem();
        
        // Verifica se um cliente foi realmente selecionado na ComboBox.
        if (clienteSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verifica se o cliente já tem uma conta (Regra de Negócio: apenas um tipo de conta por cliente)
        if (gerenciadorContas.buscarContaPorCpfCliente(clienteSelecionado.getCpf()) != null) {
            JOptionPane.showMessageDialog(this, "O cliente já possui uma conta vinculada.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Lógica para Conta Corrente
            if ("Conta Corrente".equals(tipoConta)) {
                // Converte os valores dos campos de texto formatados para double.
                // Obtém os valores dos campos
                double depInicial = getDoubleFromTextField(txtCC_DepInicial);
                double limite = getDoubleFromTextField(txtCC_Limite);
                
                ContaCorrente novaConta = new ContaCorrente(clienteSelecionado, depInicial, limite); // Cria e adiciona a Conta Corrente
                gerenciadorContas.adicionar(novaConta); // Adiciona a conta recém-criada ao GerenciadorContas.

                // Exibe mensagem de sucesso para o usuário.
                JOptionPane.showMessageDialog(this, "Conta Corrente Nº " + novaConta.getNumero() + " criada e vinculada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCamposCC(); // Limpa os campos da Conta Corrente
                
            // Lógica para Conta Investimento
            } else if ("Conta Investimento".equals(tipoConta)) {
                // Converte os valores dos campos de texto formatados para double.
                // Obtém os valores dos campos
                double montanteMinimo = getDoubleFromTextField(txtCI_MontanteMinimo);
                double depMinimo = getDoubleFromTextField(txtCI_DepMinimo);
                double depInicialCI = getDoubleFromTextField(txtCI_DepInicial);
                
                // Cria a Conta Investimento
                ContaInvestimento novaConta = new ContaInvestimento(clienteSelecionado, depInicialCI, montanteMinimo, depMinimo);
                
                // Verifica se o saldo é zero APESAR do depósito inicial > 0. Isso indica que o valor do depósito inicial não atingiu o Depósito Mínimo da Conta Investimento.
                // Validação de Depósito Mínimo da Conta Investimento
                if (novaConta.getSaldo() == 0 && depInicialCI > 0) {
                    // Exibe alerta de bloqueio por depósito inicial insuficiente.
                     JOptionPane.showMessageDialog(this, "Criação de Conta Investimento CANCELADA. O Depósito Inicial de R$ " + String.format("%.2f", depInicialCI) + " é menor que o Depósito Mínimo de R$ " + String.format("%.2f", depMinimo) + ".", "Criação Bloqueada", JOptionPane.WARNING_MESSAGE);
                } else {
                    // Adiciona a conta (se a criação foi bem-sucedida ou se o depósito inicial foi 0).
                    gerenciadorContas.adicionar(novaConta); 
                    // Exibe mensagem de sucesso.
                    JOptionPane.showMessageDialog(this, "Conta Investimento Nº " + novaConta.getNumero() + " criada e vinculada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparCamposCI(); // Limpa os campos da Conta Investimento
                }
            }
        } catch (NumberFormatException ex) {
            // Captura erro se o texto nos campos não puder ser convertido para número
            JOptionPane.showMessageDialog(this, "Valores de depósito/limite/montante devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Limpa os campos de input específicos da Conta Corrente.
    private void limparCamposCC() {
        txtCC_DepInicial.setText(""); // Limpa o campo de Depósito Inicial
        txtCC_Limite.setText(""); // Limpa o campo de Limite
    }
    
    // Limpa os campos de input específicos da Conta Investimento.
    private void limparCamposCI() {
        txtCI_MontanteMinimo.setText(""); // Limpa o Montante Mínimo
        txtCI_DepMinimo.setText(""); // Limpa o Depósito Mínimo
        txtCI_DepInicial.setText(""); // Limpa o Depósito Inicial
    }
}