package tcc_metricas;

public class VariaveisResposta {
	double processamento;
	double memoria;
	double tempo;
	
	
	
	public VariaveisResposta(double processamento, double memoria, double tempo) {
		super();
		this.processamento = processamento;
		this.memoria = memoria;
		this.tempo = tempo;
	}
	
	public double getProcessamento() {
		return processamento;
	}
	public void setProcessamento(double processamento) {
		this.processamento = processamento;
	}
	public double getMemoria() {
		return memoria;
	}
	public void setMemoria(double memoria) {
		this.memoria = memoria;
	}
	public double getTempo() {
		return tempo;
	}
	public void setTempo(double tempo) {
		this.tempo = tempo;
	}
	
	

}
