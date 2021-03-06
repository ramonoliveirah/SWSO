package br.edu.ifba.swso.business.so.memorymanager;

import java.util.List;

public class ETP {
	private int ppv;
	private int ppr;
	private char bitV;
	private char bitM;
	private List<Integer> allocatedSectors;
	
	//GUARDAR LISTA DE SETORES ONDE ENCOTRAM-SE A PÁGINA
	
	public ETP(int ppv, List<Integer> allocatedSectors) {
		this.ppv = ppv;
		this.ppr = -1;
		bitV = '0';
		bitM = '0';
		this.allocatedSectors = allocatedSectors;
	}
	public int getPpr() {
		return ppr;
	}
	public void setPpr(int ppr) {
		this.ppr = ppr;
	}
	public int getPpv() {
		return ppv;
	}
	public void setPpv(int ppv) {
		this.ppv = ppv;
	}
	public char getBitV() {
		return bitV;
	}
	public void setBitV(char bitV) {
		this.bitV = bitV;
	}
	public char getBitM() {
		return bitM;
	}
	public void setBitM(char bitM) {
		this.bitM = bitM;
	}
	public List<Integer> getAllocatedSectors() {
		return allocatedSectors;
	}
}
