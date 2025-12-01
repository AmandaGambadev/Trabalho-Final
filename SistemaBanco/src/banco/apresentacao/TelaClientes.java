package banco.apresentacao;

import banco.modelo.Cliente;
import banco.negocio.GerenciadorClientes;
import banco.negocio.GerenciadorContas;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.*;

/**
 * Tela de interface gráfica (JFrame) responsável por Manter (CRUD) Clientes.
 * Inclui campos de cadastro, tabela de visualização, busca e ordenação.
 */
public class TelaClientes extends JFrame {
    
    // Gerenciadores de Negócio
    private final GerenciadorClientes gerenciadorClientes;
    private final GerenciadorContas gerenciadorContas;
    
    // Componentes da Tabela
    private ModeloTabelaCliente tableModel; // Modelo de dados para a JTable
    private JTable tabelaClientes;
    
    // Componentes do Formulário de Cadastro
    private JTextField txtNome, txtSobrenome, txtRg, txtEndereco;
    private JFormattedTextField txtCpf;
    
    // Componentes de Ação
    private JButton btnSalvar, btnNovo, btnExcluir, btnAtualizar, btnBuscar, btnOrdenar;
    
    // Componentes de Busca e Ordenação
    private JTextField txtBusca;
    private JComboBox<String> cmbOrdenar;
    
    // Cabeçalhos das colunas da tabela
    private final String[] colunas = {"Nome", "Sobrenome", "RG", "CPF", "Endereço"};
    
    /**
     * Construtor da tela de clientes.
     * @param gc GerenciadorClientes (Lógica de Negócio para Clientes).
     * @param gco GerenciadorContas (Necessário para a exclusão de contas vinculadas).
     */
    public TelaClientes(GerenciadorClientes gc, GerenciadorContas gco) {
        this.gerenciadorClientes = gc;
        this.gerenciadorContas = gco;
        initComponents();
        setTitle("Sistema Bancário - Manter Clientes"); // Define o título da janela
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Define a operação de fechamento
        setSize(800, 600); // Define o tamanho inicial da janela
        setLocationRelativeTo(null); // Centraliza a janela na tela
    }

