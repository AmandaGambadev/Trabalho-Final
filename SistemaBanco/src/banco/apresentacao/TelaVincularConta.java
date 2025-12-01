package banco.apresentacao;

import banco.modelo.Cliente;
import banco.modelo.ContaCorrente;
import banco.modelo.ContaInvestimento;
import banco.negocio.GerenciadorClientes;
import banco.negocio.GerenciadorContas;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;

/**
 * Tela de interface gráfica (JFrame) responsável por Vincular uma nova Conta
 * (Corrente ou Investimento) a um Cliente existente.
 * Utiliza CardLayout para alternar campos específicos da conta.
 */
public class TelaVincularConta extends JFrame {
    
    // Gerenciadores de Negócio
    private final GerenciadorClientes gerenciadorClientes;
    private final GerenciadorContas gerenciadorContas;
    
    // Componentes de Seleção
    private JComboBox<Cliente> cmbClientes; // Combobox para selecionar o cliente
    private JComboBox<String> cmbTipoConta; // Combobox para selecionar o tipo de conta
    
    // Componentes de Layout Dinâmico
    private JPanel pnlCamposConta; // Painel que contém os campos específicos da conta
    private CardLayout cardLayout; // Gerenciador de layout que alterna entre painéis
    
    // Campos para Conta Corrente
    private JFormattedTextField txtCC_DepInicial, txtCC_Limite;   
    
    // Campos para Conta Investimento
    private JFormattedTextField txtCI_MontanteMinimo, txtCI_DepMinimo, txtCI_DepInicial;
    
    // Botão de Ação
    private JButton btnVincular;

    /**
     * Construtor da tela de vinculação de contas.
     * @param gc GerenciadorClientes (para listar clientes).
     * @param gco GerenciadorContas (para adicionar a nova conta).
     */
    public TelaVincularConta(GerenciadorClientes gc, GerenciadorContas gco) {
        this.gerenciadorClientes = gc;
        this.gerenciadorContas = gco;
        initComponents();
        carregarClientes(); // Carrega os clientes na combobox ao iniciar
        getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Adiciona padding
        setTitle("Sistema Bancário - Vincular Conta a Cliente");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(650, 350);
        setLocationRelativeTo(null); // Centraliza a janela
    }

