
import java.io.*;

import retrieval_model.RetrievalModel;
import retrieval_model.RetrievalModelBM25;

public class QrySopSum extends QrySop {

	public boolean docIteratorHasMatch(RetrievalModel r) {
		return this.docIteratorHasMatchMin(r);
	}

	public double getScore(RetrievalModel r) throws IOException {
		if (r instanceof RetrievalModelBM25) {
			return this.getScoreBM25(r);
		} else {
			throw new IllegalArgumentException(r.getClass().getName() + " doesn't support the SUM operator.");
		}
	}
	
	public double getDefaultScore(RetrievalModel r, int docid) throws IOException {
		if (r instanceof RetrievalModelBM25) {
			return this.getScoreBM25(r);
		} else {
			throw new IllegalArgumentException(r.getClass().getName() + " doesn't support the SUM operator.");
		}
	}

	private double getScoreBM25(RetrievalModel r) throws IOException {
		double sum = 0.0;
		int docid = this.docIteratorGetMatch();

		for (int i = 0; i < this.args.size(); i++) {
			Qry q_i = this.args.get(i);
			if (q_i.docIteratorHasMatch(r) && q_i.docIteratorGetMatch() == docid) {
				sum += ((QrySop) this.args.get(i)).getScore(r);
			}
		}
		return sum;
	}

}