    /**
     * Inicializa e configura todos os componentes visuais (UI) da tela.
     */
    private void initComponents() {
        setLayout(new BorderLayout()); // Define o layout principal como BorderLayout
        
        // --- Painel de Formulário (Norte) ---
        JPanel pnlFormulario = new JPanel(new SpringLayout()); // Usa SpringLayout para um formulário compacto
        
        // Inicializa campos de texto simples
        txtNome = new JTextField(20);
        txtSobrenome = new JTextField(20);
        txtRg = new JTextField(20);
        
        // Configura o campo de CPF com máscara
        try {
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##"); // Máscara de CPF
            cpfMask.setPlaceholderCharacter('_'); // Caractere de preenchimento
            txtCpf = new JFormattedTextField(cpfMask); // Cria o campo formatado
            txtCpf.setColumns(20); // Mantém a largura visual
        } catch (java.text.ParseException e) {
            e.printStackTrace(); // Imprime o erro se a máscara falhar
            txtCpf = new JFormattedTextField(); // Fallback para campo simples
            txtCpf.setColumns(20);
        }
        
        txtEndereco = new JTextField(20);
        
        // Adiciona labels e campos ao painel do formulário
        pnlFormulario.add(new JLabel("Nome:")); pnlFormulario.add(txtNome);
        pnlFormulario.add(new JLabel("Sobrenome:")); pnlFormulario.add(txtSobrenome);
        pnlFormulario.add(new JLabel("RG:")); pnlFormulario.add(txtRg);
        pnlFormulario.add(new JLabel("CPF:")); pnlFormulario.add(txtCpf);
        pnlFormulario.add(new JLabel("Endereço:")); pnlFormulario.add(txtEndereco);
        
        // Compacta o grid do formulário (5 linhas, 2 colunas)
        SpringUtilities.makeCompactGrid(pnlFormulario, 5, 2, 6, 6, 6, 6);
        
        add(pnlFormulario, BorderLayout.NORTH); // Adiciona o formulário na parte superior
        
        // --- Painel de Tabela (Centro) ---
        // Inicializa o modelo de dados com todos os clientes
        tableModel = new ModeloTabelaCliente(gerenciadorClientes.listarTodos());
        tabelaClientes = new JTable(tableModel); // Cria a JTable com o modelo
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite apenas uma linha selecionada
        
        // Listener para exibir o cliente selecionado no formulário
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaClientes.getSelectedRow() != -1) {
                exibirClienteSelecionado(); // Chama o método para preencher o formulário
            }
        });
        
        add(new JScrollPane(tabelaClientes), BorderLayout.CENTER); // Adiciona a tabela em um ScrollPane ao centro
        
        // --- Painel de Busca e Ordenação (Sul - parte superior) ---
        JPanel pnlBuscaOrdenacao = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Layout de fluxo à esquerda
        txtBusca = new JTextField(15);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarClientes()); // Listener para a função de busca
        
        cmbOrdenar = new JComboBox<>(new String[]{"Nome", "Sobrenome", "Salário"}); // Opções de ordenação
        
        btnOrdenar = new JButton("Ordenar");
        btnOrdenar.addActionListener(e -> ordenarClientes()); // Listener para a função de ordenação
        
        pnlBuscaOrdenacao.add(new JLabel("Buscar (Nome/Sobrenome/RG/CPF):"));
        pnlBuscaOrdenacao.add(txtBusca);
        pnlBuscaOrdenacao.add(btnBuscar);
        pnlBuscaOrdenacao.add(new JLabel("Ordenar por:"));
        pnlBuscaOrdenacao.add(cmbOrdenar);
        pnlBuscaOrdenacao.add(btnOrdenar);
        
        // --- Painel de Botões (Sul - parte inferior) ---
        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Layout de fluxo centralizado
        
        btnNovo = new JButton("Novo");
        btnNovo.addActionListener(e -> limparFormulario()); // Limpa o formulário
        
        btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvarCliente()); // Salva um novo cliente
        
        btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> atualizarCliente()); // Atualiza o cliente selecionado
        
        btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirCliente()); // Exclui o cliente selecionado
        
        pnlBotoes.add(btnNovo);
        pnlBotoes.add(btnSalvar);
        pnlBotoes.add(btnAtualizar);
        pnlBotoes.add(btnExcluir);
        
        // Combina os painéis de Busca/Ordenação e Botões no painel Sul
        JPanel pnlSul = new JPanel(new BorderLayout());
        pnlSul.add(pnlBuscaOrdenacao, BorderLayout.NORTH);
        pnlSul.add(pnlBotoes, BorderLayout.SOUTH);
        
        add(pnlSul, BorderLayout.SOUTH); // Adiciona o painel combinado na parte inferior
        
        // Inicializa botões de ação (Atualizar/Excluir) desabilitados, pois nenhum cliente está selecionado inicialmente
        btnAtualizar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }
    
    /**
     * Atualiza o ModeloTabelaCliente com uma nova lista de Clientes e notifica a JTable.
     * @param lista A lista de objetos Cliente a ser exibida.
     */
    private void carregarTabela(List<Cliente> lista) {
        // Define a nova lista no modelo e dispara o evento de atualização da tabela
        tableModel.setClientes(lista); 

        if (!lista.isEmpty()) {
            tabelaClientes.setRowSelectionInterval(0, 0); // Seleciona a primeira linha
            exibirClienteSelecionado(); // Exibe o primeiro cliente no formulário
        } else {
            limparFormulario(); // Limpa o formulário se a lista estiver vazia
        }
    }
    
    /**
     * Limpa todos os campos do formulário e redefine o estado dos botões para "Novo Cadastro".
     */
    private void limparFormulario() {
        txtNome.setText("");
        txtSobrenome.setText("");
        txtRg.setText("");
        txtCpf.setValue(null); // Limpa o JFormattedTextField de CPF
        txtEndereco.setText("");
        txtCpf.setEditable(true); // Permite edição do CPF para um novo cadastro
        btnSalvar.setEnabled(true); // Habilita Salvar
        btnAtualizar.setEnabled(false); // Desabilita Atualizar
        btnExcluir.setEnabled(false); // Desabilita Excluir
        tabelaClientes.clearSelection(); // Remove a seleção da tabela
    }
    
    /**
     * Obtém o Cliente selecionado na JTable e preenche o formulário com seus dados.
     */
    private void exibirClienteSelecionado() {
        int linhaView = tabelaClientes.getSelectedRow(); // Obtém a linha selecionada na visualização (pode ser diferente do modelo se houver ordenação)
        if (linhaView != -1) {
            int linhaModel = tabelaClientes.convertRowIndexToModel(linhaView); // Converte para o índice real do modelo de dados
            Cliente cliente = tableModel.getCliente(linhaModel); // Obtém o objeto Cliente
            
            if (cliente != null) {
                // Preenche os campos do formulário
                txtNome.setText(cliente.getNome());
                txtSobrenome.setText(cliente.getSobrenome());
                txtRg.setText(cliente.getRg());
                txtCpf.setText(cliente.getCpf());
                txtEndereco.setText(cliente.getEndereco());
                
                // Configura o estado dos botões para "Atualização/Exclusão"
                txtCpf.setEditable(false); // Bloqueia a edição do CPF para atualização
                btnSalvar.setEnabled(false); // Desabilita Salvar
                btnAtualizar.setEnabled(true); // Habilita Atualizar
                btnExcluir.setEnabled(true); // Habilita Excluir
            }
        }
    }
    
    /**
     * Constrói um objeto Cliente com base nos dados preenchidos no formulário.
     * Realiza a validação de campos obrigatórios.
     * @return O objeto Cliente criado ou null em caso de falha na validação.
     */
    private Cliente getClienteDoFormulario() {
        String nome = txtNome.getText();
        String sobrenome = txtSobrenome.getText();
        String rg = txtRg.getText();

        String cpfComMascara = txtCpf.getText(); 
        String cpfLimpo = cpfComMascara.replaceAll("[^0-9]", ""); // Remove a máscara, deixando apenas números

        String endereco = txtEndereco.getText();

        // Validação: Verifica se todos os campos obrigatórios foram preenchidos
        if (nome.isEmpty() || sobrenome.isEmpty() || rg.isEmpty() || cpfLimpo.isEmpty() || endereco.isEmpty()) {

            // Exibe mensagem de erro se algum campo estiver vazio
            JOptionPane.showMessageDialog(this, "Todos os campos (Nome, Sobrenome, RG, CPF, Endereço) são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return null; // Retorna nulo, indicando falha na validação
        }

        // Cria e retorna o objeto Cliente (com o CPF LIMPO - sem máscara)
        return new Cliente(nome, sobrenome, rg, cpfLimpo, endereco);
    }

    /**
     * Tenta salvar um novo cliente no sistema após validação.
     */
    private void salvarCliente() {
        Cliente novoCliente = getClienteDoFormulario(); // Obtém o cliente do formulário
        if (novoCliente == null) return; // Sai se a validação falhar
        
        // Verifica se já existe um cliente com o CPF (chave primária)
        if (gerenciadorClientes.buscarPorCpf(novoCliente.getCpf()) != null) {
            JOptionPane.showMessageDialog(this, "Já existe um cliente com este CPF.", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        gerenciadorClientes.adicionar(novoCliente); // Adiciona o cliente ao gerenciador
        carregarTabela(gerenciadorClientes.listarTodos()); // Recarrega a tabela para incluir o novo cliente
        limparFormulario(); // Limpa o formulário para um novo cadastro
        JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Tenta atualizar os dados do cliente atualmente selecionado.
     */
    private void atualizarCliente() {
        int linhaView = tabelaClientes.getSelectedRow(); // Linha selecionada
        if (linhaView == -1) return; // Sai se nada estiver selecionado
        
        // Obtém o cliente antigo (objeto a ser modificado)
        int linhaModel = tabelaClientes.convertRowIndexToModel(linhaView);
        Cliente clienteAntigo = tableModel.getCliente(linhaModel);
        
        Cliente clienteNovo = getClienteDoFormulario(); // Obtém os novos dados do formulário
        if (clienteNovo == null || clienteAntigo == null) return; // Sai se a validação falhar ou o cliente não for encontrado
        
        // Atualiza os dados no objeto existente (mantendo o CPF original)
        clienteAntigo.setNome(clienteNovo.getNome());
        clienteAntigo.setSobrenome(clienteNovo.getSobrenome());
        clienteAntigo.setRg(clienteNovo.getRg());
        clienteAntigo.setEndereco(clienteNovo.getEndereco());
        
        // Recarrega a tabela para refletir a mudança visual
        carregarTabela(gerenciadorClientes.listarTodos());
        
        JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Tenta excluir o cliente atualmente selecionado, incluindo suas contas.
     */
    private void excluirCliente() {
        int linhaView = tabelaClientes.getSelectedRow();
        if (linhaView == -1) return;

        // Obtém o CPF formatado da tabela (coluna 3)
        String cpfComMascara = (String) tableModel.getValueAt(linhaView, 3);

        // Limpa o CPF para o formato de busca (apenas números)
        String cpfLimpo = cpfComMascara.replaceAll("[^0-9]", ""); 

        // Busca o cliente usando o CPF limpo
        Cliente cliente = gerenciadorClientes.buscarPorCpf(cpfLimpo);

        if (cliente == null) return;

        // Pede confirmação ao usuário
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir o cliente " + cliente.getNome() + "?\n" +
                "ATENÇÃO: Todas as contas vinculadas a este cliente serão apagadas.", 
                "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // 1. Exclui as contas vinculadas a este cliente
            gerenciadorContas.excluirContasDoCliente(cliente);

            // 2. Exclui o cliente
            if (gerenciadorClientes.excluir(cliente)) {
                carregarTabela(gerenciadorClientes.listarTodos()); // Recarrega a tabela
                limparFormulario(); // Limpa o formulário
                JOptionPane.showMessageDialog(this, "Cliente e contas excluídos com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Realiza a busca de clientes com base no termo digitado.
     * Atualiza a tabela com os resultados.
     */
    private void buscarClientes() {
        String termo = txtBusca.getText(); // Obtém o termo de busca
        List<Cliente> resultados = gerenciadorClientes.buscar(termo); // Chama a lógica de busca
        carregarTabela(resultados); // Carrega a tabela com os resultados
        
        // Feedback para o usuário se a busca não retornar resultados
        if (resultados.isEmpty() && !termo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum cliente encontrado para o termo: " + termo, "Busca", JOptionPane.INFORMATION_MESSAGE);
            txtBusca.setText(""); // Limpa o campo de busca
            carregarTabela(gerenciadorClientes.listarTodos()); // Exibe todos novamente
        }
    }
    
    /**
     * Realiza a ordenação da lista de clientes atual (filtrada ou não) pelo campo selecionado.
     */
    private void ordenarClientes() {
        String campo = (String) cmbOrdenar.getSelectedItem(); // Obtém o critério de ordenação
        
        // Pega a lista atual (filtrada ou não)
        List<Cliente> listaAtual = gerenciadorClientes.buscar(txtBusca.getText()); 
        
        // Chama a lógica de ordenação
        List<Cliente> listaOrdenada = gerenciadorClientes.ordenar(campo, listaAtual);
        
        carregarTabela(listaOrdenada); // Carrega a tabela com a lista ordenada
        
        // Re-seleciona o primeiro item da lista ordenada
        if (!listaOrdenada.isEmpty()) {
            tabelaClientes.setRowSelectionInterval(0, 0);
            exibirClienteSelecionado();
        }
    }
    
    /**
     * Classe utilitária estática para ajudar na disposição dos componentes
     * usando SpringLayout (ajuda a criar layouts compactos e alinhados).
     */
    private static class SpringUtilities {
        /**
         * Cria um grid compacto e alinhado usando SpringLayout.
         * @param parent O container que utiliza SpringLayout.
         * @param rows Número de linhas.
         * @param cols Número de colunas.
         * @param initialX Posição X inicial.
         * @param initialY Posição Y inicial.
         * @param xPad Espaçamento horizontal entre colunas.
         * @param yPad Espaçamento vertical entre linhas.
         */
        public static void makeCompactGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad, int yPad) {
            SpringLayout layout;
            try {
                layout = (SpringLayout) parent.getLayout(); // Tenta obter o layout
            } catch (ClassCastException exc) {
                System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
                return;
            }

            // Lógica para calcular e aplicar as restrições de SpringLayout
            Spring x = Spring.constant(initialX);
            for (int c = 0; c < cols; c++) {
                Spring width = Spring.constant(0);
                for (int r = 0; r < rows; r++) {
                    width = Spring.max(width, getWidth(parent.getComponent(r * cols + c)));
                }
                for (int r = 0; r < rows; r++) {
                    SpringLayout.Constraints constraints = layout.getConstraints(parent.getComponent(r * cols + c));
                    constraints.setX(x);
                    constraints.setWidth(width);
                }
                x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
            }

            Spring y = Spring.constant(initialY);
            for (int r = 0; r < rows; r++) {
                Spring height = Spring.constant(0);
                for (int c = 0; c < cols; c++) {
                    height = Spring.max(height, getHeight(parent.getComponent(r * cols + c)));
                }
                for (int c = 0; c < cols; c++) {
                    SpringLayout.Constraints constraints = layout.getConstraints(parent.getComponent(r * cols + c));
                    constraints.setY(y);
                    constraints.setHeight(height);
                }
                y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
            }

            SpringLayout.Constraints pCons = layout.getConstraints(parent);
            pCons.setConstraint(SpringLayout.EAST, x);
            pCons.setConstraint(SpringLayout.SOUTH, y);
        }

        private static Spring getWidth(Component c) {
            // Retorna o spring de largura do componente
            return Spring.width(c); 
        }

        private static Spring getHeight(Component c) {
            // Retorna o spring de altura do componente
            return Spring.height(c);
        }
    }
}