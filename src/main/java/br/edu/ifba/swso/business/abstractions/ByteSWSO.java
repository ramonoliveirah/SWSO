package br.edu.ifba.swso.business.abstractions;


public class ByteSWSO {

	String valueHex;

	public ByteSWSO(String value) {
		valueHex = value; 
	}

	@Override
	public String toString() {
		return valueHex;
	}
	
}
