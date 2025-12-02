package banco.modelo;

// Classe de modelo que representa um Cliente do sistema bancário.
// Implementa a interface Comparable para permitir a ordenação natural por nome.
public class Cliente implements Comparable<Cliente> {
    private String nome;
    private String sobrenome;
    private String rg;
    private String cpf; // Armazenado internamente sem máscara (apenas números).
    private String endereco;

    // Construtor completo do Cliente.
    public Cliente(String nome, String sobrenome, String rg, String cpf, String endereco) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.rg = rg;
        this.cpf = cpf;
        this.endereco = endereco;
    }

    // --- Getters para acessar os dados do cliente ---
    public String getNome() { return nome; }
    public String getSobrenome() { return sobrenome; }
    public String getRg() { return rg; }
    public String getCpf() { return cpf; }
    public String getEndereco() { return endereco; }

    // --- Setters para permitir a atualização do cadastro ---
    public void setNome(String nome) { this.nome = nome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }
    public void setRg(String rg) { this.rg = rg; }
    // O CPF é geralmente mantido como imutável após o cadastro, por isso não tem setter.
    public void setEndereco(String endereco) { this.endereco = endereco; }

    // Implementação do método compareTo, que define a ordem natural do objeto.
    // Usamos o nome para ordenar a lista de clientes alfabeticamente.
    @Override
    public int compareTo(Cliente outro) {
        return this.nome.compareTo(outro.nome);
    }

    // Retorna uma representação em String do Cliente, formatada para exibição.
    // É usado em listas ou ComboBoxes. Inclui o CPF formatado.
    @Override
    public String toString() {
        String cpfFormatado = formatarCpfParaExibicao(this.cpf); 
        return nome + " " + sobrenome + " (CPF: " + cpfFormatado + ")";
    }
    
    // Sobrescreve o método equals para comparar se dois objetos Cliente são o mesmo.
    // A igualdade é definida apenas pela comparação do CPF (que é a chave única).
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Se for o mesmo objeto na memória.
        if (obj == null || getClass() != obj.getClass()) return false; // Se for nulo ou de classe diferente.
        Cliente cliente = (Cliente) obj; // Faz o cast para Cliente.
        return cpf.equals(cliente.cpf); // Compara o CPF.
    }
    
    // Método auxiliar para formatar o CPF que está armazenado sem máscara.
    private String formatarCpfParaExibicao(String cpfLimpo) {
        if (cpfLimpo == null || cpfLimpo.length() != 11) {
            return cpfLimpo; // Retorna sem formatação se não tiver 11 dígitos.
        }
        // Aplica a máscara ###.###.###-## usando substrings.
        return cpfLimpo.substring(0, 3) + "." +
               cpfLimpo.substring(3, 6) + "." +
               cpfLimpo.substring(6, 9) + "-" +
               cpfLimpo.substring(9, 11);
    }
}