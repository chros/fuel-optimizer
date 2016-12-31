package com.graphhopper.util;

public class Vehicle {
	private int id;
	private String modello;
	private String cilindrata;
	private String alimentazione;
	private String trasmissione;
	private double consumoUrbano;
	private double consumoExtraurbano;
	private String annoDiProduzione;	
	private String produttore;
	
	
	public Vehicle(String row) throws Exception {
		String[] tokens = row.split(",");
		
		double consumoUrbanoTmp = Double.parseDouble(tokens[6]);
		double consumoExtraurbanoTmp = Double.parseDouble(tokens[7]);
		
		if (0 >= consumoUrbanoTmp || consumoUrbanoTmp >= 200) {
			throw new Exception();
		}
		if (0 >= consumoExtraurbanoTmp || consumoExtraurbanoTmp >= 200) {
			throw new Exception();
		}
			
		id = Integer.parseInt(tokens[0]);
		cilindrata = tokens[1];
		alimentazione = tokens[2];
		produttore = tokens[3];
		modello = tokens[4];
		trasmissione = tokens[5];
		consumoUrbano = consumoUrbanoTmp * 1609.344 / 3.78541;
		consumoExtraurbano = consumoExtraurbanoTmp * 1609.344 / 3.78541;
		annoDiProduzione = tokens[8];
	}
	
	public int getId() {
		return id;
	}	
	public String getProduttore() {
		return produttore;
	}
	public String getModello() {
		return modello;
	}
	public String getCilindrata() {
		return cilindrata;
	}
	public String getAlimentazione() {
		return alimentazione;
	}
	public String getTrasmissione() {
		return trasmissione;
	}
	public double getConsumoUrbano() {
		return consumoUrbano;
	}
	public double getConsumoExtraurbano() {
		return consumoExtraurbano;
	}
	public String getAnnoDiProduzione() {
		return annoDiProduzione;
	}
	
	@Override
	public String toString() {
		return produttore + " " + modello + " - " + cilindrata + " - " +
				annoDiProduzione + " - " + alimentazione + " - " + trasmissione;
	}
}
	
