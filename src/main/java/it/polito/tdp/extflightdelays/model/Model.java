package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;



public class Model {
	
	private Graph <Airport,DefaultEdge> grafo;
	private List<Airport> aeroporti;
	private List<Flight> voli;
	private List<Tratta> tratte;
	
	private List<Tratta> rotte;
	
	private Map<Integer,Airport> aeroportiIdMap;
	
	public List<Flight> getVoli(){
		if(this.voli==null) {
			ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
			this.voli = dao.loadAllFlights();
		}
		return voli;
	}
	
	public List<Tratta> getTratte(){
		if(this.tratte==null) {
			ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
			this.tratte = dao.getTratte();
		}
		return this.tratte;
	}
	
	public List<Airport> getAeroporti(){
		if(this.aeroporti==null) {
			ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
			this.aeroporti=dao.loadAllAirports();
			
			this.aeroportiIdMap = new HashMap<Integer,Airport>();
			for(Airport a: this.aeroporti)
	    		this.aeroportiIdMap.put(a.getId(), a);
		}
		return aeroporti;
	}
	
	public void creaGrafo(int x) {
		this.grafo = new SimpleWeightedGraph<Airport,DefaultEdge>(DefaultEdge.class);
		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		rotte = new ArrayList<Tratta>();
		Graphs.addAllVertices(grafo, getAeroporti());
		for(Tratta t:getTratte()) {
			double val=getDistanzaMedia(t.getIdPartenza(),t.getIdArrivo());
			if(val>x) {
				Graphs.addEdge(grafo, aeroportiIdMap.get(t.getIdPartenza()), aeroportiIdMap.get(t.getIdArrivo()), val);
				boolean trovato=false;
				if(rotte.size()==0)
					rotte.add(t);
				for(Tratta tr:rotte) {
				 if((tr.getIdPartenza()==t.getIdPartenza() && tr.getIdArrivo()==t.getIdArrivo()) || (tr.getIdPartenza()==t.getIdArrivo() && tr.getIdArrivo()==t.getIdPartenza()))
				 trovato=true;
				}
				if(trovato==false) {
					rotte.add(t);
				}
			}
		}
	}
	
	public double getDistanzaMedia(int idPartenza, int idArrivo) {
		double km=0.0;
		int count=0;
		for(Tratta t:getTratte()) {
			if((t.getIdPartenza()==idPartenza && t.getIdArrivo()==idArrivo) || (t.getIdPartenza()==idArrivo && t.getIdArrivo()==idPartenza)) {
				km=km+(t.getDistanzaMedia()*t.getNumeroVoli());
				count = count+t.getNumeroVoli();
			}
		}
		return (km/count);
		
	}
	
	public Graph<Airport,DefaultEdge> getGrafo(){
		return this.grafo;
	}
	
	public List<Tratta> getRotte(){
		return this.rotte;
	}
	
	
	

}
