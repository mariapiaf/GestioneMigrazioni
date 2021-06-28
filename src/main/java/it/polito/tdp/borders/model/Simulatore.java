package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {
	// Modello -> qual è lo stato del sistema ad ogni passo
	private Graph<Country, DefaultEdge> grafo;
	
	// Tipi di evento -> coda prioritaria
	private PriorityQueue<Evento> queue; // è fondamentale quando andiamo ad aggiungere degli eventi mentre stiamo simulando
	
	//Parametri della simulazione -> quello che riceviamo dell'esterno per simulare il nostro esercizio
	private int N_MIGRANTI = 1000;
	private Country partenza;
	
	// valori in output
	private int T = -1;
	private Map<Country, Integer> stanziali;
	// meglio la mappa perchè potrei dover modificare mano a mano questa struttura dati, e con la mappa è più semplice
	// per ordinare poi uso dopo una linkedList
	
	
	public void init(Country country, Graph<Country, DefaultEdge> grafo) {
		// inizializza i parametri del simulatore
		this.partenza = country;
		this.grafo = grafo;
		
		// imposto lo stato iniziale
		this.T = 1;
		this.stanziali = new HashMap<Country,Integer>();
		for(Country c: this.grafo.vertexSet()) {
			stanziali.put(c, 0); // tutti i Paersi hanno persone stanziali = 0 e andando avanti questo numero si incrementa e con la mappa sarà più facile modificare
		}
		
		// creo la coda
		this.queue = new PriorityQueue<Evento>();
		// inserisco il primo evento
		this.queue.add(new Evento(T, partenza, N_MIGRANTI)); // nello stato selezionato dall'utente arrivano n migranti al tempo t = 1 (inizio)
		
	}
	
	public void run() {
		// a partire dall'evento iniziale lancio tutta la simulazione
		// Finchè la coda non si svuota
		// prendo un evento per volta e lo eseguo
		
		Evento e;
		while((e = this.queue.poll()) != null) {
			// simulo l'evento e
			this.T  = e.getT();
			int nPersone = e.getN();
			Country stato = e.getCountry();
			
			// ottengo i vicini di "stato"
			List<Country> vicini = Graphs.neighborListOf(this.grafo, stato);
			
			// una volta conosciuti i vicini possiamo calcolare quante persone si muovono e quante persone finiscono in uno stato
			// nPersone/2 si spostano e si dividono in parti uguali negli stati vicini
			int migrantiPerStato = (nPersone/2)/vicini.size();
			
			// se nPersone/2 è minore di vicini.size() allora nessuno si sposta ma diventano stanziali
			if(migrantiPerStato > 0) {
				// c'è almeno una persona che può spostarsi in ogni stato
				// le persone si possono muovere
				for(Country confinante: vicini) {
					this.queue.add(new Evento(e.getT()+1, confinante, migrantiPerStato));
				}
			}
			
			int stanziali = nPersone - migrantiPerStato*vicini.size();
			this.stanziali.put(stato, this.stanziali.get(stato)+stanziali);
			
		}
		
	}
	
	public Map<Country, Integer> getStanziali() {
		return this.stanziali;
	}
	
	public Integer getT() {
		return this.T;
	}
}
