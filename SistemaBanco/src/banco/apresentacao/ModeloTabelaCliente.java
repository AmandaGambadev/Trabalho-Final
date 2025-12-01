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

    /**
     * Construtor do modelo.
     * @param clientes A lista inicial de clientes a ser exibida.
     */
    public ModeloTabelaCliente(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    /**
     * Atualiza a lista de dados e notifica a JTable sobre a mudança.
     * @param novaLista A nova lista de clientes.
     */
    public void setClientes(List<Cliente> novaLista) {
        this.clientes = novaLista;
        
        // Notifica a JTable que a estrutura/dados completos mudaram
        fireTableDataChanged();
    }

    /**
     * Retorna o número de linhas na tabela.
     * @return O tamanho da lista de clientes.
     */
    @Override
    public int getRowCount() {
        return clientes.size();
    }

    /**
     * Retorna o número de colunas na tabela.
     * @return O tamanho do array de colunas.
     */
    @Override
    public int getColumnCount() {
        return colunas.length;
    }
    
    /**
     * Retorna o nome da coluna no índice especificado.
     * @param columnIndex O índice da coluna.
     * @return O nome da coluna.
     */
    @Override
    public String getColumnName(int columnIndex) {
        return colunas[columnIndex];
    }
    
    /**
     * Fornece o valor para uma célula específica na tabela.
     * @param rowIndex O índice da linha.
     * @param columnIndex O índice da coluna.
     * @return O valor da célula.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Cliente cliente = clientes.get(rowIndex);
        
        // Determina qual atributo do Cliente retornar com base no índice da coluna
        switch (columnIndex) {
            case 0: return cliente.getNome();
            case 1: return cliente.getSobrenome();
            case 2: return cliente.getRg();
            case 3: return formatarCpf(cliente.getCpf()); // CPF formatado
            case 4: return cliente.getEndereco();
            default: return null;
        }
    }
    
    /**
     * Retorna o objeto Cliente completo na linha especificada.
     * @param rowIndex O índice da linha (no modelo).
     * @return O objeto Cliente.
     */
    public Cliente getCliente(int rowIndex) {
        return clientes.get(rowIndex);
    }
    
    /**
     * Aplica a máscara de CPF (###.###.###-##) ao CPF limpo (apenas números).
     * @param cpfLimpo A string de CPF com 11 dígitos.
     * @return O CPF formatado ou o CPF limpo em caso de erro.
     */
    private String formatarCpf(String cpfLimpo) {
        if (cpfLimpo == null || cpfLimpo.length() != 11) {
            return cpfLimpo; // Retorna sem formatação se for inválido
        }
        try {
            MaskFormatter mask = new MaskFormatter("###.###.###-##");
            mask.setValueContainsLiteralCharacters(false); // Indica que o valor de entrada não tem a máscara
            return mask.valueToString(cpfLimpo); // Aplica a máscara e retorna
        } catch (java.text.ParseException e) {
            return cpfLimpo; // Em caso de erro na formatação, retorna o valor original
        }
    }
}