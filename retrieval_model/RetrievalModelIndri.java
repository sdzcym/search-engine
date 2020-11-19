package retrieval_model;


/**
 * An object that stores parameters for the Indri retrieval model
 * (there are none) and indicates to the query operators how the query should be
 * evaluated.
 */
public class RetrievalModelIndri extends RetrievalModel {
	protected double mu, lambda;
	
	public RetrievalModelIndri(double mu, double lambda) {
        this.mu = mu;
        this.lambda = lambda;
    }

	public String defaultQrySopName() {
		return new String("#and");
	}
	
	public double getMu() {
		return this.mu;
	}

	public double getLambda() {
		return this.lambda;
	}

}
