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

// Tela de interface gráfica (JFrame) responsável por Manter Clientes.
// Inclui campos de cadastro, tabela de visualização, busca e ordenação.
public class TelaClientes extends JFrame {
    
    // Gerenciadores de Negócio
    private final GerenciadorClientes gerenciadorClientes; // Lógica de Negócio para Clientes
    private final GerenciadorContas gerenciadorContas; // Lógica de Negócio para Contas, necessário para exclusão de contas vinculadas a clientes
    
    // Componentes da Tabela
    private ModeloTabelaCliente tableModel; // Modelo de dados para a lista de clientes
    private JTable tabelaClientes; // Componente de tabela para exibir clientes
    
    // Componentes do Formulário de Cadastro
    private JTextField txtNome, txtSobrenome, txtRg, txtEndereco; // Campos de texto simples
    private JFormattedTextField txtCpf; // Campo de texto formatado para CPF    
    
    // Componentes de Ação
    private JButton btnSalvar, btnNovo, btnExcluir, btnAtualizar, btnBuscar, btnOrdenar; // Botões de ação
    
    // Componentes de Busca e Ordenação
    private JTextField txtBusca; // Campo de texto para busca
    private JComboBox<String> cmbOrdenar; // ComboBox para opções de ordenação
    
    // Cabeçalhos das colunas da tabela
    private final String[] colunas = {"Nome", "Sobrenome", "RG", "CPF", "Endereço"};
    
    
    // Construtor da tela de clientes.
    // Define o tamanho, título e comportamento de fechamento da janela.
    // Carrega a lista inicial de clientes na tabela.
    public TelaClientes(GerenciadorClientes gc, GerenciadorContas gco) {
        this.gerenciadorClientes = gc; // Inicializa o gerenciador de clientes
        this.gerenciadorContas = gco; // Inicializa o gerenciador de contas
        initComponents(); // Configura os componentes visuais
        setTitle("Sistema Bancário - Manter Clientes"); // Define o título da janela
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Define a operação de fechamento
        setSize(800, 600); // Define o tamanho inicial da janela
        setLocationRelativeTo(null); // Centraliza a janela na tela
    }

    
    // Inicializa os componentes visuais e configura a janela, configura o layout e os listeners dos botões.
    private void initComponents() {
        setLayout(new BorderLayout()); // Define o layout principal como BorderLayout
        
        // --- Painel de Formulário (Norte) ---
        // Painel que contém os campos de entrada para cadastro de clientes
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
            txtCpf = new JFormattedTextField(); // Fallback para campo simples, deixando sem máscara
            txtCpf.setColumns(20); // Define a largura visual
        }
        
        txtEndereco = new JTextField(20);
        
        // Adiciona labels e campos ao painel do formulário, criando um layout de 5 linhas e 2 colunas
        pnlFormulario.add(new JLabel("Nome:")); pnlFormulario.add(txtNome); // cria label e campo Nome.
        pnlFormulario.add(new JLabel("Sobrenome:")); pnlFormulario.add(txtSobrenome); // cria label e campo Sobrenome.
        pnlFormulario.add(new JLabel("RG:")); pnlFormulario.add(txtRg); // cria label e campo RG.
        pnlFormulario.add(new JLabel("CPF:")); pnlFormulario.add(txtCpf); // cria label e campo CPF.
        pnlFormulario.add(new JLabel("Endereço:")); pnlFormulario.add(txtEndereco); // cria label e campo Endereço.
        
        // Compacta o grid do formulário (5 linhas, 2 colunas)
        SpringUtilities.makeCompactGrid(pnlFormulario, 5, 2, 6, 6, 6, 6);
        
        add(pnlFormulario, BorderLayout.NORTH); // Adiciona o formulário na parte superior
        
        // --- Painel de Tabela (Centro) ---
        // Cria a tabela para exibir os clientes
        // Inicializa o modelo de dados com todos os clientes
        tableModel = new ModeloTabelaCliente(gerenciadorClientes.listarTodos()); // Criap o modelo com a lista inicial
        tabelaClientes = new JTable(tableModel); // Cria a JTable com o modelo, para exibir os dados
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite apenas uma linha selecionada
        
