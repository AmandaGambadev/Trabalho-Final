package banco.modelo;

// Interface que define o contrato básico (métodos obrigatórios) para qualquer tipo de conta bancária.
public interface ContaI {
    
    // Realiza a operação de depósito na conta, retornando true em caso de sucesso.
    public boolean deposita(double valor);
    
    // Realiza a operação de saque na conta, retornando true em caso de sucesso.
    public boolean saca(double valor);
    
    // Retorna o objeto Cliente dono da conta.
    public Cliente getDono();
    
    // Retorna o número único da conta.
    public int getNumero();
    
    // Retorna o saldo atual da conta.
    public double getSaldo();
    
    // Aplica a remuneração (juros/rendimento) específica do tipo de conta.
    public void remunera();
}