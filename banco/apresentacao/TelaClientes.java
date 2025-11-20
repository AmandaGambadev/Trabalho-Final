package banco.apresentacao;

import banco.modelo.Cliente;
import banco.negocio.GerenciadorClientes;
import banco.negocio.GerenciadorContas;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class TelaClientes extends JFrame {
    
    private final GerenciadorClientes gerenciadorClientes;
    private final GerenciadorContas gerenciadorContas;
    private DefaultTableModel tableModel; // Simplificado para DefaultTableModel
    private JTable tabelaClientes;
    
    private JTextField txtNome, txtSobrenome, txtRg, txtCpf, txtEndereco;
    private JButton btnSalvar, btnNovo, btnExcluir, btnAtualizar, btnBuscar, btnOrdenar;
    private JTextField txtBusca;
    private JComboBox<String> cmbOrdenar;
    
    private final String[] colunas = {"Nome", "Sobrenome", "RG", "CPF", "Endereço"};
    
    public TelaClientes(GerenciadorClientes gc, GerenciadorContas gco) {
        this.gerenciadorClientes = gc;
        this.gerenciadorContas = gco;
        initComponents();
        carregarTabela(gerenciadorClientes.listarTodos());
        setTitle("Manter Clientes");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // ... (Layout e inicialização dos componentes - igual à versão anterior) ...
        setLayout(new BorderLayout());
        
        // --- Painel de Formulário (Norte) ---
        JPanel pnlFormulario = new JPanel(new SpringLayout());
        
        txtNome = new JTextField(20);
        txtSobrenome = new JTextField(20);
        txtRg = new JTextField(20);
        txtCpf = new JTextField(20);
        txtEndereco = new JTextField(20);
        
        pnlFormulario.add(new JLabel("Nome:")); pnlFormulario.add(txtNome);
        pnlFormulario.add(new JLabel("Sobrenome:")); pnlFormulario.add(txtSobrenome);
        pnlFormulario.add(new JLabel("RG:")); pnlFormulario.add(txtRg);
        pnlFormulario.add(new JLabel("CPF:")); pnlFormulario.add(txtCpf);
        pnlFormulario.add(new JLabel("Endereço:")); pnlFormulario.add(txtEndereco);
        
        SpringUtilities.makeCompactGrid(pnlFormulario, 5, 2, 6, 6, 6, 6);
        
        add(pnlFormulario, BorderLayout.NORTH);
        
        // --- Painel de Tabela (Centro) ---
        tableModel = new DefaultTableModel(colunas, 0); // Inicializa com 0 linhas
        tabelaClientes = new JTable(tableModel);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaClientes.getSelectedRow() != -1) {
                exibirClienteSelecionado();
            }
        });
        
        add(new JScrollPane(tabelaClientes), BorderLayout.CENTER);
        
        // --- Painel de Busca e Ordenação (Sul - parte superior) ---
        JPanel pnlBuscaOrdenacao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBusca = new JTextField(15);
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarClientes());
        
        cmbOrdenar = new JComboBox<>(new String[]{"Nome", "Sobrenome"});
        btnOrdenar = new JButton("Ordenar");
        btnOrdenar.addActionListener(e -> ordenarClientes());
        
        pnlBuscaOrdenacao.add(new JLabel("Buscar (Nome/Sobrenome/RG/CPF):"));
        pnlBuscaOrdenacao.add(txtBusca);
        pnlBuscaOrdenacao.add(btnBuscar);
        pnlBuscaOrdenacao.add(new JLabel("Ordenar por:"));
        pnlBuscaOrdenacao.add(cmbOrdenar);
        pnlBuscaOrdenacao.add(btnOrdenar);
        
        // --- Painel de Botões (Sul - parte inferior) ---
        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnNovo = new JButton("Novo");
        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvarCliente());
        btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> atualizarCliente());
        btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirCliente());
        
        pnlBotoes.add(btnNovo);
        pnlBotoes.add(btnSalvar);
        pnlBotoes.add(btnAtualizar);
        pnlBotoes.add(btnExcluir);
        
        JPanel pnlSul = new JPanel(new BorderLayout());
        pnlSul.add(pnlBuscaOrdenacao, BorderLayout.NORTH);
        pnlSul.add(pnlBotoes, BorderLayout.SOUTH);
        
        add(pnlSul, BorderLayout.SOUTH);
        
        // Inicializa botões de ação
        btnAtualizar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }
    
    /**
     * Carrega a JTable a partir da lista de Clientes.
     * Simplificado: usa DefaultTableModel e preenche linha por linha.
     */
    private void carregarTabela(List<Cliente> lista) {
        tableModel.setRowCount(0); // Limpa a tabela
        
        for (Cliente cliente : lista) {
            tableModel.addRow(new Object[]{
                cliente.getNome(),
                cliente.getSobrenome(),
                cliente.getRg(),
                cliente.getCpf(),
                cliente.getEndereco()
            });
        }
        
        if (!lista.isEmpty()) {
            tabelaClientes.setRowSelectionInterval(0, 0);
            exibirClienteSelecionado();
        } else {
            limparFormulario();
        }
    }
    
    private void limparFormulario() {
        txtNome.setText("");
        txtSobrenome.setText("");
        txtRg.setText("");
        txtCpf.setText("");
        txtEndereco.setText("");
        txtCpf.setEditable(true);
        btnSalvar.setEnabled(true);
        btnAtualizar.setEnabled(false);
        btnExcluir.setEnabled(false);
        tabelaClientes.clearSelection();
    }
    
    private void exibirClienteSelecionado() {
        int linhaView = tabelaClientes.getSelectedRow();
        if (linhaView != -1) {
            // Obtém o CPF da linha selecionada (chave para buscar o objeto Cliente real)
            String cpfSelecionado = (String) tableModel.getValueAt(linhaView, 3);
            Cliente cliente = gerenciadorClientes.buscarPorCpf(cpfSelecionado);
            
            if (cliente != null) {
                txtNome.setText(cliente.getNome());
                txtSobrenome.setText(cliente.getSobrenome());
                txtRg.setText(cliente.getRg());
                txtCpf.setText(cliente.getCpf());
                txtEndereco.setText(cliente.getEndereco());
                
                txtCpf.setEditable(false);
                btnSalvar.setEnabled(false);
                btnAtualizar.setEnabled(true);
                btnExcluir.setEnabled(true);
            }
        }
    }
    
    private Cliente getClienteDoFormulario() {
        String nome = txtNome.getText();
        String sobrenome = txtSobrenome.getText();
        String rg = txtRg.getText();
        String cpf = txtCpf.getText();
        String endereco = txtEndereco.getText();
        
        if (nome.isEmpty() || sobrenome.isEmpty() || cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome, Sobrenome e CPF são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        return new Cliente(nome, sobrenome, rg, cpf, endereco);
    }

    private void salvarCliente() {
        Cliente novoCliente = getClienteDoFormulario();
        if (novoCliente == null) return;
        
        if (gerenciadorClientes.buscarPorCpf(novoCliente.getCpf()) != null) {
            JOptionPane.showMessageDialog(this, "Já existe um cliente com este CPF.", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        gerenciadorClientes.adicionar(novoCliente);
        carregarTabela(gerenciadorClientes.listarTodos()); // Recarrega a tabela
        limparFormulario();
        JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void atualizarCliente() {
        int linhaView = tabelaClientes.getSelectedRow();
        if (linhaView == -1) return;
        
        // Obtém o CPF do cliente que está sendo atualizado
        String cpfAntigo = (String) tableModel.getValueAt(linhaView, 3);
        Cliente clienteAntigo = gerenciadorClientes.buscarPorCpf(cpfAntigo);
        
        Cliente clienteNovo = getClienteDoFormulario();
        if (clienteNovo == null || clienteAntigo == null) return;
        
        // Atualiza os dados no objeto existente
        clienteAntigo.setNome(clienteNovo.getNome());
        clienteAntigo.setSobrenome(clienteNovo.getSobrenome());
        clienteAntigo.setRg(clienteNovo.getRg());
        clienteAntigo.setEndereco(clienteNovo.getEndereco());
        
        // Atualiza a linha na tabela
        tableModel.setValueAt(clienteNovo.getNome(), linhaView, 0);
        tableModel.setValueAt(clienteNovo.getSobrenome(), linhaView, 1);
        tableModel.setValueAt(clienteNovo.getRg(), linhaView, 2);
        tableModel.setValueAt(clienteNovo.getEndereco(), linhaView, 4);
        
        JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirCliente() {
        int linhaView = tabelaClientes.getSelectedRow();
        if (linhaView == -1) return;
        
        String cpf = (String) tableModel.getValueAt(linhaView, 3);
        Cliente cliente = gerenciadorClientes.buscarPorCpf(cpf);
        
        if (cliente == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir o cliente " + cliente.getNome() + "?\n" +
                "ATENÇÃO: Todas as contas vinculadas a este cliente serão apagadas.", 
                "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            gerenciadorContas.excluirContasDoCliente(cliente);
            
            if (gerenciadorClientes.excluir(cliente)) {
                carregarTabela(gerenciadorClientes.listarTodos()); // Recarrega a lista
                limparFormulario();
                JOptionPane.showMessageDialog(this, "Cliente e contas excluídos com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Regra 1.d: Buscar
    private void buscarClientes() {
        String termo = txtBusca.getText();
        List<Cliente> resultados = gerenciadorClientes.buscar(termo);
        carregarTabela(resultados);
        if (resultados.isEmpty() && !termo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum cliente encontrado para o termo: " + termo, "Busca", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Regra 1.e: Ordenar
    private void ordenarClientes() {
        String campo = (String) cmbOrdenar.getSelectedItem();
        // Pega a lista atual (filtrada ou não)
        List<Cliente> listaAtual = gerenciadorClientes.buscar(txtBusca.getText()); 
        List<Cliente> listaOrdenada = gerenciadorClientes.ordenar(campo, listaAtual);
        
        carregarTabela(listaOrdenada);
        if (!listaOrdenada.isEmpty()) {
            tabelaClientes.setRowSelectionInterval(0, 0);
            exibirClienteSelecionado();
        }
    }
    
    // Classe utilitária para layout (simulando NetBeans)
    private static class SpringUtilities {
        public static void makeCompactGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad, int yPad) {
            SpringLayout layout;
            try {
                layout = (SpringLayout) parent.getLayout();
            } catch (ClassCastException exc) {
                System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
                return;
            }

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
    return Spring.width(c); 
}

private static Spring getHeight(Component c) {
   
    return Spring.height(c);
}
    }
}
