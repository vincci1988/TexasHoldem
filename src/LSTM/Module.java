package LSTM;

public class Module {

	public Module(int inputSize, int outputSize) {
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		outputs = new double[outputSize];
		for (int i = 0; i < outputs.length; i++)
			outputs[i] = 0;
		cells = new Cell[outputSize];
		for (int i = 0; i < cells.length; i++)
			cells[i] = new Cell(inputSize);
	}

	public Module(double[] genome) throws Exception {
		outputSize = (int) genome[0];
		inputSize = (int) genome[1];
		outputs = new double[outputSize];
		int cellGeneLength = (inputSize + 3) * 4 + 1;
		if (genome.length != cellGeneLength * outputSize + 1) 
			throw new Exception("LSTM.MODULE.MODULE(double[]): INVALID GENE LENGTH");
		cells = new Cell[outputSize];
		for (int i = 0; i < outputSize; i++) {
			double[] cellGenes = new double[cellGeneLength];
			for (int j = 0; j < cellGeneLength; j++)
				cellGenes[j] = genome[1 + i * cellGeneLength + j];
			cells[i] = new Cell(cellGenes);
		}
	}

	public double[] getGenome() {
		int cellGeneLength = (inputSize + 3) * 4 + 1;
		double[] genome = new double[cellGeneLength * outputSize + 1];
		genome[0] = outputSize;
		for (int i = 0; i < outputSize; i++) {
			double[] cellGenes = cells[i].getGenome();
			for (int j = 0; j < cellGeneLength; j++)
				genome[1 + i * cellGeneLength + j] = cellGenes[j];
		}
		return genome;
	}
	
	public void reset() {
		for (int i = 0; i < outputSize; i++) 
			cells[i].reset();
	}
	
	public double[] activate(double[] x) throws Exception {
		if (x.length != inputSize)
			throw new Exception("LSTM.MODULE.ACTIVATE(double[]): INVALID INPUT VECTOR LENGTH");
		for (int i = 0; i < outputSize; i++)
			outputs[i] = cells[i].activate(x);
		return outputs;
	}

	Cell[] cells;
	double[] outputs;
	int inputSize;
	int outputSize;
}
