package br.edu.ifba.swso.business.so.filemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ifba.swso.algorithms.IDiskScheduler;
import br.edu.ifba.swso.business.abstractions.ByteSWSO;
import br.edu.ifba.swso.business.abstractions.File;
import br.edu.ifba.swso.business.abstractions.FileInput;
import br.edu.ifba.swso.business.virtualmachine.CoreVirtualMachine;
import br.edu.ifba.swso.business.virtualmachine.harddisk.HardDisk;

/**
 * @author Ramon
 */
public class FileSystem implements IFileSystem {

	private static final long serialVersionUID = 1L;

	private final Map<Integer, File> listaFile;
	private final HardDisk hardDisk;
	private final List<Integer> listaSetoresLivres;
	private int ultimoId = 0;
	private int nSetores;
	
	public FileSystem(CoreVirtualMachine coreVirtualMachine) {
		this.hardDisk = coreVirtualMachine.getHardDisk();
		this.nSetores = this.hardDisk.getNSetores();
		this.listaFile = new HashMap<Integer, File>();
		this.listaSetoresLivres = createListOfBlankSpaces();
		
	}
	
	public void allocateFile(FileInput fileinput, IDiskScheduler diskScheduler) {
		File newFile = new File(fileinput.getFileName());
		newFile.setColor(fileinput.getColor());
		newFile.setFileID(getNewIdFile());
		newFile.setFileSize(fileinput.getListaInstrucoes().size());
		
		int[] freeSectorsList = freeSectorsList(fileinput.getListaInstrucoes().size());
		
		int[] freeSectorsReordered = diskScheduler.escalonar(freeSectorsList, hardDisk.getPositionReaderHead());
		
		ByteSWSO[] newSector = null;

		int i = 0;
		int buscarSetorArray = 0;
		
		for (ByteSWSO byTe : fileinput.getListaInstrucoes()) {
			if (newSector == null) {
				newSector = new ByteSWSO[hardDisk.getSectorSize()];
			}
			newSector[i] = byTe;

			if (newSector.length == i+1) {
				int sectorVazio = freeSectorsReordered[buscarSetorArray++];
				hardDisk.allocateData(newSector, sectorVazio);
				newFile.getAllocatedSectors().add(sectorVazio);
				newSector = null;
				i = -1;
			}
			i++;
		}
		
		if (newSector != null) {
			int sectorVazio = freeSectorsReordered[buscarSetorArray++];
			hardDisk.allocateData(newSector, sectorVazio);
			newFile.getAllocatedSectors().add(sectorVazio);
		}
		
		this.listaFile.put(newFile.getFileID(), newFile);
	}
	
	public void deallocateFile(int index) {
		File file = this.listaFile.get(index);
		for (Integer setor : file.getAllocatedSectors()) {
			listaSetoresLivres.add(setor);
		}
		Collections.sort(listaSetoresLivres);
		this.listaFile.remove(index);
	}
	
	public int seekIdFilePerSector(int nSector) {
		for (File file: listaFile.values()) {
			for (int nSetorFile : file.getAllocatedSectors()) {
				if (nSector == nSetorFile) {
					return file.getFileID();
				}
			}
		}		
		return -1; 
	}
	
	public File seekFilePerId(int id) {
		if (listaFile.containsKey(id)) {
			return listaFile.get(id);
		}
		return null;
	}

	public  Collection<File> getAllFiles() {
		return new ArrayList<File>(listaFile.values());
	}	
	
	public void simularMovimentacao(String movimento, IDiskScheduler diskScheduler) {
		if (movimento != null) {
			String[] array = movimento.split(",");
			int[] simulatorSectorsList = new int[array.length]; 

			for (int i = 0; i < array.length; i++) {
				simulatorSectorsList[i] = Integer.parseInt(array[i]) * hardDisk.getTrackSize();
			}
			
			int[] sectorsReordered = diskScheduler.escalonar(simulatorSectorsList, hardDisk.getPositionReaderHead());
			
			for (int sector : sectorsReordered) {
				hardDisk.moveReaderHead(sector/8);
			}
		}
	}
	
	//MAPA DE BITS
	public int[] freeSectorsList(int qtdInstrucoes) {
		
		int qtdSetores = qtdInstrucoes/hardDisk.getSectorSize();
		qtdSetores = (qtdInstrucoes % hardDisk.getSectorSize()) == 0 ? qtdSetores : qtdSetores+1;
		
		int[] freeSectorsList = new int[qtdSetores];
		
		for (int i = 0; i < qtdSetores; i++) {
			int sectorVazio = buscarSetorLivreDisco();
			if (sectorVazio == -1) {
				return null;
			} else {
				freeSectorsList[i] = sectorVazio;
			}
		}
		
		return freeSectorsList;
	}

	private int buscarSetorLivreDisco() {
		int nSector;
		
		if (listaSetoresLivres.size() > 0){
			nSector = listaSetoresLivres.get(0);
			listaSetoresLivres.remove(0);
			return nSector;
		} else {
			return -1;
		}
	}
	
	private int getNewIdFile() {
		return ultimoId++;
	}
	
	private List<Integer> createListOfBlankSpaces() {
		List<Integer> listaSetoresLivres = new ArrayList<Integer>(nSetores);
		for (int i = 0; i < nSetores; i++) {
			listaSetoresLivres.add(i);
		}
		return listaSetoresLivres;
	}

	
}
