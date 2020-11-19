
import java.io.IOException;

import retrieval_model.RetrievalModel;
import retrieval_model.RetrievalModelIndri;

public class QrySopWSum extends QrySop {
	public boolean docIteratorHasMatch(RetrievalModel r) {
		return this.docIteratorHasMatchMin(r);
	}

	public double getScore(RetrievalModel r) throws IOException {
		if (r instanceof RetrievalModelIndri) {
			return this.getScoreIndri(r);
		} else {
			throw new IllegalArgumentException(r.getClass().getName() + " doesn't support the WSUM operator.");
		}
	}
	
	public double getDefaultScore(RetrievalModel r, int docid) throws IOException {
		int size = this.args.size();
		
		double weight = 0.0;
		for (int i = 0; i < size; i++) {
			weight += this.args.get(i).weight;
		}
		
		double score = 0.0;
		for (int i = 0; i < size; i++) {
			QrySop q_i = (QrySop) this.args.get(i);
			score += ((q_i.weight / weight) * q_i.getDefaultScore(r, docid));
		}
		return score;
	}

	public double getScoreIndri(RetrievalModel r) throws IOException {
		int size = this.args.size();
		
		double weight = 0.0;
		for (int i = 0; i < size; i++) {
			weight += this.args.get(i).weight;
		}
		
		double score = 0.0;
		int docid = this.docIteratorGetMatch();
		for (int i = 0; i < this.args.size(); i++) {
			QrySop q_i = (QrySop) this.args.get(i);
			if (q_i.docIteratorHasMatch(r) && q_i.docIteratorGetMatch() == docid) {
				score += ((q_i.weight / weight) * q_i.getScore(r));
			} else {
				score += ((q_i.weight / weight) * q_i.getDefaultScore(r, docid));
			}
		}
		return score;
	}

}
