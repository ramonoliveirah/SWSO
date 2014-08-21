package br.edu.ifba.swso.negocio.filemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.edu.ifba.swso.negocio.abstracoes.ByteSWSO;
import br.edu.ifba.swso.negocio.abstracoes.File;
import br.edu.ifba.swso.negocio.abstracoes.FileInput;
import br.edu.ifba.swso.negocio.abstracoes.Word;
import br.edu.ifba.swso.negocio.harddisk.HardDisk;
import br.edu.ifba.swso.negocio.harddisk.Plate;
import br.edu.ifba.swso.negocio.harddisk.Track;
import br.edu.ifba.swso.util.Constantes;

/**
 * @author Ramon
 */
public class XFat implements ISistemaArquivo {

	private static final long serialVersionUID = 1L;

	private final Map<Integer, File> listaFile;
	private final HardDisk hardDisk;
	private int ultimoId = 0;

	public XFat(HardDisk hardDisk) {
		this.listaFile = new HashMap<Integer, File>();
		this.hardDisk = hardDisk;
	}

	public void carregarProcesso(byte[] arquivo, String name) {
		FileInput fileInput = new FileInput(name);

		for (String instrucao : new String(arquivo).split(";")) {
			Word word = new Word(instrucao);
			for (int i = 0; i < Constantes.WORD_SIZE; i++) {
				fileInput.writeBytes(word.getIoWord()[i]);
			}
		}
		allocateFile(fileInput);
	}

	public void allocateFile(FileInput fileinput) {
		File newFile = new File(fileinput.getFileName());
		newFile.setFileID(getNewIdFile());
		
		ByteSWSO[] newSector = null;
		int i = 0;
		for (ByteSWSO byTe : fileinput.getListaInstrucoes()) {
			if (newSector == null) {
				newSector = new ByteSWSO[Constantes.SECTOR_SIZE];
			}
			newSector[i] = byTe;

			if (newSector.length == i+1) {
				int sectorVazio = buscarSetorLivreDisco();
				hardDisk.allocateData(newSector, sectorVazio);
				newFile.getAllocatedSectors().add(sectorVazio);
				newSector = null;
				i = -1;
			}
			i++;
		}
		if (newSector != null) {
			int sectorVazio = buscarSetorLivreDisco();
			hardDisk.allocateData(newSector, sectorVazio);
			newFile.getAllocatedSectors().add(sectorVazio);
		}

		this.listaFile.put(newFile.getFileID(), newFile);
	}

	public void deallocateFile(int index) {
		this.listaFile.remove(index);
	}

	private int getNewIdFile() {
		return ultimoId++;
	}

	private int buscarSetorLivreDisco() {
		int nSector;

		for (int i = 0; i < hardDisk.getHd().length; i++) {
			Plate plate = hardDisk.getHd()[i];

			for (int j = 0; j < plate.getTracks().length; j++) {
				Track track = plate.getTracks()[j];

				for (int k = 0; k < track.getSectors().length; k++) {
					nSector = (i * Constantes.PLATE_SIZE * Constantes.TRACK_SIZE) + (j * Constantes.TRACK_SIZE) + k;

					if (track.getSectors()[k].isEmpty()) {
						return nSector;
					}
				}
			}

		}

		return -1;
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

	@Override
	public  Collection<File> getAllFiles() {
		return new ArrayList<File>(listaFile.values());
	}
	
}