        // Listener para exibir o cliente selecionado no formulário
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> { // Listener para seleção na tabela
            if (!e.getValueIsAdjusting() && tabelaClientes.getSelectedRow() != -1) { // Verifica se a seleção mudou
                exibirClienteSelecionado(); // Chama o método para preencher o formulário
            }
        });
        
        add(new JScrollPane(tabelaClientes), BorderLayout.CENTER); // Adiciona a tabela em um ScrollPane ao centro
        
        // --- Painel de Busca e Ordenação (Sul - parte superior) ---
        // Painel que contém os campos e botões para busca e ordenação de clientes
        JPanel pnlBuscaOrdenacao = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Layout de fluxo à esquerda
        txtBusca = new JTextField(15); // Campo de texto para busca
        
        btnBuscar = new JButton("Buscar"); // Botão para iniciar a busca
        btnBuscar.addActionListener(e -> buscarClientes()); // Listener para a função de busca
        
        cmbOrdenar = new JComboBox<>(new String[]{"Nome", "Sobrenome", "Salário"}); // Opções de ordenação
        
        btnOrdenar = new JButton("Ordenar"); // Botão para iniciar a ordenação
        btnOrdenar.addActionListener(e -> ordenarClientes()); // Listener para a função de ordenação
        
        // Adiciona os componentes ao painel de busca e ordenação
        pnlBuscaOrdenacao.add(new JLabel("Buscar (Nome/Sobrenome/RG/CPF):")); // label de busca
        pnlBuscaOrdenacao.add(txtBusca); // campo de busca
        pnlBuscaOrdenacao.add(btnBuscar); // botão de busca
        pnlBuscaOrdenacao.add(new JLabel("Ordenar por:")); // label de ordenação
        pnlBuscaOrdenacao.add(cmbOrdenar); // combo de ordenação
        pnlBuscaOrdenacao.add(btnOrdenar); // botão de ordenação
        
        // --- Painel de Botões (Sul - parte inferior) ---
        // Painel que contém os botões para ações
        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Layout de fluxo centralizado
        
        btnNovo = new JButton("Novo"); // Botão para limpar o formulário
        btnNovo.addActionListener(e -> limparFormulario()); // Limpa o formulário
        
        btnSalvar = new JButton("Salvar"); // Botão para salvar um novo cliente
        btnSalvar.addActionListener(e -> salvarCliente()); // Salva um novo cliente
        
        btnAtualizar = new JButton("Atualizar"); // Botão para atualizar o cliente selecionado
        btnAtualizar.addActionListener(e -> atualizarCliente()); // Atualiza o cliente selecionado
        
        btnExcluir = new JButton("Excluir"); // Botão para excluir o cliente selecionado
        btnExcluir.addActionListener(e -> excluirCliente()); // Exclui o cliente selecionado
        
        // Adiciona os botões ao painel de botões
        pnlBotoes.add(btnNovo); // Adiciona o botão Novo
        pnlBotoes.add(btnSalvar); // Adiciona o botão Salvar
        pnlBotoes.add(btnAtualizar); // Adiciona o botão Atualizar
        pnlBotoes.add(btnExcluir); // Adiciona o botão Excluir
        
        // Combina os painéis de Busca/Ordenação e Botões no painel Sul, ativando eles em sequência
        JPanel pnlSul = new JPanel(new BorderLayout()); // Painel para combinar os dois sub-painéis
        pnlSul.add(pnlBuscaOrdenacao, BorderLayout.NORTH); // Adiciona o painel de busca/ordenação na parte superior
        pnlSul.add(pnlBotoes, BorderLayout.SOUTH); // Adiciona o painel de botões na parte inferior
        
        add(pnlSul, BorderLayout.SOUTH); // Adiciona o painel combinado na parte inferior
        
        // Inicializa botões de ação (Atualizar/Excluir) desabilitados, pois nenhum cliente está selecionado inicialmente
        btnAtualizar.setEnabled(false); // Desabilita o botão Atualizar
        btnExcluir.setEnabled(false); // Desabilita o botão Excluir
    }
    
    // Atualiza o ModeloTabelaCliente com uma nova lista de Clientes e notifica a JTable.
    // Também gerencia a seleção inicial e o estado do formulário.
    private void carregarTabela(List<Cliente> lista) {
        // Define a nova lista no modelo e dispara o evento de atualização da tabela
        tableModel.setClientes(lista); // Atualiza o modelo com a nova lista

        if (!lista.isEmpty()) { // Se a lista não estiver vazia
            tabelaClientes.setRowSelectionInterval(0, 0); // Seleciona a primeira linha
            exibirClienteSelecionado(); // Exibe o primeiro cliente no formulário
        } else {
            limparFormulario(); // Limpa o formulário se a lista estiver vazia
        }
    }
    
    // Limpa todos os campos do formulário e redefine o estado dos botões para "Novo Cadastro".
    private void limparFormulario() {
        txtNome.setText(""); // Limpa o campo Nome
        txtSobrenome.setText(""); // Limpa o campo Sobrenome
        txtRg.setText(""); // Limpa o campo RG
        txtCpf.setValue(null); // Limpa o JFormattedTextField de CPF
        txtEndereco.setText(""); // Limpa o campo Endereço
        txtCpf.setEditable(true); // Permite edição do CPF para um novo cadastro
        btnSalvar.setEnabled(true); // Habilita Salvar
        btnAtualizar.setEnabled(false); // Desabilita Atualizar
        btnExcluir.setEnabled(false); // Desabilita Excluir
        tabelaClientes.clearSelection(); // Remove a seleção da tabela
    }
    
    // Obtém e exibo o Cliente selecionado na JTable e preenche o formulário com seus dados.
    private void exibirClienteSelecionado() {
        int linhaView = tabelaClientes.getSelectedRow(); // Obtém a linha selecionada na visualização (pode ser diferente do modelo se houver ordenação)
        if (linhaView != -1) { // Verifica se há uma linha selecionada
            int linhaModel = tabelaClientes.convertRowIndexToModel(linhaView); // Converte para o índice real do modelo de dados
            Cliente cliente = tableModel.getCliente(linhaModel); // Obtém o objeto Cliente
            
            if (cliente != null) { // Verifica se o cliente não é nulo
                // Preenche os campos do formulário
                txtNome.setText(cliente.getNome()); // Preenche o campo Nome
                txtSobrenome.setText(cliente.getSobrenome()); // Preenche o campo Sobrenome
                txtRg.setText(cliente.getRg()); // Preenche o campo RG
                txtCpf.setText(cliente.getCpf()); // Preenche o campo CPF
                txtEndereco.setText(cliente.getEndereco()); // Preenche o campo Endereço
                
                // Configura o estado dos botões para "Atualização/Exclusão"
                txtCpf.setEditable(false); // Bloqueia a edição do CPF para atualização
                btnSalvar.setEnabled(false); // Desabilita Salvar
                btnAtualizar.setEnabled(true); // Habilita Atualizar
                btnExcluir.setEnabled(true); // Habilita Excluir
            }
        }
    }
    
    // Constrói um objeto Cliente com base nos dados preenchidos no formulário.
    // Retorna o objeto Cliente ou null se a validação falhar.
    private Cliente getClienteDoFormulario() { // Obtém os dados do formulário
        String nome = txtNome.getText(); // Obtém o nome do campo de texto
        String sobrenome = txtSobrenome.getText(); // Obtém o sobrenome do campo de texto
        String rg = txtRg.getText(); // Obtém o RG do campo de texto

        String cpfComMascara = txtCpf.getText(); // Obtém o CPF com máscara do campo formatado
        String cpfLimpo = cpfComMascara.replaceAll("[^0-9]", ""); // Remove a máscara, o regex: [^0-9] remove tudo que não for número, ficando só os números do CPF

        String endereco = txtEndereco.getText(); // Obtém o endereço do campo de texto

        // Verifica se todos os campos obrigatórios foram preenchidos
        if (nome.isEmpty() || sobrenome.isEmpty() || rg.isEmpty() || cpfLimpo.isEmpty() || endereco.isEmpty()) {

            // Exibe mensagem de erro se algum campo estiver vazio
            JOptionPane.showMessageDialog(this, "Todos os campos (Nome, Sobrenome, RG, CPF, Endereço) são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return null; // Retorna nulo, indicando falha na validação
        }

        // Cria e retorna o objeto Cliente (com o CPF LIMPO - sem máscara)
        return new Cliente(nome, sobrenome, rg, cpfLimpo, endereco);
    }

    // Tenta salvar um novo cliente no sistema após validação.
    private void salvarCliente() {
        Cliente novoCliente = getClienteDoFormulario(); // Obtém o cliente do formulário
        if (novoCliente == null) return; // Sai se a validação falhar
        
        // Verifica se já existe um cliente com o CPF (chave primária)
        if (gerenciadorClientes.buscarPorCpf(novoCliente.getCpf()) != null) {
            JOptionPane.showMessageDialog(this, "Já existe um cliente com este CPF.", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
            return; // Sai se o CPF já estiver cadastrado
        }
        
        gerenciadorClientes.adicionar(novoCliente); // Adiciona o cliente ao gerenciador
        carregarTabela(gerenciadorClientes.listarTodos()); // Recarrega a tabela para incluir o novo cliente
        limparFormulario(); // Limpa o formulário para um novo cadastro
        JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Tenta atualizar os dados do cliente atualmente selecionado.
    private void atualizarCliente() {
        int linhaView = tabelaClientes.getSelectedRow(); // Linha selecionada
        if (linhaView == -1) return; // Sai se nada estiver selecionado
        
        // Obtém o cliente antigo, objeto que vai ser modificado
        int linhaModel = tabelaClientes.convertRowIndexToModel(linhaView); // Converte para o índice do modelo
        Cliente clienteAntigo = tableModel.getCliente(linhaModel); // Obtém o objeto Cliente existente
        
        Cliente clienteNovo = getClienteDoFormulario(); // Obtém os novos dados do formulário
        if (clienteNovo == null || clienteAntigo == null) return; // Sai se a validação falhar ou o cliente não for encontrado
        
        // Atualiza os dados no objeto existente (mantendo o CPF original)
        clienteAntigo.setNome(clienteNovo.getNome());
        clienteAntigo.setSobrenome(clienteNovo.getSobrenome());
        clienteAntigo.setRg(clienteNovo.getRg());
        clienteAntigo.setEndereco(clienteNovo.getEndereco());
        
        // Recarrega a tabela para refletir a mudança visual
        carregarTabela(gerenciadorClientes.listarTodos());
        
        // Re-seleciona o cliente atualizado na tabela
        JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    // Tenta excluir o cliente atualmente selecionado, incluindo suas contas.
    private void excluirCliente() {
        int linhaView = tabelaClientes.getSelectedRow(); // Linha selecionada
        if (linhaView == -1) return; // Sai se nada estiver selecionado

        // Obtém o CPF formatado da tabela (coluna 3)
        String cpfComMascara = (String) tableModel.getValueAt(linhaView, 3);
        // Limpa o CPF para o formato de busca (apenas números)
        String cpfLimpo = cpfComMascara.replaceAll("[^0-9]", ""); 
        // Busca o cliente usando o CPF limpo
        Cliente cliente = gerenciadorClientes.buscarPorCpf(cpfLimpo);

        if (cliente == null) return; // Sai se o cliente não for encontrado

        // Pede confirmação ao usuário
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir o cliente " + cliente.getNome() + "?\n" +
                "ATENÇÃO: Todas as contas vinculadas a este cliente serão apagadas.", 
                "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) { // Se o usuário confirmar a exclusão
            // Exclui as contas vinculadas a este cliente
            gerenciadorContas.excluirContasDoCliente(cliente);

            // Exclui o cliente
            if (gerenciadorClientes.excluir(cliente)) {
                carregarTabela(gerenciadorClientes.listarTodos()); // Recarrega a tabela
                limparFormulario(); // Limpa o formulário
                JOptionPane.showMessageDialog(this, "Cliente e contas excluídos com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Realiza a busca de clientes com base no termo digitado e atualiza a tabela com os resultados.
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
    
    // Realiza a ordenação da lista de clientes atual, filtrada ou não, pelo campo selecionado.
    private void ordenarClientes() {
        String campo = (String) cmbOrdenar.getSelectedItem(); // Obtém o critério de ordenação
        
        // Pega a lista atual
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
    
    // Classe para ajudar na disposição dos componentes
    // Usando SpringLayout, para criar layouts mais compactos
    private static class SpringUtilities {

        // Método para criar um grid compacto com SpringLayout
        // Configura as restrições de layout para alinhar os componentes em um grid
        // Recebe o container pai, número de linhas e colunas, posições iniciais e espaçamentos horizontal/vertical entre os componentes
        // Aplica as restrições de SpringLayout para organizar os componentes
        public static void makeCompactGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad, int yPad) {
            SpringLayout layout; // Declara o layout

            try {
                layout = (SpringLayout) parent.getLayout(); // Tenta obter o layout
            } catch (ClassCastException exc) {
                System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
                return;
            }

            // Lógica para calcular e aplicar as restrições de SpringLayout
            Spring x = Spring.constant(initialX); // Posição X inicial
            for (int c = 0; c < cols; c++) { // Para cada coluna
                Spring width = Spring.constant(0); // Largura inicial da coluna
                for (int r = 0; r < rows; r++) { // Para cada linha
                    width = Spring.max(width, getWidth(parent.getComponent(r * cols + c))); // Calcula a largura máxima da coluna
                }
                for (int r = 0; r < rows; r++) { // Aplica a largura calculada a cada componente na coluna
                    SpringLayout.Constraints constraints = layout.getConstraints(parent.getComponent(r * cols + c)); // Obtém as restrições do componente
                    constraints.setX(x); // Define a posição X
                    constraints.setWidth(width); // Define a largura
                }
                x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad))); // Atualiza a posição X para a próxima coluna
            }

            Spring y = Spring.constant(initialY); // Posição Y inicial
            for (int r = 0; r < rows; r++) { // Para cada linha
                Spring height = Spring.constant(0); // Altura inicial da linha
                for (int c = 0; c < cols; c++) { // Para cada coluna
                    height = Spring.max(height, getHeight(parent.getComponent(r * cols + c))); // Calcula a altura máxima da linha
                }
                for (int c = 0; c < cols; c++) { // Aplica a altura calculada a cada componente na linha
                    SpringLayout.Constraints constraints = layout.getConstraints(parent.getComponent(r * cols + c)); // Obtém as restrições do componente
                    constraints.setY(y); // Define a posição Y
                    constraints.setHeight(height); // Define a altura
                }
                y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad))); // Atualiza a posição Y para a próxima linha
            }

            SpringLayout.Constraints pCons = layout.getConstraints(parent); // Obtém as restrições do container pai
            pCons.setConstraint(SpringLayout.EAST, x); // Define a largura total do container
            pCons.setConstraint(SpringLayout.SOUTH, y); // Define a altura total do container
        }

        // Métodos auxiliares para obter as dimensões dos componentes
        private static Spring getWidth(Component c) {
            // Retorna o spring de largura do componente
            return Spring.width(c); 
        }

        // Métodos auxiliares para obter as dimensões dos componentes
        private static Spring getHeight(Component c) {
            // Retorna o spring de altura do componente
            return Spring.height(c);
        }
    }
}