package tcc_metricas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings("unused")
public class Main {

	public static void main(String[] args) {		
		// TODO Auto-generated method stub

//		//ETAPA 1 SQLITE
		String[] fatores = new String[]{"A","B","C"};
//		String[] niveis = new String[]{"-1","1"};
//		
//		
		//generateMediaExperimentos("");
		//generateMatriz(3, 2);
		generateCombinacoes();
		geraQns(fatores,new long[8][3]);
		
	}
	
	private static void geraQns(String[] fatores, long[][] matriz){
		
		for (int i = 0; i < fatores.length; i++) {
			for (int linha = 0; linha < matriz.length; linha++) {
				for (int coluna = 0; coluna < matriz[0].length; coluna++) {
					
				}
			}
			
			
			
		}
		
		
		
	} 
	private static void generateMatriz(int fatores, int niveis){
		
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
							 
							 print(linhas, colunas, matrizRegressao);
						 }else if(count <= 0){
							 matrizRegressao[i][c] = -1;
							 ++count;
							 if(count == 0)
								 count = (long) Math.abs((linhas/4));
							 
							 print(linhas, colunas, matrizRegressao);
						 }
					}
					 print(linhas, colunas, matrizRegressao);
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
							 
							 print(linhas, colunas, matrizRegressao);
						 }else if(count <= 0){
							 matrizRegressao[i][c] = -1;
							 ++count;
							 if(count == 0)
								 count = (long) Math.abs((linhas/6));
							 
							 print(linhas, colunas, matrizRegressao);
						 }
					}
					 print(linhas, colunas, matrizRegressao);
					continue y; 
				 }
			}	
		}
		
	}
	
	public static void print(long linhas, long colunas,long [][] matrizRegressao){
		
		for (int i = 0; i < linhas; i++) {
			System.out.println("");
			for (int j = 0; j < colunas; j++) {
				System.out.print(matrizRegressao[i][j] + " ");
			}
		}
		System.out.println("");
		System.out.println("------------------------");
	} 
	
	
	private static void generateMediaExperimentos(String etapa){
		
		StringBuilder query = new StringBuilder();
		StringBuilder query2 = new StringBuilder();
		StringBuilder query3 = new StringBuilder();
		
		query.append("select experimento, quantidade, tipo, etapa, plataforma, avg(tempo) as \"tempo(ms)\", min(time_inicio),min(time_fim)  from EXPERIMENTOS group by experimento, quantidade, tipo, etapa, plataforma;");
		query2.append("select avg(app_cpu_usado) as processamento,avg(memoria_usada_kb) as memoria from variaveis_resposta where time >= :inicio and time <= :fim and plataforma = ':plataforma'");		
		query3.append("INSERT INTO MEDIA_VARIAVEIS_RESPOSTAS VALUES (':experimento',:tempo,:processamento,:memoria,':etapa',':plataforma')");
		
		SqliteProvider provider = new SqliteProvider();
		try {
			
			ResultSet result = provider.executeQuery(query.toString());
			
			while(result.next()){
				
				Map parameters = new HashMap<>();
				parameters.put(":inicio", "" + result.getLong(7));
				parameters.put(":fim", "" + result.getLong(8));
				parameters.put(":plataforma",result.getString(5));		
				
				ResultSet result2 = provider.executeQuery(replaceParameters(query2, parameters));
				
				while(result2.next()){				
					
					Map<String,String> parameters2 = new HashMap<>();
					parameters2.put(":experimento", "" + result.getString(1));
					parameters2.put(":tempo", "" + result.getLong(6));
					parameters2.put(":processamento", "" + result2.getDouble(1));
					parameters2.put(":memoria", "" + result2.getDouble(2));
					parameters2.put(":etapa", result.getString(4));
					parameters2.put(":plataforma", result.getString(5));
					
					//PERSISTE MEDIA_VARIAVEIS_RESPOSTA
					provider.execute(replaceParameters(query3, parameters2));	
					
					
				}
				
				
				
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
	
	public static void generateCombinacoes() {
		String[] status = new String[] { "A", "B", "C" }; //aqui pode ser qualquer objeto que implemente Comparable
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
		System.out.println(allCombList);
	}


}
