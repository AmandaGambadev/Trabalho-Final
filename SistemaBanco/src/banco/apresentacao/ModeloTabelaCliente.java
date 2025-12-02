package banco.apresentacao;

import banco.modelo.Cliente;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.MaskFormatter;

// Implementação de AbstractTableModel para fornecer o modelo de dados
// (lista de objetos Cliente) para a JTable na TelaClientes.
public class ModeloTabelaCliente extends AbstractTableModel {

    private List<Cliente> clientes; // Lista de dados
    private final String[] colunas = {"Nome", "Sobrenome", "RG", "CPF", "Endereço"};

    // Construtor do modelo.
    // Inicializa a lista de dados com a lista fornecida.
    public ModeloTabelaCliente(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    // Atualiza a lista de dados e notifica a JTable sobre a mudança.
    public void setClientes(List<Cliente> novaLista) {
        this.clientes = novaLista; // Atualiza a lista de clientes
        
        // Notifica a JTable que a estrutura/dados completos mudaram
        fireTableDataChanged();
    }

    // Retorna o tamanho da lista de dados (número de linhas).
    @Override
    public int getRowCount() {
        return clientes.size();
    }

    
    // Retorna o tamanho do array de colunas (número de colunas).
    @Override
    public int getColumnCount() {
        return colunas.length;
    }
    
    
    //Retorna o nome da coluna no índice especificado.
    @Override
    public String getColumnName(int columnIndex) {
        return colunas[columnIndex];
    }
    
    // Fornece o valor para uma célula específica na tabela.
    // Recebe o índice da linha e da coluna que está sendo requisitado.
    // Retorna o valor correspondente do objeto Cliente.
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Cliente cliente = clientes.get(rowIndex); // Obtém o Cliente na linha especificada
        
        // Determina qual atributo do Cliente retornar com base no índice da coluna
        switch (columnIndex) {
            case 0: return cliente.getNome();
            case 1: return cliente.getSobrenome();
            case 2: return cliente.getRg();
            case 3: return formatarCpf(cliente.getCpf());
            case 4: return cliente.getEndereco();
            default: return null;
        }
    }
    
    // Recebe o índice da linha e retorna o objeto Cliente correspondente.
    // Bom para operações que necessitam do objeto Cliente inteiro.
    public Cliente getCliente(int rowIndex) {
        return clientes.get(rowIndex);
    }
    
    // Método auxiliar para formatar o CPF.
    // Recebe o CPF como uma string de 11 dígitos e aplica a máscara de CPF (###.###.###-##) ao CPF limpo (apenas números).
    // Retorna o CPF formatado.
    private String formatarCpf(String cpfLimpo) {
        if (cpfLimpo == null || cpfLimpo.length() != 11) {
            return cpfLimpo; // Retorna sem formatação se for inválido
        }
        try {
            MaskFormatter mask = new MaskFormatter("###.###.###-##"); // Define a máscara de CPF
            mask.setValueContainsLiteralCharacters(false); // Indica que o valor de entrada não tem a máscara
            return mask.valueToString(cpfLimpo); // Aplica a máscara e retorna
        } catch (java.text.ParseException e) {
            return cpfLimpo; // Em caso de erro na formatação, retorna o valor original
        }
    }
}