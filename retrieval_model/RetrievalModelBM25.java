package retrieval_model;


/**
 * An object that stores parameters for the bm25 retrieval model
 * (there are none) and indicates to the query operators how the query should be
 * evaluated.
 */
public class RetrievalModelBM25 extends RetrievalModel {
	protected double k_1, k_3, b;
	
	public RetrievalModelBM25(double k_1, double k_3, double b) {
        this.k_1 = k_1;
        this.k_3 = k_3;
        this.b = b;
    }

	public String defaultQrySopName() {
		return new String("#sum");
	}
	
	public double getK1() {
		return this.k_1;
	}

	public double getK3() {
		return this.k_3;
	}
	
	public double getB() {
		return this.b;
	}
}
