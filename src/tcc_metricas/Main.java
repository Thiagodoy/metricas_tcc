package tcc_metricas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Main {

	static SqliteProvider provider;
	static final String[]VARIAVEIS = new String[]{"tempo","processamento","memoria"};
	
	static{
		 provider = new SqliteProvider();
	} 
	public static void main(String[] args) throws SQLException {
		
		
		//ETAPA 1 SQLITE
		String[] fatores = new String[]{"A","B","C"};
		generateMediaExperimentos("SQLITE");
		geraQns(fatores, generateMatriz(3, 2),"SQLITE",3,2);		
		gerarSomaQuadradoEinfluencias("SQLITE",3,2);
		
	}
	
	private static void geraQns(String[] fatores, long[][] matriz, String etapa,int fator,int niveil){
		
		List<List<String>> combinacoes =  generateCombinacoes(fatores);		
		Map<String,VariaveisResposta>mapVariaveisRespostasMedias = new TreeMap<>();
		
		try {
			
			String query ="select experimento, tempo, processamento, memoria from media_variaveis_resposta where etapa = ':etapa'";
			Map<String,String> parameters = new HashMap<>();
			parameters.put(":etapa", "SQLITE");
			ResultSet resultSet = provider.executeQuery(replaceParameters(new StringBuilder(query), parameters));			
			
			while(resultSet.next()){				
					mapVariaveisRespostasMedias.put(resultSet.getString(1), new VariaveisResposta(resultSet.getDouble(3), resultSet.getDouble(4), resultSet.getLong(2)));	
			}
			provider.closeConnection();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
		Map<String,Double>tempo = new LinkedHashMap<>();
		Map<String,Double>processamento = new LinkedHashMap<>();
		Map<String,Double>memoria = new LinkedHashMap<>();
		Map<String,String>obs = new LinkedHashMap<>();
		
		for (String key : mapVariaveisRespostasMedias.keySet()) {
			
			int index = Integer.parseInt(key.substring(1));
			for (List<String> list : combinacoes) {//[[A], [B], [C], [A, B], [A, C], [B, C], [A, B, C]]
				
				if(list.size() == 1){
					int indexFator1 = Arrays.asList(fatores).indexOf(list.get(0));				
					tempo.put(list.get(0) + " - "+ key +" - tempo" , (double) (matriz[index - 1][indexFator1] * mapVariaveisRespostasMedias.get(key).getTempo()) );
					memoria.put(list.get(0) + " - "+ key +" - memoria" , matriz[index - 1][indexFator1] * mapVariaveisRespostasMedias.get(key).getMemoria() );
					processamento.put(list.get(0) + " - "+ key +" - processamento" , matriz[index - 1][indexFator1] * mapVariaveisRespostasMedias.get(key).getProcessamento() );
					
					if(!obs.containsKey(list.get(0)))
						obs.put(list.get(0), key);
					else
						obs.put(list.get(0),obs.get(list.get(0)) + "," + key);
						
				}
				if(list.size() == 2){
					int indexFator1 = Arrays.asList(fatores).indexOf(list.get(0));
					int indexFator2 = Arrays.asList(fatores).indexOf(list.get(1));
					
					String keyNew = list.get(0) + list.get(1) + " - " + key; 
					tempo.put(keyNew + " - tempo" , (double) (matriz[index - 1][indexFator1] * matriz[index - 1][indexFator2]  * mapVariaveisRespostasMedias.get(key).getTempo()) );
					memoria.put(keyNew + " - memoria" , matriz[index - 1][indexFator1] * matriz[index - 1][indexFator2] * mapVariaveisRespostasMedias.get(key).getMemoria() );
					processamento.put(keyNew + " - processamento" , matriz[index - 1][indexFator1] * matriz[index - 1][indexFator2] * mapVariaveisRespostasMedias.get(key).getProcessamento() );
					
					if(!obs.containsKey(list.get(0) + list.get(1)))
						obs.put(list.get(0) + list.get(1), key);
					else
						obs.put(list.get(0) + list.get(1),obs.get(list.get(0) + list.get(1)) + "," + key);
				}
				if(list.size() == 3){
					int indexFator1 = Arrays.asList(fatores).indexOf(list.get(0));
					int indexFator2 = Arrays.asList(fatores).indexOf(list.get(1));
					int indexFator3 = Arrays.asList(fatores).indexOf(list.get(2));
					
					String keyNew = list.get(0) + list.get(1) + list.get(2) + " - " + key;					
					
					tempo.put(keyNew + " - tempo" , (double) (matriz[index - 1][indexFator1] * matriz[index - 1][indexFator2] * matriz[index - 1][indexFator3]  * mapVariaveisRespostasMedias.get(key).getTempo()) );
					memoria.put(keyNew + " - memoria" , matriz[index - 1][indexFator1] * matriz[index - 1][indexFator2] * matriz[index - 1][indexFator3] * mapVariaveisRespostasMedias.get(key).getMemoria() );
					processamento.put(keyNew + " - processamento" , matriz[index - 1][indexFator1] * matriz[index - 1][indexFator2] * matriz[index - 1][indexFator3] * mapVariaveisRespostasMedias.get(key).getProcessamento() );
				

					if(!obs.containsKey(list.get(0) + list.get(1) + list.get(2)))
						obs.put(list.get(0) + list.get(1) + list.get(2), key);
					else
						obs.put(list.get(0) + list.get(1) + list.get(2),obs.get(list.get(0) + list.get(1) + list.get(2)) + "," + key);	
					
				}
			}		
			
		}
		
		
		String query ="INSERT INTO OBSERVACOES VALUES(':observacao',':etapa',':variavel',:y1,:y2,:y3,:y4,:y5,:y6,:y7,:y8,:resultado)";
		
		String[]variaveis = new String[]{"tempo","processamento","memoria"};
		
		
		for (String string : variaveis) {
			for ( String s : obs.keySet()) {
				
				Map<String,Double>mapCurrent = string.equals("tempo") ? tempo : string.equals("processamento")? processamento : memoria;			
				
				
				Map<String,String>parameters = new HashMap<>();
				parameters.put(":y1","" + (mapCurrent.containsKey(s + " - y1 - " + string) ? mapCurrent.get(s + " - y1 - " + string) : 0));
				parameters.put(":y2","" + (mapCurrent.containsKey(s + " - y2 - " + string) ? mapCurrent.get(s + " - y2 - " + string) : 0));
				parameters.put(":y3","" + (mapCurrent.containsKey(s + " - y3 - " + string) ? mapCurrent.get(s + " - y3 - " + string) : 0));
				parameters.put(":y4","" + (mapCurrent.containsKey(s + " - y4 - " + string) ? mapCurrent.get(s + " - y4 - " + string) : 0));
				parameters.put(":y5","" + (mapCurrent.containsKey(s + " - y5 - " + string) ? mapCurrent.get(s + " - y5 - " + string) : 0));
				parameters.put(":y6","" + (mapCurrent.containsKey(s + " - y6 - " + string) ? mapCurrent.get(s + " - y6 - " + string) : 0));
				parameters.put(":y7","" + (mapCurrent.containsKey(s + " - y7 - " + string) ? mapCurrent.get(s + " - y7 - " + string) : 0));
				parameters.put(":y8","" + (mapCurrent.containsKey(s + " - y8 - " + string) ? mapCurrent.get(s + " - y8 - " + string) : 0));
				
				double resultado = 0;
				int count = 1;
				for ( String ss : mapCurrent.keySet()){
					
					resultado += mapCurrent.containsKey(s + " - y"+ count +" - " + string) ? mapCurrent.get(s + " - y"+ count +" - " + string) : 0;
					count++;
				}	
				
				resultado = (1/Math.pow(niveil, fator)) * resultado;
				
				parameters.put(":etapa","" + etapa);
				parameters.put(":resultado","" + resultado);
				parameters.put(":observacao",s);
				parameters.put(":variavel",string);
				
				try {
					provider.execute(replaceParameters(new StringBuilder(query), parameters));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		System.out.println(tempo);
		System.out.println(processamento);
		System.out.println(memoria);
	}
	
	private static void gerarSomaQuadradoEinfluencias(String etapa, int fatores, int niveis) throws SQLException{
		
		
		String query = "select observacao, resultado from observacoes where etapa =':etapa' and variavel = ':variavel'";
		Map<String, Double>mapSst = new HashMap<>();
		Map<String, Double>mapSstObservacao = new LinkedHashMap<>();
		for (String variavel : VARIAVEIS) {

			Map<String,String>parameters = new HashMap<>();
			parameters.put(":etapa", etapa);
			parameters.put(":variavel", variavel);			
			
				ResultSet resultSet = provider.executeQuery(replaceParameters(new StringBuilder(query), parameters));
				double sst = 0d; //SOMA TOTAL DOS QUADRADOS				
				while(resultSet.next()){
					sst += Math.pow(resultSet.getDouble(2), 2);
					String k = "SS_" +  resultSet.getString(1) + "_" + etapa + "_" + variavel; //SS_A_SQLITE_tempo
					mapSstObservacao.put(k, Math.pow(niveis, fatores) * Math.pow(resultSet.getDouble(2),2));
				}				
				mapSst.put("SST" + variavel + etapa, Math.pow(niveis, fatores)*sst);//SSTtempoSQLITE
				provider.closeConnection();
				
				
				
		}			
		
		Set<String> setTempo = mapSstObservacao.keySet().stream().filter(t-> t.contains("tempo")).collect(Collectors.toCollection(LinkedHashSet::new));			
			
			for(String key : setTempo){
			
				String[] keys = key.split("_");
				
				String query1 = " insert into  influencia values (':fator',:memoria,:processamento,:tempo,':etapa')";
			
				//SS_A_SQLITE_tempo
				double memoria = mapSstObservacao.get("SS_"+ keys[1] +"_"+ keys[2] +"_memoria")/ mapSst.get("SST" + "memoria" + keys[2]);
				if(Double.isNaN(memoria))
					memoria = 0;
					
				double tempo = mapSstObservacao.get(key)/ mapSst.get("SST"  + "tempo" + keys[2]);
				if(Double.isNaN(tempo))
					tempo = 0;
					
				double processamento = mapSstObservacao.get("SS_"+ keys[1] +"_"+ keys[2] +"_processamento")/ mapSst.get("SST"  + "processamento" + keys[2]);
				
				if(Double.isNaN(processamento))
					processamento = 0;
			
				Map<String,String>parameters1 = new HashMap<>();
				parameters1.put(":fator",keys[1]);
				parameters1.put(":memoria","" + memoria);
				parameters1.put(":tempo","" + tempo);
				parameters1.put(":processamento","" + processamento);
				parameters1.put(":etapa",keys[2]);
				provider.execute(replaceParameters(new StringBuilder(query1), parameters1));
				provider.closeConnection();
				
		}			
	}
	
	private static long[][]  generateMatriz(int fatores, int niveis){
		
		long [][] matrizRegressao = new long[(int) Math.pow(niveis,fatores)][fatores];
		
		long m = matrizRegressao.length;
		long colunas = matrizRegressao[0].length;
		long linhas  = matrizRegressao.length;
		
		y:
		for (int c = 0; c < colunas; c++) {
			 for (int l = 0; l < linhas; l++) {
				 if(c == 0){
					 if(l < Math.abs((linhas/2)) ){
						 matrizRegressao[l][c] = 1;
					 }else{
						 matrizRegressao[l][c] = -1;
					 }
				 }
				 if(c == 1){
					 long count = (long) Math.abs((linhas/4));					 
					 for (int i = 0; i < linhas; i++) {
						   
						 if(count > 0){
							 matrizRegressao[i][c] = 1;
							 --count;
							 if(count == 0)
								 count = - (long) Math.abs((linhas/4));
							 
							
						 }else if(count <= 0){
							 matrizRegressao[i][c] = -1;
							 ++count;
							 if(count == 0)
								 count = (long) Math.abs((linhas/4));							
						 }
					}
					
					continue y; 					
				 }
				 if(c == 2){
					 long count = (long) Math.abs((linhas/6));					 
					 for (int i = 0; i < linhas; i++) {
						   
						 if(count > 0){
							 matrizRegressao[i][c] = 1;
							 --count;
							 if(count == 0)
								 count = - (long) Math.abs((linhas/6));
							 
							
						 }else if(count <= 0){
							 matrizRegressao[i][c] = -1;
							 ++count;
							 if(count == 0)
								 count = (long) Math.abs((linhas/6));						
						 }
					}
					 print(linhas, colunas, matrizRegressao);
					continue y; 
				 }
			}	
		}		
		return matrizRegressao;
		
	}
	
	public static void print(long linhas, long colunas,long [][] matrizRegressao){
		
		for (int i = 0; i < linhas; i++) {
			System.out.println("");
			for (int j = 0; j < colunas; j++) {
				System.out.print(matrizRegressao[i][j] + " ");
			}
		}		
	} 
	
	
	private static void generateMediaExperimentos(String etapa){
		
		StringBuilder query = new StringBuilder();
		StringBuilder query2 = new StringBuilder();
		StringBuilder query3 = new StringBuilder();
		
		query.append("select experimento, quantidade, tipo, etapa, plataforma, avg(tempo) as \"tempo(ms)\", min(time_inicio),max(time_fim)  from EXPERIMENTOS where etapa =':etapa' group by experimento, quantidade, tipo, etapa, plataforma;");
		query2.append("select avg(app_cpu_usado) as processamento,avg(memoria_usada_kb) as memoria from variaveis_resposta where time >= :inicio and time <= :fim and plataforma = ':plataforma'");		
		query3.append("INSERT INTO MEDIA_VARIAVEIS_RESPOSTA VALUES (':experimento',:tempo,:processamento,:memoria,':etapa',':plataforma');");
		
		
		try {
			
			
			List<Experimento> listExperimentos = new ArrayList<>();	
			Map<String,String>parameters = new HashMap<>();
			parameters.put(":etapa",etapa);
			ResultSet result = provider.executeQuery(replaceParameters(query, parameters));
			
			while(result.next()){
				listExperimentos.add(new Experimento(result.getString(1), result.getLong(2), result.getString(3), result.getString(4), result.getString(5), result.getLong(6), result.getLong(7), result.getLong(8)));
			}			
			provider.closeConnection();
			
			for (Experimento experimento : listExperimentos) {
				
				Map<String,String> parameters1 = new HashMap<>();
				parameters1.put(":inicio", "" + experimento.getMinTempo());
				parameters1.put(":fim", "" + experimento.getMaxTempo());
				parameters1.put(":plataforma",experimento.getPlataforma());	
				
				ResultSet resultSet = provider.executeQuery(replaceParameters(query2, parameters1));
				List<VariaveisResposta> listVariavesResposta = new ArrayList<>();
				while(resultSet.next()){
					listVariavesResposta.add(new VariaveisResposta(resultSet.getDouble(1)/100, resultSet.getDouble(2),experimento.getTempo()/1000));
				}
				experimento.setListVariaveisResposta(listVariavesResposta);
				provider.closeConnection();
			}
			
			for (Experimento experimento : listExperimentos) {
				
				Map<String,String> parameters2 = new HashMap<>();
				parameters2.put(":experimento", "" + experimento.getExperimento());
				parameters2.put(":tempo", "" + experimento.getListVariaveisResposta().get(0).getTempo());
				parameters2.put(":processamento", "" + experimento.getListVariaveisResposta().get(0).getProcessamento());
				parameters2.put(":memoria", "" + experimento.getListVariaveisResposta().get(0).getMemoria());
				parameters2.put(":etapa", experimento.getEtapa());
				parameters2.put(":plataforma", experimento.getPlataforma());
				provider.execute(replaceParameters(query3, parameters2));
				provider.closeConnection();				
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	private static String replaceParameters(StringBuilder query, Map<String,String>parameters){
		String q = query.toString();
		
		for(String key: parameters.keySet()){
			q = q.replace(key, parameters.get(key));
		}		
		return q;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<List<String>> generateCombinacoes(String[]fatores) {
		String[] status = fatores; //aqui pode ser qualquer objeto que implemente Comparable
		List<SortedSet<Comparable>> allCombList = new ArrayList<SortedSet<Comparable>>(); //aqui vai ficar a resposta
		for (String nstatus : status) {
			allCombList.add(new TreeSet<Comparable>(Arrays.asList(nstatus))); //insiro a combinação "1 a 1" de cada item
		}
		for (int nivel = 1; nivel < status.length; nivel++) { 
			List<SortedSet<Comparable>> statusAntes = new ArrayList<SortedSet<Comparable>>(allCombList); //crio uma cópia para poder não iterar sobre o que já foi
			for (Set<Comparable> antes : statusAntes) {
				SortedSet<Comparable> novo = new TreeSet<Comparable>(antes); //para manter ordenado os objetos dentro do set
				novo.add(status[nivel]);
				if (!allCombList.contains(novo)) { //testo para ver se não está repetido
					allCombList.add(novo);
				}
			}
		}
		Collections.sort(allCombList, new Comparator<SortedSet<Comparable>>() { //aqui só para organizar a saída de modo "bonitinho"
			@SuppressWarnings("unchecked")
			public int compare(SortedSet<Comparable> o1, SortedSet<Comparable> o2) {
				int sizeComp = o1.size() - o2.size();
				if (sizeComp == 0) {
					Iterator<Comparable> o1iIterator = o1.iterator();
					Iterator<Comparable> o2iIterator = o2.iterator();
					while (sizeComp == 0 && o1iIterator.hasNext() ) {
						sizeComp = o1iIterator.next().compareTo(o2iIterator.next());
					}
				}
				return sizeComp;
			}
		});
		
		List<List<String>>list = new ArrayList<>();
		
				for (SortedSet<Comparable> sortedSet : allCombList) {//[[A], [B], [C], [A, B], [A, C], [B, C], [A, B, C]]					
					ArrayList<String> li =new ArrayList<>();					
					for (Comparable comparable : sortedSet) { //[A]						
						li.add((String)comparable);					
					}
					list.add(li);
				}		
				
		System.out.println();
		System.out.println(allCombList);
		return list;
	}


}
