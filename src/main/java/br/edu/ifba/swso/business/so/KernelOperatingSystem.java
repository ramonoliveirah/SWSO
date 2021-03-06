package br.edu.ifba.swso.business.so;

import br.edu.ifba.swso.algorithms.IDiskScheduler;
import br.edu.ifba.swso.algorithms.IPageReplacementAlgorithm;
import br.edu.ifba.swso.algorithms.IProcessesScheduler;
import br.edu.ifba.swso.business.abstractions.File;
import br.edu.ifba.swso.business.so.filemanager.FileSystem;
import br.edu.ifba.swso.business.so.filemanager.IFileSystem;
import br.edu.ifba.swso.business.so.memorymanager.MemoryManager;
import br.edu.ifba.swso.business.so.processmanager.Process;
import br.edu.ifba.swso.business.so.processmanager.ProcessManager;
import br.edu.ifba.swso.business.virtualmachine.CoreVirtualMachine;

public class KernelOperatingSystem {

	private IFileSystem fileSystem;

	private IDiskScheduler diskSchedule;

	private IProcessesScheduler processesScheduler;

	private IPageReplacementAlgorithm pageReplacementAlgorithm;
	
	private int timeslice;

	private MemoryManager memoryManager;

	private ProcessManager processManager;

	public KernelOperatingSystem(CoreVirtualMachine coreVirtualMachine) {
		fileSystem = new FileSystem(coreVirtualMachine);
		processManager = new ProcessManager(coreVirtualMachine);
		memoryManager = new MemoryManager(coreVirtualMachine);
	}

	// CHAMADAS AO SISTEMA
	public void criarProcesso(File file, int priority) {
		try {
			Process process = processManager.criarProcesso(file);
			process.setPriority(priority);
			memoryManager.allocateProcess(process);
			System.out.println("Processo criado - PID:" + process.getPid());
		} catch (Exception e) {

		}
	}

	public void tratarPageFault(int pid, int page) {
		processManager.bloquearProcesso();
		memoryManager.allocatePage(pid, page);
	}
	
	// METHODS OF ACCESS
	public IFileSystem getFileSystem() {
		return fileSystem;
	}

	public MemoryManager getMemoryManager() {
		return memoryManager;
	}

	public ProcessManager getProcessManager() {
		return processManager;
	}

	// ALGORITHMS
	public IDiskScheduler getDiskSchedule() {
		return diskSchedule;
	}

	public void setDiskSchedule(IDiskScheduler diskSchedule) {
		this.diskSchedule = diskSchedule;
	}

	public IProcessesScheduler getProcessesScheduler() {
		return processesScheduler;
	}

	public void setProcessesScheduler(IProcessesScheduler processesScheduler) {
		this.processesScheduler = processesScheduler;
	}

	public IPageReplacementAlgorithm getPageReplacement() {
		return pageReplacementAlgorithm;
	}

	public void setPageReplacement(IPageReplacementAlgorithm pageReplacementAlgorithm) {
		this.pageReplacementAlgorithm = pageReplacementAlgorithm;
	}

	public int getTimeslice() {
		return timeslice;
	}

	public void setTimeslice(int timeslice) {
		this.timeslice = timeslice;
	}

	public void execute() {
		Process running = processManager.getEmExecucao();

		if (processesScheduler.isInterromper(processManager.getListaPronto(), running, timeslice)) {
			running = processManager.escalonamento(processesScheduler);
			memoryManager.updatePageTable();
		}

		running.incrementTimeRunning();
		processManager.incrementarTimeWaitingPronto();
	}
	
	public void finalizarProcesso(Process process) {
		processManager.finalizarProcesso(process);
	}

}