    /**
     * Inicializa e configura todos os componentes visuais (UI) da tela.
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10)); // Layout principal
        
        // --- Painel Superior (Seleção de Cliente e Tipo) ---
        JPanel pnlSelecao = new JPanel(new GridLayout(2, 2, 5, 5));
        
        cmbClientes = new JComboBox<>();
        cmbTipoConta = new JComboBox<>(new String[]{"Conta Corrente", "Conta Investimento"});
        
        // Adiciona listener para alternar os campos ao mudar o tipo de conta
        cmbTipoConta.addActionListener(e -> atualizarCamposConta()); 
        
        pnlSelecao.add(new JLabel("Cliente:"));
        pnlSelecao.add(cmbClientes);
        pnlSelecao.add(new JLabel("Tipo de Conta:"));
        pnlSelecao.add(cmbTipoConta);
        
        add(pnlSelecao, BorderLayout.NORTH); // Adiciona ao topo
        
        // --- Painel Central (Campos Dinâmicos) ---
        cardLayout = new CardLayout(); // Inicializa o CardLayout
        pnlCamposConta = new JPanel(cardLayout); // Aplica o CardLayout ao painel central
        
        // Adiciona os painéis específicos ao CardLayout com seus respectivos nomes
        pnlCamposConta.add(criarPainelContaCorrente(), "Conta Corrente");
        pnlCamposConta.add(criarPainelContaInvestimento(), "Conta Investimento");
        
        add(pnlCamposConta, BorderLayout.CENTER); // Adiciona ao centro
        
        setupCurrencyFormatters(); // Configura os formatadores de moeda para os campos
        
        // --- Painel Inferior (Botão) ---
        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        btnVincular = new JButton("Vincular Conta");
        btnVincular.addActionListener(e -> vincularConta()); // Listener para a ação de vincular
        pnlBotoes.add(btnVincular);
        
        add(pnlBotoes, BorderLayout.SOUTH); // Adiciona na parte inferior
        
        // Inicializa a visualização correta dos campos
        atualizarCamposConta();
    }
    
    /**
     * Configura e aplica o formatador de moeda/decimal para todos os JFormattedTextFields.
     */
    private void setupCurrencyFormatters() {
        // Formato para exibição (R$ 1.000,00) - Não usado para input, mas boa prática
        NumberFormat currency = NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));
        currency.setMaximumFractionDigits(2);
        NumberFormatter currencyFormatter = new NumberFormatter(currency);
        currencyFormatter.setAllowsInvalid(false);
        currencyFormatter.setOverwriteMode(true);

        // Formato simples para entrada (ex: 1000,00) que será lido como Double
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        NumberFormatter simpleFormatter = new NumberFormatter(decimalFormat);
        simpleFormatter.setValueClass(Double.class); // Define a classe do valor como Double
        simpleFormatter.setAllowsInvalid(false);

        // Factory para aplicar o formatador (uso de NumberFormatter para input de valores)
        DefaultFormatterFactory factory = new DefaultFormatterFactory(simpleFormatter, simpleFormatter, simpleFormatter);

        // Aplica o formatador a todos os campos de valor
        txtCC_DepInicial.setFormatterFactory(factory);
        txtCC_Limite.setFormatterFactory(factory);
        txtCI_MontanteMinimo.setFormatterFactory(factory);
        txtCI_DepMinimo.setFormatterFactory(factory);
        txtCI_DepInicial.setFormatterFactory(factory);
    }

    /**
     * Carrega a lista de clientes do gerenciador para a JComboBox de clientes.
     */
    private void carregarClientes() {
        cmbClientes.removeAllItems(); // Limpa itens existentes
        List<Cliente> clientes = gerenciadorClientes.listarTodos();
        for (Cliente cliente : clientes) {
            cmbClientes.addItem(cliente); // Adiciona cada cliente
        }
    }
    
    /**
     * Cria e retorna o painel com os campos específicos para Conta Corrente.
     * @return JPanel para Conta Corrente.
     */
    private JPanel criarPainelContaCorrente() {
        JPanel pnl = new JPanel(new GridLayout(3, 2, 5, 5));
        txtCC_DepInicial = new JFormattedTextField(); 
        txtCC_DepInicial.setColumns(10); 

        txtCC_Limite = new JFormattedTextField();
        txtCC_Limite.setColumns(10);
        
        pnl.add(new JLabel("Depósito Inicial (R$):"));
        pnl.add(txtCC_DepInicial);
        pnl.add(new JLabel("Limite (R$):"));
        pnl.add(txtCC_Limite);
        pnl.add(new JLabel("")); // Espaço vazio para alinhamento
        pnl.add(new JLabel("")); // Espaço vazio para alinhamento
        
        return pnl;
    }
    
    /**
     * Cria e retorna o painel com os campos específicos para Conta Investimento.
     * @return JPanel para Conta Investimento.
     */
    private JPanel criarPainelContaInvestimento() {
        JPanel pnl = new JPanel(new GridLayout(3, 2, 5, 5));
        txtCI_MontanteMinimo = new JFormattedTextField();
        txtCI_MontanteMinimo.setColumns(10);

        txtCI_DepMinimo = new JFormattedTextField();
        txtCI_DepMinimo.setColumns(10);

        txtCI_DepInicial = new JFormattedTextField();
        txtCI_DepInicial.setColumns(10);    
        
        pnl.add(new JLabel("Montante Mínimo (R$):"));
        pnl.add(txtCI_MontanteMinimo);
        pnl.add(new JLabel("Depósito Mínimo (R$):"));
        pnl.add(txtCI_DepMinimo);
        pnl.add(new JLabel("Depósito Inicial (R$):"));
        pnl.add(txtCI_DepInicial);
        
        return pnl;
    }
    
    /**
     * Alterna a visualização do painel central de campos de acordo com o tipo de conta selecionado.
     */
    private void atualizarCamposConta() {
        String tipo = (String) cmbTipoConta.getSelectedItem();
        cardLayout.show(pnlCamposConta, tipo); // Exibe o card (painel) correspondente ao tipo
    }
    
    /**
     * Converte o texto formatado do JFormattedTextField para um valor Double.
     * Lida com separadores de milhar (ponto) e decimal (vírgula) do padrão brasileiro.
     * @param field O JFormattedTextField contendo o valor.
     * @return O valor como Double.
     * @throws NumberFormatException Se o campo não contiver um número válido.
     */
    private double getDoubleFromFormattedField(JFormattedTextField field) throws NumberFormatException {
        String text = field.getText();

        if (text == null || text.trim().isEmpty()) {
            return 0.0;
        }

        // 1. Remove PONTOS de milhar (Ex: 1.000,00 -> 1000,00)
        String valorSemMilhar = text.replace(".", ""); 

        // 2. Substitui a VÍRGULA por PONTO (Ex: 1000,00 -> 1000.00)
        String valorFormatado = valorSemMilhar.replace(',', '.'); 

        // 3. Remove quaisquer caracteres não numéricos restantes que não sejam o separador decimal
        String valorLimpo = valorFormatado.replaceAll("[^0-9.]", "");

        if (valorLimpo.isEmpty()) {
            return 0.0;
        }

        // 4. Converte para Double
        return Double.parseDouble(valorLimpo);
    }
    
    /**
     * Realiza a lógica de criação e vinculação da conta ao cliente selecionado.
     * Valida se o cliente já possui uma conta.
     */
    private void vincularConta() {
        Cliente clienteSelecionado = (Cliente) cmbClientes.getSelectedItem();
        String tipoConta = (String) cmbTipoConta.getSelectedItem();
        
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
            if ("Conta Corrente".equals(tipoConta)) {
                // Obtém os valores dos campos
                double depInicial = getDoubleFromFormattedField(txtCC_DepInicial); 
                double limite = getDoubleFromFormattedField(txtCC_Limite);
                
                // Cria e adiciona a Conta Corrente
                ContaCorrente novaConta = new ContaCorrente(clienteSelecionado, depInicial, limite);
                gerenciadorContas.adicionar(novaConta);
                JOptionPane.showMessageDialog(this, "Conta Corrente Nº " + novaConta.getNumero() + " criada e vinculada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCamposCC(); // Limpa os campos da Conta Corrente
                
            } else if ("Conta Investimento".equals(tipoConta)) {
                // Obtém os valores dos campos
                double montanteMinimo = getDoubleFromFormattedField(txtCI_MontanteMinimo);
                double depMinimo = getDoubleFromFormattedField(txtCI_DepMinimo);
                double depInicial = getDoubleFromFormattedField(txtCI_DepInicial);
                
                // Cria a Conta Investimento
                ContaInvestimento novaConta = new ContaInvestimento(clienteSelecionado, depInicial, montanteMinimo, depMinimo);
                
                // Validação de Depósito Mínimo da Conta Investimento
                if (novaConta.getSaldo() == 0 && depInicial > 0) {
                    // O depósito inicial falhou na validação interna da classe ContaInvestimento (depósito < depósito mínimo)
                     JOptionPane.showMessageDialog(this, "Criação de Conta Investimento CANCELADA. O Depósito Inicial de R$ " + String.format("%.2f", depInicial) + " é menor que o Depósito Mínimo de R$ " + String.format("%.2f", depMinimo) + ".", "Criação Bloqueada", JOptionPane.WARNING_MESSAGE);
                } else {
                    // Adiciona a conta (se o saldo > 0 ou se o depósito inicial for 0)
                    gerenciadorContas.adicionar(novaConta); 
                    JOptionPane.showMessageDialog(this, "Conta Investimento Nº " + novaConta.getNumero() + " criada e vinculada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparCamposCI(); // Limpa os campos da Conta Investimento
                }
            }
        } catch (NumberFormatException ex) {
            // Captura erro se o texto nos campos não puder ser convertido para número
            JOptionPane.showMessageDialog(this, "Valores de depósito/limite/montante devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Limpa os campos de input específicos da Conta Corrente.
     */
    private void limparCamposCC() {
        txtCC_DepInicial.setValue(null); // Limpa o campo de Depósito Inicial
        txtCC_Limite.setValue(null); // Limpa o campo de Limite
    }
    
    /**
     * Limpa os campos de input específicos da Conta Investimento.
     */
    private void limparCamposCI() {
        txtCI_MontanteMinimo.setValue(null); // Limpa o Montante Mínimo
        txtCI_DepMinimo.setValue(null); // Limpa o Depósito Mínimo
        txtCI_DepInicial.setValue(null); // Limpa o Depósito Inicial
    }
}