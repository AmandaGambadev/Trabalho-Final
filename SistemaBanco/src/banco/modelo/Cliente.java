package banco.modelo;

/**
 * Classe de modelo que representa um Cliente do sistema bancário.
 * Implementa Comparable para permitir ordenação natural por nome.
 */
public class Cliente implements Comparable<Cliente> {
    private String nome;
    private String sobrenome;
    private String rg;
    private String cpf; // Armazenado sem máscara (apenas números)
    private String endereco;

    /**
     * Construtor completo do Cliente.
     * @param nome Nome do cliente.
     * @param sobrenome Sobrenome do cliente.
     * @param rg Número do RG.
     * @param cpf CPF do cliente (apenas números).
     * @param endereco Endereço completo.
     */
    public Cliente(String nome, String sobrenome, String rg, String cpf, String endereco) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.rg = rg;
        this.cpf = cpf;
        this.endereco = endereco;
    }

    // --- Getters ---
    public String getNome() { return nome; }
    public String getSobrenome() { return sobrenome; }
    public String getRg() { return rg; }
    public String getCpf() { return cpf; }
    public String getEndereco() { return endereco; }

    // --- Setters (para atualização de cadastro) ---
    public void setNome(String nome) { this.nome = nome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }
    public void setRg(String rg) { this.rg = rg; }
    // O CPF não possui setter, pois não pode ser alterado após o cadastro
    public void setEndereco(String endereco) { this.endereco = endereco; }

    /**
     * Implementação do método compareTo para ordenação natural (por nome).
     * @param outro O outro objeto Cliente para comparação.
     * @return Um valor negativo, zero ou positivo se o nome for menor, igual ou maior.
     */
    @Override
    public int compareTo(Cliente outro) {
        return this.nome.compareTo(outro.nome);
    }

    /**
     * Retorna uma representação em String do Cliente, formatada para exibição em ComboBox/Log.
     * Inclui o CPF formatado.
     * @return String formatada (Nome Sobrenome (CPF: ###.###.###-##)).
     */
    @Override
    public String toString() {
        String cpfFormatado = formatarCpfParaExibicao(this.cpf); 
        return nome + " " + sobrenome + " (CPF: " + cpfFormatado + ")";
    }
    
    /**
     * Compara se dois objetos Cliente são iguais (baseado apenas no CPF).
     * @param obj O objeto a ser comparado.
     * @return true se os CPFs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Compara a referência
        if (obj == null || getClass() != obj.getClass()) return false; // Verifica a classe
        Cliente cliente = (Cliente) obj; // Conversão (cast)
        return cpf.equals(cliente.cpf); // Compara o CPF (chave primária)
    }
    
    /**
     * Método utilitário para formatar o CPF limpo (11 dígitos) no padrão ###.###.###-##.
     * @param cpfLimpo O CPF com 11 dígitos.
     * @return O CPF formatado.
     */
    private String formatarCpfParaExibicao(String cpfLimpo) {
        if (cpfLimpo == null || cpfLimpo.length() != 11) {
            return cpfLimpo; // Retorna o valor original se for inválido
        }
        // Aplica a formatação manual
        return cpfLimpo.substring(0, 3) + "." +
               cpfLimpo.substring(3, 6) + "." +
               cpfLimpo.substring(6, 9) + "-" +
               cpfLimpo.substring(9, 11);
    }
}