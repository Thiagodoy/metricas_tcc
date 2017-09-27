package tcc_metricas;

import java.util.List;

public class Experimento {
	String experimento;
	long quantidade;
	String tipo;
	String etapa;
	String plataforma;
	long tempo;
	long minTempo;
	long maxTempo;
	List<VariaveisResposta>listVariaveisResposta;
	
	
	
	public Experimento(String experimento, long quantidade, String tipo, String etapa, String plataforma, long tempo,
			long minTempo, long maxTempo) {
		super();
		this.experimento = experimento;
		this.quantidade = quantidade;
		this.tipo = tipo;
		this.etapa = etapa;
		this.plataforma = plataforma;
		this.tempo = tempo;
		this.minTempo = minTempo;
		this.maxTempo = maxTempo;
	}
	
	
	
	
	public List<VariaveisResposta> getListVariaveisResposta() {
		return listVariaveisResposta;
	}




	public void setListVariaveisResposta(List<VariaveisResposta> listVariaveisResposta) {
		this.listVariaveisResposta = listVariaveisResposta;
	}




	public String getExperimento() {
		return experimento;
	}
	public void setExperimento(String experimento) {
		this.experimento = experimento;
	}
	public long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(long quantidade) {
		this.quantidade = quantidade;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getEtapa() {
		return etapa;
	}
	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	public String getPlataforma() {
		return plataforma;
	}
	public void setPlataforma(String plataforma) {
		this.plataforma = plataforma;
	}
	public long getTempo() {
		return tempo;
	}
	public void setTempo(long tempo) {
		this.tempo = tempo;
	}
	public long getMinTempo() {
		return minTempo;
	}
	public void setMinTempo(long minTempo) {
		this.minTempo = minTempo;
	}
	public long getMaxTempo() {
		return maxTempo;
	}
	public void setMaxTempo(long maxTempo) {
		this.maxTempo = maxTempo;
	}
	
	
}
